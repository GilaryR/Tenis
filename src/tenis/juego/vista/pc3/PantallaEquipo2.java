package tenis.juego.vista.pc3;

/**
 * Pantalla gráfica del cliente correspondiente al Equipo 2 (lado derecho de la cancha).
 * 
 * Esta clase representa la interfaz de usuario para el segundo equipo en el juego,
 * permitiendo configurar jugadores, visualizar el estado del partido, controlar
 * movimientos y realizar acciones como golpear la pelota.
 */

import tenis.juego.controlador.ControladorEquipo;
import tenis.juego.modelo.Equipo;
import tenis.juego.modelo.EstadoJuego;
import tenis.juego.modelo.Juego;
import tenis.juego.modelo.Jugador;
import tenis.juego.util.GestorImagenes;
import tenis.juego.vista.comun.DialogosPartida;
import tenis.juego.vista.comun.TextosPartida;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase principal que define la ventana del equipo 2.
 */
public class PantallaEquipo2 extends JFrame {

    /** Límites del movimiento en el mundo del juego (lado derecho). */
    private static final int MUNDO_X_MIN = 410;
    private static final int MUNDO_X_MAX = 740;
    private static final int MUNDO_Y_MIN = 70;
    private static final int MUNDO_Y_MAX = 630;

    /** Referencia al modelo del juego compartido. */
    private final Juego juego;

    /** Controlador encargado de enviar acciones al servidor. */
    private final ControladorEquipo controlador;

    /** Jugadores del equipo. */
    private Jugador jugadorDelantero;
    private Jugador jugadorTrasero;

    /** Componentes de interfaz para mostrar información. */
    private JLabel lblPuntaje;
    private JLabel lblTiempo;
    private JLabel lblEstado;
    private JLabel lblNombreEquipo;
    private JLabel lblEvento;

    /** Botones de interacción. */
    private JButton btnListo;
    private JButton btnInformacion;

    /** Panel donde se dibuja la cancha. */
    private PanelCancha panelCancha;

    /** Temporizador para actualizar la interfaz constantemente. */
    private Timer timerActualizacion;

    /** Último estado procesado para detectar cambios. */
    private EstadoJuego ultimoEstadoProcesado;

    /** Controla si ya se está mostrando la configuración. */
    private boolean mostrandoConfiguracion;

    /** Diálogo de resumen final. */
    private JDialog dialogoResumen;

    /**
     * Constructor de la pantalla del equipo 2.
     */
    public PantallaEquipo2(Juego juego, ControladorEquipo controlador, int numeroEquipo) {
        this.juego = juego;
        this.controlador = controlador;
        this.ultimoEstadoProcesado = juego.getEstado();
        this.mostrandoConfiguracion = false;
        this.dialogoResumen = null;
        iniciarComponentes();
        mostrarConfiguracion();
    }

    /**
     * Inicializa todos los componentes visuales de la ventana.
     */
    private void iniciarComponentes() {
        setTitle("Cliente Equipo 2 - Lado derecho");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 760);
        setLayout(new BorderLayout(10, 10));

        // Color de fondo general
        getContentPane().setBackground(new Color(64, 17, 34));

        /** -------- CABECERA -------- */
        JPanel cabeceraContenedor = new JPanel(new BorderLayout(0, 8));
        cabeceraContenedor.setBackground(new Color(64, 17, 34));
        cabeceraContenedor.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));

        lblNombreEquipo = new JLabel(juego.getEquipo2().getNombre(), SwingConstants.CENTER);
        lblNombreEquipo.setForeground(Color.WHITE);
        lblNombreEquipo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel cabecera = new JPanel(new GridLayout(1, 3, 10, 10));
        cabecera.setBackground(new Color(64, 17, 34));

        lblPuntaje = crearIndicador("Puntaje", "0");
        lblTiempo = crearIndicador("Tiempo", "3:00");
        lblEstado = crearIndicador("Estado", juego.getEstado().getDescripcion());

        cabecera.add((JPanel) lblPuntaje.getParent());
        cabecera.add((JPanel) lblTiempo.getParent());
        cabecera.add((JPanel) lblEstado.getParent());

        cabeceraContenedor.add(lblNombreEquipo, BorderLayout.NORTH);
        cabeceraContenedor.add(cabecera, BorderLayout.CENTER);
        add(cabeceraContenedor, BorderLayout.NORTH);

        /** -------- CANCHA -------- */
        panelCancha = new PanelCancha();
        add(panelCancha, BorderLayout.CENTER);

        /** -------- EVENTOS -------- */
        lblEvento = new JLabel("Configura tu equipo.", SwingConstants.CENTER);
        lblEvento.setForeground(Color.WHITE);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(64, 17, 34));
        panelInferior.add(lblEvento, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBackground(new Color(64, 17, 34));

        btnInformacion = crearBotonIcono(
                GestorImagenes.obtenerIconoInterfaz("informacion.png", 26, 26),
                "Informacion");

        btnInformacion.addActionListener(e -> mostrarReglas());

        panelBotones.add(btnInformacion);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    /**
     * Crea un indicador visual (título + valor).
     */
    private JLabel crearIndicador(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(108, 30, 55));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(245, 183, 177));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return lblValor;
    }

    /**
     * Muestra el diálogo de configuración del equipo.
     */
    private void mostrarConfiguracion() {
        if (mostrandoConfiguracion) return;

        mostrandoConfiguracion = true;

        JDialog dialogo = new JDialog(this, "Configurar Equipo 2", true);

        JTextField txtEquipo = new JTextField("Equipo Rojo");

        JButton btnAceptar = new JButton("Aceptar");

        btnAceptar.addActionListener(e -> {
            configurarEquipo(txtEquipo.getText(), "Jugador 1", "Jugador 2",
                    "avatar1", "avatar2", 2);
            mostrandoConfiguracion = false;
            dialogo.dispose();
        });

        dialogo.add(txtEquipo);
        dialogo.add(btnAceptar, BorderLayout.SOUTH);
        dialogo.setSize(300, 200);
        dialogo.setVisible(true);
    }

    /**
     * Configura el equipo y sus jugadores.
     */
    private void configurarEquipo(String nombreEquipo, String nombreJugador1, String nombreJugador2,
                                  String avatar1, String avatar2, int cantidadJugadores) {

        jugadorDelantero = new Jugador(nombreJugador1, avatar1, 1);
        jugadorDelantero.setX(650);
        jugadorDelantero.setY(220);

        juego.getEquipo2().setNombre(nombreEquipo);
        juego.getEquipo2().agregarJugador(jugadorDelantero);

        controlador.enviarConfiguracionEquipo(nombreEquipo, List.of(jugadorDelantero));

        iniciarTimer();
    }

    /**
     * Inicia el timer que refresca la pantalla.
     */
    private void iniciarTimer() {
        timerActualizacion = new Timer();

        timerActualizacion.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    lblPuntaje.setText(String.valueOf(juego.getEquipo2().getPuntaje()));
                    lblTiempo.setText(TextosPartida.formatearTiempo(juego.getTiempoRestante()));
                    panelCancha.repaint();
                });
            }
        }, 0, 16);
    }

    /**
     * Panel que dibuja la cancha del equipo 2.
     */
    private class PanelCancha extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            // Dibujar cancha
            g2.setColor(Color.GREEN);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Dibujar jugadores
            for (Jugador jugador : juego.getEquipo2().getJugadores()) {
                if (jugador == null) continue;

                BufferedImage img = GestorImagenes.obtenerImagenJugador(jugador.getAvatar());
                g2.drawImage(img, jugador.getX(), jugador.getY(), null);
            }
        }
    }
}