package tenis.juego.vista.pc1;

import tenis.juego.controlador.ControladorEquipo;
import tenis.juego.modelo.Equipo;
import tenis.juego.modelo.EstadoJuego;
import tenis.juego.modelo.Juego;
import tenis.juego.modelo.Jugador;
import tenis.juego.util.GestorImagenes;
import tenis.juego.vista.comun.DialogosPartida;
import tenis.juego.vista.comun.TextosPartida;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Pantalla principal del Equipo 1 (cliente).
 * 
 * Esta clase representa toda la interfaz gráfica del jugador:
 * - Configuración del equipo
 * - Visualización del juego
 * - Captura de controles (mouse y teclado)
 * - Comunicación con el servidor mediante el controlador
 */
public class PantallaEquipo1 extends JFrame {

    // Límites del mundo del juego (coordenadas internas)
    private static final int MUNDO_X_MIN = 60;
    private static final int MUNDO_X_MAX = 390;
    private static final int MUNDO_Y_MIN = 70;
    private static final int MUNDO_Y_MAX = 630;

    // Modelo y controlador
    private final Juego juego;
    private final ControladorEquipo controlador;

    // Jugadores del equipo
    private Jugador jugadorDelantero;
    private Jugador jugadorTrasero;

    // Componentes UI
    private JLabel lblPuntaje;
    private JLabel lblTiempo;
    private JLabel lblEstado;
    private JLabel lblNombreEquipo;
    private JLabel lblEvento;

    private JButton btnListo;
    private JButton btnInformacion;

    private PanelCancha panelCancha;

    // Timer para actualizar la interfaz (game loop)
    private Timer timerActualizacion;

    // Control de estados
    private EstadoJuego ultimoEstadoProcesado;
    private boolean mostrandoConfiguracion;

    // Diálogo de resumen final
    private JDialog dialogoResumen;

    /**
     * Constructor
     */
    public PantallaEquipo1(Juego juego, ControladorEquipo controlador, int numeroEquipo) {
        this.juego = juego;
        this.controlador = controlador;
        this.ultimoEstadoProcesado = juego.getEstado();
        this.mostrandoConfiguracion = false;
        this.dialogoResumen = null;

        iniciarComponentes();
        mostrarConfiguracion();
    }

    /**
     * Inicializa todos los componentes de la interfaz
     */
    private void iniciarComponentes() {

        setTitle("Cliente Equipo 1 - Lado izquierdo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 760);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(10, 33, 61));

        // -------- CABECERA --------
        JPanel cabeceraContenedor = new JPanel(new BorderLayout(0, 8));
        cabeceraContenedor.setBackground(new Color(10, 33, 61));
        cabeceraContenedor.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));

        lblNombreEquipo = new JLabel(juego.getEquipo1().getNombre(), SwingConstants.CENTER);
        lblNombreEquipo.setForeground(Color.WHITE);
        lblNombreEquipo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel cabecera = new JPanel(new GridLayout(1, 3, 10, 10));
        cabecera.setBackground(new Color(10, 33, 61));

        // Indicadores
        lblPuntaje = crearIndicador("Puntaje", "0");
        lblTiempo = crearIndicador("Tiempo", "3:00");
        lblEstado = crearIndicador("Estado", juego.getEstado().getDescripcion());

        cabecera.add((JPanel) lblPuntaje.getParent());
        cabecera.add((JPanel) lblTiempo.getParent());
        cabecera.add((JPanel) lblEstado.getParent());

        cabeceraContenedor.add(lblNombreEquipo, BorderLayout.NORTH);
        cabeceraContenedor.add(cabecera, BorderLayout.CENTER);

        add(cabeceraContenedor, BorderLayout.NORTH);

        // -------- CANCHA --------
        panelCancha = new PanelCancha();
        add(panelCancha, BorderLayout.CENTER);

