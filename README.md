# ProyectoFinalDopoContrerasMoreno
## Repositorio del proyecto final de dopo 2026-1

# Reporte de Test coverage 

## Analisis de cobertura pruebas

Con el objetivo de evaluar la calidad, confiabilidad y robustez del proyecto, se realizó un análisis de cobertura de pruebas enfocado principalmente en la capa de dominio (dominio la cual se pueden consultar sus clases y subclases dentro de este repositorio en la carpeta src), la cual concentra la lógica principal del videojuego y los componentes más relevantes desde el punto de vista arquitectónico y funcional. Este análisis permitió identificar qué tan efectivamente las pruebas unitarias desarrolladas validan el comportamiento del sistema y qué tan protegida se encuentra la lógica principal frente a posibles errores o regresiones futuras.

El proceso de análisis fue realizado utilizando JaCoCo integrado mediante EclEmma en Eclipse, permitiendo medir la cantidad de instrucciones ejecutadas durante la ejecución de las pruebas automatizadas implementadas con JUnit. Gracias a esta herramienta fue posible identificar las clases más cubiertas, los componentes críticos correctamente validados y aquellos elementos que aún presentan oportunidades de mejora dentro del proceso de pruebas.

## Herramientas utilizadas

Para la realización del análisis se utilizaron herramientas ampliamente empleadas dentro del desarrollo de software orientado a objetos y pruebas automatizadas. Las pruebas unitarias fueron desarrolladas utilizando JUnit, mientras que el análisis de cobertura fue realizado mediante JaCoCo integrado a Eclipse a través de EclEmma. Todo el proceso de desarrollo, ejecución y validación de pruebas fue llevado a cabo dentro del entorno Eclipse IDE como se va a mostrar más adelante.

## Alcance del Análisiss

De acuerdo al enunciado y las instrucciones de los enunciados el análisis de cobertura se enfocó específicamente en la capa de dominio debido a que esta contiene la lógica principal del videojuego y los componentes más críticos desde el punto de vista funcional y arquitectónico. Dentro de esta capa se encuentran elementos relacionados con manejo de colisiones, comportamiento de jugadores y enemigos, gestión de niveles, inteligencia artificial, persistencia y reglas generales del juego.

Las clases probadas se pueden observar en la carpeta de test de este repositorio:

<img width="355" height="370" alt="image" src="https://github.com/user-attachments/assets/214fdf7d-9637-4656-a1e5-b0f935af6324" />

La capa de presentación no fue considerada dentro del análisis principal debido a que contiene componentes gráficos desarrollados con Swing y lógica de renderizado cuya automatización mediante pruebas unitarias tradicionales resulta considerablemente más compleja y menos representativa desde el punto de vista de la lógica de negocio.

Los principales componentes probados se pueden ver en la carpeta de Test en este repositorio, sin embargo acá se va a explicar porque la atención especifica en estas clases:

- **CollisionManager**

Esta clase fue priorizada debido a que centraliza la lógica de colisiones del videojuego, una de las funcionalidades más críticas del sistema. Al tratarse del núcleo de interacción entre objetos del juego, cualquier error en esta clase impactaría directamente la experiencia de juego.
Las pruebas realizadas permitieron validar correctamente:

* interacción entre entidades
* detección de colisiones
* pérdida de vidas
* activación de checkpoints
* recolección de objetos
* condiciones de victoria o derrota.


- **Game**

La clase Game fue probada debido a que coordina el flujo general de la aplicación y administra el estado global del juego y tambien posee alta responsabilidad dentro de la arquitectura, por lo que asegurar su correcto funcionamiento era fundamental.

Las pruebas verificaron:

* transición entre niveles
* reinicio de partidas
* control de muertes
* cambios de estado del sistema.


- **Level**

Debido a su alto acoplamiento y relevancia estructural, esta clase recibió un enfoque importante dentro de la estrategia de pruebas puesto que la clase Level representa el centro de composición de entidades dentro del dominio, administrando:

* jugadores
* enemigos
* monedas
* paredes
* checkpoints
* bombas
* zonas seguras
* demás componentes del mapa.

