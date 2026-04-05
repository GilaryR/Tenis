package tenis.juego.vista.pc3;

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
import java.util.*;
import java.util.List;

/**
 * Ventana principal del cliente para el Equipo 2 (lado derecho de la cancha).
 *
 * <p>Esta clase representa la interfaz gráfica completa que usa el equipo 2 durante
 * una partida de tenis. Permite configurar el equipo antes de cada partida,
 * controlar los jugadores durante el juego y visualizar el estado en tiempo real.</p>
 *
 * <p>Responsabilidades principales:
 * <ul>
 *   <li>Mostrar el diálogo de configuración del equipo al inicio o entre partidas.</li>
 *   <li>Controlar el jugador delantero con el mouse y el trasero con las teclas W A S D.</li>
 *   <li>Actualizar la cancha, el marcador y los eventos cada ~16 ms (≈60 FPS).</li>
 *   <li>Detectar cambios de estado del juego y reaccionar (mostrar resumen final, reiniciar, etc.).</li>
 * </ul>
 * </p>
 *
 * <p><b>Controles del equipo 2:</b>
 * <ul>
 *   <li>Mouse (mover): desplaza al jugador delantero.</li>
 *   <li>Clic izquierdo: envía un golpe con el jugador delantero.</li>
 *   <li>Teclas W A S D: mueven al jugador trasero.</li>
 *   <li>Barra espaciadora: envía un golpe con el jugador trasero.</li>
 * </ul>
 * </p>
 *
 * <p><b>Diferencias respecto a {@code PantallaEquipo1}:</b>
 * <ul>
 *   <li>Opera sobre el lado derecho de la cancha (X: 410–740 en coordenadas de mundo).</li>
 *   <li>Usa las teclas W/A/S/D en lugar de las flechas de dirección.</li>
 *   <li>Refleja los datos de {@code juego.getEquipo2()} en lugar de {@code getEquipo1()}.</li>
 *   <li>La pelota solo se dibuja cuando su X es ≥ 400 (mitad derecha de la cancha).</li>
 *   <li>Paleta de colores rojiza en lugar de azul.</li>
 * </ul>
 * </p>
 */
public class PantallaEquipo2 extends JFrame {

    /** Límite izquierdo del área de juego del equipo 2 en coordenadas de mundo. */
    private static final int MUNDO_X_MIN = 410;

    /** Límite derecho del área de juego del equipo 2 en coordenadas de mundo. */
    private static final int MUNDO_X_MAX = 740;

    /** Límite superior del área de juego en coordenadas de mundo. */
    private static final int MUNDO_Y_MIN = 70;

    /** Límite inferior del área de juego en coordenadas de mundo. */
    private static final int MUNDO_Y_MAX = 630;

    /** Modelo central del juego; contiene el estado, los equipos y la pelota. */
    private final Juego juego;

    /** Controlador que gestiona la comunicación con el servidor para este equipo. */
    private final ControladorEquipo controlador;

    /** Jugador delantero del equipo 2, controlado con el mouse. */
    private Jugador jugadorDelantero;

    /**
     * Jugador trasero del equipo 2, controlado con las teclas W A S D.
     * Puede ser {@code null} si el equipo tiene solo un jugador.
     */
    private Jugador jugadorTrasero;

    /** Etiqueta que muestra el puntaje actual del equipo 2. */
    private JLabel lblPuntaje;

    /** Etiqueta que muestra el tiempo restante de la partida. */
    private JLabel lblTiempo;

    /** Etiqueta que muestra el estado actual del juego (ej. "En juego", "Esperando"). */
    private JLabel lblEstado;

    /** Etiqueta que muestra el nombre del equipo 2. */
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
     * Crea la ventana del cliente Equipo 2 y muestra inmediatamente el diálogo de configuración.
     *
     * @param juego          el modelo compartido del juego
     * @param controlador    el controlador que comunica este cliente con el servidor
     * @param numeroEquipo   número identificador del equipo (se espera 2 para esta clase)
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
     * Inicializa y organiza todos los componentes visuales de la ventana principal
     * (cabecera con indicadores, panel de cancha y panel inferior con botones).
     * Usa una paleta de colores rojiza para diferenciar visualmente al equipo 2.
     */
    private void iniciarComponentes() { /* ... */ }

