# TenisMultijugador

Proyecto de tenis multijugador desarrollado en Java con interfaz Swing y comunicacion por red usando UDP. El sistema permite ejecutar una partida con tres ventanas o equipos:

- `Servidor central`: coordina el estado general del juego.
- `Equipo 1`: cliente del primer equipo.
- `Equipo 2`: cliente del segundo equipo.

Tambien incluye un modo `demo` que levanta las tres pantallas en una misma maquina para pruebas locales.

## Tecnologias utilizadas

- Java 17
- Java Swing
- UDP sockets
- Apache Ant / NetBeans

## Estructura del proyecto

```text
TenisMultijugador/
├─ src/
│  ├─ tenis/
│  │  ├─ cliente/
│  │  ├─ servidor/
│  │  ├─ lanzador/
│  │  └─ juego/
│  └─ recursos/
├─ dist/
│  └─ TenisMultijugador.jar
├─ build.xml
└─ nbproject/
```

## Requisitos

- Tener instalado `Java 17` o superior.
- Para compilar desde el proyecto, tener disponible `Ant` o abrir el proyecto en `NetBeans`.

## Ejecucion rapida

### Opcion 1: usar el JAR generado

Desde la carpeta `dist`:

```bash
java -jar TenisMultijugador.jar
```

Ese comando ejecuta el modo `demo`, que abre las tres pantallas en el mismo equipo.

### Opcion 2: ejecutar por modos

La clase principal del proyecto es `tenis.Main` y acepta estos modos:

- `demo`
- `servidor` o `central`
- `equipo1` o `cliente1`
- `equipo2` o `cliente2`

Ejemplos:

```bash
java -cp build\classes tenis.Main demo
java -cp build\classes tenis.Main servidor
java -cp build\classes tenis.Main equipo1
java -cp build\classes tenis.Main equipo2
```

## Configuracion de red

La clase `ConfiguracionRed` define IPs y puertos por defecto:

- Puerto central: `5000`
- Puerto equipo 1: `5001`
- Puerto equipo 2: `5002`

Las IPs y puertos pueden sobrescribirse con propiedades del sistema al iniciar la aplicacion:

```bash
java -Dtenis.ip.central=192.168.1.10 -Dtenis.puerto.central=5000 -jar dist\TenisMultijugador.jar
```

Propiedades disponibles:

- `tenis.ip.central`
- `tenis.ip.equipo1`
- `tenis.ip.equipo2`
- `tenis.puerto.central`
- `tenis.puerto.equipo1`
- `tenis.puerto.equipo2`

## Como correrlo en varias computadoras

1. Ejecuta el servidor central en la computadora principal.
2. Configura la IP del servidor central en los clientes.
3. Ejecuta `equipo1` en la segunda computadora.
4. Ejecuta `equipo2` en la tercera computadora.
5. Verifica que los puertos `5000`, `5001` y `5002` esten permitidos por el firewall si hay problemas de conexion.

## Recursos del proyecto

El juego incluye recursos graficos y de sonido en:

- `src/recursos/imagenes`
- `src/recursos/sonidos`

## Compilacion

Si usas Ant:

```bash
ant clean
ant jar
```

El archivo generado quedara en:

```text
dist/TenisMultijugador.jar
```

## Notas

- El proyecto esta preparado para abrir tres interfaces distintas: una central y dos de equipos.
- El modo `demo` es la forma mas practica de probar el juego localmente.
- La configuracion actual contiene IPs fijas por defecto, por lo que conviene ajustarlas antes de ejecutar el juego en red real.