Las pruebas realizadas validaron principalmente la carga correcta de entidades, gestión de objetos, interacción entre componentes, y consistencia del estado interno del nivel por lo que fue en nuestra opinión la clase más importante a probar 


- **Player**

Dado que el jugador representa la entidad principal controlada por el usuario, asegurar su estabilidad era indispensable para el correcto funcionamiento del juego.

La clase Player fue probada para garantizar el correcto comportamiento del jugador dentro del mapa.
Las pruebas validaron principalmente:

* movimiento
* límites del escenario
* reinicio de posición
* colisiones
* actualización de estado



- **Enemy y sus Subclases**

La jerarquía Enemy fue una de las principales prioridades de prueba debido a que implementa:

* herencia
* polimorfismo
* comportamiento dinámico
* distintos patrones de movimiento

Las pruebas permitieron validar las cosas importantes que respetaran el diseño que se realizó y los comportamientos:

* movimiento horizontal y vertical
* cambios de dirección
* velocidades distintas
* comportamiento especializado según cada tipo de enemigo


- **MapLoader**

La clase MapLoader fue probada debido a su responsabilidad dentro de la capa de persistencia y carga de niveles.
Las pruebas verificaron:

* lectura correcta de mapas
* creación adecuada de entidades
* manejo de archivos
* control de errores durante la carga

Esto permitió validar la robustez del sistema frente a configuraciones inválidas o problemas de persistencia.

- **GameMode**

Las implementaciones de GameMode fueron probadas debido a que representan las distintas estrategias de funcionamiento del juego, aplicando el patrón de diseño Strategy.

Las pruebas verificaron comportamiento de los diferentes modos, correcta separación de responsabilidades y cambios dinámicos en la lógica de juego según el modo seleccionado.

- **AIPlayer**

Las clases relacionadas con AIPlayer fueron probadas debido a que implementan lógica de inteligencia artificial para los jugadores controlados por máquina. Las pruebas permitieron validar toma de decisiones, comportamiento automático y respuesta de la IA frente a distintos estados del entorno.

Estas pruebas fueron especialmente importantes debido a la naturaleza dinámica y variable del comportamiento automatizado.


## Estrategia de pruebas

La estrategia de pruebas que se tomó fue la de que se priorizó la validación de la lógica de dominio de forma independiente a la interfaz gráfica.

Este enfoque sigue buenas prácticas de ingeniería de software al separar la respectiva lógica de negocio, presentación y persistencia

Las pruebas fueron diseñadas para validar en general 

* salidas esperadas
* cambios de estado
* colisiones
* comportamiento polimórfico
* manejo de situaciones excepcionales


## Resultados de cobertura 

Al ejecutar desde el IDE mediante ya las herramientas mencionadas obtuvimos los resultados que se muestran en la imagen adjunta:

<img width="763" height="145" alt="image" src="https://github.com/user-attachments/assets/296cee49-5688-4dd1-9b7e-4407ace4a9e2" />

Podemos ver que la herramienta nos arroja un porcentaje e información por cada capa que tenemops, pero como se ha mencionado nos vamos a centrar en la capa de Domain, por lo que se ve que se obtuvo una cobertura de 81.5% y si se hace una inspección especifica dentro de cada clas podemos ver cuanta cobertura hay por cada clase especifica de la capa:

 <img width="948" height="656" alt="image" src="https://github.com/user-attachments/assets/aad6f51e-54be-4396-8d84-cbd1b79e5037" />

 Por lo tanto estos resultados y el porcentaje obtenido demuestra un nivel sólido de validación sobre los componentes más importantes de la lógica del videojuego donde algunos clases podemos osbservar que logramos su cobertura del 100% mientras que otras estan muy cerca de estarlo.

Las pruebas ejecutadas cubren funcionalidades relacionadas con:

* Interacción entre objetos
* Detección de colisiones
* Movimiento de entidades
* Comportamiento de inteligencia artificial
* Gestión de niveles
* Recolección de monedas
* Activación de checkpoints
* Transiciones de estado del juego
* Persistencia y carga de mapas

## Observaciones a partir de resultados

