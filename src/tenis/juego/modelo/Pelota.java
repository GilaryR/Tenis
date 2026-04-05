package tenis.juego.modelo;

import java.io.Serializable;

/**
 * Representa la pelota dentro del juego de tenis.
 * 
 * Esta clase se encarga de manejar la posición, velocidad
 * y estado de la pelota durante el desarrollo del partido.
 * 
 * La pelota puede:
 * - Iniciar su movimiento
 * - Detenerse
 * - Reiniciarse al centro del campo
 * - Cambiar dirección al colisionar
 * - Aumentar su velocidad
 * 
 * También implementa Serializable para permitir que su
 * estado pueda enviarse por red o guardarse en archivos.
 */
public class Pelota implements Serializable {

    /**
     * Identificador de versión para serialización.
     */
    private static final long serialVersionUID = 1L;

    /** Posición horizontal de la pelota */
    private int x;

    /** Posición vertical de la pelota */
    private int y;

    /** Velocidad de la pelota en el eje X */
    private int velocidadX;

    /** Velocidad de la pelota en el eje Y */
    private int velocidadY;

    /** Indica si la pelota está en juego */
    private boolean enJuego;

    /** Velocidad base utilizada al iniciar el movimiento */
    private final int velocidadBase;

    /**
     * Constructor de la pelota.
     * 
     * Inicializa la velocidad base y posiciona la pelota
     * en el centro del campo sin movimiento.
     */
    public Pelota() {
        velocidadBase = 6;
        reiniciar();
    }

    /**
     * Copia el estado de otra pelota.
     * 
     * Este método se usa para sincronizar el estado
     * de la pelota entre diferentes instancias del juego.
     * 
     * @param origen pelota de la cual se copiarán los datos
     */
    public void copiarDesde(Pelota origen) {
        x = origen.x;
        y = origen.y;
        velocidadX = origen.velocidadX;
        velocidadY = origen.velocidadY;
        enJuego = origen.enJuego;
    }

    /**
     * Actualiza la posición de la pelota según su velocidad.
     * 
     * Solo se mueve si la pelota está en juego.
     */
    public void mover() {
        if (enJuego) {
            x += velocidadX;
            y += velocidadY;
        }
    }

    /**
     * Inicia el movimiento de la pelota con dirección aleatoria.
     */
    public void iniciar() {
        int direccion = Math.random() > 0.5 ? velocidadBase : -velocidadBase;
        iniciar(direccion, (int) (Math.random() * 7) - 3);
    }

    /**
     * Inicia la pelota con una velocidad específica.
     * 
     * @param nuevaVelocidadX velocidad horizontal
     * @param nuevaVelocidadY velocidad vertical
     */
    public void iniciar(int nuevaVelocidadX, int nuevaVelocidadY) {
        x = nuevaVelocidadX > 0 ? 170 : 630;
        y = 300;
        velocidadX = nuevaVelocidadX;
        velocidadY = nuevaVelocidadY;
        enJuego = true;
    }

    /**
     * Detiene la pelota.
     */
    public void detener() {
        enJuego = false;
    }

    /**
     * Reinicia la pelota al centro del campo
     * y elimina cualquier movimiento.
     */
    public void reiniciar() {
        x = 400;
        y = 300;
        velocidadX = 0;
        velocidadY = 0;
        enJuego = false;
    }

    /**
     * Invierte la dirección vertical de la pelota.
     * 
     * Se usa cuando la pelota rebota contra
     * los bordes superior o inferior del campo.
     */
    public void invertirDireccionY() {
        velocidadY = -velocidadY;
    }

    /**
     * Aumenta la velocidad horizontal de la pelota.
     * 
     * Este método se usa cuando la pelota golpea
     * a un jugador para aumentar la dificultad del juego.
     */
    public void aumentarVelocidad() {
        if (velocidadX > 0) {
            velocidadX += 1;
        } else if (velocidadX < 0) {
            velocidadX -= 1;
        }
    }

    /**
     * Obtiene la posición horizontal.
     */
    public int getX() {
        return x;
    }

    /**
     * Modifica la posición horizontal.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Obtiene la posición vertical.
     */
    public int getY() {
        return y;
    }

    /**
     * Modifica la posición vertical.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Obtiene la velocidad horizontal.
     */
    public int getVelocidadX() {
        return velocidadX;
    }

    /**
     * Modifica la velocidad horizontal.
     */
    public void setVelocidadX(int velocidadX) {
        this.velocidadX = velocidadX;
    }

    /**
     * Obtiene la velocidad vertical.
     */
    public int getVelocidadY() {
        return velocidadY;
    }

    /**
     * Modifica la velocidad vertical.
     */
    public void setVelocidadY(int velocidadY) {
        this.velocidadY = velocidadY;
    }

    /**
     * Indica si la pelota está en juego.
     */
    public boolean isEnJuego() {
        return enJuego;
    }

    /**
     * Cambia el estado de la pelota.
     */
    public void setEnJuego(boolean enJuego) {
        this.enJuego = enJuego;
    }

    /**
     * Obtiene la velocidad base de la pelota.
     */
    public int getVelocidadBase() {
        return velocidadBase;
    }
}