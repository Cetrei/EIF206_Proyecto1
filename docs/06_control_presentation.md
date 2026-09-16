# Control y presentacion

Paquetes: cr.ac.una.reservas.presentation.controller, cr.ac.una.reservas.presentation.model y
cr.ac.una.reservas.presentation.mvc (con sus subpaquetes componentes, tema e iconos)

Responsable: Joanfer.

## Estructura de carpetas (calcada de PracticaExamen1)

Despues de la migracion a Modelo Observable, `control` se movio de ser una carpeta hermana de
`presentation` a vivir dentro de ella como `presentation.controller`, replicando exactamente la
estructura `presentacion/controller` + `presentacion/model` + `presentacion/mvc` del proyecto de
examen del curso (PracticaExamen1). Las Vistas (`LoginPanel`, `VentanaPrincipal`, los `TabXxx`, y
las dos Strategy de matriz) se movieron de estar sueltas en `presentation/` a `presentation/mvc/`.
Los paquetes reutilizables sin logica MVC (`componentes`, `tema`, `iconos`) quedaron anidados
dentro de `presentation/mvc/` en vez de como hermanos directos de `mvc`/`controller`/`model`; es
una diferencia cosmetica de organizacion de archivos, no de dependencias, y no afecta a ninguna
regla de esta capa.

Este documento existe para dejar registro de las decisiones tomadas sobre esta parte, principalmente para continuidad si se retoma el trabajo en otra sesion o con ayuda de una IA.

## Control

Los controladores son deliberadamente delgados. Su unica responsabilidad es tomar un evento de la interfaz, por ejemplo un boton presionado, validar que los campos minimos esten presentes, llamar al metodo correspondiente de service, y actualizar el Modelo con el resultado o mostrar el error via Popup. No debe existir logica de negocio dentro de un controlador, esa vive en service. Desde la migracion a Modelo Observable (ver seccion siguiente), el controlador tampoco pinta la Vista directamente: actualiza el Modelo, y es la Vista quien decide como reaccionar a ese cambio.

### SesionControl

Implementado como Singleton, con un metodo estatico obtenerInstancia(). Guarda el Usuario actualmente logueado y su rol. Los demas controladores consultan aqui para saber si el usuario actual tiene permiso de ejecutar cierta accion, por ejemplo que solo un administrador pueda entrar a Funcionarios, Categorias o Recursos. No es una pantalla, asi que no tiene Modelo Observable propio, es transversal a todos los controladores.

### Otros controladores

Uno por cada pantalla principal: LoginControl, ReservaControl, FuncionarioControl, CategoriaControl, RecursoControl, CalendarizacionControl, ActividadControl, EstadisticaControl, ademas de VentanaPrincipalControl para el shell que las contiene. Cada uno usa el servicio correspondiente descrito en 02_service.md, y desde la migracion recibe tambien el Modelo Observable de su propia pantalla (ver seccion siguiente).

## Modelo Observable (migracion a patron PracticaExamen1)

El profesor enseno y evalua un patron MVC especifico, con ejemplo de referencia en el proyecto de examen del curso (PracticaExamen1). Ese patron intercala un Modelo Observable entre Control y Vista: Control nunca pinta la Vista directamente, solo actualiza el Modelo, y la Vista se entera del cambio porque esta suscrita a el. Esta seccion documenta la decision de adoptar ese patron en este proyecto y cual es la forma final que quedo en cada capa; los detalles especificos de cada pantalla estan en "Notas de continuidad" mas abajo.

Antes de esta migracion, `control` llamaba metodos publicos de `presentation` (`vista.mostrarCategorias(...)`, `vista.mostrarUsuario(...)`, etc) para refrescar la interfaz despues de cada operacion. Eso funcionaba pero no es el patron que se enseno en clase ni el que se evalua, asi que se migro pantalla por pantalla a la forma siguiente:

**Modelo (`presentation/model/`)**: una clase por pantalla que extiende `AbstractModel` (copiado literal del `AbstractModel` de PracticaExamen1: envuelve un `java.beans.PropertyChangeSupport`, expone `addPropertyChangeListener` y un metodo protegido `notificarCambio(prop, anterior, nuevo)`). Cada Modelo declara una constante `PROP_*` por cada pieza de estado que la Vista necesita pintar, con su getter y su setter; el setter siempre llama `notificarCambio(...)`. El Modelo nunca importa Swing ni `service`, es estado puro.

