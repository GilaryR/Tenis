package tenis.juego.modelo;

import java.io.Serializable;

/**
 * Representa la pelota utilizada en el juego de tenis.
 * 
 * Esta clase se encarga de manejar:
 * - La posición de la pelota dentro de la cancha
 * - La velocidad en los ejes X y Y
 * - El movimiento de la pelota
 * - El inicio y reinicio de la pelota
 * - El control de si la pelota está en juego
 * 
 * También permite copiar su estado desde otra pelota, lo cual
 * es útil para sincronizar el estado del juego en aplicaciones
 * cliente-servidor.
 * 
 * Implementa Serializable para permitir guardar o transmitir
 * el estado de la pelota.
 */
public class Pelota implements Serializable {

    /** Identificador de versión para serialización */
    private static final long serialVersionUID = 1L;

    /** Posición horizontal de la pelota */
    private int x;

    /** Posición vertical de la pelota */
    private int y;

    /** Velocidad de la pelota en el eje X */
    private int velocidadX;

    /** Velocidad de la pelota en el eje Y */
    private int velocidadY;

    /** Indica si la pelota está actualmente en movimiento */
    private boolean enJuego;

    /** Velocidad base utilizada al iniciar la pelota */
    private final int velocidadBase;

    /**
     * Constructor de la pelota.
     * 
     * Inicializa la velocidad base y posiciona la pelota
     * en el centro de la cancha.
     */
    public Pelota() {
        velocidadBase = 6;
        reiniciar();
    }

    /**
     * Copia el estado de otra pelota.
     * 
     * Este método se usa para sincronizar el estado de la pelota
     * entre diferentes instancias del juego.
     * 
     * @param origen pelota desde la cual se copiarán los valores
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
     * 
     * La pelota puede moverse hacia la izquierda o derecha
     * con una variación aleatoria en la velocidad vertical.
     */
    public void iniciar() {
        int direccion = Math.random() > 0.5 ? velocidadBase : -velocidadBase;
        iniciar(direccion, (int) (Math.random() * 7) - 3);
    }

    /**
     * Inicia la pelota con velocidades específicas.
     * 
     * @param nuevaVelocidadX velocidad horizontal inicial
     * @param nuevaVelocidadY velocidad vertical inicial
     */
    public void iniciar(int nuevaVelocidadX, int nuevaVelocidadY) {
        x = nuevaVelocidadX > 0 ? 170 : 630;
        y = 300;
        velocidadX = nuevaVelocidadX;
        velocidadY = nuevaVelocidadY;
        enJuego = true;
    }

    /**
     * Detiene el movimiento de la pelota.
     */
    public void detener() {
        enJuego = false;
    }

    /**
     * Reinicia la pelota a su posición inicial en el centro de la cancha.
     * 
     * También detiene el movimiento de la pelota.
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
     * Se utiliza cuando la pelota rebota en los límites
     * superior o inferior de la cancha.
     */
    public void invertirDireccionY() {
        velocidadY = -velocidadY;
    }

    /**
     * Aumenta gradualmente la velocidad horizontal de la pelota.
     * 
     * Esto se usa para incrementar la dificultad del juego
     * conforme avanza el partido.
     */
    public void aumentarVelocidad() {
        if (velocidadX > 0) {
            velocidadX += 1;
        } else if (velocidadX < 0) {
            velocidadX -= 1;
        }
    }

    /**
     * Obtiene la posición horizontal de la pelota.
     * 
     * @return posición X
     */
    public int getX() {
        return x;
    }

    /**
     * Establece la posición horizontal de la pelota.
     * 
     * @param x nueva posición horizontal
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Obtiene la posición vertical de la pelota.
     * 
     * @return posición Y
     */
    public int getY() {
        return y;
    }

    /**
     * Establece la posición vertical de la pelota.
     * 
     * @param y nueva posición vertical
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Obtiene la velocidad horizontal de la pelota.
     * 
     * @return velocidad en el eje X
     */
    public int getVelocidadX() {
        return velocidadX;
    }

    /**
     * Establece la velocidad horizontal de la pelota.
     * 
     * @param velocidadX nueva velocidad en X
     */
    public void setVelocidadX(int velocidadX) {
        this.velocidadX = velocidadX;
    }

    /**
     * Obtiene la velocidad vertical de la pelota.
     * 
     * @return velocidad en el eje Y
     */
    public int getVelocidadY() {
        return velocidadY;
    }

    /**
     * Establece la velocidad vertical de la pelota.
     * 
     * @param velocidadY nueva velocidad en Y
     */
    public void setVelocidadY(int velocidadY) {
        this.velocidadY = velocidadY;
    }

    /**
     * Indica si la pelota está en juego.
     * 
     * @return true si la pelota se está moviendo
     */
    public boolean isEnJuego() {
        return enJuego;
    }

    /**
     * Cambia el estado de la pelota (en juego o detenida).
     * 
     * @param enJuego nuevo estado de la pelota
     */
    public void setEnJuego(boolean enJuego) {
        this.enJuego = enJuego;
    }

    /**
     * Obtiene la velocidad base de la pelota.
     * 
     * @return velocidad base
     */
    public int getVelocidadBase() {
        return velocidadBase;
    }
}