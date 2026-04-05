package tenis.juego.modelo;

/**
 * Enumeración que representa los diferentes estados del juego.
 * 
 * Permite controlar en qué fase se encuentra el partido
 * y qué acciones pueden realizarse en cada momento.
 */
public enum EstadoJuego {

    /** El sistema espera que los equipos se configuren */
    ESPERANDO_EQUIPOS("Esperando configuracion de equipos"),

    /** Ambos equipos están listos para iniciar el partido */
    LISTO_PARA_INICIAR("Listo para iniciar"),

    /** El partido se encuentra en desarrollo */
    JUGANDO("Partido en juego"),

    /** Estado especial donde el siguiente punto decide el ganador */
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
     * @return descripción del estado
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Devuelve la representación en texto del estado.
     */
    @Override
    public String toString() {
        return descripcion;
    }
}