**Vista (`presentation/`, las clases `TabXxx`/`LoginPanel`/`VentanaPrincipal`)**: implementa `PropertyChangeListener`, recibe el Modelo de su pantalla por constructor y se suscribe ahi mismo (`modelo.addPropertyChangeListener(this)`). En `propertyChange(evento)` compara `evento.getPropertyName()` contra las constantes `PROP_*` del Modelo y actualiza sus componentes Swing segun corresponda. Los metodos que antes eran publicos para que Control los llamara (por ejemplo `mostrarCategorias(List<Categoria>)`) pasaron a privados, porque ahora solo se disparan desde `propertyChange`. La Vista sigue exponiendo getters de sus componentes y metodos de utilidad como `limpiarFormularioX()` o `mostrarError()`, y en las pantallas con tabla+seleccion (Categorias, Recursos, Funcionarios, Reservas) agrega un metodo `xxxEnFila(int indice)` que resuelve el indice de fila visible a la entidad completa, reemplazando el estado duplicado que antes vivia en el Control para ese mismo proposito.

**Controlador (`control/`, las clases `XxxControl`)**: el constructor pasa de `(Vista, Frame)` a `(Modelo, Vista, Frame[, servicios])` (el `Frame` se mantuvo en todas las pantallas migradas, aunque el TODO original proponia quitarlo en Estadisticas y Reservas, porque sigue siendo necesario para los `Popup` de error/confirmacion y para `GeneracionReporteControl`; ver el Javadoc de cada Control para el razonamiento puntual). La regla fija del Controller, calcada de `ProyectoController` en PracticaExamen1, es: extraer datos de la Vista, delegar a `service` (que valida), actualizar el Modelo (nunca la Vista directamente), y en el catch llamar `vista.mostrarError()` o `Popup.mostrarAviso(...)`. Los unicos metodos de Vista que el Controller sigue llamando directo son los de configuracion de UI que no son estado de negocio observable: `limpiarFormularioX()`, dialogos modales efimeros como `Popup` o `pedirNuevaPrioridadYEstado`, y en Reservas los metodos de volcado puntual del flujo de IA (`vista.mostrarActividad(...)`, etc), porque son el resultado de una accion del usuario sobre el formulario en ese instante, no estado que deba sobrevivir a un repintado.

Regla de código: cero comentarios de linea o de bloque en las clases de Modelo/Vista/Control tocadas durante esta migracion (salvo Javadoc publico minimo donde una interfaz lo exige). Cualquier nota de contexto o decision va en este documento, no en el codigo.

Codigo muerto: cada version anterior de un `.java` reescrito durante la migracion (constructores viejos, clases enteras que ya no se instancian) se movio a `_deprecated/` en la raiz del proyecto, conservando la ruta relativa original con sufijo `.java.old` (por ejemplo `_deprecated/control/CategoriaControl.java.old`), para no perder nada por si hacia falta revertir. Esa carpeta esta fuera de `src/` a proposito para que Maven no la compile. Se borra por completo unicamente cuando todas las pantallas esten migradas, todo compile y la demo funcione.

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

### GUI Designer de IntelliJ y Maven

Los paneles se construyen con el GUI Designer de IntelliJ (.form mas clase bound), tal como se enseno en clase. GridLayoutManager (IntelliJ) es el layout manager usado por defecto en los formularios.

Maven no compila archivos .form de forma nativa, el metodo generado $$$setupUI$$$() solo lo procesa el compilador interno de IntelliJ. Mientras se trabaja dentro de IntelliJ esto no da ningun problema. Antes de empaquetar el entregable final, es obligatorio convertir cada .form a codigo Java plano:

