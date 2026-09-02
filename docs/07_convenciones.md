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

El proyecto usa Maven, con el pom.xml en la raiz del proyecto. Se eligio Maven especificamente porque Surefire y Failsafe, que el enunciado pide por nombre, son plugins de Maven.

Estructura de carpetas: el codigo de la aplicacion va en src/main/java/com/una/reservas/... y las pruebas van en src/test/java/com/una/reservas/..., siguiendo el mismo arbol de paquetes que el codigo que prueban.

Convencion de nombres que Surefire y Failsafe usan para decidir que ejecutar y cuando:

Una clase que termina en Test, por ejemplo ReservaTest o FuncionarioServiceTest, es una prueba de unidad. Surefire la ejecuta con el comando mvn test. Las pruebas de unidad se enfocan en una sola clase, sin tocar archivos ni red, por ejemplo validar que intentarReservar rechace una fecha pasada usando un Dao falso en vez del real.

Una clase que termina en IT, por ejemplo FuncionarioDaoXmlIT, es una prueba de integracion. Failsafe la ejecuta con el comando mvn verify, en una fase separada despues de las pruebas de unidad. Las pruebas de integracion validan flujos completos que involucran mas de una capa, por ejemplo crear un funcionario, guardarlo mediante el DAO real contra un archivo XML de prueba, y volver a leerlo.

Ya existen dos archivos de ejemplo en el proyecto para que cada quien siga el mismo patron: src/test/java/com/una/reservas/model/ReservaTest.java (prueba de unidad ya funcional) y src/test/java/com/una/reservas/persistence/FuncionarioDaoXmlIT.java (prueba de integracion, comentada, para descomentar cuando el DAO real este listo).

Cada quien es responsable de las pruebas de la capa que le corresponde, siguiendo la division de trabajo descrita en 00_arquitectura_general.md.

## Dependencias del proyecto

Cualquier libreria nueva que se agregue al proyecto, por ejemplo la libreria de PDF o el cliente HTTP para llamar a Gemini, debe agregarse como dependencia en el pom.xml y comunicarse al resto del equipo, para evitar que el proyecto no compile en la maquina de otra persona por falta de una dependencia. Despues de que alguien mas agregue una dependencia nueva, el resto del equipo debe correr mvn install (o el equivalente de su IDE) para que se descargue localmente antes de compilar.
