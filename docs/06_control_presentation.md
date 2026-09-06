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

Esta seccion se actualiza conforme avance la implementacion, para que cualquier sesion de trabajo futura, con o sin IA, entienda en que punto quedo esta capa sin necesidad de releer todo el codigo fuente.

### Estado actual (login + ventana principal + cuenta)

Componentes reutilizables (Tarjeta, BotonPrimario, BotonSecundario, BotonIcono, CampoTexto, Popup, MatrizPanel/CeldaMatriz, GraficoBarras, TablaDatos) y los paquetes tema e iconos estan completos y en uso.

LoginPanel + LoginControl: completos en cuanto a interfaz y enganche de eventos. LoginControl.autenticar(id, clave) sigue lanzando una excepcion controlada a proposito, marcado con TODO(service), porque AutenticacionService todavia no existe (persistence y service estan vacios en el repo). No se debe reemplazar ese TODO por una autenticacion temporal (usuario fijo, archivo, etc.), ver la justificacion en el propio metodo y en docs/02_service.md.

PanelCuenta: completo (ver el propio .java para el detalle de por que TarjetaDatos/TarjetaAcciones son hojas custom-create="true" en el .form). Expone cargarDatos(nombre, id, telefono), setModoAdmin(boolean) para alternar entre Mi Perfil (propio) y Editando Perfil (un administrador editando a otro funcionario), y los enganches alGuardar/alCambiarContrasena/alCerrarSesion.

VentanaPrincipal + VentanaPrincipalControl (nuevos): VentanaPrincipal ya tiene tema aplicado (fondo, tipografia, icono de logo via IconoAplicador) y btnCuenta reemplazado por un BotonIcono real (antes era un JButton crudo sin custom-create, sin estilo). VentanaPrincipalControl.mostrarEn(JFrame) arma la vista, la conecta con SesionControl (nombre/rol en el encabezado, oculta las pestanas de administrador si el usuario es Funcionario) y engancha el boton de cuenta para abrir un JDialog propio con PanelCuenta en modo propio. Se uso un JDialog simple en vez de Popup para esto porque Popup.form esta disenado para icono+titulo+mensaje+botones (un aviso), no para alojar un formulario completo como PanelCuenta.

LoginControl.intentarIngresar ya navega a VentanaPrincipalControl.mostrarEn(...) tras iniciar sesion en SesionControl, y VentanaPrincipalControl.cerrarSesionYVolverALogin hace el camino inverso (vuelve a construir LoginPanel + LoginControl sobre el mismo JFrame). El flujo login, ventana principal, cerrar sesion es simetrico y esta completo del lado de presentation/control; falta unicamente que AutenticacionService exista para que intentarIngresar deje de fallar siempre.

Pendiente explicitamente fuera del alcance de esta capa (no tocar sin coordinar): persistence (companero A) y service (Joanfer, pero depende de persistence) siguen vacios para Reservas, Funcionarios, Recursos, Calendarizacion, Actividades y Estadisticas. Esas pestanas solo existen como pestanas vacias (pnlReservas, pnlFuncionarios, etc, expuestas por VentanaPrincipal.obtenerPanelX()) a la espera de sus propios control+vista, que a su vez dependen de que existan los servicios correspondientes.

### Estado actual (pestana Categorias)

CategoriaService ya existe (ver docs/02_service.md), asi que la pestana de Categorias (funcionalidad 4 del enunciado) esta completa en interfaz + cableado, siguiendo el mismo patron que PanelCuenta.

TabCategorias (nuevo, con su .form): tres paneles custom-create="true" (TarjetaTitulo, TarjetaCategoria, TarjetaTabla) que en createUIComponents() se reemplazan por instancias reales de Tarjeta con su contenido armado a mano, igual patron que PanelCuenta.createUIComponents(). No existe una clase separada "TarjetaTitulo": el encabezado con icono+titulo+subtitulo+boton de accion que cada pestana necesita es el propio encabezado que ya soporta Tarjeta (setIcono/setTitulo/setSubtitulo/setAccion), asi que cualquier pestana nueva (Funcionarios, Recursos, etc) arma su TarjetaTitulo de la misma forma, solo cambiando icono/titulo/subtitulo y que boton de reporte engancha.

La fila de "Datos de la Categoria" usa CampoTexto para ID (solo lectura, con setPlaceholder("Se autogenera al guardar") en vez de vacio, ya que CampoTexto.setPlaceholder hace setText real, no un placeholder que se borra al escribir) y Descripcion, mas BotonPrimario para Guardar y BotonIcono para Borrar/Limpiar. La tabla usa TablaDatos con columnas ID/DESCRIPCION/ACCIONES; la columna ACCIONES queda vacia a proposito porque TablaDatos no soporta botones embebidos por celda, la fila completa es clickeable via TablaDatos.alHacerClickFila (mismo efecto que el icono de editar de la captura de referencia, con menos codigo).

CategoriaControl (nuevo): mantiene una lista en memoria (categoriasMostradas) sincronizada con las filas visibles, para resolver "el usuario hizo click en la fila i" a una Categoria sin necesitar un buscarPorId publico en CategoriaService (ese metodo es intencionalmente package-private). Guardar crea o modifica segun haya o no una categoria seleccionada; Borrar pide confirmacion con un Popup de dos botones (Cancelar/Borrar) antes de llamar a CategoriaService.eliminar, que ya rechaza el borrado si hay recursos asociados; Buscar filtra contra CategoriaService.buscarPorDescripcion (no un filtro visual). El boton "Generar Reporte PDF" esta enganchado (alGenerarReporte) pero solo muestra un Popup informativo por ahora: ReporteFactory/ReporteCategorias no existen todavia (ver docs/04_report.md), marcado con TODO(report) en CategoriaControl.generarReporte, mismo criterio que el TODO(service) que ya existia en LoginControl antes de que AutenticacionService existiera. No reemplazar ese TODO por una generacion de PDF improvisada en control.

VentanaPrincipalControl.inicializarPestanas() arma TabCategorias+CategoriaControl y lo coloca dentro de vista.obtenerPanelCategorias(). Cualquier pestana nueva que se agregue debe registrarse ahi con el mismo patron (construir Tab*, construir su *Control, agregarlo al panel correspondiente).

Se agregaron los iconos ETIQUETA, BORRADOR, CAJA y MAS al enum Icono (paquete iconos) para esta pestana y para que las pestanas siguientes (Recursos usa una "caja", por ejemplo) no necesiten agregarlos de nuevo.
