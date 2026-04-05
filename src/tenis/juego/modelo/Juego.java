package tenis.juego.modelo;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

public class Juego implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int PUNTAJE_OBJETIVO = 10;
    private static final int DIFERENCIA_MINIMA = 2;
    private static final int CENTRO_CANCHA_X = 400;
    private static final int RADIO_COLISION_JUGADOR = 34;
    private static final long ENFRIAMIENTO_GOLPE_MS = 180;

    private final Equipo equipo1;
    private final Equipo equipo2;
    private final Pelota pelota;
    private EstadoJuego estado;
    private int tiempoRestante;
    private transient Timer temporizador;
    private String ganador;
    private int duracionPartidoSegundos;
    private String ultimoEvento;
    private boolean puntoDeOroActivo;
    private boolean saquePendiente;
    private String equipoSacador;
    private int zonaSacador;
    private String ultimoGolpeClave;
    private long ultimoGolpeTiempoMs;

    public Juego(String nombreEquipo1, String nombreEquipo2) {
        this(nombreEquipo1, nombreEquipo2, 180);
    }

    public Juego(String nombreEquipo1, String nombreEquipo2, int duracionSegundos) {
        equipo1 = new Equipo(nombreEquipo1);
        equipo2 = new Equipo(nombreEquipo2);
        pelota = new Pelota();
        duracionPartidoSegundos = duracionSegundos;
        tiempoRestante = duracionSegundos;
        estado = EstadoJuego.ESPERANDO_EQUIPOS;
        ganador = "";
        ultimoEvento = "Configura ambos equipos para comenzar.";
        saquePendiente = false;
        equipoSacador = "EQUIPO1";
        zonaSacador = 1;
        ultimoGolpeClave = "";
        ultimoGolpeTiempoMs = 0L;
    }

    public synchronized void sincronizarDesde(Juego origen) {
        equipo1.actualizarDesde(origen.equipo1);
        equipo2.actualizarDesde(origen.equipo2);
        pelota.copiarDesde(origen.pelota);
        estado = origen.estado;
        tiempoRestante = origen.tiempoRestante;
        ganador = origen.ganador;
        duracionPartidoSegundos = origen.duracionPartidoSegundos;
        ultimoEvento = origen.ultimoEvento;
        puntoDeOroActivo = origen.puntoDeOroActivo;
        saquePendiente = origen.saquePendiente;
        equipoSacador = origen.equipoSacador;
        zonaSacador = origen.zonaSacador;
        ultimoGolpeClave = origen.ultimoGolpeClave;
        ultimoGolpeTiempoMs = origen.ultimoGolpeTiempoMs;
    }

    public synchronized Equipo getEquipo1() {
        return equipo1;
    }

    public synchronized Equipo getEquipo2() {
        return equipo2;
    }

    public synchronized Pelota getPelota() {
        return pelota;
    }

    public synchronized EstadoJuego getEstado() {
        return estado;
    }

    public synchronized void setEstado(EstadoJuego estado) {
        this.estado = estado;
    }

    public synchronized int getTiempoRestante() {
        return tiempoRestante;
    }

    public synchronized void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public synchronized String getGanador() {
        return ganador;
    }

    public synchronized int getDuracionPartidoSegundos() {
        return duracionPartidoSegundos;
    }

    public synchronized int getDuracionPartidoMinutos() {
        return duracionPartidoSegundos / 60;
    }

    public synchronized String getUltimoEvento() {
        return ultimoEvento;
    }

    public synchronized boolean isPuntoDeOroActivo() {
        return puntoDeOroActivo;
    }

    public synchronized boolean isSaquePendiente() {
        return saquePendiente;
    }

    public synchronized String getEquipoSacador() {
        return equipoSacador;
    }

    public synchronized int getZonaSacador() {
        return zonaSacador;
    }

    public synchronized Equipo getEquipoPorId(int id) {
        return id == 1 ? equipo1 : equipo2;
    }

    public synchronized void configurarDuracionMinutos(int minutos) {
        if (estado == EstadoJuego.JUGANDO || minutos < 3 || minutos > 5) {
            return;
        }
        duracionPartidoSegundos = minutos * 60;
        tiempoRestante = duracionPartidoSegundos;
        ultimoEvento = "Duracion configurada en " + minutos + " minutos.";
    }

    public synchronized void marcarEquipoListo(int numeroEquipo, boolean listo) {
        getEquipoPorId(numeroEquipo).setListo(listo);
        actualizarEstadoDeEspera();
    }

    public synchronized void actualizarEstadoDeEspera() {
        if (estado == EstadoJuego.JUGANDO || estado == EstadoJuego.PUNTO_DE_ORO) {
            return;
        }
        if (equipo1.isListo() && equipo2.isListo()) {
            estado = EstadoJuego.LISTO_PARA_INICIAR;
            ultimoEvento = "Ambos equipos estan listos. El servidor puede iniciar el partido.";
        } else {
            estado = EstadoJuego.ESPERANDO_EQUIPOS;
            ultimoEvento = "Esperando a que ambos equipos confirmen la configuracion.";
        }
    }

    public synchronized boolean puedeIniciar() {
        return equipo1.isListo() && equipo2.isListo() && estado == EstadoJuego.LISTO_PARA_INICIAR;
    }

    public synchronized void iniciarJuego() {
        if (!puedeIniciar()) {
            return;
        }
        puntoDeOroActivo = false;
        ganador = "";
        tiempoRestante = duracionPartidoSegundos;
        estado = EstadoJuego.JUGANDO;
        ultimoEvento = "El partido ha comenzado.";
        prepararSaque("EQUIPO1");
        iniciarTemporizador();
    }

    private void iniciarTemporizador() {
        detenerTemporizador();
        temporizador = new Timer();
        temporizador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (Juego.this) {
                    if (estado != EstadoJuego.JUGANDO || puntoDeOroActivo) {
                        return;
                    }
                    if (tiempoRestante > 0) {
                        tiempoRestante--;
                    }
                    if (tiempoRestante == 0) {
                        if (equipo1.getPuntaje() == equipo2.getPuntaje()) {
                            puntoDeOroActivo = true;
                            estado = EstadoJuego.PUNTO_DE_ORO;
                            ultimoEvento = "Tiempo agotado. Se activa el punto de oro.";
                            prepararSaque("EQUIPO1");
                        } else {
                            ganador = equipo1.getPuntaje() > equipo2.getPuntaje() ? equipo1.getNombre() : equipo2.getNombre();
                            ultimoEvento = "Tiempo agotado. Gana " + ganador + ".";
                            finalizarJuego();
                        }
                    }
                }
            }
        }, 1000, 1000);
    }

    public synchronized void actualizarFisica(int limiteSuperior, int limiteInferior, int limiteIzquierdo, int limiteDerecho, int posicionMalla) {
        if (estado != EstadoJuego.JUGANDO && estado != EstadoJuego.PUNTO_DE_ORO) {
            return;
        }

        if (saquePendiente) {
            posicionarPelotaEnSacador();
            return;
        }

        int xAnterior = pelota.getX();
        int yAnterior = pelota.getY();
        pelota.mover();

        if (pelota.getY() <= limiteSuperior) {
            pelota.setY(limiteSuperior);
            pelota.invertirDireccionY();
            ultimoEvento = "La pelota reboto en el limite superior.";
        } else if (pelota.getY() >= limiteInferior) {
            pelota.setY(limiteInferior);
            pelota.invertirDireccionY();
            ultimoEvento = "La pelota reboto en el limite inferior.";
        }

        if (tocaMalla(xAnterior, yAnterior, posicionMalla)) {
            Equipo rival = pelota.getVelocidadX() > 0 ? equipo2 : equipo1;
            registrarPunto(rival, "La pelota golpeo la malla.");
            return;
        }

        if (procesarColisionConJugadores()) {
            return;
        }

        if (pelota.getX() <= limiteIzquierdo) {
            registrarPuntoPorSalida("EQUIPO2", "La pelota salio por el lado del Equipo 1.");
        } else if (pelota.getX() >= limiteDerecho) {
            registrarPuntoPorSalida("EQUIPO1", "La pelota salio por el lado del Equipo 2.");
        }
    }

    private boolean tocaMalla(int xAnterior, int yAnterior, int posicionMalla) {
        int centroCanchaY = 350;
        int alturaVisibleMalla = 90;
        boolean cruzoDesdeIzquierda = xAnterior < posicionMalla && pelota.getX() >= posicionMalla;
        boolean cruzoDesdeDerecha = xAnterior > posicionMalla && pelota.getX() <= posicionMalla;
        boolean cruzoPlanoMalla = cruzoDesdeIzquierda || cruzoDesdeDerecha;
        boolean enAlturaDeMallaAntes = Math.abs(yAnterior - centroCanchaY) <= alturaVisibleMalla / 2;
        boolean enAlturaDeMallaAhora = Math.abs(pelota.getY() - centroCanchaY) <= alturaVisibleMalla / 2;
        return cruzoPlanoMalla && (enAlturaDeMallaAntes || enAlturaDeMallaAhora);
    }

    public synchronized boolean verificarGolpe(String idEquipo, Jugador jugador) {
        if (estado != EstadoJuego.JUGANDO && estado != EstadoJuego.PUNTO_DE_ORO) {
            return false;
        }

        if (saquePendiente) {
            return idEquipo.equals(equipoSacador) && jugador.getZona() == zonaSacador;
        }

        boolean pelotaEnLadoCorrecto = "EQUIPO1".equals(idEquipo) ? pelota.getX() <= 400 : pelota.getX() >= 400;
        if (!pelotaEnLadoCorrecto) {
            return false;
        }

        int distanciaX = Math.abs(pelota.getX() - jugador.getX());
        int distanciaY = Math.abs(pelota.getY() - jugador.getY());
        return distanciaX < 55 && distanciaY < 55;
    }

    public synchronized void golpearPelota(String idEquipo, Jugador jugador, int direccionX) {
        if (!verificarGolpe(idEquipo, jugador)) {
            return;
        }

        aplicarGolpe(idEquipo, jugador, direccionX, true);
        ultimoEvento = jugador.getNombre() + " devolvio la pelota para " + idEquipo + ".";
    }

    private void aplicarGolpe(String idEquipo, Jugador jugador, int direccionX, boolean golpeManual) {
        saquePendiente = false;
        int yPelotaAntesDelGolpe = pelota.getY();
        int desplazamientoSalida = direccionX > 0 ? RADIO_COLISION_JUGADOR + 14 : -RADIO_COLISION_JUGADOR - 14;
        pelota.setX(jugador.getX() + desplazamientoSalida);
        pelota.setY(jugador.getY());
        pelota.setVelocidadX(direccionX);

        int deltaY = yPelotaAntesDelGolpe - jugador.getY();
        int nuevaVelocidadY = Math.max(-6, Math.min(6, deltaY / 5));
        if (nuevaVelocidadY == 0) {
            nuevaVelocidadY = jugador.getZona() == 1 ? -2 : 2;
        }
        if (golpeManual) {
            nuevaVelocidadY *= 2;
        }
        pelota.setVelocidadY(nuevaVelocidadY);
        pelota.setEnJuego(true);
        if (Math.abs(pelota.getVelocidadX()) < 13) {
            pelota.aumentarVelocidad();
        }

        ultimoGolpeClave = construirClaveJugador(idEquipo, jugador);
        ultimoGolpeTiempoMs = System.currentTimeMillis();
    }

    private boolean procesarColisionConJugadores() {
        for (Jugador jugador : equipo1.getJugadores()) {
            if (hayColisionConJugador("EQUIPO1", jugador)) {
                aplicarGolpe("EQUIPO1", jugador, 12, false);
                ultimoEvento = jugador.getNombre() + " devolvio la pelota para EQUIPO1.";
                return true;
            }
        }
        for (Jugador jugador : equipo2.getJugadores()) {
            if (hayColisionConJugador("EQUIPO2", jugador)) {
                aplicarGolpe("EQUIPO2", jugador, -12, false);
                ultimoEvento = jugador.getNombre() + " devolvio la pelota para EQUIPO2.";
                return true;
            }
        }
        return false;
    }

    private boolean hayColisionConJugador(String idEquipo, Jugador jugador) {
        if ("EQUIPO1".equals(idEquipo) && pelota.getVelocidadX() >= 0) {
            return false;
        }
        if ("EQUIPO2".equals(idEquipo) && pelota.getVelocidadX() <= 0) {
            return false;
        }

        String clave = construirClaveJugador(idEquipo, jugador);
        if (clave.equals(ultimoGolpeClave) && System.currentTimeMillis() - ultimoGolpeTiempoMs < ENFRIAMIENTO_GOLPE_MS) {
            return false;
        }

        int dx = pelota.getX() - jugador.getX();
        int dy = pelota.getY() - jugador.getY();
        return dx * dx + dy * dy <= RADIO_COLISION_JUGADOR * RADIO_COLISION_JUGADOR;
    }

    private String construirClaveJugador(String idEquipo, Jugador jugador) {
        return idEquipo + ":" + jugador.getZona() + ":" + jugador.getNombre();
    }

    private void prepararSaque(String idEquipoSacador) {
        equipoSacador = idEquipoSacador;
        zonaSacador = seleccionarZonaSacador(obtenerEquipoPorCodigo(idEquipoSacador));
        saquePendiente = true;
        pelota.reiniciar();
        posicionarPelotaEnSacador();
        ultimoGolpeClave = "";
        ultimoGolpeTiempoMs = 0L;
        ultimoEvento = "Saque para " + obtenerEquipoPorCodigo(idEquipoSacador).getNombre() + ".";
    }

    private int seleccionarZonaSacador(Equipo equipoSacadorActual) {
        if (equipoSacadorActual.getCantidadJugadores() <= 1) {
            return 1;
        }
        return Math.random() > 0.5 ? 1 : 2;
    }

    private Equipo obtenerEquipoPorCodigo(String idEquipo) {
        return "EQUIPO1".equals(idEquipo) ? equipo1 : equipo2;
    }

    private void posicionarPelotaEnSacador() {
        Jugador sacador = obtenerEquipoPorCodigo(equipoSacador).obtenerJugadorEnZona(zonaSacador);
        if (sacador == null) {
            return;
        }
        int offset = "EQUIPO1".equals(equipoSacador) ? 28 : -28;
        pelota.setX(sacador.getX() + offset);
        pelota.setY(sacador.getY());
        pelota.setVelocidadX(0);
        pelota.setVelocidadY(0);
        pelota.setEnJuego(false);
    }

    private void registrarPunto(Equipo equipoQueAnota, String razon) {
        equipoQueAnota.sumarPunto();
        ultimoEvento = razon + " Punto para " + equipoQueAnota.getNombre() + ".";

        if (resolverFinDePunto(equipoQueAnota, "Punto de oro para %s.", "Victoria por diferencia minima de 2 para %s.")) {
            return;
        }
        prepararSaque(equipoQueAnota == equipo1 ? "EQUIPO1" : "EQUIPO2");
    }

    private void registrarPuntoPorSalida(String idEquipoSacador, String razon) {
        Equipo equipoQueAnota = obtenerEquipoPorCodigo(idEquipoSacador);
        equipoQueAnota.sumarPunto();
        ultimoEvento = razon + " Punto para " + equipoQueAnota.getNombre() + ".";

        if (resolverFinDePunto(equipoQueAnota, null, null)) {
            return;
        }
        prepararSaque(idEquipoSacador);
    }

    private boolean resolverFinDePunto(Equipo equipoQueAnota, String mensajePuntoDeOro, String mensajeVictoria) {
        if (puntoDeOroActivo) {
            ganador = equipoQueAnota.getNombre();
            if (mensajePuntoDeOro != null) {
                ultimoEvento = String.format(mensajePuntoDeOro, ganador);
            }
            finalizarJuego();
            return true;
        }

        if (equipoQueAnota.getPuntaje() >= PUNTAJE_OBJETIVO) {
            int diferencia = Math.abs(equipo1.getPuntaje() - equipo2.getPuntaje());
            if (diferencia >= DIFERENCIA_MINIMA) {
                ganador = equipoQueAnota.getNombre();
                if (mensajeVictoria != null) {
                    ultimoEvento = String.format(mensajeVictoria, ganador);
                }
                finalizarJuego();
                return true;
            }
        }
        return false;
    }

    public synchronized void finalizarJuego() {
        estado = EstadoJuego.TERMINADO;
        detenerTemporizador();
        pelota.detener();
    }

    private void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.cancel();
            temporizador = null;
        }
    }

    public synchronized void reiniciarJuego() {
        detenerTemporizador();
        equipo1.reiniciarPuntaje();
        equipo2.reiniciarPuntaje();
        equipo1.setListo(false);
        equipo2.setListo(false);
        pelota.reiniciar();
        tiempoRestante = duracionPartidoSegundos;
        ganador = "";
        puntoDeOroActivo = false;
        estado = EstadoJuego.ESPERANDO_EQUIPOS;
        saquePendiente = false;
        equipoSacador = "EQUIPO1";
        zonaSacador = 1;
        ultimoGolpeClave = "";
        ultimoGolpeTiempoMs = 0L;
        ultimoEvento = "Partido reiniciado. Esperando equipos.";
    }
}