Para cada panel con .form, click derecho sobre el archivo .form en el arbol de proyecto y usar la opcion de IntelliJ para generar el codigo Java del formulario (el texto exacto del menu varia segun version, buscar algo como generar codigo de GUI). Esto reemplaza el metodo generado por codigo fuente Java normal dentro del .java. Despues de generar el codigo para todos los paneles, borrar los archivos .form, ya no se necesitan, y correr mvn clean package para confirmar que el proyecto compila sin depender del runtime de IntelliJ. Recien despues de esto se genera el comprimido para el Aula Virtual.

Se puede seguir editando visualmente con el .form durante todo el desarrollo, este paso de exportar solo se hace una vez, cerca de la entrega.

### Paquete tema

cr.ac.una.reservas.presentation.tema centraliza toda la paleta de colores y fuentes de la interfaz, para que ningun componente tenga colores fijos escritos a mano.

Interfaz Tema con un metodo por cada color o fuente con nombre semantico (colorFondoTarjeta, colorPrimario, colorPeligro, etc), no por nombre de color literal, para que cambiar de tema no signifique renombrar nada en los componentes. Implementaciones: TemaOscuro (el tema por defecto y el que se ve en las capturas de referencia) y TemaClaro.

GestorTema es un singleton que guarda el Tema activo y lo expone con temaActivo(). Cuando se llama a establecerTema(nuevoTema), notifica a todos los listeners registrados (interfaz CambioTemaListener, con un solo metodo onCambioTema(Tema)). Cada componente de presentation.componentes se registra como listener en su constructor y repinta sus colores cuando el tema cambia. Este es el mismo patron Observer que ya se uso en ReservaService, aplicado aqui a cambios de tema en vez de cambios de reserva.

Los componentes nunca preguntan que tema hay activo mas alla de leerlo una vez al construirse o cuando onCambioTema se los da; no deben guardar su propia copia de colores fuera de eso, para que un cambio de tema en caliente los actualice a todos sin excepcion.

### Paquete iconos

cr.ac.una.reservas.presentation.iconos implementa un sistema de iconos por fuente (icon font) sin depender de ninguna libreria externa, usando unicamente Font.createFont del JDK.

La fuente usada es Font Awesome 6 Free Solid (fa-solid-900.ttf), licencia SIL Open Font License, gratuita y de uso libre. El archivo va en src/main/resources/fonts/fa-solid-900.ttf, para que quede empacado dentro del jar final y no dependa de que el icono este instalado en el sistema operativo de quien ejecute el programa.

Icono es un enum que mapea un nombre semantico (USUARIO, CANDADO, BASURA, AJUSTES, PDF, EDITAR, etc) a su punto de codigo Unicode dentro de esa fuente. FuenteIconos carga el archivo .ttf una sola vez con Font.createFont y lo cachea, exponiendo obtenerFuente(tamano) para pedirlo a un tamano especifico. IconoAplicador tiene metodos de conveniencia para aplicarle un icono, tamano y color a un JLabel o a un AbstractButton (JButton) en una sola llamada.

Si se necesita un icono nuevo que no este en el enum, buscar su punto de codigo en el mapa de glifos oficial de Font Awesome (fontawesome.com/search, estilo Solid) y agregarlo a Icono.

### Componentes reutilizables (presentation.componentes)

Cada componente visual repetido en varias pantallas (segun las capturas de referencia: login, funcionarios, categorias) es su propia clase con .form propio, para no repetir estilos y para que un cambio de diseno se haga en un solo lugar.

Tarjeta: panel contenedor con fondo redondeado (dibujado a mano con Graphics2D, fillRoundRect), usado como el contenedor principal de cada pantalla, como se ve en el login y en los formularios de categorias y funcionarios.

CampoTexto: JTextField con label arriba, icono opcional a la izquierda, fondo y borde segun el tema activo. setEtiqueta, setPlaceholder y setIcono permiten configurarlo dinamicamente para reusarlo en cualquier campo del sistema, no solo en login.

BotonPrimario: boton solido con el color primario del tema, esquinas redondeadas dibujadas a mano, y efecto hover. Es el boton de accion principal, por ejemplo Ingresar al Sistema, Guardar, Extraer Datos con IA.

BotonSecundario: mismo tamano y forma que BotonPrimario, pero con fondo transparente y borde mas notorio, dibujado con Graphics2D. Tiene una Variante (NEUTRO o PELIGRO); PELIGRO usa el color de peligro del tema para el borde y el texto, como el boton Generar Reporte PDF en las capturas de referencia.

