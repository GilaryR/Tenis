package tenis.juego.controlador;

import tenis.juego.comunicacion.Mensaje;
import tenis.juego.modelo.DatosEquipo;
import tenis.juego.modelo.Juego;
import tenis.juego.modelo.Jugador;

import java.net.InetAddress;
import java.util.List;

/**
 * Controlador del equipo (cliente).
 * 
 * Se encarga de:
 * - Enviar acciones del jugador al servidor
 * - Recibir actualizaciones del juego
 * - Sincronizar el estado del juego local
 * - Gestionar sonidos de la partida
 * 
 * Extiende ControladorJuegoBase, por lo que hereda la lógica de comunicación en red.
 */
public class ControladorEquipo extends ControladorJuegoBase {

    // Identificador único del equipo (ej: "1", "2")
    private final String idEquipo;

    // Monitor que gestiona los sonidos según el estado del juego
    private final MonitorSonidosPartida monitorSonidos;

    // Indica si el juego ya ha comenzado
    private boolean juegoIniciado;

    /**
     * Constructor del controlador de equipo.
     * 
     * @param juego instancia del juego local
     * @param puertoLocal puerto donde escucha este cliente
     * @param direccionCentral dirección IP del servidor central
     * @param puertoCentral puerto del servidor central
     * @param idEquipo identificador del equipo
     */
    public ControladorEquipo(Juego juego, int puertoLocal, InetAddress direccionCentral, int puertoCentral, String idEquipo) throws Exception {
        super(juego, puertoLocal);
        this.idEquipo = idEquipo;
        this.monitorSonidos = new MonitorSonidosPartida(juego);

        // Configura el destino de los mensajes (servidor)
        configurarDestino(direccionCentral, puertoCentral);
    }

    /**
     * Procesa los mensajes recibidos desde el servidor.
     */
    @Override
    public void procesarMensaje(Mensaje mensaje, InetAddress origen, int puerto) {

        switch (mensaje.getTipo()) {

            // Actualiza el estado del juego
            case "ACTUALIZAR_JUEGO":
                juego.sincronizarDesde((Juego) mensaje.getDatos());
                procesarSonidosDeEstado();
                break;

            // Indica que el juego ha iniciado
            case "INICIAR_JUEGO":
                juegoIniciado = true;
                monitorSonidos.reiniciar(juego);
                break;

            default:
                break;
        }
    }

    /**
     * Procesa los sonidos según el estado actual del juego.
     */
    private void procesarSonidosDeEstado() {
        monitorSonidos.procesar(juego, idEquipo);
    }

    /**
     * Envía la configuración del equipo al servidor.
     * 
     * @param nombreEquipo nombre del equipo
     * @param jugadores lista de jugadores
     */
    public void enviarConfiguracionEquipo(String nombreEquipo, List<Jugador> jugadores) {
        enviarMensaje(new Mensaje(
                "CONFIG_" + idEquipo,
                new DatosEquipo(idEquipo, nombreEquipo, jugadores),
                idEquipo
        ));
    }

    /**
     * Notifica al servidor que el equipo está listo.
     */
    public void notificarListo() {
        enviarMensaje(new Mensaje(
                "LISTO_" + idEquipo,
                Boolean.TRUE,
                idEquipo
        ));
    }

    /**
     * Envía la posición actual de un jugador.
     * 
     * Se envía una copia para evitar efectos secundarios.
     */
    public void enviarPosicion(Jugador jugador) {
        enviarMensaje(new Mensaje(
                "POSICION_" + idEquipo,
                jugador.copiar(),
                idEquipo
        ));
    }

    /**
     * Envía un evento de golpe realizado por un jugador.
     */
    public void enviarGolpe(Jugador jugador) {
        enviarMensaje(new Mensaje(
                "GOLPE_" + idEquipo,
                jugador.copiar(),
                idEquipo
        ));
    }

    /**
     * Solicita al servidor iniciar una nueva partida.
     */
    public void solicitarNuevaPartida() {
        enviarMensaje(new Mensaje(
                "SOLICITAR_REINICIO",
                null,
                idEquipo
        ));
    }

    /**
     * Solicita una actualización del estado del juego.
     */
    @Override
    public void enviarActualizacion() {
        enviarMensaje(new Mensaje(
                "ESTADO_" + idEquipo,
                null,
                idEquipo
        ));
    }

    /**
     * @return identificador del equipo
     */
    public String getIdEquipo() {
        return idEquipo;
    }

    /**
     * @return true si el juego ya inició
     */
    public boolean isJuegoIniciado() {
        return juegoIniciado;
    }
}