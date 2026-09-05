# Modelo de dominio

Paquete: cr.ac.una.reservas.model

Responsable principal: Joanfer. Es la capa mas compartida del proyecto, tanto persistence como service y presentation dependen de estas clases, por lo que cualquier cambio aqui debe avisarse al resto del equipo antes de hacerlo.

## Proposito

Contener las entidades del negocio como objetos simples, sin logica de negocio dentro de ellas mas alla de validaciones basicas de sus propios datos (por ejemplo, que la hora de fin sea posterior a la hora de inicio). La logica de negocio que involucra a varias entidades a la vez, como asignar un recurso disponible, vive en service, no aqui.

Estas clases se anotan con JAXB para que la capa de persistencia las pueda serializar a XML directamente, sin necesidad de una capa de conversion intermedia.

## Entidades

### Usuario

Clase base o interfaz comun para Administrador y Funcionario, segun se decida durante la implementacion, ya que ambos comparten id, clave y rol.

Campos: id (String), clave (String), rol (enum RolUsuario con valores ADMINISTRADOR y FUNCIONARIO).

### Funcionario

Extiende o compone Usuario. Campos adicionales: nombre (String), telefono (String).

Regla: al crear un Funcionario, la clave inicial es igual al id. Esta regla se aplica en service, no en el modelo, el modelo solo almacena el valor que le den.

### Administrador

Extiende o compone Usuario, sin campos adicionales por ahora salvo que surja necesidad durante la implementacion.

### Categoria

Campos: id (String, autogenerado con formato tipo CAT-000001 segun el ejemplo del enunciado), descripcion (String).

### Recurso

Campos: id (String, ingresado manualmente, ej un numero de activo), categoria (referencia a Categoria), descripcion (String).

### Reserva

Campos: id (String, autogenerado tipo RES-000001), funcionario (referencia a Funcionario), actividad (String, nombre de la actividad), fecha (LocalDate), horaInicio (LocalTime), horaFin (LocalTime), categoriasRequeridas (lista de Categoria), recursosAsignados (lista de Recurso, un recurso por cada categoria requerida, asignado tras el exito de la reserva), estado (enum EstadoReserva con valores ACTIVA y CANCELADA).

Validacion basica que puede vivir en esta clase o en un validador de util: horaFin debe ser posterior a horaInicio, fecha no puede ser en el pasado al momento de crear la reserva.

## Notas para JAXB

Cada entidad que se persista de forma independiente (Funcionario, Categoria, Recurso, Reserva) tiene anotacion de elemento raiz para poder serializarse en su propio archivo o dentro de una lista contenedora, segun se defina en 03_persistence.md. Las referencias entre entidades, por ejemplo Reserva apuntando a Funcionario, se resuelven guardando el id como referencia (idFuncionario, idCategoria, idsCategoriasRequeridas, idsRecursosAsignados) y no el objeto completo embebido, para evitar duplicacion de datos en el XML y problemas de referencias circulares en la serializacion. Los campos de objeto completo (funcionario, categoria, categoriasRequeridas, recursosAsignados) estan marcados como XmlTransient, es decir JAXB los ignora por completo, y es la capa de service la encargada de llenarlos resolviendo esos ids contra los DAOs cuando se necesite el objeto completo.

LocalDate y LocalTime no los sabe convertir JAXB de forma nativa, por eso Reserva usa dos adaptadores ya creados, LocalDateAdapter y LocalTimeAdapter, marcados con XmlJavaTypeAdapter sobre los campos fecha, horaInicio y horaFin. No hace falta tocarlos, ya estan listos en el paquete model.

## Notas para quien use IA para generar estas clases

Generar clases Java planas, con getters y setters, constructor vacio (requerido por JAXB) y un constructor con todos los campos para comodidad. No agregar logica de negocio compleja, no agregar dependencias a persistence, service, control o presentation dentro de estas clases. Mantener los nombres de campos exactamente como estan listados aqui, porque service y persistence los referencian por nombre.