BotonIcono: boton cuadrado de solo icono, sin texto, para acciones secundarias dentro de una fila o barra, como el icono de basura junto a Guardar, el candado o el engranaje de ajustes de cuenta, o el boton de apagar/cerrar sesion en la barra superior. Tambien tiene Variante NEUTRO o PELIGRO igual que BotonSecundario.

Todos los componentes de esta lista implementan CambioTemaListener y se registran contra GestorTema en su propio constructor, por lo que cualquier pantalla que los use no necesita preocuparse por el tema, cada componente se mantiene actualizado por si mismo.

## Notas de continuidad

Esta seccion se actualiza conforme avance la implementacion, para que cualquier sesion de trabajo futura, con o sin IA, entienda en que punto quedo esta capa sin necesidad de releer todo el codigo fuente. El detalle de que hace cada pantalla funcionalmente (que servicio usa, que reportes genera, decisiones de negocio como quien ve que) esta debajo tal como se documento cuando cada pantalla se construyo por primera vez; lo que cambio con la migracion a Modelo Observable (seccion de arriba) fue exclusivamente el mecanismo de comunicacion entre Control y Vista, no el comportamiento visible de ninguna pantalla.

### Estado actual: las 9 pantallas migradas a Modelo Observable

Las 8 funcionalidades del enunciado (Login+cuenta, Reservas, Funcionarios, Categorias, Recursos, Calendarizacion, Actividades, Estadisticas) mas la VentanaPrincipal que las contiene ya siguen el patron Modelo-Vista-Control de PracticaExamen1 descrito en "Modelo Observable" mas arriba. Por pantalla, el Modelo que quedo es:

- `LoginModel`: `PROP_MENSAJE_ERROR` (String). El flujo de cambio de clave sigue usando `Popup.mostrarAviso` directo desde `LoginControl`, no es estado observable de esta pantalla.
- `VentanaPrincipalModel`: un unico `PROP_USUARIO`, guarda nombre y rol juntos porque siempre se muestran a la vez.
- `CategoriaModel`: `PROP_CATEGORIAS` (List\<Categoria\>), `PROP_CATEGORIA_SELECCIONADA` (Categoria o null).
- `RecursoModel`: `PROP_RECURSOS`, `PROP_RECURSO_SELECCIONADO`, `PROP_CATEGORIAS_DISPONIBLES` (para el combo de categoria del formulario y el filtro).
- `FuncionarioModel`: `PROP_FUNCIONARIOS`, `PROP_FUNCIONARIO_SELECCIONADO`.
- `CalendarizacionModel` y `ActividadModel`: cada uno con `PROP_MATRIZ` (la `MatrizFillStrategy`/`ProgramacionActividadStrategy` ya armada por el Control, sin descomponerla en listas propias); `CalendarizacionModel` ademas tiene `PROP_CATEGORIAS_DISPONIBLES` para el combo de filtro.
- `EstadisticaModel`: `PROP_RECURSOS`/`PROP_SUBTITULO_RECURSOS` y `PROP_ACTIVIDADES`/`PROP_SUBTITULO_ACTIVIDADES`, dos pares independientes porque son dos filtros de fecha sin relacion entre si.
- `ReservaModel`: `PROP_CATEGORIAS_DISPONIBLES`, `PROP_RESERVAS`, `PROP_RESERVA_SELECCIONADA`, `PROP_RESULTADO_INTENTO` (el `ResultadoReserva` del ultimo intento, exito o fracaso). Cuidado deliberado: en `TabReservas.propertyChange`, `PROP_RESULTADO_INTENTO` solo dispara `limpiarFormulario()` cuando el resultado fue exitoso; en fracaso el formulario queda intacto para que el usuario pueda ajustar los datos y reintentar, tal como exige el enunciado. El Modelo nunca decide limpiar nada por si mismo, solo expone el resultado.

