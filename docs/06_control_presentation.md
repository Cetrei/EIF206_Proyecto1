# Control y presentacion

Paquetes: cr.ac.una.reservas.control y cr.ac.una.reservas.presentation

Responsable: Joanfer.

Este documento existe para dejar registro de las decisiones tomadas sobre esta parte, principalmente para continuidad si se retoma el trabajo en otra sesion o con ayuda de una IA.

## Control

Los controladores son deliberadamente delgados. Su unica responsabilidad es tomar un evento de la interfaz, por ejemplo un boton presionado, validar que los campos minimos esten presentes, llamar al metodo correspondiente de service, y actualizar la vista con el resultado o mostrar el error. No debe existir logica de negocio dentro de un controlador, esa vive en service.

### SesionControl

Implementado como Singleton, con un metodo estatico obtenerInstancia(). Guarda el Usuario actualmente logueado y su rol. Los demas controladores consultan aqui para saber si el usuario actual tiene permiso de ejecutar cierta accion, por ejemplo que solo un administrador pueda entrar a Funcionarios, Categorias o Recursos.

### Otros controladores

Uno por cada pantalla principal: LoginControl, ReservaControl, FuncionarioControl, CategoriaControl, RecursoControl, CalendarizacionControl, ActividadesControl, EstadisticaControl. Cada uno usa el servicio correspondiente descrito en 02_service.md.

## Presentation

Interfaz grafica en Swing. La meta declarada para este proyecto es superar visualmente el ejemplo de pantallas del enunciado, que es deliberadamente basico, manteniendo toda la funcionalidad pedida.

### Componente MatrizPanel

Componente reutilizable para las dos vistas tipo matriz que pide el enunciado, la calendarizacion de recursos por fecha y categoria, y la programacion semanal de actividades. Ambas comparten la misma estructura visual, una grilla con filas de horas y columnas variables, pero difieren en que va en cada columna y que se muestra en cada celda.

Se implementa con el patron Strategy. MatrizPanel se encarga de dibujar la grilla, las cabeceras de fila con las horas del dia, el scroll y el manejo general de la tabla. Recibe una implementacion de una interfaz MatrizFillStrategy que resuelve dos cosas especificas de cada caso: cuales son las columnas a mostrar, por ejemplo los recursos de una categoria o los dias de la semana, y que contenido y color va en cada celda dado una fila y columna especifica, ademas de que pasa si el usuario hace click en una celda ocupada.

Las dos implementaciones esperadas son CalendarizacionRecursoStrategy y ProgramacionActividadStrategy.

### Observadores en presentation

Las vistas de calendarizacion, actividades, y estadisticas si esta abierta en el momento, implementan la interfaz ReservaObserver descrita en 02_service.md y se registran contra ReservaService al abrirse, y se desregistran al cerrarse, para refrescarse automaticamente cuando se crea o cancela una reserva en cualquier parte del sistema, sin necesidad de refrescar manualmente ni de sondear el estado cada cierto tiempo.

### Pantallas esperadas

Login, cambio de clave, reservas del funcionario con opcion de extraccion por IA, funcionarios, categorias, recursos, calendarizacion de recursos, programacion de actividades, estadisticas con sus graficos. Todas con opcion de generar reporte en PDF donde el enunciado lo pide.

## Notas de continuidad

Esta seccion se actualiza conforme avance la implementacion, para que cualquier sesion de trabajo futura, con o sin IA, entienda en que punto quedo esta capa sin necesidad de releer todo el codigo fuente.
