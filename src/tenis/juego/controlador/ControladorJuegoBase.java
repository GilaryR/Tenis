
package tenis.juego.controlador;

/**
 * Clase base para los controladores del juego.
 * 
 * Esta clase se encarga de manejar la comunicación por red usando UDP,
 * tanto para enviar como recibir mensajes entre los diferentes componentes
 * del juego (clientes, servidor, etc.).
 * 
 * Define funcionalidades comunes como:
 * - Crear el socket de comunicación
 * - Enviar mensajes
 * - Recibir mensajes
 * - Manejar la conexión
 * 
 * Las clases que hereden de esta deben implementar cómo se envían
 * las actualizaciones del juego.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */

import tenis.juego.modelo.Juego;
import tenis.juego.comunicacion.Mensaje;
import tenis.juego.comunicacion.EmisorUDP;
import tenis.juego.comunicacion.ReceptorUDP;
import tenis.juego.comunicacion.ManejadorMensajes;

import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class ControladorJuegoBase implements ManejadorMensajes {

    /** Referencia al modelo principal del juego */
    protected final Juego juego;

    /** Objeto encargado de enviar mensajes por UDP */
    protected EmisorUDP emisor;

    /** Objeto encargado de recibir mensajes por UDP */
    protected ReceptorUDP receptor;

    /** Socket UDP usado para la comunicación */
    protected final DatagramSocket socket;

    /** Indica si el controlador está conectado o activo */
    protected boolean conectado;
    
    /**
     * Constructor de la clase base.
     * 
     * Inicializa el socket, el emisor y el receptor de mensajes.
     * Además, inicia el hilo receptor para escuchar mensajes entrantes.
     * 
     * @param juego instancia del juego
     * @param puertoLocal puerto en el que se va a escuchar
     * @throws Exception si ocurre algún error al crear el socket
     */
    public ControladorJuegoBase(Juego juego, int puertoLocal) throws Exception {
        this.juego = juego;
        this.socket = new DatagramSocket(puertoLocal);
        this.emisor = new EmisorUDP(socket);
        this.receptor = new ReceptorUDP(socket, this);
        this.emisor.setMostrarLogs(false);
        this.receptor.setMostrarLogs(false);
        this.receptor.start();
        this.conectado = true;
    }
    
    /**
     * Configura la dirección y el puerto de destino para enviar mensajes.
     * 
     * @param direccion dirección IP destino
     * @param puerto puerto destino
     */
    public void configurarDestino(InetAddress direccion, int puerto) {
        emisor.setDestino(direccion, puerto);
    }
    
    /**
     * Envía un mensaje usando el destino previamente configurado.
     * 
     * @param mensaje mensaje a enviar
     */
    public void enviarMensaje(Mensaje mensaje) {
        if (emisor != null && conectado) {
            try {
                emisor.enviar(mensaje);
            } catch (Exception e) {
                System.err.println("Error enviando mensaje: " + e.getMessage());
            }
        }
    }
    
    /**
     * Envía un mensaje a una dirección y puerto específicos.
     * 
     * @param mensaje mensaje a enviar
     * @param direccion dirección IP destino
     * @param puerto puerto destino
     */
    public void enviarMensaje(Mensaje mensaje, InetAddress direccion, int puerto) {
        if (conectado) {
            try {
                emisor.enviar(mensaje, direccion, puerto);
            } catch (Exception e) {
                System.err.println("Error enviando mensaje: " + e.getMessage());
            }
        }
    }
    
    /**
     * Detiene la comunicación del controlador.
     * 
     * Cierra el receptor de mensajes y el socket para liberar recursos.
     */
    public void detener() {
        conectado = false;
        if (receptor != null) receptor.detener();
        if (socket != null && !socket.isClosed()) socket.close();
    }
    
    /**
     * Método abstracto que debe implementar cada controlador.
     * 
     * Sirve para enviar actualizaciones del estado del juego.
     */
    public abstract void enviarActualizacion();
}