`VentanaPrincipalControl.inicializarPestanas()` es el unico lugar donde se arma cada par Modelo+Vista+Control: construye el Modelo de la pantalla una sola vez y lo inyecta tanto en el `TabXxx` como en el `XxxControl` correspondiente (no se agrego ningun `obtenerModelo()` a las Vistas para que Control se lo pida; el Modelo se construye aparte y se pasa a ambos).

Cada `XxxControl` viejo (constructor `(Vista, Frame[, servicios])`, sin Modelo) esta conservado en `_deprecated/control/XxxControl.java.old` por si hace falta revertir algo, y se borra recien cuando la seccion "Antes de entregar" del TODO de migracion quede completa.

Popup, PanelCuenta y el resto de `presentation.componentes` (Tarjeta, BotonPrimario, BotonSecundario, BotonIcono, CampoTexto, TablaDatos, BarraSuperior, MatrizPanel/CeldaMatriz, GraficoBarras, PanelEstadistica) no llevan Modelo Observable propio a proposito: son componentes reutilizables sin logica de negocio, exponen callbacks (`alHacerClick`, `alGuardar`, `alSeleccionarFila`, etc) sobre su propio estado visual interno, igual que antes de la migracion.

### Historial funcional por pantalla (que hace cada una, previo a la migracion de Modelo)

Lo que sigue describe el comportamiento funcional de cada pantalla (que servicio usa, que valida, que reporte genera, decisiones de negocio) tal como se establecio cuando cada una se construyo. Nada de esto cambio con la migracion a Modelo Observable, solo cambio como Control y Vista se comunican entre si (ver seccion de arriba).

**Login + sesion + cuenta**: `LoginControl.intentarIngresar` delega en `AutenticacionService`, y en exito navega a `VentanaPrincipalControl.mostrarEn(...)`; `VentanaPrincipalControl.cerrarSesionYVolverALogin` hace el camino inverso. `PanelCuenta` expone `cargarDatos`, `setModoAdmin` (alterna "Mi Perfil" vs "Editando Perfil") y los enganches `alGuardar`/`alCambiarContrasena`/`alCerrarSesion`.

**Categorias**: `CategoriaService` ya validaba todo (crear/modificar/eliminar con reglas de negocio, `buscarPorDescripcion` para el filtro). `TabCategorias` arma su UI con `Tarjeta`+`CampoTexto`+`BotonPrimario`+`BotonIcono`+`TablaDatos`, misma estructura que las pantallas de Funcionarios y Recursos que se construyeron despues siguiendo este mismo patron. El boton "Generar Reporte PDF" ya esta conectado a `ReporteFactory`/`ReporteCategorias`.

**Calendarizacion y Actividades**: `EscritorPdfBasico` (paquete report) transforma cualquier lista de datos a tabla PDF sin conocer la libreria usada. `CalendarizacionRecursoStrategy` arma, por cada recurso de la categoria filtrada, la disponibilidad por hora del dia; `ProgramacionActividadStrategy` arma 7 columnas (lunes a domingo) y permite mas de una reserva por celda a la vez (`DatosCelda.apilada(lineas, color)`, pintada en `colorPeligro()` del tema cuando hay 2+ actividades apiladas). Ambos controles siguen implementando `ReservaObserver` y registrandose en `ReservaService` para refrescar su matriz automaticamente cuando se crea o cancela una reserva en cualquier parte del sistema; ese Observer es distinto del patron Modelo Observable de presentation y no se toco al migrar (`onReservaCreada`/`onReservaCancelada` ahora actualizan el Modelo en vez de la Vista, pero la suscripcion en si es identica a como estaba).

**Estadisticas**: unica pantalla sin `.form` propio, su contenido son dos instancias del componente reutilizable `PanelEstadistica<T>` (uno para "Recursos Usados por Categoria", otro para "Actividades por Semana"), cada uno con su propio filtro de fecha "desde"/"hasta" y boton "Cargar", sin auto-refresco por cambios de reserva en otras pestanas (a diferencia de Calendarizacion/Actividades, aqui el usuario elige un rango explicito). `ReporteEstadisticas` es la unica implementacion de `GeneradorReporte<T>` que escribe dos tablas independientes en el mismo PDF (`DatosReporteEstadisticas` agrupa ambas listas + sus subtitulos de periodo), soportado por la sobrecarga `EscritorPdfBasico.generar(ruta, titulo, subtitulo, List<Seccion>)`.