El análisis de cobertura realizado se centró principalmente en la capa de dominio del proyecto, debido a que esta concentra la lógica principal del videojuego y los componentes más relevantes desde el punto de vista arquitectónico y funcional.

Durante el proceso de pruebas se identificó que algunos componentes gráficos pertenecientes a la capa presentacion presentan una complejidad considerable para su automatización mediante pruebas unitarias tradicionales, especialmente aquellos relacionados con:

* renderizado gráfico
* manejo de eventos Swing
* interacción visual
* temporizadores asociados a la interfaz

Por esta razón, el enfoque principal del análisis fue validar exhaustivamente la lógica de negocio y las reglas del juego, priorizando los componentes críticos del sistema.

Adicionalmente, durante el desarrollo se realizaron múltiples procesos de refactorización orientados a mejorar la arquitectura orientada a objetos del proyecto. Como resultado de estas mejoras, algunas clases heredadas de versiones iniciales fueron eliminadas o reemplazadas por nuevas abstracciones más adecuadas, permitiendo aumentar la calidad general del sistema y optimizar la cobertura de pruebas.

También debe considerarse que, debido a la naturaleza dinámica del videojuego y a la presencia de inteligencia artificial, existen comportamientos difíciles de cubrir completamente mediante pruebas unitarias determinísticas, especialmente aquellos asociados a decisiones automáticas y eventos dependientes del tiempo de ejecución.

A pesar de estas limitaciones, la cobertura obtenida logra validar de forma satisfactoria las funcionalidades más importantes y críticas del sistema logrando así un porcentaje alto que cumple con lo que se piden en los requisitos del juego y enunciados del proyecto.

## Evaluación de calidad

La cobertura final obtenida sobre la capa de dominio fue del 81%, resultado que evidencia un nivel alto de validación sobre la lógica principal del videojuego. Este porcentaje demuestra que las funcionalidades más críticas del sistema fueron sometidas a pruebas exhaustivas, permitiendo verificar el comportamiento correcto de múltiples componentes esenciales dentro de la arquitectura del proyecto.

A lo largo del proceso de pruebas se logró validar correctamente la interacción entre entidades, el manejo de colisiones, la gestión de niveles, los diferentes modos de juego, la persistencia y el comportamiento de la inteligencia artificial implementada. De igual forma, las pruebas permitieron comprobar el correcto funcionamiento de varias jerarquías de clases construidas mediante herencia y polimorfismo, fortaleciendo la confiabilidad general del sistema.

La mejora progresiva de la cobertura estuvo acompañada por procesos constantes de refactorización y optimización arquitectónica. Durante el desarrollo se eliminaron componentes obsoletos y se reemplazaron por abstracciones más adecuadas al dominio del videojuego, permitiendo incrementar tanto la mantenibilidad del código como la calidad de las pruebas realizadas. Esto contribuyó directamente a obtener un diseño más modular, extensible y coherente con los principios de Programación Orientada a Objetos.

El porcentaje alcanzado resulta especialmente satisfactorio considerando la complejidad del proyecto, la presencia de múltiples entidades dinámicas, el uso de inteligencia artificial, la integración de persistencia y la existencia de una capa gráfica basada en Swing. A pesar de las limitaciones propias de las interfaces gráficas y de ciertos comportamientos dinámicos difíciles de automatizar completamente, la lógica principal del sistema logró ser validada de manera sólida y consistente.

En términos generales, los resultados obtenidos evidencian que el proyecto mantiene un nivel adecuado de robustez, estabilidad y mantenibilidad, reflejando una arquitectura diseñada bajo buenas prácticas de ingeniería de software y un enfoque claro hacia la calidad del código y la capacidad de evolución futura del sistema.


## Conclusiones

El análisis de cobertura de pruebas permitió validar de forma efectiva el comportamiento de los componentes más importantes del proyecto, especialmente aquellos pertenecientes a la capa de dominio.

La utilización de pruebas unitarias junto con herramientas de análisis como JaCoCo facilitó la detección temprana de errores, la validación de reglas de negocio y la mejora continua de la arquitectura del sistema a lo largo del desarrollo.

