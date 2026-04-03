
package tenis.juego.controlador;


import tenis.juego.modelo.EstadoJuego;
import tenis.juego.modelo.Juego;
import tenis.juego.util.ReproductorSonido;

/**
 * Clase encargada de controlar los sonidos durante la partida.
 * 
 * Se encarga de detectar cambios en el juego (tiempo, puntajes, eventos)
 * y reproducir los sonidos correspondientes como:
 * - Cuenta regresiva
 * - Punto anotado
 * - Golpe en la malla
 * - Victoria o derrota
 * 
 * Guarda el estado anterior del juego para comparar y saber cuándo
 * debe reproducir un sonido.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */
public final class MonitorSonidosPartida {

    /** Último tiempo registrado para evitar repetir sonidos */
    private int ultimoTiempoEscuchado;

    /** Último puntaje registrado del equipo 1 */
    private int ultimoPuntajeEquipo1;

    /** Último puntaje registrado del equipo 2 */
    private int ultimoPuntajeEquipo2;

    /** Último evento registrado del juego */
    private String ultimoEvento;

    /** Indica si ya se notificó el fin del juego */
    private boolean finNotificado;

    /**
     * Constructor de la clase.
     * 
     * Inicializa los valores con el estado actual del juego.
     * 
     * @param juego instancia del juego
     */
    public MonitorSonidosPartida(Juego juego) {
        reiniciar(juego);
    }

    /**
     * Procesa el estado actual del juego y reproduce sonidos según los cambios detectados.
     * 
     * @param juego estado actual del juego
     * @param idEquipoLocal identificador del equipo local (para saber si ganó o perdió)
     */
    public void procesar(Juego juego, String idEquipoLocal) {

        // Sonido de cuenta regresiva cuando el tiempo está entre 1 y 4 segundos
        if (juego.getTiempoRestante() != ultimoTiempoEscuchado
                && juego.getTiempoRestante() > 0
                && juego.getTiempoRestante() <= 4) {
            ReproductorSonido.reproducirCuentaRegresiva();
        }

        String eventoActual = juego.getUltimoEvento();

        // Detecta cambios en el puntaje
        boolean cambioPuntaje = juego.getEquipo1().getPuntaje() != ultimoPuntajeEquipo1
                || juego.getEquipo2().getPuntaje() != ultimoPuntajeEquipo2;

        // Detecta cambios en el evento
        boolean cambioEvento = !eventoActual.equals(ultimoEvento);

        // Sonido cuando se hace un punto
        if (cambioPuntaje || (cambioEvento && eventoActual.toLowerCase().contains("punto para"))) {
            ReproductorSonido.reproducirPunto();
        }

        // Sonido cuando la pelota golpea la malla
        if (cambioEvento && eventoActual.toLowerCase().contains("malla")) {
            ReproductorSonido.reproducirMalla();
        }

        // Sonido de victoria o derrota cuando el juego termina
        if (idEquipoLocal != null && juego.getEstado() == EstadoJuego.TERMINADO && !finNotificado) {

            String nombrePropio = "EQUIPO1".equals(idEquipoLocal)
                    ? juego.getEquipo1().getNombre()
                    : juego.getEquipo2().getNombre();

            if (nombrePropio.equals(juego.getGanador())) {
                ReproductorSonido.reproducirVictoria();
            } else {
                ReproductorSonido.reproducirGameOver();
            }

            finNotificado = true;
        }

        // Actualiza los valores para la siguiente comparación
        ultimoTiempoEscuchado = juego.getTiempoRestante();
        ultimoPuntajeEquipo1 = juego.getEquipo1().getPuntaje();
        ultimoPuntajeEquipo2 = juego.getEquipo2().getPuntaje();
        ultimoEvento = eventoActual;
    }

    /**
     * Reinicia los valores internos del monitor.
     * 
     * Se usa cuando inicia una nueva partida para evitar sonidos incorrectos.
     * 
     * @param juego estado actual del juego
     */
    public void reiniciar(Juego juego) {
        ultimoTiempoEscuchado = juego.getTiempoRestante();
        ultimoPuntajeEquipo1 = juego.getEquipo1().getPuntaje();
        ultimoPuntajeEquipo2 = juego.getEquipo2().getPuntaje();
        ultimoEvento = juego.getUltimoEvento();
        finNotificado = false;
    }
}