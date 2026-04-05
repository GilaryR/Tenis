package tenis.cliente;

import tenis.juego.controlador.ControladorEquipo;
import tenis.juego.modelo.Juego;
import tenis.juego.util.ConfiguracionRed;
import tenis.juego.vista.pc3.PantallaEquipo2;

import javax.swing.SwingUtilities;

/**
 * Clase principal para iniciar el cliente correspondiente al Equipo 2.
 * 
 * Esta clase se encarga de:
 * - Crear la instancia del juego.
 * - Configurar la conexión con el servidor central.
 * - Inicializar el controlador del equipo 2.
 * - Lanzar la interfaz gráfica del Equipo 2.
 * 
 * Se ejecuta en el hilo de eventos de Swing para garantizar
 * el correcto funcionamiento de la interfaz gráfica.
 */
public final class ClienteEquipo2Main {

    /**
     * Constructor privado para evitar la creación de instancias,
     * ya que esta clase solo contiene el método main.
     */
    private ClienteEquipo2Main() {
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
                 * Se crea una nueva instancia del juego con nombres
                 * iniciales para ambos equipos.
                 */
                Juego juego = new Juego("Equipo 1", "Equipo 2");

                /**
                 * Se inicializa el controlador del equipo 2.
                 * 
                 * Parámetros:
                 * - juego: modelo compartido del juego.
                 * - PUERTO_EQUIPO2: puerto local del cliente.
                 * - getDireccionCentral(): dirección del servidor central.
                 * - PUERTO_CENTRAL: puerto del servidor.
                 * - "EQUIPO2": identificador del cliente.
                 */
                ControladorEquipo controlador = new ControladorEquipo(
                        juego,
                        ConfiguracionRed.PUERTO_EQUIPO2,
                        ConfiguracionRed.getDireccionCentral(),
                        ConfiguracionRed.PUERTO_CENTRAL,
                        "EQUIPO2");

                /**
                 * Se crea la interfaz gráfica del Equipo 2.
                 */
                PantallaEquipo2 pantalla = new PantallaEquipo2(juego, controlador, 2);

                /**
                 * Se hace visible la ventana en pantalla.
                 */
                pantalla.setVisible(true);

            } catch (Exception e) {

                /**
                 * En caso de error, se lanza una excepción en tiempo de ejecución
                 * indicando que no fue posible iniciar el cliente.
                 */
                throw new RuntimeException("No fue posible iniciar el cliente del Equipo 2.", e);
            }
        });
    }
}