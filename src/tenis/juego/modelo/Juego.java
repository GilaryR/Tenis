package tenis.juego.modelo;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase principal que representa la lógica completa de un partido de tenis.
 * 
 * Esta clase se encarga de:
 * - Gestionar los equipos participantes
 * - Controlar la pelota y su movimiento
 * - Administrar el tiempo del partido
 * - Controlar el estado del juego
 * - Registrar puntos y determinar el ganador
 * - Controlar el saque y las colisiones con jugadores
 * - Manejar la física básica del juego
 * 
 * El partido termina cuando:
 * - Un equipo alcanza el puntaje objetivo con diferencia mínima
 * - Se acaba el tiempo y un equipo tiene más puntos
 * - Se activa el punto de oro y alguien anota
 * 
 * Implementa Serializable para permitir guardar el estado del juego.
 */
public class Juego implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Puntaje necesario para ganar el partido */
    private static final int PUNTAJE_OBJETIVO = 10;

    /** Diferencia mínima de puntos para declarar ganador */
    private static final int DIFERENCIA_MINIMA = 2;

    /** Posición central de la cancha en el eje X */
    private static final int CENTRO_CANCHA_X = 400;

    /** Radio de colisión entre jugador y pelota */
    private static final int RADIO_COLISION_JUGADOR = 34;

    /** Tiempo de enfriamiento entre golpes para evitar múltiples colisiones */
    private static final long ENFRIAMIENTO_GOLPE_MS = 180;

    /** Equipo 1 del partido */
    private final Equipo equipo1;

    /** Equipo 2 del partido */
    private final Equipo equipo2;

    /** Pelota utilizada en el juego */
    private final Pelota pelota;

    /** Estado actual del partido */
    private EstadoJuego estado;

    /** Tiempo restante del partido en segundos */
    private int tiempoRestante;

    /** Temporizador encargado de disminuir el tiempo del partido */
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

    /** Identificador del equipo que realizará el saque */
    private String equipoSacador;

    /** Zona del jugador que realizará el saque */
    private int zonaSacador;

    /** Identificador del último jugador que golpeó la pelota */
    private String ultimoGolpeClave;

    /** Momento en milisegundos del último golpe */
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
     * Constructor que crea un partido con duración personalizada.
     * 
     * @param nombreEquipo1 nombre del equipo 1
     * @param nombreEquipo2 nombre del equipo 2
     * @param duracionSegundos duración del partido en segundos
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
     * Sincroniza el estado de este juego con otro objeto Juego.
     * 
     * Se usa normalmente en entornos cliente-servidor para actualizar
     * el estado del partido entre diferentes instancias.
     * 
     * @param origen juego desde el cual se copiarán los datos
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
     * Obtiene el equipo 1.
     * 
     * @return equipo 1 del partido
     */
    public synchronized Equipo getEquipo1() {
        return equipo1;
    }

    /**
     * Obtiene el equipo 2.
     * 
     * @return equipo 2 del partido
     */
    public synchronized Equipo getEquipo2() {
        return equipo2;
    }

    /**
     * Obtiene la pelota del juego.
     * 
     * @return objeto pelota
     */
    public synchronized Pelota getPelota() {
        return pelota;
    }

    /**
     * Obtiene el estado actual del juego.
     * 
     * @return estado del juego
     */
    public synchronized EstadoJuego getEstado() {
        return estado;
    }

    /**
     * Cambia el estado del juego.
     * 
     * @param estado nuevo estado del juego
     */
    public synchronized void setEstado(EstadoJuego estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el tiempo restante del partido.
     * 
     * @return tiempo restante en segundos
     */
    public synchronized int getTiempoRestante() {
        return tiempoRestante;
    }

    /**
     * Establece el tiempo restante del partido.
     * 
     * @param tiempoRestante tiempo en segundos
     */
    public synchronized void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    /**
     * Obtiene el nombre del equipo ganador.
     * 
     * @return nombre del ganador
     */
    public synchronized String getGanador() {
        return ganador;
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
     * Finaliza el juego actual.
     * 
     * Se detiene el temporizador y la pelota deja de moverse.
     */
    public synchronized void finalizarJuego() {
        estado = EstadoJuego.TERMINADO;
        detenerTemporizador();
        pelota.detener();
    }

    /**
     * Reinicia completamente el partido.
     * 
     * - Reinicia puntajes
     * - Reinicia el tiempo
     * - Reinicia la pelota
     * - Vuelve al estado inicial
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
}