Los resultados obtenidos evidencian que la lógica principal del videojuego posee un comportamiento estable y consistente frente a distintos escenarios de ejecución, permitiendo garantizar un nivel adecuado de confiabilidad sobre funcionalidades críticas como movimiento, colisiones, interacción entre entidades, gestión de niveles, inteligencia artificial y persistencia.

Asimismo, el proceso de pruebas contribuyó significativamente a fortalecer la modularidad y extensibilidad del proyecto, favoreciendo la incorporación de nuevas funcionalidades sin afectar el comportamiento existente del sistema.

Finalmente, la cobertura alcanzada demuestra que la arquitectura implementada fue diseñada considerando criterios de calidad de software, mantenibilidad y capacidad de prueba, consolidando un proyecto estructurado bajo buenas prácticas de ingeniería de software y nuestra materia de Desarollo Orientado por Objetos.

# Reporte de Análisis Estático 

## objetivo del análisis estático 

Con el propósito de mejorar la calidad, mantenibilidad y robustez del proyecto, se realizó un análisis estático del código fuente enfocado principalmente en la capa de dominio y en la arquitectura general del sistema.

El análisis permitió identificar problemas relacionados con clases obsoletas, código redundante, posibles conflictos arquitectónicos,
y componentes que ya no hacían parte activa de la lógica principal del videojuego.

Además, este proceso ayudó a fortalecer la organización del proyecto y mejorar la coherencia entre la implementación final y el diseño orientado a objetos planteado durante el desarrollo y tambien la coherencia entre el diseño relacionado con el UML en la herramienta astah el cual puede ser descargado y consultado en este repositorio.

## Observaciones y hallazgos

Durante las primeras etapas del análisis se identificó la existencia de varias clases heredadas de versiones iniciales del proyecto que ya no participaban activamente en la arquitectura final del sistema. Estas clases permanecían dentro del repositorio debido a procesos de refactorización y evolución progresiva del diseño por lo que se había olvidado tomar decisiones sobre estas clases obsoletas. 

Entre los principales casos encontrados se identificaron clases relacionadas con implementaciones antiguas de enemigos y objetivos del juego como por ejemplo se encontraba Obstacle.java y sus clases hijas FastObstacle.java y BasicObstacle.java , las cuales habían sido reemplazadas posteriormente por nuevas abstracciones más coherentes con el dominio actual del proyecto. La permanencia de estos componentes generaba:

* duplicidad conceptual
* advertencias innecesarias
* baja cobertura de pruebas,
* conflictos dentro del análisis estático

Como resultado, se tomó la decisión de eliminar o desacoplar dichas clases obsoletas, permitiendo reducir significativamente la cantidad de advertencias y mejorar la consistencia arquitectónica del sistema puesto que esto tambien estaba afectando la cobertura de las pruebas que teniamos ya que las pruebas si habian evolucionado y se habian hecho para las ultimas versiones y se habian olvidado para estas clases viejas y asi la cobertura estaba bajando considerablemente.

El análisis realizado permitió evidenciar que gran parte de las advertencias iniciales no estaban asociadas necesariamente a errores funcionales, sino a residuos de versiones anteriores del diseño o a componentes temporales utilizados durante procesos de refactorización.

También se identificó que algunas clases centrales del sistema, como Level y ciertos componentes gráficos de la capa presentacion, poseen naturalmente una complejidad mayor debido a la cantidad de responsabilidades y entidades que administran. Esto resulta esperable dentro de un proyecto de videojuegos con múltiples sistemas interactuando simultáneamente.

Adicionalmente, la presencia de interfaces, herencia y polimorfismo incrementó la modularidad del proyecto, facilitando la extensibilidad futura y reduciendo la necesidad de modificar componentes existentes ante nuevas funcionalidades que puedan surgir más adelante.

## Mejoras Arquitectonicas

Ademas de las respectivas eliminaciones de clasess residuales que se hicieron el análisis estático también permitió validar varias mejoras realizadas durante el desarrollo del proyecto. Entre las más importantes se encuentran:

