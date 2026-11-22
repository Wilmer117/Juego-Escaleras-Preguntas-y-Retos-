# Juego de escalera, preguntas y retos

Proyecto 3 del Laboratorio de Estructuras de Datos (2026). Juego de tablero para 2 a 4 jugadores, en Java,
con **todas las estructuras programadas con nodos propios**. No se usa `LinkedList`, `ArrayDeque`, `Stack`,
`Queue`, `ArrayList`, `HashMap`, `Hashtable` ni `TreeMap`.

**Integrantes:** _(nombre 1)_, _(nombre 2)_, _(nombre 3)_, _(nombre 4)_, _(nombre 5)_
**Video (YouTube):** _(pegar el enlace aquí)_

---

## Cómo ejecutarlo

Requiere Java 17 o superior (probado con OpenJDK 21).

| Sistema | Comando |
|---|---|
| Windows | doble clic en `compilar_y_ejecutar.bat` (elige 1 = consola, 2 = interfaz gráfica) |
| Linux / macOS | `./compilar_y_ejecutar.sh` (gráfica) o `./compilar_y_ejecutar.sh consola` |
| NetBeans / IntelliJ / VS Code | abrir la carpeta `src` como fuente y ejecutar `escalera.app.MainGrafico` o `escalera.app.MainConsola` |

A mano:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out escalera.app.MainConsola            # corrida de verificación
java -cp out escalera.app.MainConsola aleatoria  # + una partida aleatoria completa
java -cp out escalera.app.MainGrafico            # interfaz gráfica
```

---

## El lugar de cada estructura

| Estructura | Clase | Qué guarda | Para qué se usa |
|---|---|---|---|
| Lista doblemente enlazada | `ListaDobleTablero`, `NodoCasilla` | Casillas (número y tipo) | Orden físico del tablero, ida y regreso. El *siguiente* de una casilla es la contigua. |
| Cola sobre lista simple | `ColaTurnos` | Nombres en turno | Quien juega sale del frente y, si no ganó, entra al final. |
| Pila sobre lista simple | `PilaJugadas` | Dado, origen y casilla alcanzada | Consultar las últimas 5 jugadas; la más reciente es la cima. |
| Tabla hash (4 cubetas con lista simple) | `TablaJugadores` | Nombre, posición, aciertos, fallos, castigo | Actualizar a un jugador sin recorrer el tablero. |
| Árbol binario de búsqueda | `ArbolPreguntas` | Preguntas con clave `dificultad × 100 + id` | Buscar la pregunta de un reto; inorden, camino, nivel, grado, peso, altura, hojas, completo. |
| Árbol binario de decisión | `ArbolDecision`, `NodoPregunta`, `NodoAccion` | La regla de llegada | Decidir si no se mueve, si hay reto, si hay salto o si se queda. |
| Grafo dirigido | `GrafoSaltos` | Escaleras y serpientes | En la partida se sigue la arista en un solo sentido. |
| Union-Find + Kruskal / Prim | `UnionFind`, `ValidadorCiclos` | Conjuntos de casillas | Rechazar un salto que cerraría un ciclo (15—3). |
| Árbol de ranking (opcional) | `ArbolRanking` | Jugadores por aciertos | Ranking al final de la partida. |

### El corazón del laboratorio: ¿por qué el siguiente de 3 es 4 y quien cae en 3 termina en 11?

Son dos estructuras distintas que **no se mezclan**:

- `NodoCasilla` solo tiene `anterior` y `siguiente`. No tiene ningún campo de "salto". Por eso
  `tablero.numeroSiguiente(3)` devuelve **4**.
- El salto vive en `GrafoSaltos`: la arista dirigida `3 → 11`. Cuando la ficha llega a 3, el árbol de decisión
  pregunta *"¿hay escalera o serpiente?"*, el grafo responde que sí y `Juego.aplicarLlegada` mueve la ficha a
  `grafo.destinoDe(3)`, que es **11**.

Analogía: la lista doble es la fila de baldosas del piso (cada baldosa conoce la de al lado); la escalera es un
objeto apoyado encima del piso. Quitar la escalera no cambia el orden de las baldosas.

En el código: `escalera/juego/Juego.java`, método `aplicarLlegada`, caso `SIGUE_ARISTA`.

---

## Reglas implementadas

- Tablero de 1 a 50. La primera parte construye 20 casillas y luego `FabricaTablero.extenderHasta` alarga la misma lista a 50.
- Saltos: escaleras 3→11, 8→15, 14→19; serpientes 17→4, 18→6.
- Retos en 5, 12, 20, 27, 33 y 41. Dificultad 1 si la casilla ≤ 15, 2 si ≤ 35, 3 en otro caso.
- Acertar un reto: suma acierto y **juega otra vez en el mismo ciclo**, antes de volver a la cola.
- Fallar un reto: suma fallo y se **marca el castigo en el jugador**; la próxima vez que salga del frente no tira y vuelve al final. La cola sigue siendo simple.
- Gana quien llega **exactamente** a 50. Si el dado pasa de 50, la ficha no se mueve.
- Un nombre que no está en la tabla no juega y se avisa. Una cola vacía avisa. Nada detiene el programa.

Árbol de decisión (se arma con nodos en `ArbolDecision`, no con una cadena de `if`):

```
¿la posición más el dado pasa de 50?
├─ sí → la ficha no se mueve
└─ no → ¿la casilla de llegada es un reto?
        ├─ sí → ¿la respuesta es correcta?
        │       ├─ sí → acierta y juega otra vez
        │       └─ no → falla y pierde el turno siguiente
        └─ no → ¿hay escalera o serpiente?
                ├─ sí → se sigue esa arista y se actualiza la tabla
                └─ no → se queda en la casilla
