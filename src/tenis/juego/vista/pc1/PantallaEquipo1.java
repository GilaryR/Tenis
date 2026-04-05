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
 * Ventana principal del cliente para el Equipo 1 (lado izquierdo de la cancha).
 *
 * <p>Esta clase representa la interfaz gráfica completa que usa el equipo 1 durante
 * una partida de tenis. Permite configurar el equipo antes de cada partida,
 * controlar los jugadores durante el juego y visualizar el estado en tiempo real.</p>
 *
 * <p>Responsabilidades principales:
 * <ul>
 *   <li>Mostrar el diálogo de configuración del equipo al inicio o entre partidas.</li>
 *   <li>Controlar el jugador delantero con el mouse y el trasero con las teclas de dirección.</li>
 *   <li>Actualizar la cancha, el marcador y los eventos cada ~16 ms (≈60 FPS).</li>
 *   <li>Detectar cambios de estado del juego y reaccionar (mostrar resumen final, reiniciar, etc.).</li>
 * </ul>
 * </p>
 *
 * <p><b>Controles del equipo 1:</b>
 * <ul>
 *   <li>Mouse (mover): desplaza al jugador delantero.</li>
 *   <li>Clic izquierdo: envía un golpe con el jugador delantero.</li>
 *   <li>Teclas de dirección (↑ ↓ ← →): mueven al jugador trasero.</li>
 *   <li>Barra espaciadora: envía un golpe con el jugador trasero.</li>
 * </ul>
 * </p>
 */
public class PantallaEquipo1 extends JFrame {

    /** Límite izquierdo del área de juego en coordenadas de mundo. */
    private static final int MUNDO_X_MIN = 60;

    /** Límite derecho del área de juego en coordenadas de mundo. */
    private static final int MUNDO_X_MAX = 390;

    /** Límite superior del área de juego en coordenadas de mundo. */
    private static final int MUNDO_Y_MIN = 70;

    /** Límite inferior del área de juego en coordenadas de mundo. */
    private static final int MUNDO_Y_MAX = 630;

    /** Modelo central del juego; contiene el estado, los equipos y la pelota. */
    private final Juego juego;

    /** Controlador que gestiona la comunicación con el servidor para este equipo. */
    private final ControladorEquipo controlador;

    /** Jugador delantero del equipo 1, controlado con el mouse. */
    private Jugador jugadorDelantero;

    /**
     * Jugador trasero del equipo 1, controlado con las teclas de dirección.
     * Puede ser {@code null} si el equipo tiene solo un jugador.
     */
    private Jugador jugadorTrasero;

    /** Etiqueta que muestra el puntaje actual del equipo 1. */
    private JLabel lblPuntaje;

    /** Etiqueta que muestra el tiempo restante de la partida. */
    private JLabel lblTiempo;

    /** Etiqueta que muestra el estado actual del juego (ej. "En juego", "Esperando"). */
    private JLabel lblEstado;

    /** Etiqueta que muestra el nombre del equipo 1. */
    private JLabel lblNombreEquipo;

    /** Etiqueta de la barra inferior que describe el último evento de la partida. */
    private JLabel lblEvento;

    /** Botón que el jugador pulsa para indicar que su equipo está listo para comenzar. */
    private JButton btnListo;

    /** Botón para mostrar las reglas del juego. */
    private JButton btnInformacion;

    /** Panel personalizado donde se dibuja la cancha, los jugadores y la pelota. */
    private PanelCancha panelCancha;

    /** Timer que refresca la UI cada ~16 ms. */
    private Timer timerActualizacion;

    /** Último estado del juego procesado; se usa para detectar transiciones de estado. */
    private EstadoJuego ultimoEstadoProcesado;

    /** Indica si el diálogo de configuración está actualmente abierto. */
    private boolean mostrandoConfiguracion;

    /** Referencia al diálogo de resumen final, para poder cerrarlo programáticamente. */
    private JDialog dialogoResumen;

