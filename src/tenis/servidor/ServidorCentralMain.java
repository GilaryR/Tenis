
package tenis.servidor;

import tenis.juego.controlador.ControladorCentral;
import tenis.juego.modelo.Juego;
import tenis.juego.util.ConfiguracionRed;
import tenis.juego.vista.pc2.PantallaCentral;
import javax.swing.SwingUtilities;

/**
 * Clase principal para iniciar el servidor central del juego.
 * 
 * Esta clase se encarga de:
 * - Crear la instancia del juego
 * - Inicializar el controlador central
 * - Mostrar la interfaz gráfica del servidor
 * 
 * Usa SwingUtilities para asegurar que la interfaz gráfica
 * se ejecute correctamente en el hilo de eventos de Swing.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */
public final class ServidorCentralMain {

    /**
     * Constructor privado para evitar que la clase sea instanciada.
     */
    private ServidorCentralMain() {
    }

    /**
     * Método principal del programa.
     * 
     * Inicia el servidor central del juego creando el modelo,
     * el controlador y la interfaz gráfica.
     * 
     * @param args argumentos de la línea de comandos (no utilizados)
     */
    public static void main(String[] args) {

        // Ejecuta la interfaz gráfica en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Crea el modelo del juego
                Juego juego = new Juego("Equipo 1", "Equipo 2");

                // Inicializa el controlador central con el puerto configurado
                ControladorCentral controlador = new ControladorCentral(
                        juego,
                        ConfiguracionRed.PUERTO_CENTRAL
                );

                // Crea la interfaz gráfica del servidor
                PantallaCentral pantalla = new PantallaCentral(juego, controlador);

                // Muestra la ventana
                pantalla.setVisible(true);

            } catch (Exception e) {
                // Lanza error si no se puede iniciar el servidor
                throw new RuntimeException("No fue posible iniciar el servidor central.", e);
            }
        });
    }
}