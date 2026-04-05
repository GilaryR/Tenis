package tenis.juego.util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase utilitaria encargada de gestionar la reproducción de sonidos del juego.
 *  * @author Aleja
 * Funciones principales:
 * - Cargar sonidos desde recursos o sistema de archivos
 * - Convertir formatos de audio para compatibilidad
 * - Reproducir efectos de sonido específicos
 * - Permitir habilitar o deshabilitar el sonido globalmente
 */
public final class ReproductorSonido {

    /**
     * Mapa que almacena los sonidos cargados (nombre lógico -> Clip de audio)
     */
    private static final Map<String, Clip> sonidos = new HashMap<>();

    /**
     * Indica si el sonido está habilitado o no
     */
    private static boolean habilitado = true;

    /**
     * Bloque estático que carga todos los sonidos al iniciar la clase
     */
    static {
        cargar("victoria", "victoria.wav");
        cargar("malla", "choque_malla.wav");
        cargar("cuenta_regresiva", "cuenta_regresiva.wav");
        cargar("game_over", "game_over.wav");
        cargar("punto", "punto_ganado.wav");
    }

    /**
     * Constructor privado para evitar instanciación (clase utilitaria)
     */
    private ReproductorSonido() {
    }

    /**
     * Carga un sonido y lo guarda en el mapa
     *
     * @param nombre nombre lógico del sonido
     * @param nombreArchivo archivo de audio
     */
    private static void cargar(String nombre, String nombreArchivo) {
        try (AudioInputStream audio = abrirAudioCompatible(nombreArchivo)) {

            // Si no se encuentra el audio, se informa en consola
            if (audio == null) {
                System.err.println("No se encontro el sonido: " + nombreArchivo);
                return;
            }

            // Crear información de línea de audio
            DataLine.Info info = new DataLine.Info(Clip.class, audio.getFormat());

            // Obtener el clip de audio
            Clip clip = (Clip) AudioSystem.getLine(info);

            // Cargar el audio en el clip
            clip.open(audio);

            // Guardar en el mapa
            sonidos.put(nombre, clip);

        } catch (Exception e) {
            System.err.println("No se pudo cargar sonido " + nombreArchivo + ": " + e.getMessage());
        }
    }

    /**
     * Abre un archivo de audio y lo convierte si es necesario
     * para que sea compatible con el sistema
     *
     * @param nombreArchivo nombre del archivo
     * @return AudioInputStream compatible o null si falla
     */
    private static AudioInputStream abrirAudioCompatible(String nombreArchivo) {
        try {
            // Abrir audio original
            AudioInputStream original = abrirAudioOriginal(nombreArchivo);

            if (original == null) {
                return null;
            }

            // Obtener formato original
            AudioFormat formatoOriginal = original.getFormat();

            // Verificar si el formato es soportado directamente
            DataLine.Info infoOriginal = new DataLine.Info(Clip.class, formatoOriginal);
            if (AudioSystem.isLineSupported(infoOriginal)) {
                return original;
            }

            // Intentar convertir a formato PCM estándar
            AudioFormat formatoConvertido = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    formatoOriginal.getSampleRate(),
                    16,
                    formatoOriginal.getChannels(),
                    formatoOriginal.getChannels() * 2,
                    formatoOriginal.getSampleRate(),
                    false
            );

            if (AudioSystem.isConversionSupported(formatoConvertido, formatoOriginal)) {
                return AudioSystem.getAudioInputStream(formatoConvertido, original);
            }

            // Intentar un formato de respaldo (44.1 kHz)
            AudioFormat formatoRespaldo = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100f,
                    16,
                    Math.max(1, formatoOriginal.getChannels()),
                    Math.max(1, formatoOriginal.getChannels()) * 2,
                    44100f,
                    false
            );

            if (AudioSystem.isConversionSupported(formatoRespaldo, formatoOriginal)) {
                return AudioSystem.getAudioInputStream(formatoRespaldo, original);
            }

            // Si no se puede convertir, se retorna el original
            return original;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Intenta abrir un archivo de audio desde distintas ubicaciones:
     * 1. Recursos del classpath
     * 2. Carpeta src (modo desarrollo)
     * 3. Carpeta build/classes (modo compilado)
     *
     * @param nombreArchivo nombre del archivo
     * @return AudioInputStream o null si no se encuentra
     */
    private static AudioInputStream abrirAudioOriginal(String nombreArchivo) {
        try {
            // Buscar en recursos del classpath
            URL recurso = ReproductorSonido.class.getResource("/recursos/sonidos/" + nombreArchivo);
            if (recurso != null) {
                return AudioSystem.getAudioInputStream(recurso);
            }

            // Buscar en carpeta src
            File archivoSrc = new File("src/recursos/sonidos/" + nombreArchivo);
            if (archivoSrc.exists()) {
                return AudioSystem.getAudioInputStream(archivoSrc);
            }

            // Buscar en carpeta build
            File archivoBuild = new File("build/classes/recursos/sonidos/" + nombreArchivo);
            if (archivoBuild.exists()) {
                return AudioSystem.getAudioInputStream(archivoBuild);
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * Reproduce un sonido por su nombre lógico
     *
     * @param nombre clave del sonido
     */
    private static void reproducir(String nombre) {

        // Si el sonido está deshabilitado, no hacer nada
        if (!habilitado) {
            return;
        }

        Clip clip = sonidos.get(nombre);

        if (clip != null) {

            // Si ya está sonando, detenerlo
            if (clip.isRunning()) {
                clip.stop();
            }

            // Reiniciar desde el inicio
            clip.setFramePosition(0);

            // Reproducir
            clip.start();
        }
    }

    /**
     * Reproduce el sonido de punto ganado
     */
    public static void reproducirPunto() {
        reproducir("punto");
    }

    /**
     * Reproduce el sonido de choque con la malla
     */
    public static void reproducirMalla() {
        reproducir("malla");
    }

    /**
     * Reproduce el sonido de cuenta regresiva
     */
    public static void reproducirCuentaRegresiva() {
        reproducir("cuenta_regresiva");
    }

    /**
     * Reproduce el sonido de victoria
     */
    public static void reproducirVictoria() {
        reproducir("victoria");
    }

    /**
     * Reproduce el sonido de fin de juego
     */
    public static void reproducirGameOver() {
        reproducir("game_over");
    }

    /**
     * Permite habilitar o deshabilitar todos los sonidos del juego
     *
     * @param habilitado true para activar sonido, false para desactivarlo
     */
    public static void habilitar(boolean habilitado) {
        ReproductorSonido.habilitado = habilitado;
    }
}