package tenis.juego.controlador;


import tenis.juego.comunicacion.Mensaje;
import tenis.juego.modelo.DatosEquipo;
import tenis.juego.modelo.Equipo;
import tenis.juego.modelo.EstadoJuego;
import tenis.juego.modelo.Juego;
import tenis.juego.modelo.Jugador;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controlador principal del juego.
 * 
 * Esta clase se encarga de manejar toda la lógica central de la partida,
 * como la comunicación con los equipos, actualizar el estado del juego,
 * procesar acciones de los jugadores y enviar actualizaciones.
 * 
 * También controla la física del juego usando un timer que se ejecuta
 * constantemente (como un loop del juego).
 * 
 * Extiende de ControladorJuegoBase, por lo que hereda la parte de comunicación.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */
public class ControladorCentral extends ControladorJuegoBase {

    /** Límites del campo de juego */
    private static final int LIMITE_SUPERIOR = 60;
    private static final int LIMITE_INFERIOR = 640;
    private static final int LIMITE_IZQUIERDO = 55;
    private static final int LIMITE_DERECHO = 745;

    /** Posición de la malla en el campo */
    private static final int POSICION_MALLA = 400;

    /** Guarda las direcciones IP de cada equipo */
    private final Map<String, InetAddress> direccionesEquipos = new HashMap<>();

    /** Guarda los puertos de cada equipo */
    private final Map<String, Integer> puertosEquipos = new HashMap<>();

    /** Maneja los sonidos del juego */
    private final MonitorSonidosPartida monitorSonidos;

    /** Timer que actualiza la física del juego */
    private Timer timerFisica;

    /** Indica si el juego está en reinicio */
    private boolean reinicioProgramado;

    /**
     * Constructor del controlador central.
     * 
     * Inicializa el monitor de sonidos y el timer de la física.
     * 
     * @param juego instancia del juego
     * @param puertoLocal puerto donde se escucha la conexión
     * @throws Exception si ocurre un error en la inicialización
     */
    public ControladorCentral(Juego juego, int puertoLocal) throws Exception {
        super(juego, puertoLocal);
        this.monitorSonidos = new MonitorSonidosPartida(juego);
        iniciarTimerFisica();
    }

