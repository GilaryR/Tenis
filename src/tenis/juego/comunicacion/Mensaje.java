
package tenis.juego.comunicacion;


/**
 * Clase que representa un mensaje enviado entre los componentes del juego.
 * 
 * Esta clase se usa para transportar información por la red (UDP),
 * por eso implementa Serializable.
 * 
 * Un mensaje contiene:
 * - El tipo de mensaje (tipo)
 * - Los datos que se envían (datos)
 * - El momento en que se creó (timestamp)
 * - El origen del mensaje (quién lo envió)
 * 
 * Se utiliza en la comunicación entre cliente y servidor.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mensaje implements Serializable {

    /** ID de serialización (recomendado para objetos Serializable) */
    private static final long serialVersionUID = 1L;
    
    /** Tipo de mensaje (ej: CONFIG, GOLPE, POSICION, etc.) */
    private final String tipo;

    /** Datos que contiene el mensaje (puede ser cualquier objeto) */
    private final Object datos;

    /** Momento en que se creó el mensaje */
    private final long timestamp;

    /** Identifica quién envió el mensaje */
    private final String origen;
    
    /**
     * Constructor del mensaje.
     * 
     * Genera automáticamente el origen usando el usuario del sistema
     * y el nombre del host.
     * 
     * @param tipo tipo de mensaje
     * @param datos datos asociados al mensaje
     */
    public Mensaje(String tipo, Object datos) {
        this.tipo = tipo;
        this.datos = datos;
        this.timestamp = System.currentTimeMillis();
        this.origen = System.getProperty("user.name") + "@" + getLocalHostName();
    }
    
    /**
     * Constructor del mensaje con origen definido manualmente.
     * 
     * @param tipo tipo de mensaje
     * @param datos datos del mensaje
     * @param origen identificador del emisor
     */
    public Mensaje(String tipo, Object datos, String origen) {
        this.tipo = tipo;
        this.datos = datos;
        this.timestamp = System.currentTimeMillis();
        this.origen = origen;
    }
    
    /**
     * Obtiene el nombre del host local.
     * 
     * @return nombre del equipo o "unknown" si ocurre un error
     */
    private String getLocalHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    /**
     * Devuelve el tipo del mensaje.
     */
    public String getTipo() { return tipo; }

    /**
     * Devuelve los datos del mensaje.
     */
    public Object getDatos() { return datos; }

    /**
     * Devuelve el timestamp del mensaje.
     */
    public long getTimestamp() { return timestamp; }

    /**
     * Devuelve el origen del mensaje.
     */
    public String getOrigen() { return origen; }
    
    /**
     * Devuelve la hora formateada del mensaje.
     * 
     * @return hora en formato HH:mm:ss.SSS
     */
    public String getFormattedTime() {
        return new SimpleDateFormat("HH:mm:ss.SSS")
                .format(new Date(timestamp));
    }
    
    /**
     * Representación en texto del mensaje.
     * 
     * Se usa principalmente para logs o depuración.
     */
    @Override
    public String toString() {
        return "[" + getFormattedTime() + "] " 
                + origen + " → " + tipo + " | " + datos;
    }
}