    /**
     * Crea la ventana del cliente Equipo 1 y muestra inmediatamente el diálogo de configuración.
     *
     * @param juego          el modelo compartido del juego
     * @param controlador    el controlador que comunica este cliente con el servidor
     * @param numeroEquipo   número identificador del equipo (se espera 1 para esta clase)
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
     * Inicializa y organiza todos los componentes visuales de la ventana principal
     * (cabecera con indicadores, panel de cancha y panel inferior con botones).
     * Usa una paleta de colores azul oscuro para identificar visualmente al equipo 1.
     */
    private void iniciarComponentes() {
        setTitle("Cliente Equipo 1 - Lado izquierdo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 760);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(10, 33, 61));

        JPanel cabeceraContenedor = new JPanel(new BorderLayout(0, 8));
        cabeceraContenedor.setBackground(new Color(10, 33, 61));
        cabeceraContenedor.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));

        lblNombreEquipo = new JLabel(juego.getEquipo1().getNombre(), SwingConstants.CENTER);
        lblNombreEquipo.setForeground(Color.WHITE);
        lblNombreEquipo.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel cabecera = new JPanel(new GridLayout(1, 3, 10, 10));
        cabecera.setBackground(new Color(10, 33, 61));
        lblPuntaje = crearIndicador("Puntaje", "0");
        lblTiempo = crearIndicador("Tiempo", "3:00");
        lblEstado = crearIndicador("Estado", juego.getEstado().getDescripcion());
        cabecera.add((JPanel) lblPuntaje.getParent());
        cabecera.add((JPanel) lblTiempo.getParent());
        cabecera.add((JPanel) lblEstado.getParent());
        cabeceraContenedor.add(lblNombreEquipo, BorderLayout.NORTH);
        cabeceraContenedor.add(cabecera, BorderLayout.CENTER);
        add(cabeceraContenedor, BorderLayout.NORTH);

        panelCancha = new PanelCancha();
        add(panelCancha, BorderLayout.CENTER);

