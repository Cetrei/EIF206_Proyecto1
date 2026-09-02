# Convenciones del proyecto

Aplican a todo el equipo, sin importar en que capa se este trabajando.

## Politica de librerias

Este curso, por norma general, no permite usar librerias o tecnicas que no se hayan visto en clase, para favorecer que el estudiante implemente las cosas por su cuenta. Esto aplica a todas las capas del proyecto.

Excepciones ya conocidas para este proyecto: JAXB esta permitido porque se vio en clase, aunque en Java 11 o superior requiere agregar la dependencia del runtime, ver 03_persistence.md. El uso de HTTP para conectarse a la API de Gemini esta permitido de forma explicita porque no hay otra forma de llamar a un servicio externo, ver 05_ai_extraction.md.

Cualquier otra libreria que se quiera agregar, por ejemplo para generar PDF, ver 04_report.md, debe confirmarse primero con el profesor o revisando el material de clase, antes de integrarla al proyecto. No asumir que algo esta permitido solo porque es una practica comun en la industria.

## Nombres

Clases en PascalCase, por ejemplo ReservaService, FuncionarioDaoXml. Metodos y variables en camelCase, por ejemplo intentarReservar, idFuncionario. Constantes en mayusculas separadas por guion bajo, por ejemplo ESTADO_ACTIVA si se usa una constante en lugar de un enum. Paquetes siempre en minusculas, siguiendo la estructura descrita en 00_arquitectura_general.md.

Los nombres deben estar en espanol para conceptos de negocio, por ejemplo Reserva, Funcionario, Categoria, para mantener consistencia con el enunciado del proyecto y facilitar la revision por parte del profesor.

## Manejo de errores

Toda condicion de negocio que impida completar una accion, por ejemplo falta de disponibilidad de un recurso, clave incorrecta, o intento de eliminar un funcionario con reservas activas, se maneja lanzando una excepcion propia del paquete util, no con codigos de retorno ni con valores nulos silenciosos. Las excepciones propias deben extender de una excepcion base del proyecto, por ejemplo ReservaAppException, para poder capturarlas de forma generica en control si es necesario.

Los mensajes de las excepciones deben ser claros y estar redactados pensando en que se van a mostrar directamente al usuario final en la interfaz, sin jerga tecnica ni referencias a clases o archivos internos.

## Validacion de datos

Segun las reglas del enunciado, todos los datos deben validarse y cualquier error debe reportarse adecuadamente. La validacion de formato basico, por ejemplo que un campo obligatorio no este vacio, puede hacerse en control antes de llamar a service. La validacion de reglas de negocio, por ejemplo que la fecha de una reserva no sea en el pasado, se hace en service.

## Control de versiones y GitHub

El repositorio debe mantenerse actualizado de forma permanente, no solo al final, porque habra revisiones de avance segun el enunciado.

Cada persona trabaja en una rama propia, por ejemplo nombre de la capa que le corresponde, y hace pull request hacia main cuando su parte compila y funciona de forma aislada. Evitar hacer commits directos a main salvo para cambios muy pequenos como ajustes de documentacion.

Mensajes de commit cortos y descriptivos, en espanol, describiendo que se hizo, por ejemplo agrega implementacion de FuncionarioDaoXml, o corrige validacion de horario en ReservaService.

## Pruebas

El proyecto debe incluir pruebas con JUnit Jupiter, tanto de unidad, ejecutadas con el plugin Surefire, como de integracion, ejecutadas con el plugin Failsafe. Las pruebas de unidad se enfocan en clases individuales de service, por ejemplo validar que intentarReservar rechace una fecha pasada. Las pruebas de integracion validan flujos completos que involucran mas de una capa, por ejemplo crear un funcionario, guardarlo mediante el DAO real, y volver a leerlo.

Cada quien es responsable de las pruebas de la capa que le corresponde, siguiendo la division de trabajo descrita en 00_arquitectura_general.md.

## Dependencias del proyecto

Cualquier libreria nueva que se agregue al proyecto, por ejemplo la libreria de PDF o el cliente HTTP para llamar a Gemini, debe agregarse al archivo de gestion de dependencias del proyecto, Maven o Gradle segun se elija, y comunicarse al resto del equipo, para evitar que el proyecto no compile en la maquina de otra persona por falta de una dependencia.
