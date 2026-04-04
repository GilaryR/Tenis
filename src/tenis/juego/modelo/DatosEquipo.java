package tenis.juego.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un objeto de transferencia de datos (DTO)
 * para la información de un equipo dentro del juego.
 * 
 * Esta clase se utiliza para enviar o almacenar la información
 * básica de un equipo sin modificar directamente el objeto original.
 * 
 * Contiene:
 * - Identificador del equipo
 * - Nombre del equipo
 * - Lista de jugadores del equipo
 * 
 * Para evitar modificar los datos originales, esta clase crea
 * copias de los jugadores cuando se almacenan o se devuelven.
 * 
 * Implementa Serializable para permitir que los datos del equipo
 * puedan enviarse a través de red o almacenarse en archivos.
 */
public class DatosEquipo implements Serializable {

    /** Identificador de versión para serialización */
    private static final long serialVersionUID = 1L;

    /** Identificador del equipo (por ejemplo EQUIPO1 o EQUIPO2) */
    private final String idEquipo;

    /** Nombre del equipo */
    private final String nombreEquipo;

    /** Lista de jugadores pertenecientes al equipo */
    private final List<Jugador> jugadores;

    /**
     * Constructor que crea un objeto de datos del equipo.
     * 
     * Se realiza una copia de cada jugador para evitar modificar
     * directamente los objetos originales del equipo.
     * 
     * @param idEquipo identificador del equipo
     * @param nombreEquipo nombre del equipo
     * @param jugadores lista de jugadores del equipo
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
     * Se devuelve una copia de los jugadores para evitar
     * modificaciones externas sobre los datos originales.
     * 
     * @return copia de la lista de jugadores
     */
    public List<Jugador> getJugadores() {

        List<Jugador> copia = new ArrayList<>();

        for (Jugador jugador : jugadores) {
            copia.add(jugador.copiar());
        }

        return copia;
    }
}