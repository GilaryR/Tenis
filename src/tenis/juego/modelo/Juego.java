package tenis.juego.modelo;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase principal que controla la lógica del partido de tenis.
 *
 * Esta clase administra:
 * - Los equipos participantes
 * - La pelota del juego
 * - El estado del partido
 * - El tiempo restante
 * - El sistema de saque
 * - El sistema de puntuación
 * - La detección de colisiones
 * - La física básica del juego
 *
 * También gestiona reglas como:
 * - Punto de oro
 * - Diferencia mínima de victoria
 * - Rebotes contra límites
 * - Colisiones con jugadores
 * - Golpes manuales
 *
 * Implementa Serializable para permitir enviar el estado del
 * juego por red o guardarlo.
 */
public class Juego implements Serializable {

    /** Identificador de versión para serialización */
    private static final long serialVersionUID = 1L;

    /** Puntaje necesario para ganar el partido */
    private static final int PUNTAJE_OBJETIVO = 10;

    /** Diferencia mínima de puntos para ganar */
    private static final int DIFERENCIA_MINIMA = 2;

    /** Coordenada X del centro de la cancha */
    private static final int CENTRO_CANCHA_X = 400;

    /** Radio usado para detectar colisión con jugadores */
    private static final int RADIO_COLISION_JUGADOR = 34;

    /** Tiempo mínimo entre golpes del mismo jugador */
    private static final long ENFRIAMIENTO_GOLPE_MS = 180;

    /** Equipo 1 del partido */
    private final Equipo equipo1;

    /** Equipo 2 del partido */
    private final Equipo equipo2;

    /** Pelota utilizada en el juego */
    private final Pelota pelota;

    /** Estado actual del juego */
    private EstadoJuego estado;

    /** Tiempo restante del partido en segundos */
    private int tiempoRestante;

    /** Temporizador del partido */
    private transient Timer temporizador;

    /** Nombre del equipo ganador */
    private String ganador;

    /** Duración total del partido en segundos */
    private int duracionPartidoSegundos;

    /** Último evento ocurrido en el juego */
    private String ultimoEvento;

    /** Indica si el punto de oro está activo */
    private boolean puntoDeOroActivo;

    /** Indica si hay un saque pendiente */
    private boolean saquePendiente;

    /** Identificador del equipo que debe sacar */
    private String equipoSacador;

    /** Zona del jugador que realizará el saque */
    private int zonaSacador;

    /** Identificador del último jugador que golpeó la pelota */
    private String ultimoGolpeClave;

    /** Tiempo del último golpe registrado */
    private long ultimoGolpeTiempoMs;

    /**
     * Constructor que crea un partido con duración por defecto (3 minutos).
     *
     * @param nombreEquipo1 nombre del primer equipo
     * @param nombreEquipo2 nombre del segundo equipo
     */
    public Juego(String nombreEquipo1, String nombreEquipo2) {
        this(nombreEquipo1, nombreEquipo2, 180);
    }

    /**
     * Constructor principal del juego.
     *
     * @param nombreEquipo1 nombre del primer equipo
     * @param nombreEquipo2 nombre del segundo equipo
     * @param duracionSegundos duración total del partido
     */
    public Juego(String nombreEquipo1, String nombreEquipo2, int duracionSegundos) {
        equipo1 = new Equipo(nombreEquipo1);
        equipo2 = new Equipo(nombreEquipo2);
        pelota = new Pelota();
        duracionPartidoSegundos = duracionSegundos;
        tiempoRestante = duracionSegundos;
        estado = EstadoJuego.ESPERANDO_EQUIPOS;
        ganador = "";
        ultimoEvento = "Configura ambos equipos para comenzar.";
        saquePendiente = false;
        equipoSacador = "EQUIPO1";
        zonaSacador = 1;
        ultimoGolpeClave = "";
        ultimoGolpeTiempoMs = 0L;
    }

    /**
     * Sincroniza el estado del juego con otro objeto Juego.
     * Se utiliza principalmente en aplicaciones cliente-servidor.
     *
     * @param origen juego del cual se copiarán los datos
     */
    public synchronized void sincronizarDesde(Juego origen) {
        equipo1.actualizarDesde(origen.equipo1);
        equipo2.actualizarDesde(origen.equipo2);
        pelota.copiarDesde(origen.pelota);
        estado = origen.estado;
        tiempoRestante = origen.tiempoRestante;
        ganador = origen.ganador;
        duracionPartidoSegundos = origen.duracionPartidoSegundos;
        ultimoEvento = origen.ultimoEvento;
        puntoDeOroActivo = origen.puntoDeOroActivo;
        saquePendiente = origen.saquePendiente;
        equipoSacador = origen.equipoSacador;
        zonaSacador = origen.zonaSacador;
        ultimoGolpeClave = origen.ultimoGolpeClave;
        ultimoGolpeTiempoMs = origen.ultimoGolpeTiempoMs;
    }