* migración desde estructuras simples hacia jerarquías de herencia más robustas
* implementación de interfaces para desacoplar comportamientos
* separación adecuada entre dominio, presentación y persistencia
* centralización de responsabilidades mediante clases especializadas

Especificamente meidante la transición desde modelos antiguos basados en obstáculos genéricos hacia una jerarquía de enemigos (Enemy) permitió obtener una arquitectura más extensible, modular y alineada con principios de Programación Orientada a Objetos.

De igual manera, la introducción de componentes como CollisionManager, GameMode, PlayerFactory y las interfaces Collidable, Movable y Collectible contribuyó a disminuir el acoplamiento entre clases y mejorar la organización general del código pues se estaban separando las responsibilidades y unificando logica y congruencia con el diseño de UML realizado. 

## Evaluacion de calidad

El análisis estático permitió mejorar considerablemente la calidad general del proyecto, especialmente después de la eliminación de clases obsoletas y la consolidación de una arquitectura más limpia y coherente.

La reducción de advertencias y conflictos reflejó una evolución positiva del diseño, evidenciando un proceso constante de refactorización orientado a fortalecer principios de mantenibilidad, modularidad y reutilización de código.

Asimismo, la estructura final del sistema demuestra un uso adecuado de conceptos de Programación Orientada a Objetos como:

* abstracción
* herencia
* encapsulamiento
* polimorfismo
* separación de responsabilidades.

La arquitectura obtenida facilita la incorporación de nuevas funcionalidades, la creación de nuevos tipos de entidades y la evolución futura del videojuego sin afectar significativamente los componentes ya implementados haciendo asi que se cumpla parte de los requisitos que hacian falta por cubir del enunciado de proyecto que se proporcionó al inicio del desarrollo del proyecto.


## Conclusiones

El análisis estático realizado a lo largo del desarrollo del proyecto permitió identificar y corregir múltiples aspectos relacionados con la calidad y organización del código fuente. Este proceso no solo ayudó a detectar advertencias y posibles conflictos arquitectónicos, sino que también evidenció la evolución progresiva del sistema hacia una estructura más sólida y coherente con los principios de Programación Orientada a Objetos.

Uno de los resultados más importantes obtenidos fue la identificación de clases heredadas de versiones iniciales del proyecto que ya no participaban activamente en la lógica final del videojuego. La eliminación y refactorización de estos componentes permitió reducir significativamente conflictos y redundancias dentro del sistema, mejorando tanto la mantenibilidad como la claridad de la arquitectura implementada.

Asimismo, el análisis permitió consolidar una estructura más modular y extensible, basada en el uso adecuado de abstracción, herencia, interfaces y polimorfismo. Componentes como Enemy, GameMode, CollisionManager y las diferentes interfaces implementadas reflejan una arquitectura orientada a la reutilización de código y a la separación clara de responsabilidades, facilitando la incorporación de nuevas funcionalidades sin afectar el comportamiento existente del sistema.

De igual manera, el proceso de análisis estático ayudó a fortalecer la coherencia entre el diseño UML y la implementación final del proyecto, permitiendo mantener una relación más clara entre el modelo conceptual y el código desarrollado. Esto contribuyó a obtener un sistema más organizado, comprensible y alineado con buenas prácticas de ingeniería de software.

Finalmente, los resultados obtenidos evidencian que el proyecto alcanzó un nivel adecuado de calidad estructural y mantenibilidad, demostrando una evolución constante durante el proceso de desarrollo. La combinación entre refactorización, pruebas y análisis estático permitió construir una solución más robusta, preparada para futuras extensiones y consistente con los objetivos académicos y técnicos planteados desde el inicio del proyecto.

# Diagrama de clases UML

El resultado final del diagrama de clases en UML de la capa de dominio especifica tras sus respectivos refactors y ajustes quedó de la forma:

<img width="907" height="644" alt="image" src="https://github.com/user-attachments/assets/07e72db1-72b7-467b-9bfc-6b21ead121d6" />

El archivo UML puede ser consultado en este repositorio y descargado para su más profunda revisión.

# Patrones de Diseño usados

## Introducción

