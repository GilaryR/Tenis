package tenis.juego.modelo;

import java.io.Serializable;

/**
 * Representa un jugador dentro de un equipo en el juego de tenis.
 * 
 * Esta clase administra:
 * - El nombre del jugador
 * - El avatar o representación visual
 * - La zona que ocupa en la cancha
 * - La posición del jugador dentro del campo (coordenadas X y Y)
 * - El tipo de control utilizado para mover al jugador
 * 
 * Los jugadores pueden moverse en la cancha mediante métodos
 * que modifican su posición en los ejes X y Y.
 * 
 * Implementa Serializable para permitir guardar o transmitir
 * el estado del jugador en aplicaciones cliente-servidor.
 */
public class Jugador implements Serializable {

    /** Identificador de versión para serialización */
    private static final long serialVersionUID = 1L;

    /** Nombre del jugador */
    private String nombre;

    /** Avatar o imagen representativa del jugador */
    private String avatar;

    /** Zona del jugador dentro del equipo (posición en la cancha) */
    private int zona;

    /** Posición horizontal del jugador */
    private int x;

    /** Posición vertical del jugador */
    private int y;

    /** Tipo de control usado para mover al jugador (mouse o teclado) */
    private final String tipoControl;

    /**
     * Constructor que crea un jugador con nombre, avatar y zona.
     * 
     * Dependiendo de la zona se asigna automáticamente el tipo de control:
     * - Zona 1 → Control con MOUSE
     * - Zona 2 → Control con TECLADO
     * 
     * @param nombre nombre del jugador
     * @param avatar avatar del jugador
     * @param zona zona del jugador en la cancha
     */
    public Jugador(String nombre, String avatar, int zona) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.zona = zona;
        this.tipoControl = zona == 1 ? "MOUSE" : "TECLADO";
    }

    /**
     * Crea una copia del jugador actual.
     * 
     * Este método se utiliza para duplicar el estado del jugador,
     * por ejemplo cuando se sincronizan datos entre cliente y servidor.
     * 
     * @return nueva instancia de Jugador con los mismos datos
     */
    public Jugador copiar() {
        Jugador copia = new Jugador(nombre, avatar, zona);
        copia.setX(x);
        copia.setY(y);
        return copia;
    }

    /**
     * Obtiene el nombre del jugador.
     * 
     * @return nombre del jugador
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del jugador.
     * 
     * @param nombre nuevo nombre del jugador
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el avatar del jugador.
     * 
     * @return avatar del jugador
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Establece el avatar del jugador.
     * 
     * @param avatar nuevo avatar del jugador
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Obtiene la zona que ocupa el jugador en la cancha.
     * 
     * @return zona del jugador
     */
    public int getZona() {
        return zona;
    }

    /**
     * Obtiene la posición horizontal del jugador.
     * 
     * @return coordenada X
     */
    public int getX() {
        return x;
    }

    /**
     * Establece la posición horizontal del jugador.
     * 
     * @param x nueva posición en el eje X
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Obtiene la posición vertical del jugador.
     * 
     * @return coordenada Y
     */
    public int getY() {
        return y;
    }

    /**
     * Establece la posición vertical del jugador.
     * 
     * @param y nueva posición en el eje Y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Obtiene el tipo de control del jugador.
     * 
     * @return tipo de control (MOUSE o TECLADO)
     */
    public String getTipoControl() {
        return tipoControl;
    }

    /**
     * Mueve al jugador hacia arriba en la cancha.
     * 
     * @param cantidad número de unidades que se moverá
     */
    public void moverArriba(int cantidad) {
        y -= cantidad;
    }

    /**
     * Mueve al jugador hacia abajo en la cancha.
     * 
     * @param cantidad número de unidades que se moverá
     */
    public void moverAbajo(int cantidad) {
        y += cantidad;
    }

    /**
     * Mueve al jugador hacia la izquierda.
     * 
     * @param cantidad número de unidades que se moverá
     */
    public void moverIzquierda(int cantidad) {
        x -= cantidad;
    }

    /**
     * Mueve al jugador hacia la derecha.
     * 
     * @param cantidad número de unidades que se moverá
     */
    public void moverDerecha(int cantidad) {
        x += cantidad;
    }
}