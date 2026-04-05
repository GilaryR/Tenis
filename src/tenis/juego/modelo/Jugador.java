package tenis.juego.modelo;

import java.io.Serializable;

/**
 * Representa un jugador dentro del juego de tenis.
 * 
 * Cada jugador pertenece a un equipo y tiene una posición dentro del campo
 * de juego. También contiene información visual como el avatar y define
 * el tipo de control que utilizará el jugador (mouse o teclado).
 * 
 * La posición del jugador se maneja mediante coordenadas (x, y)
 * que representan su ubicación dentro del campo de juego.
 * 
 * Esta clase implementa Serializable para permitir que los datos
 * del jugador puedan enviarse a través de red o guardarse en archivos.
 */
public class Jugador implements Serializable {

    /**
     * Identificador de versión para serialización.
     */
    private static final long serialVersionUID = 1L;

    /** Nombre del jugador */
    private String nombre;

    /** Avatar o imagen que representa al jugador */
    private String avatar;

    /** Zona del campo donde juega (1 o 2) */
    private int zona;

    /** Posición horizontal del jugador en el campo */
    private int x;

    /** Posición vertical del jugador en el campo */
    private int y;

    /** Tipo de control que utiliza el jugador */
    private final String tipoControl;

    /**
     * Constructor de la clase Jugador.
     * 
     * Inicializa un jugador con su nombre, avatar y zona dentro del campo.
     * Dependiendo de la zona, se define automáticamente el tipo de control:
     * 
     * Zona 1 → Control con mouse  
     * Zona 2 → Control con teclado
     * 
     * @param nombre nombre del jugador
     * @param avatar avatar o imagen del jugador
     * @param zona zona del campo donde jugará
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
     * Este método se utiliza para evitar modificar directamente
     * el objeto original cuando se necesita enviar o compartir
     * la información del jugador.
     * 
     * @return copia del jugador
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
     * Modifica el nombre del jugador.
     * 
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el avatar del jugador.
     * 
     * @return avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Modifica el avatar del jugador.
     * 
     * @param avatar nuevo avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Obtiene la zona del jugador dentro del campo.
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
     * @param x nueva coordenada X
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
     * @param y nueva coordenada Y
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
     * Mueve el jugador hacia arriba.
     * 
     * @param cantidad cantidad de movimiento
     */
    public void moverArriba(int cantidad) {
        y -= cantidad;
    }

    /**
     * Mueve el jugador hacia abajo.
     * 
     * @param cantidad cantidad de movimiento
     */
    public void moverAbajo(int cantidad) {
        y += cantidad;
    }

    /**
     * Mueve el jugador hacia la izquierda.
     * 
     * @param cantidad cantidad de movimiento
     */
    public void moverIzquierda(int cantidad) {
        x -= cantidad;
    }

    /**
     * Mueve el jugador hacia la derecha.
     * 
     * @param cantidad cantidad de movimiento
     */
    public void moverDerecha(int cantidad) {
        x += cantidad;
    }
}