    /**
     * Inicia el partido si ambos equipos están listos.
     */
    public synchronized void iniciarJuego() {
        if (!puedeIniciar()) {
            return;
        }

        puntoDeOroActivo = false;
        ganador = "";
        tiempoRestante = duracionPartidoSegundos;
        estado = EstadoJuego.JUGANDO;
        ultimoEvento = "El partido ha comenzado.";

        prepararSaque("EQUIPO1");
        iniciarTemporizador();
    }

    /**
     * Inicia el temporizador que controla el tiempo del partido.
     * Reduce el tiempo restante cada segundo.
     */
    private void iniciarTemporizador() {

        detenerTemporizador();

        temporizador = new Timer();

        temporizador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                synchronized (Juego.this) {

                    if (estado != EstadoJuego.JUGANDO || puntoDeOroActivo) {
                        return;
                    }

                    if (tiempoRestante > 0) {
                        tiempoRestante--;
                    }

                    if (tiempoRestante == 0) {

                        if (equipo1.getPuntaje() == equipo2.getPuntaje()) {

                            puntoDeOroActivo = true;
                            estado = EstadoJuego.PUNTO_DE_ORO;

                            ultimoEvento = "Tiempo agotado. Se activa el punto de oro.";

                            prepararSaque("EQUIPO1");

                        } else {

                            ganador = equipo1.getPuntaje() > equipo2.getPuntaje()
                                    ? equipo1.getNombre()
                                    : equipo2.getNombre();

                            ultimoEvento = "Tiempo agotado. Gana " + ganador + ".";

                            finalizarJuego();
                        }
                    }
                }
            }
        }, 1000, 1000);
    }

    /**
     * Actualiza la física del juego.
     *
     * Controla:
     * - movimiento de la pelota
     * - rebotes
     * - colisiones
     * - puntos
     *
     * @param limiteSuperior borde superior de la cancha
     * @param limiteInferior borde inferior
     * @param limiteIzquierdo borde izquierdo
     * @param limiteDerecho borde derecho
     * @param posicionMalla posición de la red
     */
    public synchronized void actualizarFisica(
            int limiteSuperior,
            int limiteInferior,
            int limiteIzquierdo,
            int limiteDerecho,
            int posicionMalla) {

        if (estado != EstadoJuego.JUGANDO && estado != EstadoJuego.PUNTO_DE_ORO) {
            return;
        }

        if (saquePendiente) {
            posicionarPelotaEnSacador();
            return;
        }

        int xAnterior = pelota.getX();
        int yAnterior = pelota.getY();

        pelota.mover();

        if (pelota.getY() <= limiteSuperior) {
            pelota.setY(limiteSuperior);
            pelota.invertirDireccionY();
        } else if (pelota.getY() >= limiteInferior) {
            pelota.setY(limiteInferior);
            pelota.invertirDireccionY();
        }

        if (tocaMalla(xAnterior, yAnterior, posicionMalla)) {
            Equipo rival = pelota.getVelocidadX() > 0 ? equipo2 : equipo1;
            registrarPunto(rival, "La pelota golpeo la malla.");
            return;
        }

        if (procesarColisionConJugadores()) {
            return;
        }

        if (pelota.getX() <= limiteIzquierdo) {
            registrarPuntoPorSalida("EQUIPO2", "La pelota salio por el lado del Equipo 1.");
        } else if (pelota.getX() >= limiteDerecho) {
            registrarPuntoPorSalida("EQUIPO1", "La pelota salio por el lado del Equipo 2.");
        }
    }

    /**
     * Finaliza el partido.
     *
     * Cambia el estado del juego a TERMINADO,
     * detiene el temporizador y la pelota.
     */
    public synchronized void finalizarJuego() {
        estado = EstadoJuego.TERMINADO;
        detenerTemporizador();
        pelota.detener();
    }

    /**
     * Reinicia completamente el estado del partido.
     */
    public synchronized void reiniciarJuego() {

        detenerTemporizador();

        equipo1.reiniciarPuntaje();
        equipo2.reiniciarPuntaje();

        equipo1.setListo(false);
        equipo2.setListo(false);

        pelota.reiniciar();

        tiempoRestante = duracionPartidoSegundos;

        ganador = "";

        puntoDeOroActivo = false;

        estado = EstadoJuego.ESPERANDO_EQUIPOS;

        saquePendiente = false;

        equipoSacador = "EQUIPO1";

        zonaSacador = 1;

        ultimoGolpeClave = "";

        ultimoGolpeTiempoMs = 0L;

        ultimoEvento = "Partido reiniciado. Esperando equipos.";
    }

    /**
     * Detiene el temporizador del juego.
     */
    private void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.cancel();
            temporizador = null;
        }
    }
}