package tenis.juego.util;

import tenis.juego.modelo.Jugador;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase utilitaria encargada de gestionar la carga, almacenamiento
 * y manipulación de imágenes del juego.
 *  * @author Aleja
 * Funciones principales:
 * - Cargar imágenes desde recursos o sistema de archivos
 * - Escalar imágenes
 * - Crear iconos para la interfaz
 * - Generar fichas visuales de jugadores
 * - Mantener un cache para optimizar rendimiento
 */
public final class GestorImagenes {

    // Ruta base donde se encuentran las imágenes dentro del proyecto
    private static final String BASE_RECURSOS = "/recursos/imagenes/";

    /**
     * Mapa que relaciona la imagen del jugador con su imagen
     * correspondiente en el menú de selección.
     */
    private static final Map<String, String> OPCIONES_MENU = new LinkedHashMap<>();

    /**
     * Cache de imágenes ya cargadas para evitar recargas innecesarias
     */
    private static final Map<String, BufferedImage> cache = new LinkedHashMap<>();

    /**
     * Bloque estático para inicializar las opciones del menú
     */
    static {
        OPCIONES_MENU.put("jug1.png", "Opcion1.png");
        OPCIONES_MENU.put("jug2.png", "Opcion2.png");
        OPCIONES_MENU.put("jug3.png", "Opcion3.png");
        OPCIONES_MENU.put("jug4.png", "Opcion4.png");
        OPCIONES_MENU.put("jug5.png", "Opcion5 .png"); // ⚠️ Ojo: tiene un espacio en el nombre
    }

    /**
     * Constructor privado para evitar instanciación (clase utilitaria)
     */
    private GestorImagenes() {
    }

    /**
     * Obtiene todas las claves de jugadores disponibles
     *
     * @return arreglo con nombres de archivos de jugadores
     */
    public static String[] obtenerClavesJugadores() {
        return OPCIONES_MENU.keySet().toArray(new String[0]);
    }

    /**
     * Obtiene el icono de una opción del menú de selección de jugadores
     *
     * @param claveJugador nombre del archivo del jugador
     * @param ancho ancho deseado
     * @param alto alto deseado
     * @return ImageIcon escalado
     */
    public static ImageIcon obtenerIconoOpcion(String claveJugador, int ancho, int alto) {
        BufferedImage imagen = cargar(BASE_RECURSOS + "MenuOpcionesJugadores/" + OPCIONES_MENU.get(claveJugador));
        return new ImageIcon(escalar(imagen, ancho, alto));
    }

    /**
     * Carga la imagen original del jugador
     *
     * @param claveJugador nombre del archivo del jugador
     * @return imagen del jugador
     */
    public static BufferedImage obtenerImagenJugador(String claveJugador) {
        return cargar(BASE_RECURSOS + "Jugadores/" + claveJugador);
    }

    /**
     * Obtiene un icono para elementos generales de la interfaz
     *
     * @param nombreArchivo ruta relativa dentro de recursos
     * @param ancho ancho deseado
     * @param alto alto deseado
     * @return ImageIcon escalado
     */
    public static ImageIcon obtenerIconoInterfaz(String nombreArchivo, int ancho, int alto) {
        BufferedImage imagen = cargar(BASE_RECURSOS + nombreArchivo);
        return new ImageIcon(escalar(imagen, ancho, alto));
    }

    /**
     * Genera una ficha visual del jugador con:
     * - fondo semi-transparente
     * - color del equipo
     * - avatar
     * - nombre
     *
     * @param jugador objeto jugador
     * @param colorEquipo color representativo del equipo
     * @return imagen compuesta de la ficha
     */
    public static BufferedImage obtenerFichaJugador(Jugador jugador, Color colorEquipo) {

        // Obtener avatar del jugador
        BufferedImage avatar = obtenerImagenJugador(jugador.getAvatar());

        // Crear imagen base de la ficha
        BufferedImage ficha = new BufferedImage(84, 96, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = ficha.createGraphics();

        // Activar suavizado (mejor calidad visual)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo semitransparente
        g2.setColor(new Color(255, 255, 255, 35));
        g2.fillRoundRect(4, 4, 76, 88, 24, 24);

        // Fondo del equipo
        g2.setColor(colorEquipo);
        g2.fillRoundRect(8, 8, 68, 68, 18, 18);

        // Dibujar avatar escalado
        g2.drawImage(escalar(avatar, 60, 60), 12, 12, null);

        // Dibujar nombre del jugador
        g2.setColor(Color.WHITE);
        g2.drawString(jugador.getNombre(), 8, 88);

        // Liberar recursos gráficos
        g2.dispose();

        return ficha;
    }

    /**
     * Carga una imagen utilizando cache
     *
     * @param rutaClasspath ruta dentro del proyecto
     * @return imagen cargada o cacheada
     */
    private static BufferedImage cargar(String rutaClasspath) {
        return cache.computeIfAbsent(rutaClasspath, GestorImagenes::cargarInterno);
    }

    /**
     * Método interno para cargar imagen desde:
     * 1. Recursos del classpath
     * 2. Sistema de archivos (src/)
     *
     * @param rutaClasspath ruta de la imagen
     * @return imagen cargada o placeholder si falla
     */
    private static BufferedImage cargarInterno(String rutaClasspath) {
        try (InputStream stream = GestorImagenes.class.getResourceAsStream(rutaClasspath)) {

            // Intentar cargar desde recursos (JAR / classpath)
            if (stream != null) {
                return ImageIO.read(stream);
            }

            // Intentar cargar desde carpeta src (modo desarrollo)
            String rutaArchivo = "src" + rutaClasspath.replace('/', File.separatorChar);
            File archivo = new File(rutaArchivo);

            if (archivo.exists()) {
                return ImageIO.read(archivo);
            }

        } catch (Exception e) {
            System.err.println("No se pudo cargar imagen: " + rutaClasspath + " -> " + e.getMessage());
        }

        // Crear imagen placeholder si falla la carga
        BufferedImage placeholder = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = placeholder.createGraphics();

        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(0, 0, 80, 80);

        g2.setColor(Color.WHITE);
        g2.drawRect(1, 1, 78, 78);
        g2.drawString("IMG", 26, 42);

        g2.dispose();

        return placeholder;
    }

    /**
     * Escala una imagen al tamaño deseado usando suavizado
     *
     * @param imagen imagen original
     * @param ancho ancho nuevo
     * @param alto alto nuevo
     * @return imagen escalada
     */
    private static Image escalar(BufferedImage imagen, int ancho, int alto) {
        return imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
    }
}