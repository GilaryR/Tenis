package tenis.juego.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa los datos de un equipo dentro del juego.
 * 
 * Se utiliza como un objeto de transferencia de datos (DTO)
 * para enviar información de un equipo sin exponer directamente
 * los objetos originales del sistema.
 * 
 * Contiene:
 * - Identificador del equipo
 * - Nombre del equipo
 * - Lista de jugadores
 */
public class DatosEquipo implements Serializable {

    /** Identificador de versión para serialización */
    private static final long serialVersionUID = 1L;

    /** Identificador único del equipo */
    private final String idEquipo;

    /** Nombre del equipo */
    private final String nombreEquipo;

    /** Lista de jugadores del equipo */
    private final List<Jugador> jugadores;

    /**
     * Constructor de la clase DatosEquipo.
     * 
     * Se realiza una copia de cada jugador para evitar
     * modificar los objetos originales.
     * 
     * @param idEquipo identificador del equipo
     * @param nombreEquipo nombre del equipo
     * @param jugadores lista de jugadores
     */
    public DatosEquipo(String idEquipo, String nombreEquipo, List<Jugador> jugadores) {

        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.jugadores = new ArrayList<>();

        for (Jugador jugador : jugadores) {
            this.jugadores.add(jugador.copiar());
        }
    }

    /**
     * Obtiene el identificador del equipo.
     * 
     * @return id del equipo
     */
    public String getIdEquipo() {
        return idEquipo;
    }

    /**
     * Obtiene el nombre del equipo.
     * 
     * @return nombre del equipo
     */
    public String getNombreEquipo() {
        return nombreEquipo;
    }

    /**
     * Obtiene la lista de jugadores del equipo.
     * 
     * Devuelve una copia de los jugadores para evitar
     * modificaciones externas.
     * 
     * @return lista de jugadores
     */
    public List<Jugador> getJugadores() {

        List<Jugador> copia = new ArrayList<>();

        for (Jugador jugador : jugadores) {
            copia.add(jugador.copiar());
        }

        return copia;
    }
}