package tenis.lanzador;

import tenis.juego.controlador.ControladorCentral;
import tenis.juego.controlador.ControladorEquipo;
import tenis.juego.modelo.Juego;
import tenis.juego.util.ConfiguracionRed;
import tenis.juego.vista.pc1.PantallaEquipo1;
import tenis.juego.vista.pc2.PantallaCentral;
import tenis.juego.vista.pc3.PantallaEquipo2;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Clase encargada de lanzar el sistema completo del juego en modo demo local.
 *  * @author Aleja
 * Funciones principales:
 * - Crear instancias del juego para cada nodo (central y equipos)
 * - Inicializar las interfaces gráficas
 * - Configurar controladores de red
 * - Lanzar las ventanas con una pequeña demora entre ellas
 */

public final class ServidorLanzador {

    /**
     * Tiempo de espera entre la apertura de cada ventana (en milisegundos)
     */
    private static final int DEMORA_ENTRE_VENTANAS_MS = 300;

    /**
     * Método principal para iniciar la demo local del sistema
     */
    public void iniciar() {
        try {
            // Crear instancias independientes del juego para cada nodo
            Juego juegoCentral = new Juego("Equipo 1", "Equipo 2");
            Juego juegoEquipo1 = new Juego("Equipo 1", "Equipo 2");
            Juego juegoEquipo2 = new Juego("Equipo 1", "Equipo 2");

            // Ejecutar la inicialización en el hilo de Swing (EDT)
            SwingUtilities.invokeLater(() -> iniciarSecuencia(juegoCentral, juegoEquipo1, juegoEquipo2));

        } catch (Exception e) {
            throw new RuntimeException("No fue posible iniciar la demo local.", e);
        }
    }

    /**
     * Inicia la secuencia de apertura de ventanas:
     * 1. Pantalla central
     * 2. Pantalla equipo 1
     * 3. Pantalla equipo 2
     *
     * Se utiliza un temporizador para espaciar la apertura
     */
    private void iniciarSecuencia(Juego juegoCentral, Juego juegoEquipo1, Juego juegoEquipo2) {

        // Iniciar pantalla central primero
        iniciarCentral(juegoCentral);

        // Programar siguiente paso (equipo 1)
        programarSiguientePaso(e -> {

            iniciarEquipo1(juegoEquipo1);

            // Programar siguiente paso (equipo 2)
            programarSiguientePaso(evento -> iniciarEquipo2(juegoEquipo2));
        });
    }

    /**
     * Inicializa la pantalla central del sistema
     *
     * @param juegoCentral instancia del juego para el nodo central
     */
    private void iniciarCentral(Juego juegoCentral) {
        try {
            // Crear controlador central con puerto configurado
            ControladorCentral controladorCentral =
                    new ControladorCentral(juegoCentral, ConfiguracionRed.PUERTO_CENTRAL);

            // Crear interfaz gráfica central
            PantallaCentral pantallaCentral =
                    new PantallaCentral(juegoCentral, controladorCentral);

            // Mostrar ventana
            pantallaCentral.setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException("No fue posible abrir la pantalla central en modo demo.", e);
        }
    }

    /**
     * Inicializa la pantalla del Equipo 1
     *
     * @param juegoEquipo1 instancia del juego para el equipo 1
     */
    private void iniciarEquipo1(Juego juegoEquipo1) {
        try {
            // Crear controlador del equipo 1 con configuración de red
            ControladorEquipo controladorEquipo1 = new ControladorEquipo(
                    juegoEquipo1,
                    ConfiguracionRed.PUERTO_EQUIPO1,
                    ConfiguracionRed.getDireccionCentral(),
                    ConfiguracionRed.PUERTO_CENTRAL,
                    "EQUIPO1"
            );

            // Crear interfaz gráfica del equipo 1
            PantallaEquipo1 pantallaEquipo1 =
                    new PantallaEquipo1(juegoEquipo1, controladorEquipo1, 1);

            // Mostrar ventana
            pantallaEquipo1.setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException("No fue posible abrir el cliente del Equipo 1 en modo demo.", e);
        }
    }

    /**
     * Inicializa la pantalla del Equipo 2
     *
     * @param juegoEquipo2 instancia del juego para el equipo 2
     */
    private void iniciarEquipo2(Juego juegoEquipo2) {
        try {
            // Crear controlador del equipo 2 con configuración de red
            ControladorEquipo controladorEquipo2 = new ControladorEquipo(
                    juegoEquipo2,
                    ConfiguracionRed.PUERTO_EQUIPO2,
                    ConfiguracionRed.getDireccionCentral(),
                    ConfiguracionRed.PUERTO_CENTRAL,
                    "EQUIPO2"
            );

            // Crear interfaz gráfica del equipo 2
            PantallaEquipo2 pantallaEquipo2 =
                    new PantallaEquipo2(juegoEquipo2, controladorEquipo2, 2);

            // Mostrar ventana
            pantallaEquipo2.setVisible(true);

        } catch (Exception e) {
            throw new RuntimeException("No fue posible abrir el cliente del Equipo 2 en modo demo.", e);
        }
    }

    /**
     * Programa la ejecución de una acción con un pequeño retraso
     * utilizando un Timer de Swing
     *
     * @param accion acción a ejecutar después del tiempo de espera
     */
    private void programarSiguientePaso(ActionListener accion) {

        // Crear temporizador con la demora definida
        Timer temporizador = new Timer(DEMORA_ENTRE_VENTANAS_MS, accion);

        // Ejecutar solo una vez
        temporizador.setRepeats(false);

        // Iniciar temporizador
        temporizador.start();
    }
}
