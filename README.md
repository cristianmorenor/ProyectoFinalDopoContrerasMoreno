# ProyectoFinalDopoContrerasMoreno
Repositorio del proyecto final de dopo 2026-1

# Reporte de Test coverage 

## Analisis de cobertura pruebas

Con el objetivo de evaluar la calidad, confiabilidad y robustez del proyecto, se realizó un análisis de cobertura de pruebas enfocado principalmente en la capa de dominio (dominio), la cual concentra la lógica principal del videojuego y los componentes más relevantes desde el punto de vista arquitectónico y funcional. Este análisis permitió identificar qué tan efectivamente las pruebas unitarias desarrolladas validan el comportamiento del sistema y qué tan protegida se encuentra la lógica principal frente a posibles errores o regresiones futuras.

El proceso de análisis fue realizado utilizando JaCoCo integrado mediante EclEmma en Eclipse, permitiendo medir la cantidad de instrucciones ejecutadas durante la ejecución de las pruebas automatizadas implementadas con JUnit. Gracias a esta herramienta fue posible identificar las clases más cubiertas, los componentes críticos correctamente validados y aquellos elementos que aún presentan oportunidades de mejora dentro del proceso de pruebas.

## Herramientas utilizadas

Para la realización del análisis se utilizaron herramientas ampliamente empleadas dentro del desarrollo de software orientado a objetos y pruebas automatizadas. Las pruebas unitarias fueron desarrolladas utilizando JUnit, mientras que el análisis de cobertura fue realizado mediante JaCoCo integrado a Eclipse a través de EclEmma. Todo el proceso de desarrollo, ejecución y validación de pruebas fue llevado a cabo dentro del entorno Eclipse IDE como se va a mostrar más adelante.

## Alcance del Análisiss

De acuerdo al enunciado y las instrucciones de los enunciados el análisis de cobertura se enfocó específicamente en la capa de dominio debido a que esta contiene la lógica principal del videojuego y los componentes más críticos desde el punto de vista funcional y arquitectónico. Dentro de esta capa se encuentran elementos relacionados con manejo de colisiones, comportamiento de jugadores y enemigos, gestión de niveles, inteligencia artificial, persistencia y reglas generales del juego.

La capa de presentación no fue considerada dentro del análisis principal debido a que contiene componentes gráficos desarrollados con Swing y lógica de renderizado cuya automatización mediante pruebas unitarias tradicionales resulta considerablemente más compleja y menos representativa desde el punto de vista de la lógica de negocio.