    /**
     * Crea un panel indicador de dos líneas (título + valor) con el estilo visual del equipo 2.
     *
     * @param titulo etiqueta descriptiva del indicador (ej. "Puntaje")
     * @param valor  valor inicial que se mostrará en grande (ej. "0")
     * @return la {@link JLabel} del valor, para poder actualizarla posteriormente
     */
    private JLabel crearIndicador(String titulo, String valor) { /* ... */ }

    /**
     * Muestra el diálogo modal de configuración del equipo 2.
     *
     * <p>Permite ingresar el nombre del equipo, la cantidad de jugadores (1 o 2),
     * los nombres de cada jugador y seleccionar visualmente su avatar.
     * Si el diálogo ya está abierto, este método no hace nada.</p>
     *
     * <p>El nombre por defecto del equipo es {@code "Equipo Rojo"}.</p>
     */
    private void mostrarConfiguracion() { /* ... */ }

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
    private JPanel crearSelectorVisual(String titulo, String[] seleccion) { /* ... */ }

    /**
     * Aplica la configuración del equipo 2 al modelo, registra los jugadores en sus
     * posiciones iniciales del lado derecho y prepara los controles de teclado/mouse.
     *
     * <p>Posiciones iniciales en coordenadas de mundo:
     * <ul>
     *   <li>Jugador delantero: X=650, Y=220</li>
     *   <li>Jugador trasero: X=520, Y=470 (solo si {@code cantidadJugadores == 2})</li>
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
                                  String avatar1, String avatar2, int cantidadJugadores) { /* ... */ }

    /**
     * Registra los listeners de mouse (para el jugador delantero) y las acciones
     * de teclado W/A/S/D y barra espaciadora (para el jugador trasero) sobre el panel de cancha.
     */
    private void configurarControles() { /* ... */ }

    /**
     * Registra una acción de movimiento para el jugador trasero asociada a una tecla W/A/S/D.
     *
     * <p>El movimiento queda limitado al área del equipo 2
     * ({@code MUNDO_X_MIN}–{@code MUNDO_X_MAX}, {@code MUNDO_Y_MIN}–{@code MUNDO_Y_MAX}).</p>
     *
     * @param tecla nombre de la tecla (ej. {@code "W"}, {@code "A"})
     * @param dx    desplazamiento horizontal en unidades de mundo por pulsación
     * @param dy    desplazamiento vertical en unidades de mundo por pulsación
     */
    private void registrarAccionTeclado(String tecla, int dx, int dy) { /* ... */ }

    /**
     * Agrega (o reemplaza) el botón "Aceptar Partida" en el panel inferior.
     * Al pulsarlo, notifica al servidor que este equipo está listo para jugar.
     */
    private void mostrarBotonListo() { /* ... */ }

    /**
     * Inicia el timer de actualización de la UI (≈60 FPS) si no está ya en ejecución.
     *
     * <p>Cada tick actualiza el puntaje, el tiempo, el estado, el nombre del equipo,
     * el texto de evento y repinta la cancha. También llama a
     * {@link #procesarCambioDeEstado()} para detectar transiciones importantes.</p>
     */
    private void iniciarTimer() { /* ... */ }

    /**
     * Detecta transiciones de estado del juego y ejecuta las acciones correspondientes:
     * <ul>
     *   <li>Si el juego acaba de terminar → muestra el resumen final.</li>
     *   <li>Si se regresa al estado de espera → cierra el resumen y abre la configuración de nuevo.</li>
     * </ul>
     */
    private void procesarCambioDeEstado() { /* ... */ }

    /**
     * Mueve el jugador delantero a la posición actual del mouse (en coordenadas de mundo)
     * y envía la nueva posición al servidor.
     *
     * @param e evento del mouse con las coordenadas locales del panel de cancha
     */
    private void moverJugadorDelantero(MouseEvent e) { /* ... */ }

    /**
     * Crea una etiqueta de formulario con estilo visual consistente (fuente negrita, texto blanco).
     *
     * @param texto texto que mostrará la etiqueta
     * @return la {@link JLabel} creada y estilizada
     */
    private JLabel crearEtiquetaFormulario(String texto) { /* ... */ }

    /**
     * Crea un botón principal con el estilo visual del juego (fondo azul oscuro, texto blanco).
     *
     * @param texto texto que mostrará el botón
     * @return el {@link JButton} creado y estilizado
     */
    private JButton crearBotonPrincipal(String texto) { /* ... */ }

    /**
     * Crea un botón de icono con tooltip, usado para acciones secundarias (ej. mostrar reglas).
     *
     * @param icono   icono que se mostrará en el botón
     * @param tooltip texto que aparece al pasar el mouse sobre el botón
     * @return el {@link JButton} creado con el icono y el tooltip
     */
    private JButton crearBotonIcono(ImageIcon icono, String tooltip) { /* ... */ }

    /**
     * Muestra el diálogo de reglas del juego usando {@link DialogosPartida}.
     */
    private void mostrarReglas() { /* ... */ }

    /**
     * Muestra el diálogo de ayuda de controles específicos para el equipo 2
     * (teclas W A S D y barra espaciadora).
     */
    private void mostrarAyudaControles() { /* ... */ }

    /**
     * Crea y muestra el diálogo de resumen final de la partida.
     * Si ya hay uno abierto, lo cierra antes de abrir el nuevo.
     */
    private void mostrarResumenFinal() { /* ... */ }

    /**
     * Crea un panel de resumen para un equipo, mostrando su nombre, jugadores y puntaje final.
     *
     * @param equipo el equipo cuyos datos se mostrarán
     * @param acento color de acento para el borde del panel (diferencia visualmente los equipos)
     * @return el panel de resumen listo para agregar a un contenedor
     */
    private JPanel crearPanelResumenEquipo(Equipo equipo, Color acento) { /* ... */ }

    /**
     * Crea una tarjeta de resumen con un título y un valor destacado.
     *
     * @param titulo descripción del dato (ej. "Ganador")
     * @param valor  valor a mostrar; si es nulo o vacío se muestra {@code "Sin definir"}
     * @return el {@link JPanel} con la tarjeta estilizada
     */
    private JPanel crearTarjetaResumen(String titulo, String valor) { /* ... */ }

    /**
     * Calcula y formatea el tiempo total jugado en la partida actual.
     *
     * @return cadena con el tiempo jugado en formato {@code m:ss}
     */
    private String formatearTiempoTotal() { /* ... */ }

    /**
     * Cierra el diálogo de resumen final si está abierto y libera su referencia.
     */
    private void cerrarResumenFinal() { /* ... */ }

    /**
     * Cierra el resumen final, rehabilita el botón de listo, solicita al servidor
     * una nueva partida y abre el diálogo de configuración nuevamente.
     */
    private void cerrarResumenYPrepararNuevaPartida() { /* ... */ }

    /**
     * Formatea una cantidad de segundos al formato {@code m:ss}.
     *
     * @param segundos tiempo en segundos a formatear
     * @return cadena formateada (ej. {@code "2:05"})
     */
    private String formatearTiempo(int segundos) { /* ... */ }

    /**
     * Indica si el juego se encuentra actualmente en un estado donde los jugadores
     * pueden interactuar (jugando o en punto de oro).
     *
     * @return {@code true} si el estado es {@link EstadoJuego#JUGANDO} o
     *         {@link EstadoJuego#PUNTO_DE_ORO}; {@code false} en caso contrario
     */
    private boolean estaEnJuego() { /* ... */ }

    /**
     * Formatea el estado del juego en un texto legible para el jugador.
     *
     * @param estado el estado actual del juego
     * @return texto descriptivo del estado (ej. {@code "En juego"})
     */
    private String formatearEstadoJugador(EstadoJuego estado) { /* ... */ }

    /**
     * Formatea el texto del último evento para mostrarlo en la barra inferior.
     *
     * @param evento cadena del último evento registrado en el juego
     * @return texto formateado listo para mostrar al jugador
     */
    private String formatearEventoJugador(String evento) { /* ... */ }

    /**
     * Limita un valor entero dentro de un rango {@code [min, max]}.
     *
     * @param valor valor a limitar
     * @param min   límite inferior (inclusive)
     * @param max   límite superior (inclusive)
     * @return el valor limitado al rango dado
     */
    private int limitar(int valor, int min, int max) { /* ... */ }

    // -------------------------------------------------------------------------

    /**
     * Panel personalizado que dibuja la cancha de tenis desde la perspectiva del equipo 2
     * (lado derecho), los jugadores del equipo 2 y la pelota cuando está en su mitad.
     *
     * <p>La línea divisoria de la cancha se dibuja en el borde izquierdo del panel,
     * a diferencia de {@code PantallaEquipo1} donde se dibuja en el derecho.
     * Los márgenes de interpolación están ajustados al lado derecho:
     * X local va de 70 a {@code getWidth()-40} (en lugar de 40 a {@code getWidth()-70}).</p>
     */
    private class PanelCancha extends JPanel {

        /**
         * Crea el panel de cancha con el color de fondo del campo de juego.
         */
        PanelCancha() { /* ... */ }

        /**
         * Convierte una coordenada X local del panel (píxeles) a coordenada X del mundo.
         *
         * @param xLocal coordenada X en píxeles dentro del panel
         * @return coordenada X equivalente en el espacio del modelo de juego (410–740)
         */
        int localAMundoX(int xLocal) { /* ... */ }

        /**
         * Convierte una coordenada Y local del panel (píxeles) a coordenada Y del mundo.
         *
         * @param yLocal coordenada Y en píxeles dentro del panel
         * @return coordenada Y equivalente en el espacio del modelo de juego (70–630)
         */
        int localAMundoY(int yLocal) { /* ... */ }

        /**
         * Convierte una coordenada X del mundo a coordenada X local del panel (píxeles).
         *
         * @param xMundo coordenada X en el espacio del modelo de juego (410–740)
         * @return coordenada X equivalente en píxeles dentro del panel
         */
        int mundoALocalX(int xMundo) { /* ... */ }

        /**
         * Convierte una coordenada Y del mundo a coordenada Y local del panel (píxeles).
         *
         * @param yMundo coordenada Y en el espacio del modelo de juego (70–630)
         * @return coordenada Y equivalente en píxeles dentro del panel
         */
        int mundoALocalY(int yMundo) { /* ... */ }

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
        private int interpolar(int valor, int origenMin, int origenMax, int destinoMin, int destinoMax) { /* ... */ }

        /**
         * Dibuja la cancha de tenis desde la perspectiva del equipo 2, los jugadores
         * del equipo 2 y la pelota (cuando su X es ≥ 400, es decir, en el lado derecho).
         *
         * <p>La línea divisoria se dibuja verticalmente en el borde izquierdo del área
         * de juego ({@code x=40}), representando la red desde esta perspectiva.</p>
         *
         * @param g contexto gráfico proporcionado por Swing
         */
        @Override
        protected void paintComponent(Graphics g) { /* ... */ }

        /**
         * Dibuja un jugador individual en la cancha en su posición actual.
         *
         * <p>Renderiza el avatar (imagen), el nombre y el tipo de control
         * debajo de la imagen. Si el jugador es {@code null}, no hace nada.</p>
         *
         * @param g2      contexto gráfico 2D
         * @param jugador jugador a dibujar; puede ser {@code null}
         */
        private void dibujarJugador(Graphics2D g2, Jugador jugador) { /* ... */ }
    }
}