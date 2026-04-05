package tenis.juego.vista.pc2;

import tenis.juego.controlador.ControladorCentral;
import tenis.juego.modelo.Equipo;
import tenis.juego.modelo.EstadoJuego;
import tenis.juego.modelo.Juego;
import tenis.juego.modelo.Jugador;
import tenis.juego.util.GestorImagenes;
import tenis.juego.vista.comun.DialogosPartida;
import tenis.juego.vista.comun.TextosPartida;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Pantalla del servidor central.
 *   * @author Aleja
 * Representa la vista principal del juego desde el lado del servidor:
 * - Muestra la malla (red central)
 * - Muestra el estado de ambos equipos
 * - Controla el inicio de la partida
 * - Permite configurar la duración del juego
 * - Muestra el resumen final
 */
public class PantallaCentral extends JFrame {

    // Modelo del juego compartido
    private final Juego juego;

    // Controlador central que gestiona la lógica del servidor
    private final ControladorCentral controlador;

    // Labels de interfaz
    private JLabel lblTituloEquipo1;
    private JLabel lblTituloEquipo2;
    private JLabel lblPuntaje1;
    private JLabel lblPuntaje2;
    private JLabel lblTiempo;
    private JLabel lblEstadoEquipos;
    private JLabel lblEvento;

    // Controles
    private JButton btnIniciar;
    private JSpinner spnMinutos;
    private JPanel panelConfiguracion;
    private JButton btnReglas;

    // Panel gráfico de la malla
    private PanelMalla panelMalla;

    // Timer para refrescar la UI
    private Timer timer;

    // Control de cambios de estado
    private EstadoJuego ultimoEstadoProcesado;

    // Diálogo de resumen final
    private JDialog dialogoResumen;

    /**
     * Constructor principal.
     */
    public PantallaCentral(Juego juego, ControladorCentral controlador) {
        this.juego = juego;
        this.controlador = controlador;
        this.ultimoEstadoProcesado = juego.getEstado();
        this.dialogoResumen = null;

        iniciarComponentes();
        iniciarRefresco();
    }

