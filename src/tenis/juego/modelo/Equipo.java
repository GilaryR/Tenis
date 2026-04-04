package tenis.juego.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un equipo dentro del juego de tenis.
 * 
 * Esta clase se encarga de administrar:
 * - El nombre del equipo
 * - El puntaje obtenido durante el partido
 * - Los jugadores que pertenecen al equipo
 * - El estado de preparación del equipo (si está listo para jugar)
 * - El avatar representativo del equipo
 * 
 * Además proporciona métodos para:
 * - Agregar o eliminar jugadores
 * - Obtener jugadores según su zona en la cancha
 * - Reiniciar o aumentar el puntaje
 * - Sincronizar los datos del equipo con otro equipo
 * 
 * Implementa Serializable para permitir guardar o transmitir
 * el estado del equipo en aplicaciones cliente-servidor.
 */
public class Equipo implements Serializable {

    /** Identificador de versión para serialización */
    private static final long serialVersionUID = 1L;

    /** Nombre del equipo */
    private String nombre;

    /** Puntaje actual del equipo durante el partido */
    private int puntaje;

    /** Lista de jugadores que pertenecen al equipo */
    private final List<Jugador> jugadores;

    /** Indica si el equipo está listo para iniciar el partido */
    private boolean listo;

    /** Avatar o imagen representativa del equipo */
    private String avatarEquipo;

    /**
     * Constructor que crea un equipo con un nombre específico.
     * 
     * Inicializa la lista de jugadores y deja el avatar vacío.
     * 
     * @param nombre nombre del equipo
     */
    public Equipo(String nombre) {
        this.nombre = nombre;
        this.jugadores = new ArrayList<>();
        this.avatarEquipo = "";
    }

    /**
     * Sincroniza los datos de este equipo con otro equipo.
     * 
     * Copia:
     * - nombre
     * - puntaje
     * - estado de listo
     * - avatar
     * - lista de jugadores
     * 
     * Se usa normalmente en aplicaciones cliente-servidor
     * para mantener el estado actualizado.
     * 
     * @param origen equipo desde el cual se copiarán los datos
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
     * Establece el nombre del equipo.
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
     * Indica si el equipo está listo para iniciar el juego.
     * 
     * @return true si el equipo está listo
     */
    public boolean isListo() {
        return listo;
    }

    /**
     * Cambia el estado de preparación del equipo.
     * 
     * @param listo true si el equipo está listo
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
     * Establece el avatar del equipo.
     * 
     * @param avatarEquipo ruta o nombre del avatar
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
     * @param jugador jugador que se agregará al equipo
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
     * Busca un jugador según la zona que ocupa en la cancha.
     * 
     * @param zona zona donde se encuentra el jugador
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
     * Obtiene la cantidad total de jugadores del equipo.
     * 
     * @return número de jugadores
     */
    public int getCantidadJugadores() {
        return jugadores.size();
    }
}