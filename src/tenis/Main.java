
package tenis;

import tenis.cliente.ClienteEquipo1Main;
import tenis.cliente.ClienteEquipo2Main;
import tenis.lanzador.ServidorLanzador;
import tenis.servidor.ServidorCentralMain;

/**
 * Clase principal del sistema.
 * 
 * Esta clase permite ejecutar el programa en diferentes modos
 * dependiendo del argumento que se pase por consola.
 * 
 * Los modos disponibles son:
 * - servidor / central: inicia el servidor central
 * - equipo1 / cliente1: inicia el cliente del equipo 1
 * - equipo2 / cliente2: inicia el cliente del equipo 2
 * - demo: ejecuta una simulación completa del sistema
 * 
 * Si no se pasan argumentos, se ejecuta el modo "demo" por defecto.
 * 
 * @author Gilary Rugeles
 * @version 1.0
 */
public class Main {

    /**
     * Método principal del programa.
     * 
     * Determina el modo de ejecución según los argumentos
     * y lanza la parte correspondiente del sistema.
     * 
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {

        // Determina el modo (por defecto "demo")
        String modo = args.length == 0 ? "demo" : args[0].toLowerCase();

        switch (modo) {

            // Inicia el servidor central
            case "servidor":
            case "central":
                ServidorCentralMain.main(new String[0]);
                break;

            // Inicia el cliente del equipo 1
            case "equipo1":
            case "cliente1":
                ClienteEquipo1Main.main(new String[0]);
                break;

            // Inicia el cliente del equipo 2
            case "equipo2":
            case "cliente2":
                ClienteEquipo2Main.main(new String[0]);
                break;

            // Modo demo (ejecución completa)
            case "demo":
            default:
                new ServidorLanzador().iniciar();
                break;
        }
    }
}