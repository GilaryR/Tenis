
package tenis.juego.comunicacion;

import java.net.InetAddress;

/**
 * Interfaz que define cómo se deben procesar los mensajes recibidos.
 * 
 * Cualquier clase que implemente esta interfaz debe definir qué hacer
 * cuando llega un mensaje desde la red.
 * 
 * Se usa principalmente junto con el receptor UDP, que recibe mensajes
 * y los envía a esta interfaz para que sean procesados.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */


public interface ManejadorMensajes {

    /**
     * Método que se ejecuta cuando se recibe un mensaje.
     * 
     * @param mensaje mensaje recibido
     * @param origen dirección IP desde donde se envió el mensaje
     * @param puerto puerto desde donde se envió el mensaje
     */
    void procesarMensaje(Mensaje mensaje, InetAddress origen, int puerto);
}