        lblEvento = new JLabel("Configura tu equipo.", SwingConstants.CENTER);
        lblEvento.setForeground(Color.WHITE);
        lblEvento.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(10, 33, 61));
        panelInferior.add(lblEvento, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panelBotones.setBackground(new Color(10, 33, 61));
        btnInformacion = crearBotonIcono(GestorImagenes.obtenerIconoInterfaz("informacion.png", 26, 26), "Informacion");
        btnInformacion.addActionListener(e -> mostrarReglas());
        panelBotones.add(btnInformacion);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        add(panelInferior, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    /**
     * Crea un panel indicador de dos líneas (título + valor) con el estilo visual del equipo 1.
     *
     * @param titulo etiqueta descriptiva del indicador (ej. "Puntaje")
     * @param valor  valor inicial que se mostrará en grande (ej. "0")
     * @return la {@link JLabel} del valor, para poder actualizarla posteriormente
     */
    private JLabel crearIndicador(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(22, 58, 95));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 30)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(133, 193, 233));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 26));
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        return lblValor;
    }

    /**
     * Muestra el diálogo modal de configuración del equipo 1.
     *
     * <p>Permite ingresar el nombre del equipo, la cantidad de jugadores (1 o 2),
     * los nombres de cada jugador y seleccionar visualmente su avatar.
     * Si el diálogo ya está abierto, este método no hace nada.</p>
     *
     * <p>El nombre por defecto del equipo es {@code "Equipo Azul"}.</p>
     */
    private void mostrarConfiguracion() {
        if (mostrandoConfiguracion) {
            return;
        }
        mostrandoConfiguracion = true;
        JDialog dialogo = new JDialog(this, "Configurar Equipo 1", true);
        dialogo.setSize(760, 560);
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.setLocationRelativeTo(this);
        dialogo.getContentPane().setBackground(new Color(22, 92, 78));

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        formulario.setBackground(new Color(22, 92, 78));

        JTextField txtEquipo = new JTextField("Equipo Azul");
        JSpinner spnJugadores = new JSpinner(new SpinnerNumberModel(2, 1, 2, 1));
        JTextField txtJugador1 = new JTextField("Jugador 1");
        JTextField txtJugador2 = new JTextField("Jugador 2");
        String[] avatarDelantero = {GestorImagenes.obtenerClavesJugadores()[0]};
        String[] avatarTrasero = {GestorImagenes.obtenerClavesJugadores()[1]};

        formulario.add(crearEtiquetaFormulario("Nombre del equipo"));
        formulario.add(txtEquipo);
        formulario.add(crearEtiquetaFormulario("Cantidad de jugadores"));
        formulario.add(spnJugadores);
        formulario.add(crearEtiquetaFormulario("Jugador delantero"));
        formulario.add(txtJugador1);
        formulario.add(crearEtiquetaFormulario("Jugador trasero"));
        formulario.add(txtJugador2);

        JPanel selectores = new JPanel(new GridLayout(2, 1, 10, 10));
        selectores.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        selectores.setBackground(new Color(22, 92, 78));
        selectores.add(crearSelectorVisual("Selecciona el jugador delantero", avatarDelantero));
        selectores.add(crearSelectorVisual("Selecciona el jugador trasero", avatarTrasero));

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setBackground(new Color(22, 58, 95));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        btnAceptar.addActionListener(e -> {
            int cantidad = (Integer) spnJugadores.getValue();
            configurarEquipo(txtEquipo.getText().trim(), txtJugador1.getText().trim(), txtJugador2.getText().trim(),
                    avatarDelantero[0], avatarTrasero[0], cantidad);
            mostrarAyudaControles();
            mostrandoConfiguracion = false;
            dialogo.dispose();
        });

        dialogo.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                mostrandoConfiguracion = false;
            }
        });

        dialogo.add(formulario, BorderLayout.NORTH);
        dialogo.add(new JScrollPane(selectores), BorderLayout.CENTER);
        dialogo.add(btnAceptar, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }

    /**
     * Crea un panel de selección visual de avatar para un jugador.
     *
     * <p>Muestra todos los avatares disponibles como botones de selección exclusiva ({@link JToggleButton}).
     * Al seleccionar uno, actualiza el arreglo {@code seleccion[0]} con la clave correspondiente.</p>
     *
     * @param titulo    texto descriptivo que encabeza el selector
     * @param seleccion arreglo de un elemento que almacena la clave del avatar seleccionado;
     *                  se usa como referencia mutable para comunicar la selección al llamador
     * @return el panel con el selector visual listo para agregar a un formulario
     */
    private JPanel crearSelectorVisual(String titulo, String[] seleccion) {
        JPanel contenedor = new JPanel(new BorderLayout(8, 8));
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 75)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        contenedor.setBackground(new Color(30, 116, 98));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitulo.setForeground(Color.WHITE);
        contenedor.add(lblTitulo, BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(1, 0, 8, 8));
        opciones.setBackground(new Color(30, 116, 98));
        ButtonGroup grupo = new ButtonGroup();
        for (String clave : GestorImagenes.obtenerClavesJugadores()) {
            JToggleButton boton = new JToggleButton(GestorImagenes.obtenerIconoOpcion(clave, 105, 105));
            boton.setMargin(new Insets(4, 4, 4, 4));
            boton.setFocusPainted(false);
            boton.setBackground(Color.WHITE);
            boton.setPreferredSize(new Dimension(120, 120));
            boton.addActionListener(e -> seleccion[0] = clave);
            if (clave.equals(seleccion[0])) {
                boton.setSelected(true);
            }
            grupo.add(boton);
            opciones.add(boton);
        }
        contenedor.add(opciones, BorderLayout.CENTER);
        return contenedor;
    }

    /**
     * Aplica la configuración del equipo 1 al modelo, registra los jugadores en sus
     * posiciones iniciales del lado izquierdo y prepara los controles de teclado/mouse.
     *
     * <p>Posiciones iniciales en coordenadas de mundo:
     * <ul>
     *   <li>Jugador delantero: X=150, Y=220</li>
     *   <li>Jugador trasero: X=280, Y=470 (solo si {@code cantidadJugadores == 2})</li>
     * </ul>
     * </p>
     *
     * @param nombreEquipo      nombre del equipo ingresado por el usuario
     * @param nombreJugador1    nombre del jugador delantero
     * @param nombreJugador2    nombre del jugador trasero (ignorado si {@code cantidadJugadores == 1})
     * @param avatar1           clave del avatar del jugador delantero
     * @param avatar2           clave del avatar del jugador trasero
     * @param cantidadJugadores número de jugadores activos: 1 o 2
     */
    private void configurarEquipo(String nombreEquipo, String nombreJugador1, String nombreJugador2,
                                  String avatar1, String avatar2, int cantidadJugadores) {
        List<Jugador> jugadores = new ArrayList<>();
        jugadorDelantero = new Jugador(nombreJugador1.isBlank() ? "Jugador 1" : nombreJugador1, avatar1, 1);
        jugadorDelantero.setX(150);
        jugadorDelantero.setY(220);
        jugadores.add(jugadorDelantero);

        juego.getEquipo1().setNombre(nombreEquipo.isBlank() ? "Equipo Azul" : nombreEquipo);
        juego.getEquipo1().limpiarJugadores();
        juego.getEquipo1().agregarJugador(jugadorDelantero);

        jugadorTrasero = null;
        if (cantidadJugadores == 2) {
            jugadorTrasero = new Jugador(nombreJugador2.isBlank() ? "Jugador 2" : nombreJugador2, avatar2, 2);
            jugadorTrasero.setX(280);
            jugadorTrasero.setY(470);
            jugadores.add(jugadorTrasero);
            juego.getEquipo1().agregarJugador(jugadorTrasero);
        }

        controlador.enviarConfiguracionEquipo(juego.getEquipo1().getNombre(), jugadores);
        configurarControles();
        mostrarBotonListo();
        iniciarTimer();
    }

    /**
     * Registra los listeners de mouse (para el jugador delantero) y las acciones
     * de teclado de dirección y barra espaciadora (para el jugador trasero) sobre el panel de cancha.
     */
    private void configurarControles() {
        panelCancha.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                moverJugadorDelantero(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                moverJugadorDelantero(e);
            }
        });

        panelCancha.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (jugadorDelantero != null && estaEnJuego()) {
                    controlador.enviarGolpe(jugadorDelantero);
                }
            }
        });

        registrarAccionTeclado("UP", 0, -18);
        registrarAccionTeclado("DOWN", 0, 18);
        registrarAccionTeclado("LEFT", -18, 0);
        registrarAccionTeclado("RIGHT", 18, 0);
        panelCancha.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "golpear");
        panelCancha.getActionMap().put("golpear", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jugadorTrasero != null && estaEnJuego()) {
                    controlador.enviarGolpe(jugadorTrasero);
                }
            }
        });
    }

    /**
     * Registra una acción de movimiento para el jugador trasero asociada a una tecla de dirección.
     *
     * <p>El movimiento queda limitado al área del equipo 1
     * ({@code MUNDO_X_MIN}–{@code MUNDO_X_MAX}, {@code MUNDO_Y_MIN}–{@code MUNDO_Y_MAX}).</p>
     *
     * @param tecla nombre de la tecla (ej. {@code "UP"}, {@code "LEFT"})
     * @param dx    desplazamiento horizontal en unidades de mundo por pulsación
     * @param dy    desplazamiento vertical en unidades de mundo por pulsación
     */
    private void registrarAccionTeclado(String tecla, int dx, int dy) {
        panelCancha.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(tecla), tecla);
        panelCancha.getActionMap().put(tecla, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jugadorTrasero == null || !estaEnJuego()) {
                    return;
                }
                jugadorTrasero.setX(limitar(jugadorTrasero.getX() + dx, MUNDO_X_MIN, MUNDO_X_MAX));
                jugadorTrasero.setY(limitar(jugadorTrasero.getY() + dy, MUNDO_Y_MIN, MUNDO_Y_MAX));
                controlador.enviarPosicion(jugadorTrasero);
            }
        });
    }

    /**
     * Agrega (o reemplaza) el botón "Aceptar Partida" en el panel inferior.
     * Al pulsarlo, notifica al servidor que este equipo está listo para jugar.
     */
    private void mostrarBotonListo() {
        if (btnListo != null) {
            ((JPanel) btnInformacion.getParent()).remove(btnListo);
        }
        btnListo = crearBotonPrincipal("Aceptar Partida");
        btnListo.addActionListener(e -> {
            controlador.notificarListo();
            btnListo.setEnabled(false);
            lblEvento.setText("Equipo confirmado. Esperando al servidor.");
        });
        ((JPanel) btnInformacion.getParent()).add(btnListo);
        revalidate();
        repaint();
    }

    /**
     * Inicia el timer de actualización de la UI (≈60 FPS) si no está ya en ejecución.
     *
     * <p>Cada tick actualiza el puntaje, el tiempo, el estado, el nombre del equipo,
     * el texto de evento y repinta la cancha. También llama a
     * {@link #procesarCambioDeEstado()} para detectar transiciones importantes.</p>
     */
    private void iniciarTimer() {
        if (timerActualizacion != null) {
            return;
        }
        timerActualizacion = new Timer();
        timerActualizacion.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    lblPuntaje.setText(String.valueOf(juego.getEquipo1().getPuntaje()));
                    lblTiempo.setText(formatearTiempo(juego.getTiempoRestante()));
                    lblEstado.setText(formatearEstadoJugador(juego.getEstado()));
                    lblNombreEquipo.setText(juego.getEquipo1().getNombre());
                    lblEvento.setText(formatearEventoJugador(juego.getUltimoEvento()));
                    panelCancha.repaint();
                    procesarCambioDeEstado();
                });
            }
        }, 0, 16);
    }

    /**
     * Detecta transiciones de estado del juego y ejecuta las acciones correspondientes:
     * <ul>
     *   <li>Si el juego acaba de terminar → muestra el resumen final.</li>
     *   <li>Si se regresa al estado de espera → cierra el resumen y abre la configuración de nuevo.</li>
     * </ul>
     */
    private void procesarCambioDeEstado() {
        EstadoJuego estadoActual = juego.getEstado();
        if (estadoActual == EstadoJuego.TERMINADO && ultimoEstadoProcesado != EstadoJuego.TERMINADO) {
            mostrarResumenFinal();
        }
        if (ultimoEstadoProcesado == EstadoJuego.TERMINADO && estadoActual == EstadoJuego.ESPERANDO_EQUIPOS) {
            cerrarResumenFinal();
            if (btnListo != null) {
                btnListo.setEnabled(true);
            }
            SwingUtilities.invokeLater(this::mostrarConfiguracion);
        }
        ultimoEstadoProcesado = estadoActual;
    }

    /**
     * Mueve el jugador delantero a la posición actual del mouse (en coordenadas de mundo)
     * y envía la nueva posición al servidor.
     *
     * @param e evento del mouse con las coordenadas locales del panel de cancha
     */
    private void moverJugadorDelantero(MouseEvent e) {
        if (jugadorDelantero == null || !estaEnJuego()) {
            return;
        }
        jugadorDelantero.setX(panelCancha.localAMundoX(e.getX()));
        jugadorDelantero.setY(panelCancha.localAMundoY(e.getY()));
        controlador.enviarPosicion(jugadorDelantero);
        panelCancha.repaint();
    }

    /**
     * Crea una etiqueta de formulario con estilo visual consistente (fuente negrita, texto blanco).
     *
     * @param texto texto que mostrará la etiqueta
     * @return la {@link JLabel} creada y estilizada
     */
    private JLabel crearEtiquetaFormulario(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        return etiqueta;
    }

    /**
     * Crea un botón principal con el estilo visual del juego (fondo azul oscuro, texto blanco).
     *
     * @param texto texto que mostrará el botón
     * @return el {@link JButton} creado y estilizado
     */
    private JButton crearBotonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(new Color(22, 58, 95));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return boton;
    }

    /**
     * Crea un botón de icono con tooltip, usado para acciones secundarias (ej. mostrar reglas).
     *
     * @param icono   icono que se mostrará en el botón
     * @param tooltip texto que aparece al pasar el mouse sobre el botón
     * @return el {@link JButton} creado con el icono y el tooltip
     */
    private JButton crearBotonIcono(ImageIcon icono, String tooltip) {
        JButton boton = new JButton(icono);
        boton.setToolTipText(tooltip);
        boton.setBackground(new Color(36, 104, 84));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return boton;
    }

    /**
     * Muestra el diálogo de reglas del juego usando {@link DialogosPartida}.
     */
    private void mostrarReglas() {
        DialogosPartida.mostrarReglas(this);
    }

    /**
     * Muestra el diálogo de ayuda de controles específicos para el equipo 1
     * (teclas de dirección y barra espaciadora).
     */
    private void mostrarAyudaControles() {
        DialogosPartida.mostrarAyudaControles(this, "Usa las flechas para moverte y la barra espaciadora para golpear.");
    }

    /**
     * Crea y muestra el diálogo de resumen final de la partida.
     * Si ya hay uno abierto, lo cierra antes de abrir el nuevo.
     */
    private void mostrarResumenFinal() {
        cerrarResumenFinal();
        dialogoResumen = DialogosPartida.crearDialogoResumen(this, juego, this::cerrarResumenYPrepararNuevaPartida);
        dialogoResumen.setVisible(true);
    }

    /**
     * Crea un panel de resumen para un equipo, mostrando su nombre, jugadores y puntaje final.
     *
     * @param equipo el equipo cuyos datos se mostrarán
     * @param acento color de acento para el borde del panel (diferencia visualmente los equipos)
     * @return el panel de resumen listo para agregar a un contenedor
     */
    private JPanel crearPanelResumenEquipo(Equipo equipo, Color acento) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(12, 66, 55));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(acento, 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel titulo = new JLabel(equipo.getNombre(), SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        List<Jugador> jugadores = new ArrayList<>(equipo.getJugadores());
        JPanel panelJugadores = new JPanel();
        panelJugadores.setOpaque(false);
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.Y_AXIS));
        if (jugadores.isEmpty()) {
            JLabel vacio = new JLabel("Sin jugadores registrados");
            vacio.setForeground(Color.WHITE);
            vacio.setFont(new Font("SansSerif", Font.BOLD, 15));
            panelJugadores.add(vacio);
        } else {
            for (Jugador jugador : jugadores) {
                if (jugador == null) {
                    continue;
                }
                JPanel fichaJugador = new JPanel(new GridLayout(2, 1, 0, 2));
                fichaJugador.setOpaque(false);
                fichaJugador.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

                JLabel lblNombre = new JLabel(jugador.getNombre());
                lblNombre.setForeground(Color.WHITE);
                lblNombre.setFont(new Font("SansSerif", Font.BOLD, 16));

                JLabel lblPuntajeJugador = new JLabel("Puntaje: " + equipo.getPuntaje());
                lblPuntajeJugador.setForeground(new Color(220, 240, 255));
                lblPuntajeJugador.setFont(new Font("SansSerif", Font.PLAIN, 15));

                fichaJugador.add(lblNombre);
                fichaJugador.add(lblPuntajeJugador);
                panelJugadores.add(fichaJugador);
            }
        }

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        JLabel lblPuntajeEquipo = new JLabel("Puntaje del equipo: " + equipo.getPuntaje(), SwingConstants.CENTER);
        lblPuntajeEquipo.setForeground(new Color(220, 240, 255));
        lblPuntajeEquipo.setFont(new Font("SansSerif", Font.BOLD, 16));
        pie.add(lblPuntajeEquipo, BorderLayout.CENTER);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panelJugadores, BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Crea una tarjeta de resumen con un título y un valor destacado.
     *
     * @param titulo descripción del dato (ej. "Ganador")
     * @param valor  valor a mostrar; si es nulo o vacío se muestra {@code "Sin definir"}
     * @return el {@link JPanel} con la tarjeta estilizada
     */
    private JPanel crearTarjetaResumen(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(new Color(22, 58, 95));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(123, 201, 255), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(210, 234, 255));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lblValor = new JLabel(valor == null || valor.isBlank() ? "Sin definir" : valor, SwingConstants.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Calcula y formatea el tiempo total jugado en la partida actual.
     *
     * @return cadena con el tiempo jugado en formato {@code m:ss}
     */
    private String formatearTiempoTotal() {
        int tiempoJugado = Math.max(0, juego.getDuracionPartidoSegundos() - juego.getTiempoRestante());
        return formatearTiempo(tiempoJugado);
    }

    /**
     * Cierra el diálogo de resumen final si está abierto y libera su referencia.
     */
    private void cerrarResumenFinal() {
        if (dialogoResumen != null) {
            dialogoResumen.dispose();
            dialogoResumen = null;
        }
    }

    /**
     * Cierra el resumen final, rehabilita el botón de listo, solicita al servidor
     * una nueva partida y abre el diálogo de configuración nuevamente.
     */
    private void cerrarResumenYPrepararNuevaPartida() {
        cerrarResumenFinal();
        if (btnListo != null) {
            btnListo.setEnabled(true);
        }
        controlador.solicitarNuevaPartida();
        SwingUtilities.invokeLater(this::mostrarConfiguracion);
    }

    /**
     * Formatea una cantidad de segundos al formato {@code m:ss}.
     *
     * @param segundos tiempo en segundos a formatear
     * @return cadena formateada (ej. {@code "2:05"})
     */
    private String formatearTiempo(int segundos) {
        return TextosPartida.formatearTiempo(segundos);
    }

    /**
     * Indica si el juego se encuentra actualmente en un estado donde los jugadores
     * pueden interactuar (jugando o en punto de oro).
     *
     * @return {@code true} si el estado es {@link EstadoJuego#JUGANDO} o
     *         {@link EstadoJuego#PUNTO_DE_ORO}; {@code false} en caso contrario
     */
    private boolean estaEnJuego() {
        return juego.getEstado() == EstadoJuego.JUGANDO || juego.getEstado() == EstadoJuego.PUNTO_DE_ORO;
    }

    /**
     * Formatea el estado del juego en un texto legible para el jugador.
     *
     * @param estado el estado actual del juego
     * @return texto descriptivo del estado (ej. {@code "En juego"})
     */
    private String formatearEstadoJugador(EstadoJuego estado) {
        return TextosPartida.formatearEstadoJugador(estado);
    }

    /**
     * Formatea el texto del último evento para mostrarlo en la barra inferior.
     *
     * @param evento cadena del último evento registrado en el juego
     * @return texto formateado listo para mostrar al jugador
     */
    private String formatearEventoJugador(String evento) {
        return TextosPartida.formatearEventoJugador(evento);
    }

    /**
     * Limita un valor entero dentro de un rango {@code [min, max]}.
     *
     * @param valor valor a limitar
     * @param min   límite inferior (inclusive)
     * @param max   límite superior (inclusive)
     * @return el valor limitado al rango dado
     */
    private int limitar(int valor, int min, int max) {
        return Math.max(min, Math.min(valor, max));
    }

    // -------------------------------------------------------------------------

    /**
     * Panel personalizado que dibuja la cancha de tenis desde la perspectiva del equipo 1
     * (lado izquierdo), los jugadores del equipo 1 y la pelota cuando está en su mitad.
     *
     * <p>La línea divisoria de la cancha se dibuja en el borde derecho del panel,
     * representando la red desde esta perspectiva. Los márgenes de interpolación están
     * ajustados al lado izquierdo: X local va de 40 a {@code getWidth()-70}.</p>
     */
    private class PanelCancha extends JPanel {

        /**
         * Crea el panel de cancha con el color de fondo del campo de juego.
         */
        PanelCancha() {
            setBackground(new Color(17, 93, 89));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }

        /**
         * Convierte una coordenada X local del panel (píxeles) a coordenada X del mundo.
         *
         * @param xLocal coordenada X en píxeles dentro del panel
         * @return coordenada X equivalente en el espacio del modelo de juego (60–390)
         */
        int localAMundoX(int xLocal) {
            return interpolar(xLocal, 40, getWidth() - 70, MUNDO_X_MIN, MUNDO_X_MAX);
        }

        /**
         * Convierte una coordenada Y local del panel (píxeles) a coordenada Y del mundo.
         *
         * @param yLocal coordenada Y en píxeles dentro del panel
         * @return coordenada Y equivalente en el espacio del modelo de juego (70–630)
         */
        int localAMundoY(int yLocal) {
            return interpolar(yLocal, 40, getHeight() - 50, MUNDO_Y_MIN, MUNDO_Y_MAX);
        }

        /**
         * Convierte una coordenada X del mundo a coordenada X local del panel (píxeles).
         *
         * @param xMundo coordenada X en el espacio del modelo de juego (60–390)
         * @return coordenada X equivalente en píxeles dentro del panel
         */
        int mundoALocalX(int xMundo) {
            return interpolar(xMundo, MUNDO_X_MIN, MUNDO_X_MAX, 40, getWidth() - 70);
        }

        /**
         * Convierte una coordenada Y del mundo a coordenada Y local del panel (píxeles).
         *
         * @param yMundo coordenada Y en el espacio del modelo de juego (70–630)
         * @return coordenada Y equivalente en píxeles dentro del panel
         */
        int mundoALocalY(int yMundo) {
            return interpolar(yMundo, MUNDO_Y_MIN, MUNDO_Y_MAX, 40, getHeight() - 50);
        }

        /**
         * Realiza una interpolación lineal de un valor desde un rango origen a un rango destino,
         * aplicando límites para evitar desbordamientos.
         *
         * @param valor      valor a interpolar
         * @param origenMin  mínimo del rango de origen
         * @param origenMax  máximo del rango de origen
         * @param destinoMin mínimo del rango de destino
         * @param destinoMax máximo del rango de destino
         * @return valor interpolado en el rango destino
         */
        private int interpolar(int valor, int origenMin, int origenMax, int destinoMin, int destinoMax) {
            int valorLimitado = Math.max(origenMin, Math.min(valor, origenMax));
            double proporcion = (double) (valorLimitado - origenMin) / Math.max(1, origenMax - origenMin);
            return destinoMin + (int) Math.round(proporcion * (destinoMax - destinoMin));
        }

        /**
         * Dibuja la cancha de tenis desde la perspectiva del equipo 1, los jugadores
         * del equipo 1 y la pelota (cuando su X es ≤ 400, es decir, en el lado izquierdo).
         *
         * <p>La línea divisoria se dibuja verticalmente en el borde derecho del área
         * de juego ({@code x = getWidth()-40}), representando la red desde esta perspectiva.</p>
         *
         * @param g contexto gráfico proporcionado por Swing
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(31, 143, 103));
            g2.fillRoundRect(20, 30, getWidth() - 60, getHeight() - 60, 24, 24);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(20, 30, getWidth() - 60, getHeight() - 60, 24, 24);
            g2.drawLine(getWidth() - 40, 30, getWidth() - 40, getHeight() - 30);

            List<Jugador> jugadores = new ArrayList<>(juego.getEquipo1().getJugadores());
            for (Jugador jugador : jugadores) {
                dibujarJugador(g2, jugador);
            }

            if (juego.getPelota().getX() <= 400) {
                int xLocal = mundoALocalX(juego.getPelota().getX());
                int yLocal = mundoALocalY(juego.getPelota().getY());
                g2.setColor(new Color(255, 210, 55));
                g2.fillOval(xLocal - 10, yLocal - 10, 20, 20);
            }
        }

        /**
         * Dibuja un jugador individual en la cancha en su posición actual.
         *
         * <p>Renderiza el avatar (imagen), el nombre y el tipo de control
         * debajo de la imagen. Si el jugador es {@code null}, no hace nada.</p>
         *
         * @param g2      contexto gráfico 2D
         * @param jugador jugador a dibujar; puede ser {@code null}
         */
        private void dibujarJugador(Graphics2D g2, Jugador jugador) {
            if (jugador == null) {
                return;
            }
            int xLocal = mundoALocalX(jugador.getX());
            int yLocal = mundoALocalY(jugador.getY());
            BufferedImage avatar = GestorImagenes.obtenerImagenJugador(jugador.getAvatar());
            g2.drawImage(avatar, xLocal - 28, yLocal - 34, 56, 68, null);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString(jugador.getNombre(), xLocal - 28, yLocal + 48);
            g2.drawString(jugador.getTipoControl(), xLocal - 28, yLocal + 61);
        }
    }
}