```

---

## Corrida de verificación

Ana, Luis y Marta. Dados fijos 3, 4, 8, 1, 6. En el reto de la casilla 12, Ana responde 5. Salida real del programa:

```
Dado 3: Ana cae en 3 y sube a 11
Dado 4: Luis pasa de 0 a 4 y se queda
Dado 8: Marta cae en 8 y sube a 15
Dado 1: Ana pasa de 11 a 12. Reto de dificultad 1, pregunta 101. Responde "5": falla, pierde el turno siguiente
Dado 6: Luis pasa de 4 a 10 y se queda
```

Otros resultados que imprime `MainConsola`:

- Turnos: `Ana, Luis, Marta, Ana, Luis`.
- Tabla: `cubeta 0: Ana` · `cubeta 1: Luis -> Marta`. Ana = 272, Luis = 413, Marta = 501.
- Árbol: inorden `101, 102, 103, 104, 201, 202, 203, 301, 302, 303`; buscar 101 → camino `201 → 101`, nivel 1, grado 1; peso 9, altura 4, 4 hojas, no es completo.
- Meta: en 48 con dado 5 no se mueve; en 47 con dado 3 llega a 50 y gana.
- Pila: la cima es la jugada más reciente (Luis, dado 6).
- Ciclos: se aceptan los 5 saltos y la prueba 11—8; **se rechaza 15—3** porque 3, 11, 8 y 15 ya están en el mismo conjunto. Prim con peso 1 también la rechaza.

### ¿Por qué 15—3 no entra al tablero?

Mirando los saltos sin dirección: 3—11 une {3, 11}; 8—15 une {8, 15}; la prueba 11—8 une esos dos grupos en
{3, 8, 11, 15}. Si ahora se agrega 15—3, ambos extremos ya están en el mismo conjunto: se cerraría el ciclo
3—11—8—15—3. `UnionFind.unir(15, 3)` devuelve `false` y la arista se rechaza. Las aristas de prueba **solo se
validan**: nunca se agregan al grafo de la partida, por eso en el juego 3 lleva a 11 y 11 no lleva a ningún lado.

Dijkstra no se usa: cada paso del dado cuesta lo mismo y la meta no es una ruta de peso mínimo.

---

## Interfaz gráfica y punto de IA

`MainGrafico` abre una ventana Swing con:

- Tablero de 10 × 5 en zigzag, dibujado recorriendo la lista doble; escaleras y serpientes dibujadas desde las aristas del grafo; fichas con la inicial de cada jugador.
- Botones: tirar dado, jugar automático hasta que haya ganador, corrida de verificación, partida aleatoria de 2 a 4 jugadores, cargar preguntas desde `preguntas.txt`.
- Pestañas: **Bitácora**, **Asistente IA**, **Estructuras** (estado en vivo de lista, cola, tabla, pila, árboles y grafo) y **Ranking**.

**IA específica del proyecto** (paquete `escalera.ia`, IA simbólica basada en reglas):

1. `AsistenteIA.explicar`: después de cada jugada explica en lenguaje natural qué estructura actuó, distinguiendo el enlace de la lista doble de la arista del grafo, y muestra el camino recorrido en el árbol de decisión.
2. `AsistenteIA.analizarRiesgo`: simula los seis valores del dado para el jugador del frente y calcula la probabilidad de escalera, serpiente, reto o quedarse quieto, y la posición esperada.
3. `JugadorIA`: jugador automático que responde los retos razonando con una base de conocimiento (`BaseConocimiento`) a partir del enunciado, sin leer la respuesta guardada. Se equivoca con más probabilidad en las preguntas difíciles.
4. Pistas: un jugador humano puede pedirle una pista a la IA dentro del diálogo del reto.

---

## Estructura del código

```
src/escalera/
├── modelo/       Casilla, TipoCasilla, Jugador, Pregunta, Jugada, Arista
├── estructuras/  ListaDobleTablero, NodoCasilla, ColaTurnos, PilaJugadas, TablaJugadores,
│                 ArbolPreguntas, ArbolRanking, GrafoSaltos, UnionFind, ValidadorCiclos
├── decision/     ArbolDecision, NodoDecision, NodoPregunta, NodoAccion, ContextoLlegada, Accion
├── juego/        Juego (motor), Dado, DadoFijo, DadoAleatorio, ConfiguracionTablero,
│                 FabricaTablero, RepresentadorTablero, CargadorPreguntas, ...
├── ia/           AsistenteIA, JugadorIA, BaseConocimiento
└── app/          MainConsola, MainGrafico, VentanaJuego, PanelJuego, PanelTablero, DialogoJugadores
```

Buenas prácticas aplicadas: una responsabilidad por clase, nombres descriptivos en español, constantes en
lugar de números mágicos (`ConfiguracionTablero`), interfaces para lo que cambia (`Dado`, `ProveedorRespuestas`,
`Bitacora`), y comentarios Javadoc en cada clase.