Durante el desarrollo del proyecto se implementaron distintos patrones de diseño con el objetivo de mejorar la modularidad, extensibilidad, reutilización y mantenibilidad del sistema. La utilización de estos patrones permitió construir una arquitectura más organizada y flexible, facilitando la incorporación de nuevas funcionalidades sin afectar significativamente los componentes ya existentes.

Los patrones aplicados se relacionan principalmente con la gestión de comportamientos dinámicos, creación de objetos, reutilización de estructuras comunes y separación de responsabilidades dentro de la arquitectura orientada a objetos del videojuego.

## 1. Strategy Pattern
Uno de los patrones más importantes implementados en el proyecto fue el patrón Strategy, utilizado principalmente en las clases relacionadas con los modos de juego.

Este patrón se encuentra representado mediante la interfaz GameMode y sus diferentes implementaciones, como PvPMode y PvMachineMode. La idea principal detrás de esta implementación fue encapsular diferentes comportamientos de juego dentro de clases independientes, permitiendo cambiar dinámicamente la lógica del sistema dependiendo del modo seleccionado.

Gracias a este patrón, la clase principal del juego no necesita conocer los detalles específicos de cada modo de juego, sino únicamente interactuar con la abstracción GameMode. Esto permitió reducir el acoplamiento y aumentar la extensibilidad del sistema, facilitando la posibilidad de agregar nuevos modos de juego en el futuro sin modificar las clases ya existentes.

De manera similar, este patrón también puede observarse en las implementaciones relacionadas con inteligencia artificial mediante la interfaz AIPlayer y clases como RandomAI o ExpertAI, donde diferentes estrategias de comportamiento automático pueden ser intercambiadas dinámicamente según el contexto del juego.

## 2.Factory Method

El patrón Factory fue implementado mediante la clase PlayerFactory, encargada de centralizar la creación de jugadores dentro del sistema.

La utilización de esta fábrica permitió desacoplar la lógica de creación de objetos del resto de la aplicación, evitando que múltiples clases deban instanciar directamente jugadores concretos como RedPlayer, BluePlayer o GreenPlayer ( ver en la capa de dominio )

Gracias a esta solución, el sistema puede crear distintos tipos de jugadores de manera controlada y centralizada, mejorando la mantenibilidad y permitiendo futuras extensiones sin afectar otras partes del código.

La implementación de este patrón también contribuyó a reducir duplicación y fortalecer la cohesión del sistema.

## 3. MVC

El proyecto implementa una arquitectura inspirada en el patrón MVC mediante la separación de paquetes en:

* dominio
* presentacion
* persistencia

La capa dominio contiene la lógica principal del juego y las reglas de negocio. La capa presentacion administra la interfaz gráfica y el renderizado visual mediante Swing. Finalmente, la capa persistencia encapsula responsabilidades relacionadas con carga de mapas, guardado de información y manejo de archivos.

Por lo que existe esta separación y asi se logra el uso de este patrón de diseño 

## 4. Uso de Interfaces y polimorfismo como Patrón Arquitectónico

El uso de interfaces dentro del proyecto constituye una de las decisiones arquitectónicas más importantes implementadas durante el desarrollo.

Interfaces como Collidable, Movable, Collectible y AIPlayer permitieron desacoplar comportamientos específicos de las implementaciones concretas, favoreciendo la reutilización y extensibilidad del sistema.

Gracias a esto, fue posible agregar nuevos tipos de enemigos, monedas, entidades y comportamientos sin necesidad de modificar significativamente la arquitectura existente, respetando principios como Open/Closed y programación orientada a abstracciones.

Por medio de esto  esto el proyecto hace un uso extensivo de diseño polimórfico mediante interfaces y jerarquías de herencia.

Por ejemplo, CollisionManager puede trabajar de manera genérica con cualquier objeto que implemente Collidable, independientemente de si se trata de un jugador, enemigo, moneda, bomba o pared. De esta forma, las interacciones del sistema se basan en abstracciones y no en clases específicas.

La utilización de polimorfismo permitió además facilitar la extensibilidad futura del sistema y reducir considerablemente el acoplamiento entre componentes.