**Reservas**: `ReservaControl.solicitarReserva` arma un `DatosNuevaReserva` con los datos del formulario y el id del usuario logueado, y lo pasa a `ReservaService.intentarReservar`. Decision sobre quien ve que: aunque el enunciado dice que Reservas es solo para funcionarios, la pestana nunca se oculta para administrador (a diferencia de Funcionarios/Categorias/Recursos) porque el prototipo de referencia del enunciado muestra justamente a un administrador usando esta pantalla; se resolvio permitiendo que un administrador tambien reserve a su propio nombre, pero viendo el listado de TODAS las reservas del sistema (`ReservaService.listarTodasOrdenadas`) en vez de solo las propias (`ReservaService.listarReservasDeFuncionario` para un funcionario). Cancelar sigue el mismo patron de confirmacion con `Popup` de dos botones que `CategoriaControl.confirmarBorrado`, delegando en `ReservaService.cancelarReserva`. El boton "Extraer Datos con IA" ya esta completamente conectado a `ExtractorReservaService` (categorias + frase libre → `DatosReservaExtraidos` → volcado en el formulario con `mostrarActividad`/`mostrarFecha`/`mostrarHoraInicio`/`mostrarHoraFin`/`mostrarCategoriasSeleccionadas`); el usuario siempre revisa y confirma con el mismo boton "Solicitar Reserva", la IA nunca aplica la reserva por su cuenta.

Con esto, las 8 funcionalidades del enunciado tienen vista+control+reporte conectados y migrados al patron Modelo Observable. Pendiente fuera del alcance de esta capa: nada relevante a `control`/`presentation` en este momento; ver el TODO de migracion en la raiz del proyecto para los checklists finales de Tests, Documentacion (este mismo archivo y `00_arquitectura_general.md`), export de `.form` a codigo plano, y borrado de `_deprecated/`.

### Correccion de validacion de formato faltante (trim e inputs)

Revision posterior encontro varios controles que no recortaban espacios de los campos de texto antes de pasarlos a `service`, a pesar de que la validacion de formato basico es responsabilidad de `control` segun 07_convenciones.md. Se corrigio en `LoginControl.intentarIngresar` y `LoginControl.confirmarCambioClave` (trim del ID), `RecursoControl.guardar` (trim de ID y descripcion), `CategoriaControl.guardar` (trim de descripcion), y `VentanaPrincipalControl.guardarMiCuenta` (que no validaba el telefono en absoluto; ahora exige no vacio y el mismo formato que `FuncionarioControl`, con trim). `ConfiguracionIaControl.guardar` tambien se ajusto para recortar el API key antes de llamar al service, aunque es redundante: `ConfiguracionIaService.guardar` ya hacia `trim()` internamente, asi que ese caso no era un bug real.

Tests agregados, todos siguiendo el patron existente de instanciar la Vista real (no un mock) e inyectar valores con espacios via sus metodos `mostrarXxx`, luego disparando el guardado real (click del boton via el arbol Swing, o reflection sobre el metodo privado cuando la Vista no expone gancho publico, como en el dialogo de cambio de clave de Login y en `guardarMiCuenta`): `RecursoControlTest`, `CategoriaControlTest` y el nuevo `LoginControlTest` corren con Surefire porque usan Dao/Service falsos. `VentanaPrincipalControlIT` es nuevo y corre con Failsafe en vez de Surefire, porque `VentanaPrincipalControl.inicializarPestanas()` siempre resuelve servicios via `ServiceFactory`/`DaoFactory` reales (XML en `reservas.data.dir`) sin gancho de inyeccion para las pantallas que arma internamente, asi que no es aislable como prueba de unidad; usa un ID de funcionario unico por prueba (`System.nanoTime()`) para no chocar con otros datos de prueba.

### Popup de carga en extraccion IA y robustez de los spinners de fecha

