
package tenis.juego.comunicacion;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Clase encargada de recibir mensajes a través de UDP.
 * 
 * Esta clase funciona como un hilo (Thread) que está constantemente
 * escuchando mensajes entrantes desde la red.
 * 
 * Cuando recibe un mensaje:
 * - Lo deserializa (convierte de bytes a objeto Mensaje)
 * - Muestra información en consola (si los logs están activos)
 * - Llama al manejador para procesar el mensaje
 * 
 * También permite detener el receptor y llevar un conteo de mensajes recibidos.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */
public class ReceptorUDP extends Thread {

    /** Socket UDP utilizado para recibir mensajes */
    private final DatagramSocket socket;

    /** Objeto que se encarga de procesar los mensajes recibidos */
    private final ManejadorMensajes manejador;

    /** Indica si el receptor sigue activo */
    private boolean activo = true;

    /** Nombre del receptor (para mostrar en logs) */
    private String nombre;

    /** Indica si se deben mostrar logs en consola */
    private boolean mostrarLogs = true;

    /** Contador de mensajes recibidos */
    private int contadorMensajes = 0;
    
    /**
     * Constructor básico del receptor.
     * 
     * @param socket socket UDP
     * @param manejador encargado de procesar los mensajes
     */
    public ReceptorUDP(DatagramSocket socket, ManejadorMensajes manejador) {
        this(socket, manejador, "Receptor");
    }
    
    /**
     * Constructor del receptor con nombre personalizado.
     * 
     * @param socket socket UDP
     * @param manejador manejador de mensajes
     * @param nombre nombre del receptor (para logs)
     */
    public ReceptorUDP(DatagramSocket socket, ManejadorMensajes manejador, String nombre) {
        this.socket = socket;
        this.manejador = manejador;
        this.nombre = nombre;

        try {
            socket.setSoTimeout(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mostrarLogs) {
            System.out.println("👂 [" + nombre + "] Escuchando en puerto: " + socket.getLocalPort());
        }
    }
    
    /**
     * Método principal del hilo.
     * 
     * Se ejecuta continuamente mientras el receptor esté activo.
     * Escucha paquetes UDP, los convierte a objetos Mensaje
     * y los envía al manejador para su procesamiento.
     */
    @Override
    public void run() {
        byte[] buffer = new byte[65536];

        while (activo && !socket.isClosed()) {
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(paquete);
                contadorMensajes++;
                
                // Deserializa el mensaje recibido
                ByteArrayInputStream bais = new ByteArrayInputStream(
                        paquete.getData(), 0, paquete.getLength());
                ObjectInputStream ois = new ObjectInputStream(bais);
                Mensaje mensaje = (Mensaje) ois.readObject();
                
                // Logs del mensaje recibido
                if (mostrarLogs) {
                    System.out.println("\n📥 [" + nombre + "] RECIBIDO #" + contadorMensajes);
                    System.out.println("   📡 Desde: " + paquete.getAddress().getHostAddress() + ":" + paquete.getPort());
                    System.out.println("   📨 Tipo: " + mensaje.getTipo());
                    System.out.println("   📦 Datos: " + 
                        (mensaje.getDatos() != null 
                        ? mensaje.getDatos().toString().substring(0, Math.min(50, mensaje.getDatos().toString().length())) 
                        : "null"));
                    System.out.println("   ⏱️  Tamaño: " + paquete.getLength() + " bytes");
                    System.out.println("   🕐 Timestamp: " + mensaje.getFormattedTime());
                }
                
                // Procesa el mensaje recibido
                manejador.procesarMensaje(
                        mensaje,
                        paquete.getAddress(),
                        paquete.getPort()
                );
                
            } catch (SocketTimeoutException e) {
                // No pasa nada, sigue esperando mensajes
            } catch (Exception e) {
                if (activo && mostrarLogs) {
                    System.err.println("❌ [" + nombre + "] Error recibiendo: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Detiene el receptor.
     * 
     * Finaliza el bucle de escucha, muestra información en consola
     * y cierra el socket.
     */
    public void detener() {
        activo = false;

        if (mostrarLogs) {
            System.out.println("🛑 [" + nombre + "] Deteniendo receptor. Total mensajes: " + contadorMensajes);
        }

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
    
    /**
     * Activa o desactiva los logs en consola.
     * 
     * @param mostrar true para mostrar logs, false para ocultarlos
     */
    public void setMostrarLogs(boolean mostrar) {
        this.mostrarLogs = mostrar;
    }
    
    /**
     * Devuelve la cantidad de mensajes recibidos.
     * 
     * @return número de mensajes recibidos
     */
    public int getContadorMensajes() {
        return contadorMensajes;
    }
}