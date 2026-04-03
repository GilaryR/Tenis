
package tenis.juego.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Clase de configuración de red del sistema.
 * 
 * Esta clase contiene las direcciones IP y puertos utilizados
 * para la comunicación entre el servidor central y los equipos.
 * 
 * Permite obtener las direcciones de forma segura usando InetAddress
 * y también permite cambiar los valores mediante propiedades del sistema.
 * 
 * Es una clase utilitaria, por lo que no se puede instanciar.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */
public final class ConfiguracionRed {

    /** Dirección IP del servidor central */
    public static final String IP_CENTRAL = System.getProperty("tenis.ip.central", "192.168.195.210");

    /** Dirección IP del equipo 1 */
    public static final String IP_EQUIPO1 = System.getProperty("tenis.ip.equipo1", "192.168.195.213");

    /** Dirección IP del equipo 2 */
    public static final String IP_EQUIPO2 = System.getProperty("tenis.ip.equipo2", "192.168.195.55");

    /** Puerto del servidor central */
    public static final int PUERTO_CENTRAL = Integer.getInteger("tenis.puerto.central", 5000);

    /** Puerto del equipo 1 */
    public static final int PUERTO_EQUIPO1 = Integer.getInteger("tenis.puerto.equipo1", 5001);

    /** Puerto del equipo 2 */
    public static final int PUERTO_EQUIPO2 = Integer.getInteger("tenis.puerto.equipo2", 5002);

    /**
     * Constructor privado para evitar instancias.
     */
    private ConfiguracionRed() {
    }

    /**
     * Devuelve la dirección del servidor central.
     * 
     * @return dirección IP del servidor central
     * @throws UnknownHostException si la IP no es válida
     */
    public static InetAddress getDireccionCentral() throws UnknownHostException {
        return InetAddress.getByName(IP_CENTRAL);
    }

    /**
     * Devuelve la dirección del equipo 1.
     * 
     * @return dirección IP del equipo 1
     * @throws UnknownHostException si la IP no es válida
     */
    public static InetAddress getDireccionEquipo1() throws UnknownHostException {
        return InetAddress.getByName(IP_EQUIPO1);
    }

    /**
     * Devuelve la dirección del equipo 2.
     * 
     * @return dirección IP del equipo 2
     * @throws UnknownHostException si la IP no es válida
     */
    public static InetAddress getDireccionEquipo2() throws UnknownHostException {
        return InetAddress.getByName(IP_EQUIPO2);
    }
}