package tenis.juego.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un equipo dentro del juego de tenis.
 * 
 * Un equipo está compuesto por uno o más jugadores y contiene
 * información relacionada con el desarrollo del partido como:
 * 
 * - Nombre del equipo
 * - Puntaje obtenido
 * - Lista de jugadores
 * - Estado de preparación del equipo
 * - Avatar o representación visual del equipo
 * 
 * Esta clase permite gestionar los jugadores del equipo,
 * actualizar la información desde otro equipo y controlar
 * el puntaje durante el juego.
 * 
 * Implementa Serializable para permitir que los datos del
 * equipo puedan enviarse por red o almacenarse en archivos.
 */
public class Equipo implements Serializable {

    /**
     * Identificador de versión para serialización.
     */
    private static final long serialVersionUID = 1L;

    /** Nombre del equipo */
    private String nombre;

    /** Puntaje actual del equipo */
    private int puntaje;

    /** Lista de jugadores que pertenecen al equipo */
    private final List<Jugador> jugadores;

    /** Indica si el equipo está listo para comenzar el juego */
    private boolean listo;

    /** Avatar o imagen representativa del equipo */
    private String avatarEquipo;

    /**
     * Constructor de la clase Equipo.
     * 
     * Inicializa el equipo con un nombre y crea una lista vacía
     * de jugadores.
     * 
     * @param nombre nombre del equipo
     */
    public Equipo(String nombre) {
        this.nombre = nombre;
        this.jugadores = new ArrayList<>();
        this.avatarEquipo = "";
    }

    /**
     * Actualiza la información del equipo a partir de otro equipo.
     * 
     * Este método copia los datos del equipo origen para sincronizar
     * el estado del equipo actual.
     * 
     * @param origen equipo del cual se copiarán los datos
     */
    public void actualizarDesde(Equipo origen) {
        nombre = origen.nombre;
        puntaje = origen.puntaje;
        listo = origen.listo;
        avatarEquipo = origen.avatarEquipo;

        jugadores.clear();

        for (Jugador jugador : origen.jugadores) {
            jugadores.add(jugador.copiar());
        }
    }

    /**
     * Obtiene el nombre del equipo.
     * 
     * @return nombre del equipo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modifica el nombre del equipo.
     * 
     * @param nombre nuevo nombre del equipo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el puntaje actual del equipo.
     * 
     * @return puntaje del equipo
     */
    public int getPuntaje() {
        return puntaje;
    }

    /**
     * Establece el puntaje del equipo.
     * 
     * @param puntaje nuevo puntaje
     */
    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    /**
     * Obtiene la lista de jugadores del equipo.
     * 
     * @return lista de jugadores
     */
    public List<Jugador> getJugadores() {
        return jugadores;
    }

    /**
     * Indica si el equipo está listo para jugar.
     * 
     * @return true si el equipo está listo
     */
    public boolean isListo() {
        return listo;
    }

    /**
     * Cambia el estado de preparación del equipo.
     * 
     * @param listo estado de preparación
     */
    public void setListo(boolean listo) {
        this.listo = listo;
    }

    /**
     * Obtiene el avatar del equipo.
     * 
     * @return avatar del equipo
     */
    public String getAvatarEquipo() {
        return avatarEquipo;
    }

    /**
     * Modifica el avatar del equipo.
     * 
     * @param avatarEquipo nuevo avatar
     */
    public void setAvatarEquipo(String avatarEquipo) {
        this.avatarEquipo = avatarEquipo;
    }

    /**
     * Incrementa el puntaje del equipo en uno.
     */
    public void sumarPunto() {
        puntaje++;
    }

    /**
     * Reinicia el puntaje del equipo a cero.
     */
    public void reiniciarPuntaje() {
        puntaje = 0;
    }

    /**
     * Agrega un jugador al equipo.
     * 
     * @param jugador jugador a agregar
     */
    public void agregarJugador(Jugador jugador) {
        jugadores.add(jugador);
    }

    /**
     * Elimina todos los jugadores del equipo.
     */
    public void limpiarJugadores() {
        jugadores.clear();
    }

    /**
     * Busca un jugador que se encuentre en una zona específica.
     * 
     * @param zona zona del campo
     * @return jugador encontrado o null si no existe
     */
    public Jugador obtenerJugadorEnZona(int zona) {
        for (Jugador jugador : jugadores) {
            if (jugador.getZona() == zona) {
                return jugador;
            }
        }
        return null;
    }

    /**
     * Obtiene la cantidad de jugadores del equipo.
     * 
     * @return número de jugadores
     */
    public int getCantidadJugadores() {
        return jugadores.size();
    }
}