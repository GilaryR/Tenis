package tenis.juego.modelo;

/**
 * Enumeración que representa los diferentes estados en los que puede
 * encontrarse el juego de tenis durante su ejecución.
 * 
 * Cada estado describe una fase específica del partido, permitiendo
 * controlar el flujo del juego y las acciones que se pueden realizar
 * en cada momento.
 * 
 * Los estados posibles son:
 * 
 * - ESPERANDO_EQUIPOS: El juego está esperando que los equipos
 *   configuren sus jugadores y estén listos.
 * 
 * - LISTO_PARA_INICIAR: Ambos equipos ya están configurados y
 *   el partido puede comenzar.
 * 
 * - JUGANDO: El partido está en curso y la pelota está en movimiento.
 * 
 * - PUNTO_DE_ORO: Se activa cuando el tiempo termina y hay empate.
 *   El siguiente punto decide el ganador.
 * 
 * - TERMINADO: El partido ha finalizado y ya existe un ganador.
 * 
 * Cada estado contiene una descripción que puede mostrarse
 * en la interfaz del juego.
 */
public enum EstadoJuego {

    /** Estado en el que el sistema espera la configuración de los equipos */
    ESPERANDO_EQUIPOS("Esperando configuracion de equipos"),

    /** Ambos equipos están listos y el partido puede iniciar */
    LISTO_PARA_INICIAR("Listo para iniciar"),

    /** El partido se encuentra en desarrollo */
    JUGANDO("Partido en juego"),

    /** Estado especial donde el siguiente punto define al ganador */
    PUNTO_DE_ORO("Punto de oro"),

    /** El partido ha terminado */
    TERMINADO("Partido terminado");

    /** Descripción textual del estado */
    private final String descripcion;

    /**
     * Constructor del estado del juego.
     * 
     * @param descripcion descripción del estado
     */
    EstadoJuego(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción del estado.
     * 
     * @return descripción del estado del juego
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Devuelve la descripción del estado como representación en texto.
     * 
     * Este método se usa cuando se imprime el estado directamente,
     * por ejemplo en interfaces gráficas o mensajes del sistema.
     * 
     * @return descripción del estado
     */
    @Override
    public String toString() {
        return descripcion;
    }
}