package tenis.juego.vista.comun;

import tenis.juego.modelo.Equipo;
import tenis.juego.modelo.Juego;
import tenis.juego.modelo.Jugador;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria encargada de crear y mostrar todos los diálogos
 * relacionados con la partida.
 * 
 * Incluye:
 * - Reglas del juego
 * - Ayuda de controles
 * - Resumen final de la partida
 * 
 * Es final y con constructor privado porque solo usa métodos estáticos.
 */
public final class DialogosPartida {

    /**
     * Constructor privado para evitar instanciación.
     */
    private DialogosPartida() {
    }

    /**
     * Muestra un cuadro de diálogo con las reglas del juego.
     */
    public static void mostrarReglas(JFrame parent) {
        JOptionPane.showMessageDialog(
                parent,
                TextosPartida.reglas(),
                "Informacion",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Muestra una ventana con instrucciones de cómo jugar.
     * 
     * @param parent ventana padre
     * @param textoControlesTeclado instrucciones del jugador con teclado
     */
    public static void mostrarAyudaControles(JFrame parent, String textoControlesTeclado) {

        JDialog ayuda = new JDialog(parent, "Como jugar", true);
        ayuda.setSize(420, 260);
        ayuda.setLayout(new BorderLayout(10, 10));
        ayuda.setLocationRelativeTo(parent);

        // Fondo
        ayuda.getContentPane().setBackground(new Color(22, 92, 78));

        // Título
        JLabel titulo = new JLabel("Como jugar", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        // Contenido en HTML
        String texto = "<html><div style='padding:8px;font-family:sans-serif;'>"
                + "<b>Jugador de mouse</b><br>"
                + "Mueve el mouse para desplazarte y haz clic para golpear.<br><br>"
                + "<b>Jugador de teclado</b><br>"
                + textoControlesTeclado
                + "</div></html>";

        JLabel contenido = new JLabel(texto);
        contenido.setForeground(Color.WHITE);

        // Botón cerrar
        JButton btnCerrar = crearBotonPrincipal("Cerrar");
        btnCerrar.addActionListener(e -> ayuda.dispose());

        ayuda.add(titulo, BorderLayout.NORTH);
        ayuda.add(contenido, BorderLayout.CENTER);
        ayuda.add(btnCerrar, BorderLayout.SOUTH);

        ayuda.setVisible(true);
    }

    /**
     * Crea el diálogo de resumen final del partido.
     * 
     * @param parent ventana padre
     * @param juego  datos del juego
     * @param alCerrar acción a ejecutar al cerrar
     * @return diálogo configurado
     */
    public static JDialog crearDialogoResumen(JFrame parent, Juego juego, Runnable alCerrar) {

        JDialog dialogoResumen = new JDialog(parent, "Resumen final", false);
        dialogoResumen.setSize(640, 470);
        dialogoResumen.setLocationRelativeTo(parent);
        dialogoResumen.setLayout(new BorderLayout(12, 12));
        dialogoResumen.getContentPane().setBackground(new Color(17, 89, 74));

        // -------- TÍTULO --------
        JLabel titulo = new JLabel("Resumen de la partida", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(16, 12, 0, 12));

        // -------- EQUIPOS --------
        JPanel panelEquipos = new JPanel(new GridLayout(1, 2, 12, 0));
        panelEquipos.setOpaque(false);
        panelEquipos.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        panelEquipos.add(crearPanelResumenEquipo(juego.getEquipo1(), new Color(34, 111, 196)));
        panelEquipos.add(crearPanelResumenEquipo(juego.getEquipo2(), new Color(34, 111, 196)));

        // -------- PIE (DATOS GENERALES) --------
        JPanel pie = new JPanel(new GridLayout(1, 2, 12, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        pie.add(crearTarjetaResumen(
                "Tiempo total",
                TextosPartida.formatearTiempo(
                        Math.max(0, juego.getDuracionPartidoSegundos() - juego.getTiempoRestante())
                )
        ));

        pie.add(crearTarjetaResumen("Ganador", juego.getGanador()));

        // -------- BOTÓN CERRAR --------
        JButton btnCerrar = crearBotonPrincipal("Cerrar");
        btnCerrar.addActionListener(e -> alCerrar.run());

        JPanel panelCerrar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelCerrar.setOpaque(false);
        panelCerrar.add(btnCerrar);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(panelEquipos, BorderLayout.CENTER);
        centro.add(panelCerrar, BorderLayout.SOUTH);

        // -------- AGREGAR TODO --------
        dialogoResumen.add(titulo, BorderLayout.NORTH);
        dialogoResumen.add(centro, BorderLayout.CENTER);
        dialogoResumen.add(pie, BorderLayout.SOUTH);

        return dialogoResumen;
    }

    /**
     * Crea el panel visual de un equipo en el resumen.
     */
    private static JPanel crearPanelResumenEquipo(Equipo equipo, Color acento) {

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(12, 66, 55));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(acento, 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        // Nombre del equipo
        JLabel titulo = new JLabel(equipo.getNombre(), SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));

        List<Jugador> jugadores = new ArrayList<>(equipo.getJugadores());

        JPanel panelJugadores = new JPanel();
        panelJugadores.setOpaque(false);
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.Y_AXIS));

        // Si no hay jugadores
        if (jugadores.isEmpty()) {
            JLabel vacio = new JLabel("Sin jugadores registrados");
            vacio.setForeground(Color.WHITE);
            vacio.setFont(new Font("SansSerif", Font.BOLD, 15));
            panelJugadores.add(vacio);
        } else {
            for (Jugador jugador : jugadores) {
                if (jugador == null) continue;

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

        JLabel lblPuntajeEquipo = new JLabel(
                "Puntaje del equipo: " + equipo.getPuntaje(),
                SwingConstants.CENTER
        );
        lblPuntajeEquipo.setForeground(new Color(220, 240, 255));
        lblPuntajeEquipo.setFont(new Font("SansSerif", Font.BOLD, 16));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panelJugadores, BorderLayout.CENTER);
        panel.add(lblPuntajeEquipo, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea una tarjeta con información resumen (ej: tiempo, ganador).
     */
    private static JPanel crearTarjetaResumen(String titulo, String valor) {

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(new Color(22, 58, 95));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(123, 201, 255), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setForeground(new Color(210, 234, 255));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel lblValor = new JLabel(
                (valor == null || valor.isBlank()) ? "Sin definir" : valor,
                SwingConstants.CENTER
        );
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 18));

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea un botón con estilo personalizado.
     */
    private static JButton crearBotonPrincipal(String texto) {

        JButton boton = new JButton(texto);
        boton.setBackground(new Color(22, 58, 95));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        return boton;
    }
}