
package tenis.juego.comunicacion;


/**
 * Clase encargada de enviar mensajes a través de UDP.
 * 
 * Esta clase se encarga de serializar objetos de tipo {@code Mensaje}
 * y enviarlos por red usando sockets UDP.
 * 
 * Permite configurar un destino por defecto o enviar mensajes a una
 * dirección específica. También puede mostrar información en consola
 * para depuración (logs).
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class EmisorUDP {

    /** Socket UDP utilizado para enviar los mensajes */
    private final DatagramSocket socket;

    /** Dirección IP destino por defecto */
    private InetAddress direccionDestino;

    /** Puerto destino por defecto */
    private int puertoDestino;

    /** Nombre del emisor (para mostrar en logs) */
    private String nombre;

    /** Indica si se deben mostrar logs en consola */
    private boolean mostrarLogs = true;
    
    /**
     * Constructor del emisor.
     * 
     * @param socket socket UDP que se utilizará para enviar mensajes
     */
    public EmisorUDP(DatagramSocket socket) {
        this.socket = socket;
        this.nombre = "Emisor";
    }
    
    /**
     * Constructor del emisor con nombre personalizado.
     * 
     * @param socket socket UDP
     * @param nombre nombre del emisor (para logs)
     */
    public EmisorUDP(DatagramSocket socket, String nombre) {
        this.socket = socket;
        this.nombre = nombre;
    }
    
    /**
     * Configura la dirección y puerto destino por defecto.
     * 
     * @param direccion dirección IP destino
     * @param puerto puerto destino
     */
    public void setDestino(InetAddress direccion, int puerto) {
        this.direccionDestino = direccion;
        this.puertoDestino = puerto;

        if (mostrarLogs) {
            System.out.println("📤 [" + nombre + "] Destino configurado: " 
                    + direccion.getHostAddress() + ":" + puerto);
        }
    }
    
    /**
     * Envía un mensaje usando el destino previamente configurado.
     * 
     * @param mensaje mensaje a enviar
     * @throws Exception si el destino no está configurado o hay error al enviar
     */
    public void enviar(Mensaje mensaje) throws Exception {
        if (direccionDestino == null) {
            throw new IllegalStateException("Destino no configurado");
        }
        enviar(mensaje, direccionDestino, puertoDestino);
    }
    
    /**
     * Envía un mensaje a una dirección y puerto específicos.
     * 
     * Serializa el objeto {@code Mensaje} a bytes y lo envía mediante UDP.
     * 
     * @param mensaje mensaje a enviar
     * @param direccion dirección IP destino
     * @param puerto puerto destino
     * @throws Exception si ocurre un error durante el envío
     */
    public void enviar(Mensaje mensaje, InetAddress direccion, int puerto) throws Exception {

        // Serializa el mensaje a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(mensaje);
        oos.flush();

        byte[] buffer = baos.toByteArray();

        // Crea el paquete UDP
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccion, puerto);

        // Envía el paquete
        socket.send(paquete);
        
        // Muestra información en consola si los logs están activos
        if (mostrarLogs) {
            String tipo = mensaje.getTipo();
            String datosStr = mensaje.getDatos() != null ? mensaje.getDatos().toString() : "null";

            if (datosStr.length() > 50) {
                datosStr = datosStr.substring(0, 50) + "...";
            }
            
            System.out.println("🚀 [" + nombre + "] ENVIADO → " 
                    + direccion.getHostAddress() + ":" + puerto);
            System.out.println("   📨 Tipo: " + tipo);
            System.out.println("   📦 Datos: " + datosStr);
            System.out.println("   ⏱️  Tamaño: " + buffer.length + " bytes");
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
}