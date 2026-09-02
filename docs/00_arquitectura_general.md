# Arquitectura General del Proyecto

## Contexto

Sistema de escritorio en Java para gestionar reservas de recursos (salas, computadoras, proyectores, etc) de una organizacion. Los detalles funcionales completos estan en el enunciado del proyecto (EIF206_Proyecto1_PDF), este documento y los demas en esta carpeta describen como se construye, no que se pide.

## Organizacion en capas

El proyecto sigue una arquitectura por capas. Cada capa solo conoce a la capa inmediatamente inferior, nunca al reves.

```
presentation   Ventanas y paneles Swing. Solo dibuja y captura eventos de usuario.
control        Traduce eventos de la interfaz en llamadas a la capa de servicio. No contiene logica de negocio.
service        Logica de negocio: reglas de reservas, disponibilidad, estadisticas, extraccion de datos por IA.
model          Entidades del dominio (Funcionario, Recurso, Categoria, Reserva, Actividad, Usuario).
persistence    Acceso a los archivos XML mediante DAO. No sabe nada de logica de negocio ni de interfaz.
report         Generacion de reportes en PDF.
util           Validaciones comunes, excepciones propias, utilidades de fecha y formato.
```

Regla de dependencia: presentation depende de control, control depende de service, service depende de model y persistence. Ninguna capa inferior importa clases de una capa superior. Por ejemplo, una clase en persistence jamas debe importar algo de presentation o control.

## Paquetes Java

```
com.una.reservas.model
com.una.reservas.persistence
com.una.reservas.service
com.una.reservas.control
com.una.reservas.presentation
com.una.reservas.report
com.una.reservas.ai
com.una.reservas.util
```

## Division de trabajo

Joanfer: presentation, control, service, model. Es quien integra todo y mantiene la coherencia general del sistema.

Companero A: persistence (DAO con JAXB).

Companero B: report (generacion de PDF) y ai (extraccion de datos con Gemini).

Cada capa asignada a un companero se comunica con el resto del sistema unicamente a traves de interfaces definidas de antemano (ver los documentos especificos de cada capa en esta misma carpeta). Esto permite que cada quien trabaje en su parte sin bloquear a los demas, y que una implementacion pueda sustituirse por otra sin romper nada, siempre que respete el contrato de la interfaz.

## Patrones de diseno usados y donde

| Patron | Donde se usa | Para que |
|---|---|---|
| DAO | persistence | Separar el acceso a datos XML del resto del sistema |
| Factory Method | service, en la creacion de DAOs y de generadores de reporte | Evitar que el service dependa de clases concretas de persistence o report |
| Observer | service, en ReservaService, y presentation, en las vistas que deben refrescarse | Que las vistas de calendarizacion, actividades y estadisticas se actualicen automaticamente cuando cambia una reserva, sin que el service conozca a las vistas |
| Strategy | ai, en la extraccion de datos, y presentation, en el llenado de las vistas tipo matriz | Permitir varias formas de resolver un mismo problema sin condicionales gigantes y sin tocar el codigo que las usa |
| Singleton | control, en el gestor de sesion | Un unico punto de verdad sobre quien esta logueado y su rol, accesible desde cualquier controlador |

Principio de sustitucion de Liskov: todas las interfaces de esta arquitectura, Dao, GeneradorReporte, ExtractorReserva y MatrizFillStrategy, estan disenadas para que cualquier implementacion sea intercambiable sin que el codigo que la usa necesite saberlo ni cambiar. Si una implementacion nueva rompe el comportamiento esperado por la interfaz, el problema es de esa implementacion, no de quien la consume.

## Persistencia

Se usa serializacion XML mediante JAXB (Java Architecture for XML Binding). Las clases del modelo se anotan con anotaciones como XmlRootElement y XmlElement, y JAXB se encarga de convertirlas a XML legible y de vuelta a objetos. El detalle esta en 03_persistence.md.

## Documentos de esta carpeta

00_arquitectura_general.md, este documento.

01_model.md, entidades del dominio.

02_service.md, logica de negocio, service layer.

03_persistence.md, capa de persistencia XML con JAXB, para companero A.

04_report.md, generacion de reportes PDF, para companero B.

05_ai_extraction.md, extraccion de datos por IA con Gemini y fallback, para companero B.

06_control_presentation.md, controladores y vistas, contexto de la parte de Joanfer.

07_convenciones.md, convenciones de codigo, nombres, manejo de errores y estructura de commits.

## Como leer estos documentos si sos una IA ayudando en este proyecto

Cada documento describe una capa o componente de forma autocontenida: que problema resuelve, que interfaces debe exponer, que estructura de archivos y clases se espera, y cualquier restriccion importante. Si estas generando codigo para una de estas capas, respeta exactamente los nombres de metodos e interfaces descritos aqui, porque otras partes del sistema, escritas por otras personas, dependen de esos nombres tal cual estan escritos. No agregues metodos adicionales en las interfaces compartidas sin avisar a quien mantiene la capa que las consume, porque romperia la compilacion de esa parte.
