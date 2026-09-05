# Capa de servicio

Paquete: cr.ac.una.reservas.service

Responsable: Joanfer.

## Proposito

Contener toda la logica de negocio del sistema. Es la capa mas importante porque aqui vive la regla central del proyecto: la asignacion de recursos a una reserva. Control llama a service, service usa persistence para leer y guardar datos, y usa model para representar las entidades. Service nunca importa nada de presentation ni de control.

## Servicios principales

### AutenticacionService

Metodos esperados: autenticar(id, clave) devuelve el Usuario si las credenciales son correctas o lanza una excepcion propia si no lo son, cambiarClave(idUsuario, claveActual, claveNueva).

### FuncionarioService

Metodos esperados: buscarPorId(id), buscarPorNombre(nombre), listarTodos(), crear(funcionario), donde la clave se inicializa igual al id, modificar(funcionario), eliminar(id).

### CategoriaService

Metodos esperados: buscarPorDescripcion(texto), listarTodas(), crear(categoria), donde el id se autogenera, modificar(categoria), eliminar(id).

### RecursoService

Metodos esperados: listarPorCategoria(idCategoria), buscarPorDescripcion(texto), crear(recurso), modificar(recurso), eliminar(id).

### ReservaService

Este es el servicio central del proyecto. Metodos esperados:

listarReservasDeFuncionario(idFuncionario)

intentarReservar(datosReserva) donde datosReserva incluye actividad, fecha, horaInicio, horaFin, funcionario y la lista de categorias requeridas. Este metodo debe primero verificar disponibilidad de al menos una unidad libre de cada categoria requerida en ese rango de fecha y hora, sin comprometer ningun recurso todavia. Si todas las categorias tienen disponibilidad, recien ahi se asigna el primer recurso disponible de cada una y se crea la reserva con estado ACTIVA. Si alguna categoria no tiene disponibilidad, el metodo debe devolver informacion de cuales categorias fallaron, sin crear ninguna reserva ni comprometer ningun recurso. Esto evita reservas parciales, es decir, nunca se debe quedar una reserva a medias con solo algunos recursos asignados.

cancelarReserva(idReserva), que libera todos los recursos asignados y cambia el estado a CANCELADA. Solo aplica a reservas futuras segun el enunciado.

Al crear o cancelar una reserva con exito, ReservaService debe notificar a sus observadores registrados, ver la seccion de Observer mas abajo.

### EstadisticaService

Metodos esperados: recursosReservadosEnPeriodo(desde, hasta) devuelve, por categoria, la cantidad de veces que se reservo un recurso de esa categoria en el rango dado. actividadesPorSemanaEnPeriodo(desde, hasta) devuelve, por semana dentro del rango, la cantidad de actividades programadas en esa semana.

## Patron Observer en ReservaService

Definir una interfaz ReservaObserver con dos metodos: onReservaCreada(Reserva reserva) y onReservaCancelada(Reserva reserva).

ReservaService mantiene una lista interna de observadores, con metodos agregarObservador(ReservaObserver observador) y quitarObservador(ReservaObserver observador). Cuando intentarReservar tiene exito, o cuando cancelarReserva se completa, ReservaService recorre la lista y llama al metodo correspondiente en cada observador.

Las vistas de presentation que necesiten refrescarse automaticamente, como la de calendarizacion, la de actividades y la de estadisticas si esta abierta, implementan ReservaObserver y se registran al abrirse. Esto evita que ReservaService necesite conocer clases de presentation, la dependencia va en el sentido correcto: presentation conoce a service, no al reves. ReservaService solo conoce la interfaz ReservaObserver, que vive en el mismo paquete service o en un paquete comun de contratos.

## Patron Factory para crear DAOs y generadores de reporte

Service no debe instanciar directamente clases concretas de persistence como FuncionarioDaoXml, ni clases concretas de report. En su lugar, se usa una fabrica, por ejemplo DaoFactory con un metodo como obtenerFuncionarioDao() que devuelve un tipo de interfaz Dao, y ReporteFactory con un metodo como obtenerGenerador(tipoReporte) que devuelve un GeneradorReporte. Esto permite cambiar la implementacion concreta sin tocar el codigo de service.

## Manejo de errores

Todas las validaciones de negocio, por ejemplo intentar reservar con fecha pasada, o intentar eliminar un funcionario que tiene reservas activas, deben lanzar excepciones propias del paquete util, por ejemplo ReglaDeNegocioException, con un mensaje claro que la capa de control pueda mostrar directamente al usuario sin tener que interpretarlo.

## Notas para quien use IA para generar codigo en esta capa

Esta capa depende de las interfaces Dao definidas en persistence, ver 03_persistence.md, y de las clases de model, ver 01_model.md. No generar acceso directo a archivos ni a XML aqui, eso vive exclusivamente en persistence. No generar codigo de interfaz grafica aqui. Si se necesita un dato que persistence todavia no provee, senalarlo explicitamente en vez de inventar una implementacion temporal que quede olvidada.