    /**
     * Inicializa todos los componentes gráficos.
     */
    private void iniciarComponentes() {

        setTitle("Servidor Central - Malla");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 700);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(8, 24, 35));

        // -------- CABECERA --------
        JPanel cabecera = new JPanel(new GridLayout(1, 5, 10, 10));
        cabecera.setBackground(new Color(8, 24, 35));
        cabecera.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));

        lblPuntaje1 = crearTarjetaValor("Equipo 1", "0", new Color(85, 190, 255));
        lblTiempo = crearTarjetaValor("Tiempo", "3:00", new Color(255, 214, 102));
        lblPuntaje2 = crearTarjetaValor("Equipo 2", "0", new Color(255, 116, 102));
        lblEstadoEquipos = crearTarjetaTexto("Equipos", "Esperando");
        lblEvento = crearTarjetaTexto("Evento", "Configura ambos equipos");

        cabecera.add((JPanel) lblPuntaje1.getParent());
        cabecera.add((JPanel) lblTiempo.getParent());
        cabecera.add((JPanel) lblPuntaje2.getParent());
        cabecera.add((JPanel) lblEstadoEquipos.getParent());
        cabecera.add((JPanel) lblEvento.getParent());

        add(cabecera, BorderLayout.NORTH);

        // -------- PANEL CENTRAL (MALLA) --------
        panelMalla = new PanelMalla();
        add(panelMalla, BorderLayout.CENTER);

        // -------- CONFIGURACIÓN --------
        panelConfiguracion = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelConfiguracion.setBackground(new Color(8, 24, 35));
        panelConfiguracion.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        spnMinutos = new JSpinner(
                new SpinnerNumberModel(juego.getDuracionPartidoMinutos(), 3, 5, 1)
        );

        // Cambio de duración del juego
        spnMinutos.addChangeListener(e -> {
            juego.configurarDuracionMinutos((Integer) spnMinutos.getValue());
            controlador.enviarActualizacion();
        });

        btnIniciar = new JButton("Iniciar juego");
        btnIniciar.addActionListener(e -> controlador.iniciarPartido());

        panelConfiguracion.add(crearEtiqueta("Duracion"));
        panelConfiguracion.add(spnMinutos);
        panelConfiguracion.add(btnIniciar);

        // -------- BOTONES --------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 10));
        panelBotones.setBackground(new Color(8, 24, 35));

        btnReglas = crearBotonIcono(
                GestorImagenes.obtenerIconoInterfaz("informacion.png", 34, 34),
                "Reglas"
        );

        btnReglas.addActionListener(e -> mostrarReglas());
        panelBotones.add(btnReglas);

        // -------- PANEL INFERIOR --------
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(8, 24, 35));
        panelInferior.add(panelConfiguracion, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    /**
     * Crea una etiqueta de formulario.
     */
    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        return etiqueta;
    }

    /**
     * Crea un botón con icono.
     */
    private JButton crearBotonIcono(ImageIcon icono, String tooltip) {
        JButton boton = new JButton(icono);
        boton.setToolTipText(tooltip);
        boton.setOpaque(true);
        boton.setBackground(new Color(22, 54, 72));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 45)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        return boton;
    }

    /**
     * Crea tarjeta de valor (puntaje, tiempo).
     */
    private JLabel crearTarjetaValor(String titulo, String valor, Color colorTitulo) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 41, 58));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 28)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(colorTitulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));

        if ("Equipo 1".equals(titulo)) {
            lblTituloEquipo1 = lblTitulo;
        } else if ("Equipo 2".equals(titulo)) {
            lblTituloEquipo2 = lblTitulo;
        }

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 34));

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return lblValor;
    }

    /**
     * Crea tarjeta de texto (estado/evento).
     */
    private JLabel crearTarjetaTexto(String titulo, String valor) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 41, 58));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 28)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(163, 184, 196));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return lblValor;
    }

    /**
     * Inicia el refresco automático de la interfaz.
     */
    private void iniciarRefresco() {

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {

                    // Actualiza datos
                    lblPuntaje1.setText(String.valueOf(juego.getEquipo1().getPuntaje()));
                    lblPuntaje2.setText(String.valueOf(juego.getEquipo2().getPuntaje()));
                    lblTituloEquipo1.setText(juego.getEquipo1().getNombre());
                    lblTituloEquipo2.setText(juego.getEquipo2().getNombre());
                    lblTiempo.setText(formatearTiempo(juego.getTiempoRestante()));

                    // Estado equipos
                    lblEstadoEquipos.setText(
                            (juego.getEquipo1().isListo() ? "E1 listo" : "E1 espera") + " | " +
                            (juego.getEquipo2().isListo() ? "E2 listo" : "E2 espera")
                    );

                    lblEvento.setText(juego.getUltimoEvento());

                    // Habilitar inicio
                    btnIniciar.setEnabled(juego.puedeIniciar());

                    // Mostrar u ocultar configuración
                    boolean mostrarConfiguracion =
                            juego.getEstado() != EstadoJuego.JUGANDO &&
                            juego.getEstado() != EstadoJuego.PUNTO_DE_ORO;

                    panelConfiguracion.setVisible(mostrarConfiguracion);
                    spnMinutos.setEnabled(mostrarConfiguracion);

                    panelMalla.repaint();

                    procesarCambioDeEstado();
                });
            }
        }, 0, 33);
    }

    /**
     * Detecta cambios importantes del estado del juego.
     */
    private void procesarCambioDeEstado() {

        EstadoJuego estadoActual = juego.getEstado();

        if (estadoActual == EstadoJuego.TERMINADO &&
            ultimoEstadoProcesado != EstadoJuego.TERMINADO) {

            mostrarResumenFinal();
        }

        if (ultimoEstadoProcesado == EstadoJuego.TERMINADO &&
            estadoActual == EstadoJuego.ESPERANDO_EQUIPOS) {

            cerrarResumenFinal();
        }

        ultimoEstadoProcesado = estadoActual;
    }

    /**
     * Formatea tiempo.
     */
    private String formatearTiempo(int segundos) {
        return TextosPartida.formatearTiempo(segundos);
    }

    /**
     * Muestra reglas del juego.
     */
    private void mostrarReglas() {
        DialogosPartida.mostrarReglas(this);
    }

    /**
     * Muestra el resumen final.
     */
    private void mostrarResumenFinal() {
        cerrarResumenFinal();

        dialogoResumen = DialogosPartida.crearDialogoResumen(
                this,
                juego,
                () -> {
                    cerrarResumenFinal();
                    controlador.reiniciarPartido();
                }
        );

        dialogoResumen.setVisible(true);
    }

    /**
     * Cierra el resumen final si está abierto.
     */
    private void cerrarResumenFinal() {
        if (dialogoResumen != null) {
            dialogoResumen.dispose();
            dialogoResumen = null;
        }
    }

    /**
     * Panel interno que dibuja la malla y la pelota.
     */
    private class PanelMalla extends JPanel {

        PanelMalla() {
            setBackground(new Color(31, 143, 103));
            setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 70));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            int centroX = getWidth() / 2;
            int centroY = getHeight() / 2;

            // Fondo
            g2.setColor(new Color(31, 143, 103));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Bordes
            g2.setColor(new Color(255, 255, 255, 55));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(36, 36, getWidth() - 72, getHeight() - 72, 24, 24);

            // Poste central
            g2.setColor(new Color(245, 245, 245));
            g2.fillRect(centroX - 7, 90, 14, getHeight() - 180);

            // Parte superior red
            g2.setStroke(new BasicStroke(4f));
            g2.drawLine(centroX - 36, 100, centroX + 36, 100);

            // Malla
            g2.setColor(new Color(185, 193, 199));
            g2.setStroke(new BasicStroke(2f));

            for (int x = centroX - 28; x <= centroX + 28; x += 8) {
                g2.drawLine(x, 100, x, getHeight() - 100);
            }

            for (int y = 120; y <= getHeight() - 120; y += 16) {
                g2.drawLine(centroX - 28, y, centroX + 28, y);
            }

            // Pelota
            if (!juego.isSaquePendiente() &&
                Math.abs(juego.getPelota().getX() - 400) <= 80) {

                int xPelota = centroX + (juego.getPelota().getX() - 400);
                int yPelota = centroY + (juego.getPelota().getY() - 350);

                g2.setColor(new Color(255, 214, 10));
                g2.fillOval(xPelota - 12, yPelota - 12, 24, 24);
            }

            // Mensaje final
            if (juego.getEstado() == EstadoJuego.TERMINADO) {
                g2.setColor(new Color(0, 0, 0, 155));
                g2.fillRoundRect(getWidth() / 2 - 180, 35, 360, 54, 18, 18);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 20));
                g2.drawString("Ganador: " + juego.getGanador(), getWidth() / 2 - 145, 68);
            }
        }
    }
}