Se resolvieron los dos pendientes del README. `ReservaControl.extraerConIA` llamaba a `ExtractorReservaService.extraer` (HTTP bloqueante, hasta 25s de timeout mas reintento local) directo en el callback del boton, que corre en el EDT, dejando la interfaz congelada sin ningun indicador. Se dividio en `extraerConIA` (valida la frase, muestra `Popup.mostrarCargando` y lanza un `SwingWorker`), `procesarResultadoExtraccion` (lo que antes era el cuerpo del try, ahora corre en `done()` ya en el EDT) y `procesarFalloExtraccion` (lo que antes era el catch, ahora se dispara si `get()` lanza `ExecutionException`/`InterruptedException`). `Popup` gano un segundo constructor `Popup(Frame, boolean modal)` y un `Popup.mostrarCargando(Frame, titulo, mensaje)` que crea un dialogo no modal, sin botones, `DO_NOTHING_ON_CLOSE` para que no se pueda cerrar con Alt+F4, y que el propio control cierra llamando `popup.cerrar()` cuando el worker termina; el tipo nuevo `Popup.Tipo.CARGANDO` usa el icono `NUBE` del tema.

Los tres `JSpinner` de fecha del sistema (`TabReservas`, `TabCalendarizacion`, `PanelEstadistica`) usaban `JSpinner.DateEditor(spinner, "dd/MM/yyyy")`, cuyo `DateFormatter` interno es estricto: escribir `5/5/26` a mano no parseaba porque exige ceros y año de 4 digitos en las posiciones exactas. Se agrego `EditorFechaFlexible` (presentation.mvc.componentes, clase utilitaria estatica igual que `SpinnerTematizado`) con `aplicar(JSpinner)`, que instala un `JFormattedTextField.AbstractFormatter` propio: intenta parsear con `SimpleDateFormat` lenient contra una lista de patrones (`dd/MM/yyyy`, `d/M/yyyy`, `dd/MM/yy`, `d/M/yy`, `yyyy-MM-dd`) tanto en edicion manual como al usar las flechas del spinner (mismo editor subyacente), y siempre re-muestra `dd/MM/yyyy` al confirmar. Reemplaza el `spinner.setEditor(new JSpinner.DateEditor(...))` original en los tres lugares.

De paso se reviso que ninguna pantalla con boton de carga ("Cargar", "Cargar Matriz") dejara un fallo en silencio, que era el caso real: `CalendarizacionControl.cargarMatriz`/`generarReporte` y `ActividadControl.cargarSemana`/`generarReporte` no tenian ningun try/catch, asi que cualquier excepcion (incluida una fecha nula del spinner) moria en el listener de Swing sin avisar al usuario. Ambos ahora validan fecha nula antes de llamar a `service` y envuelven el resto en `catch (RuntimeException)`, mostrando `Popup.mostrarAviso` de tipo ERROR. `EstadisticaControl.cargarRecursos`/`cargarActividades` ya capturaban `ReservaAppException`; se les agrego la misma validacion de fecha nula y un segundo catch de `RuntimeException` para no dejar pasar fallos que no sean reglas de negocio.

Bug relacionado encontrado despues: si el usuario escribia una fecha a mano en el spinner y hacia click directo en el boton de accion (Cargar, Cargar Matriz, Solicitar Reserva, Generar Reporte) sin que el campo de texto perdiera el foco antes, `JFormattedTextField` nunca dispara su `commitEdit()` automatico, asi que `spinner.getValue()` devolvia el valor anterior en vez de lo tecleado, sin ningun aviso. Se agrego `EditorFechaFlexible.confirmarEdicion(JSpinner)`, que fuerza `campoTexto.commitEdit()` y, si el texto no es parseable ni por el formatter tolerante, revierte el campo a mostrar el ultimo valor valido en vez de dejarlo inconsistente. Se invoca al inicio de los cuatro metodos que leen un spinner de fecha antes de actuar: `TabReservas.obtenerFecha`, `TabCalendarizacion.obtenerFecha`, `PanelEstadistica.obtenerFechaDesde` y `obtenerFechaHasta`; con eso cubre todos los botones que dependen de esos metodos (Solicitar Reserva, Generar Reporte de Reservas, Cargar Matriz y su reporte en Calendarizacion, y los dos "Cargar" de Estadisticas) sin duplicar la logica en cada Control.