    /**
     * Inicia el timer que actualiza la física del juego.
     * 
     * Se ejecuta cada 16 ms (aprox 60 FPS).
     * Solo actualiza si el juego está en estado JUGANDO o PUNTO_DE_ORO.
     */
    private void iniciarTimerFisica() {
        timerFisica = new Timer();
        timerFisica.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (juego.getEstado() == EstadoJuego.JUGANDO || juego.getEstado() == EstadoJuego.PUNTO_DE_ORO) {
                    reinicioProgramado = false;
                    juego.actualizarFisica(LIMITE_SUPERIOR, LIMITE_INFERIOR, LIMITE_IZQUIERDO, LIMITE_DERECHO, POSICION_MALLA);
                    procesarSonidosLocales();
                    broadcastEstado();
                }
            }
        }, 0, 16);
    }

    /**
     * Procesa los mensajes que llegan desde los equipos.
     * 
     * Dependiendo del tipo de mensaje, realiza diferentes acciones:
     * - CONFIG: configurar equipo
     * - LISTO: marcar equipo listo
     * - POSICION: actualizar posición jugador
     * - GOLPE: procesar golpe
     * - SOLICITAR_REINICIO: reiniciar juego
     */
    @Override
    public void procesarMensaje(Mensaje mensaje, InetAddress origen, int puerto) {
        String tipo = mensaje.getTipo();

        if (tipo.startsWith("CONFIG_")) {
            registrarDireccion(tipo.substring(7), origen, puerto);
            actualizarConfiguracion((DatosEquipo) mensaje.getDatos());
            return;
        }

        if (tipo.startsWith("LISTO_")) {
            String idEquipo = tipo.substring(6);
            registrarDireccion(idEquipo, origen, puerto);
            marcarEquipoListo(idEquipo);
            return;
        }

        if (tipo.startsWith("POSICION_")) {
            actualizarPosicion(tipo.substring(9), (Jugador) mensaje.getDatos());
            return;
        }

        if (tipo.startsWith("GOLPE_")) {
            procesarGolpe(tipo.substring(6), (Jugador) mensaje.getDatos());
            return;
        }

        if ("SOLICITAR_REINICIO".equals(tipo)) {
            reiniciarPartido();
        }
    }

    /**
     * Guarda la IP y puerto de un equipo.
     */
    private void registrarDireccion(String idEquipo, InetAddress direccion, int puerto) {
        direccionesEquipos.put(idEquipo, direccion);
        puertosEquipos.put(idEquipo, puerto);
    }

    /**
     * Actualiza la información de un equipo (nombre, jugadores, avatar).
     */
    private void actualizarConfiguracion(DatosEquipo datosEquipo) {
        Equipo equipo = obtenerEquipo(datosEquipo.getIdEquipo());

        equipo.setNombre(datosEquipo.getNombreEquipo());
        equipo.setAvatarEquipo(
                datosEquipo.getJugadores().isEmpty() ? "" :
                        datosEquipo.getJugadores().get(0).getAvatar()
        );

        equipo.limpiarJugadores();

        for (Jugador jugador : datosEquipo.getJugadores()) {
            equipo.agregarJugador(jugador.copiar());
        }

        equipo.setListo(false);
        juego.actualizarEstadoDeEspera();
        broadcastEstado();
    }

    /**
     * Marca un equipo como listo para jugar.
     */
    private void marcarEquipoListo(String idEquipo) {
        obtenerEquipo(idEquipo).setListo(true);
        juego.actualizarEstadoDeEspera();
        broadcastEstado();
    }

    /**
     * Actualiza la posición de un jugador.
     */
    private void actualizarPosicion(String idEquipo, Jugador jugadorActualizado) {
        Jugador jugador = obtenerEquipo(idEquipo)
                .obtenerJugadorEnZona(jugadorActualizado.getZona());

        if (jugador != null) {
            jugador.setX(jugadorActualizado.getX());
            jugador.setY(jugadorActualizado.getY());
        }
    }

    /**
     * Procesa un golpe de un jugador.
     */
    private void procesarGolpe(String idEquipo, Jugador jugador) {
        Jugador jugadorOficial = obtenerEquipo(idEquipo)
                .obtenerJugadorEnZona(jugador.getZona());

        if (jugadorOficial == null) return;

        int direccion = "EQUIPO1".equals(idEquipo) ? 11 : -11;

        if (juego.verificarGolpe(idEquipo, jugadorOficial)) {
            juego.golpearPelota(idEquipo, jugadorOficial, direccion);
            broadcastEstado();
        }
    }

    /**
     * Ejecuta los sonidos del juego.
     */
    private void procesarSonidosLocales() {
        monitorSonidos.procesar(juego, null);
    }

    /**
     * Inicia el partido si todo está listo.
     */
    public void iniciarPartido() {
        if (!juego.puedeIniciar()) return;

        juego.iniciarJuego();
        broadcastMensaje(new Mensaje("INICIAR_JUEGO", null, "CENTRAL"));
        broadcastEstado();
    }

    /**
     * Reinicia el partido.
     */
    public void reiniciarPartido() {
        juego.reiniciarJuego();
        reinicioProgramado = false;
        monitorSonidos.reiniciar(juego);
        broadcastEstado();
    }

    /**
     * Devuelve el equipo según su ID.
     */
    private Equipo obtenerEquipo(String idEquipo) {
        return "EQUIPO1".equals(idEquipo)
                ? juego.getEquipo1()
                : juego.getEquipo2();
    }

    /**
     * Envía el estado del juego a todos.
     */
    private void broadcastEstado() {
        broadcastMensaje(new Mensaje("ACTUALIZAR_JUEGO", juego, "CENTRAL"));
    }

    /**
     * Envía un mensaje a todos los equipos registrados.
     */
    private void broadcastMensaje(Mensaje mensaje) {
        for (Map.Entry<String, InetAddress> entry : direccionesEquipos.entrySet()) {

            Integer puerto = puertosEquipos.get(entry.getKey());
            if (puerto == null) continue;

            try {
                emisor.enviar(mensaje, entry.getValue(), puerto);
            } catch (Exception e) {
                System.err.println("No se pudo enviar a " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Envía una actualización manual del juego.
     */
    @Override
    public void enviarActualizacion() {
        broadcastEstado();
    }

    /**
     * Detiene el controlador y el timer.
     */
    @Override
    public void detener() {
        super.detener();
        if (timerFisica != null) {
            timerFisica.cancel();
        }
    }
}