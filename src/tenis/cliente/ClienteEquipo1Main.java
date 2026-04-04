package tenis.cliente;

import tenis.juego.controlador.ControladorEquipo;
import tenis.juego.modelo.Juego;
import tenis.juego.util.ConfiguracionRed;
import tenis.juego.vista.pc1.PantallaEquipo1;

import javax.swing.SwingUtilities;

/**
 * Clase principal para iniciar el cliente correspondiente al Equipo 1.
 * 
 * Esta clase se encarga de:
 * - Crear la instancia del juego.
 * - Configurar la conexión con el servidor central.
 * - Inicializar el controlador del equipo.
 * - Lanzar la interfaz gráfica del Equipo 1.
 * 
 * Se ejecuta en el hilo de eventos de Swing para asegurar
 * el correcto manejo de la interfaz gráfica.
 */
public final class ClienteEquipo1Main {

    /**
     * Constructor privado para evitar la creación de instancias,
     * ya que esta clase solo contiene el método main.
     */
    private ClienteEquipo1Main() {
    }

    /**
     * Método principal de ejecución del cliente.
     * 
     * @param args argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        // Ejecuta la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {

                /**
                 * Se crea una nueva instancia del juego con los nombres
                 * iniciales de los equipos.
                 */
                Juego juego = new Juego("Equipo 1", "Equipo 2");

                /**
                 * Se inicializa el controlador del equipo 1.
                 * 
                 * Parámetros:
                 * - juego: instancia compartida del modelo del juego.
                 * - PUERTO_EQUIPO1: puerto local del cliente.
                 * - getDireccionCentral(): dirección IP del servidor central.
                 * - PUERTO_CENTRAL: puerto del servidor central.
                 * - "EQUIPO1": identificador del cliente.
                 */
                ControladorEquipo controlador = new ControladorEquipo(
                        juego,
                        ConfiguracionRed.PUERTO_EQUIPO1,
                        ConfiguracionRed.getDireccionCentral(),
                        ConfiguracionRed.PUERTO_CENTRAL,
                        "EQUIPO1");

                /**
                 * Se crea la interfaz gráfica del Equipo 1.
                 */
                PantallaEquipo1 pantalla = new PantallaEquipo1(juego, controlador, 1);

                /**
                 * Se hace visible la ventana en pantalla.
                 */
                pantalla.setVisible(true);

            } catch (Exception e) {

                /**
                 * En caso de error, se lanza una excepción en tiempo de ejecución
                 * indicando que no fue posible iniciar el cliente.
                 */
                throw new RuntimeException("No fue posible iniciar el cliente del Equipo 1.", e);
            }
        });
    }
}