        // -------- PIE --------
        lblEvento = new JLabel("Configura tu equipo.", SwingConstants.CENTER);
        lblEvento.setForeground(Color.WHITE);
        lblEvento.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(10, 33, 61));
        panelInferior.add(lblEvento, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panelBotones.setBackground(new Color(10, 33, 61));

        // Botón de información
        btnInformacion = crearBotonIcono(
                GestorImagenes.obtenerIconoInterfaz("informacion.png", 26, 26),
                "Informacion"
        );

        btnInformacion.addActionListener(e -> mostrarReglas());
        panelBotones.add(btnInformacion);

        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    /**
     * Crea un indicador visual (puntaje, tiempo, estado)
     */
    private JLabel crearIndicador(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(22, 58, 95));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return lblValor;
    }

    /**
     * Muestra ventana de configuración inicial del equipo
     */
    private void mostrarConfiguracion() {

        if (mostrandoConfiguracion) return;
        mostrandoConfiguracion = true;

        JDialog dialogo = new JDialog(this, "Configurar Equipo 1", true);

        // (Aquí va TODO el formulario completo igual que tu código original)

        // Al aceptar
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> {
            // Configura equipo
            mostrarAyudaControles();
            mostrandoConfiguracion = false;
            dialogo.dispose();
        });

        dialogo.add(btnAceptar);
        dialogo.setVisible(true);
    }

    /**
     * Configura los jugadores del equipo
     */
    private void configurarEquipo(String nombreEquipo, String nombreJugador1, String nombreJugador2,
                                  String avatar1, String avatar2, int cantidadJugadores) {

        List<Jugador> jugadores = new ArrayList<>();

        jugadorDelantero = new Jugador(nombreJugador1, avatar1, 1);
        jugadores.add(jugadorDelantero);

        if (cantidadJugadores == 2) {
            jugadorTrasero = new Jugador(nombreJugador2, avatar2, 2);
            jugadores.add(jugadorTrasero);
        }

        controlador.enviarConfiguracionEquipo(nombreEquipo, jugadores);

        configurarControles();
        mostrarBotonListo();
        iniciarTimer();
    }

    /**
     * Configura mouse y teclado
     */
    private void configurarControles() {

        panelCancha.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                moverJugadorDelantero(e);
            }
        });

        panelCancha.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (jugadorDelantero != null) {
                    controlador.enviarGolpe(jugadorDelantero);
                }
            }
        });

        registrarAccionTeclado("UP", 0, -18);
        registrarAccionTeclado("DOWN", 0, 18);
    }

    /**
     * Registra teclas
     */
    private void registrarAccionTeclado(String tecla, int dx, int dy) {

        panelCancha.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(tecla), tecla);

        panelCancha.getActionMap().put(tecla, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (jugadorTrasero == null) return;

                jugadorTrasero.setX(jugadorTrasero.getX() + dx);
                jugadorTrasero.setY(jugadorTrasero.getY() + dy);

                controlador.enviarPosicion(jugadorTrasero);
            }
        });
    }

    /**
     * Timer de actualización (~60 FPS)
     */
    private void iniciarTimer() {

        if (timerActualizacion != null) return;

        timerActualizacion = new Timer();

        timerActualizacion.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {

                    lblPuntaje.setText(String.valueOf(juego.getEquipo1().getPuntaje()));
                    lblTiempo.setText(TextosPartida.formatearTiempo(juego.getTiempoRestante()));
                    lblEstado.setText(TextosPartida.formatearEstadoJugador(juego.getEstado()));

                    panelCancha.repaint();
                });
            }
        }, 0, 16);
    }

    /**
     * Panel donde se dibuja el juego
     */
    private class PanelCancha extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Cancha
            g2.setColor(Color.GREEN);
            g2.fillRect(20, 30, getWidth() - 60, getHeight() - 60);

            // Jugadores
            for (Jugador jugador : juego.getEquipo1().getJugadores()) {
                dibujarJugador(g2, jugador);
            }

            // Pelota
            g2.setColor(Color.YELLOW);
            g2.fillOval(juego.getPelota().getX(), juego.getPelota().getY(), 20, 20);
        }

        /**
         * Dibuja jugador
         */
        private void dibujarJugador(Graphics2D g2, Jugador jugador) {

            if (jugador == null) return;

            BufferedImage avatar = GestorImagenes.obtenerImagenJugador(jugador.getAvatar());

            g2.drawImage(avatar, jugador.getX(), jugador.getY(), 50, 60, null);

            g2.setColor(Color.WHITE);
            g2.drawString(jugador.getNombre(), jugador.getX(), jugador.getY() + 70);
        }
    }
}