package tenis.juego.vista.comun;

import tenis.juego.modelo.EstadoJuego;

/**
 * Clase utilitaria que centraliza todos los textos usados en la partida.
 * 
 * Funciones principales:
 * - Proveer reglas del juego
 * - Formatear tiempo
 * - Traducir estados del juego a texto amigable
 * - Simplificar eventos del sistema para mostrarlos al jugador
 * 
 * Es una clase final con constructor privado porque solo contiene métodos estáticos.
 */
public final class TextosPartida {

    /**
     * Constructor privado para evitar instanciación.
     */
    private TextosPartida() {
    }

    /**
     * Devuelve las reglas del juego en formato texto.
     * 
     * @return String con las reglas
     */
    public static String reglas() {
        return "1. Si la pelota toca la malla, el punto es para el rival.\n"
                + "2. Se gana el juego al alcanzar 10 puntos con una diferencia mínima de 2.\n"
                + "3. Si el tiempo termina en empate, se juega un punto de oro.\n"
                + "4. Cada equipo debe evitar que la pelota caiga en su lado de la cancha."
                + "5. El saque inicial se asigna aleatoriamente.";
    }

    /**
     * Convierte segundos a formato mm:ss.
     * 
     * Ejemplo: 125 -> "2:05"
     * 
     * @param segundos tiempo en segundos
     * @return tiempo formateado
     */
    public static String formatearTiempo(int segundos) {
        return String.format("%d:%02d", segundos / 60, segundos % 60);
    }

    /**
     * Convierte el estado del juego (enum) a un texto amigable para el usuario.
     * 
     * @param estado estado actual del juego
     * @return descripción simplificada
     */
    public static String formatearEstadoJugador(EstadoJuego estado) {
        switch (estado) {
            case ESPERANDO_EQUIPOS:
                return "Esperando";
            case LISTO_PARA_INICIAR:
                return "Listos";
            case JUGANDO:
                return "Jugando";
            case PUNTO_DE_ORO:
                return "Punto final";
            case TERMINADO:
                return "Termino";
            default:
                // Si no coincide, usa la descripción del enum
                return estado.getDescripcion();
        }
    }

    /**
     * Convierte mensajes largos del sistema en mensajes cortos para el jugador.
     * 
     * Se basa en palabras clave usando contains().
     * 
     * @param evento mensaje original del sistema
     * @return mensaje simplificado para UI
     */
    public static String formatearEventoJugador(String evento) {

        // Se convierte a minúscula para evitar problemas de comparación
        String eventoMinuscula = evento.toLowerCase();

        if (eventoMinuscula.contains("configura ambos equipos")) {
            return "Configura tu equipo";
        }
        if (eventoMinuscula.contains("ambos equipos estan listos")) {
            return "Esperando inicio";
        }
        if (eventoMinuscula.contains("ha comenzado")) {
            return "Empieza la partida";
        }
        if (eventoMinuscula.contains("saque para")) {
            return "Preparate para sacar";
        }
        if (eventoMinuscula.contains("golpeo la malla")) {
            return "Toco la malla";
        }
        if (eventoMinuscula.contains("salio por el lado")) {
            return "Pelota fuera";
        }
        if (eventoMinuscula.contains("punto para")) {
            return "Punto";
        }
        if (eventoMinuscula.contains("punto de oro")) {
            return "Punto decisivo";
        }
        if (eventoMinuscula.contains("tiempo agotado")) {
            return "Tiempo terminado";
        }
        if (eventoMinuscula.contains("victoria")) {
            return "Ganaron el partido";
        }
        if (eventoMinuscula.contains("reboto")) {
            return "La pelota sigue en juego";
        }
        if (eventoMinuscula.contains("devolvio la pelota")) {
            return "Sigue la jugada";
        }
        if (eventoMinuscula.contains("reiniciado")) {
            return "Nueva partida";
        }

        // Valor por defecto si no coincide con ningún caso
        return "En juego";
    }
}