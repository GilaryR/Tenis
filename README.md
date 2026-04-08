🎾 Tenis Multijugador (UDP)

Descripción del juego
Tenis Multijugador es un videojuego desarrollado en Java que simula un partido de tenis entre dos equipos en tiempo real. Cada jugador controla su lado de la cancha, interactuando con la pelota mediante movimientos y acciones sincronizadas a través de una red.El juego utiliza comunicación UDP.


🔗 Enlace código fuente



🎮 Reglas del juego
1. Si la pelota toca la malla, el punto es para el rival.
2. Se gana el juego al alcanzar 10 puntos con una diferencia mínima de 2
3. Si el tiempo termina en empate, se juega un punto de oro.
4. Cada equipo debe evitar que la pelota caiga en su lado de la cancha.
5. El saque inicial se asigna aleatoriamente.


🏗️ Arquitectura del sistema

El sistema sigue una arquitectura por capas, separando responsabilidades:

1. Modelo (Modelo del dominio)

Contiene las clases principales del juego:

Juego
Jugador
Equipo
Pelota
EstadoJuego
DatosEquipo

👉 Maneja la lógica del estado del juego.

2. Controlador

Coordina la lógica del juego:

ControladorCentral
ControladorEquipo
ControladorJuegoBase

👉 Se encarga de:

Procesar entradas
Actualizar el modelo
Coordinar la comunicación

3. Comunicación (Red)

Encargada del envío y recepción de datos:

EmisorUDP
ReceptorUDP
ManejadorMensajes
Mensaje

👉 Implementa la comunicación en red entre jugadores.

4. Utilidades
ConfiguracionRed
GestorImagenes

5. Cliente
ClienteEquipo1Main
ClienteEquipo2Main

👉 Puntos de entrada para cada jugador.


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

🧩 Patrones de diseño usados (y por qué)
✔️ MVC (Modelo-Vista-Controlador)
Se separa la lógica del juego (modelo) de la interacción (controlador).
Mejora la mantenibilidad y organización.
✔️ Observer (Observador)
Utilizado en la comunicación de eventos del juego.
Permite actualizar el estado en diferentes partes del sistema cuando ocurre un cambio.
✔️ Singleton (posible en configuración/red)
Para manejar configuraciones globales como red.
Evita múltiples instancias inconsistentes.
✔️ Factory (implícito en mensajes)
Creación de objetos Mensaje según el tipo recibido.
Facilita la extensión del sistema de comunicación.

🌐 Comunicación UDP y P2P
🔹 UDP (User Datagram Protocol)
Protocolo no orientado a conexión.
No garantiza entrega ni orden de los mensajes.
Es más rápido que TCP → ideal para videojuegos en tiempo real.

👉 En el juego:
EmisorUDP envía paquetes.
ReceptorUDP escucha mensajes entrantes.

🔹 Peer-to-Peer (P2P)
Cada cliente actúa como:
Cliente
Servidor

👉 Ventaja:
Comunicación directa

👉 Funcionamiento:
Cada cliente abre un puerto.
Se envían mensajes directamente entre IPs.
Se sincroniza el estado del juego entre ambos.

🧠 Aplicación de principios SOLID
✔️ S - Single Responsibility
Cada clase tiene una única responsabilidad:
EmisorUDP → enviar datos
ReceptorUDP → recibir datos
Jugador → representar jugador

✔️ O - Open/Closed
El sistema permite extender funcionalidades sin modificar código existente:
Nuevos tipos de mensajes
Nuevas reglas de juego

✔️ L - Liskov Substitution
Las clases hijas pueden reemplazar a sus padres sin afectar el sistema:
Controladores derivados de ControladorJuegoBase

✔️ I - Interface Segregation
Se separan responsabilidades en clases pequeñas en lugar de interfaces grandes.

✔️ D - Dependency Inversion
Los controladores dependen de abstracciones, no de implementaciones concretas.

⚠️ Dificultades y soluciones
❌ Problema: Pérdida de paquetes (UDP)
Solución: manejo de estados y sincronización frecuente.

❌ Problema: Desincronización entre clientes
Solución: envío constante del estado del juego.

❌ Problema: Complejidad en comunicación P2P
Solución: separación clara en clases de comunicación (EmisorUDP, ReceptorUDP).

❌ Problema: Organización del código
Solución: uso de arquitectura por capas y principios SOLID.

🚀 Cómo ejecutar el juego
Compilar el proyecto en Java (NetBeans recomendado).
Ejecutar:
ClienteEquipo1Main
ClienteEquipo2Main
Configurar IP y puertos en ConfiguracionRed.
Iniciar la partida en ambos clientes.
