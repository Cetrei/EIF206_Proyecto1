# Generacion de reportes PDF

Paquete: cr.ac.una.reservas.report

Responsable: Companero B.

## Proposito

El enunciado pide que todas las funcionalidades incluyan la opcion de generar un reporte en formato PDF de lo que se este mostrando, por ejemplo el listado de categorias, el listado de funcionarios, la calendarizacion de un dia, la programacion de una semana, o las estadisticas. Esta capa centraliza esa generacion para que no se repita codigo de PDF en cada vista.

## Antes de empezar, verificar politica de librerias con el profesor

En este curso, la regla general es no usar librerias que no se hayan visto en clase, se promueve implementar las cosas a mano. Java no trae una forma nativa de generar PDF, asi que antes de agregar una libreria como iText o Apache PDFBox, confirmar con el profesor o revisar si en clase ya se vio alguna forma de generar PDF. Si no se vio ninguna y no hay objecion del profesor a usar una puntualmente para este caso, iText o PDFBox son las opciones estandar de la industria. Si el profesor no permite ninguna libreria nueva, la alternativa es generar el PDF manualmente escribiendo el formato PDF basico como texto, o generar el reporte como HTML y usar una utilidad de impresion a PDF del propio sistema operativo, pero esto ultimo es mucho mas trabajo y solo se recomienda si no queda otra opcion.

Esta decision hay que tomarla antes de escribir codigo de esta capa, para no rehacer el trabajo despues.

## Interfaz esperada

Definir una interfaz GeneradorReporte de tipo T, con un metodo generar(String rutaDestino, List de T datos, mapa opcional de metadatos como titulo del reporte). El metodo produce un archivo PDF en la ruta indicada y no devuelve nada, o devuelve un booleano de exito si se prefiere para manejo de errores.

Implementaciones esperadas, una por tipo de listado que necesite reporte, por ejemplo ReporteFuncionarios, ReporteCategorias, ReporteRecursos, ReporteCalendarizacion, ReporteActividades, ReporteEstadisticas. Todas implementan GeneradorReporte con el tipo de dato que corresponda.

## Formato esperado del reporte

Basandose en el ejemplo del enunciado, ejemplo de reporte PDF con el listado de categorias, el formato minimo esperado es: un titulo describiendo el reporte, y una tabla con encabezados claros y una fila por cada registro. No es necesario un diseno elaborado, pero si debe ser legible y presentable para la defensa. Si se quiere mejorar el diseno respecto al ejemplo del enunciado, es bienvenido, siempre que la tabla siga siendo el elemento central.

## Como se conecta con el resto del sistema

La capa de control o presentation, al presionar el boton de imprimir o generar reporte, obtiene el generador correspondiente a traves de una fabrica ReporteFactory definida en service, ver 02_service.md, pasa los datos ya obtenidos de service, y llama a generar. Esta capa no necesita saber de donde vienen los datos ni como se obtuvieron, solo recibe una lista de objetos y produce el PDF.

## Notas para quien use IA para generar codigo en esta capa

Genera unicamente clases dentro del paquete report. No agregues logica de negocio ni acceso a persistence o service directamente, esta capa solo recibe datos ya listos y produce un archivo. Si necesitas saber la forma exacta de los datos de alguna entidad, revisa 01_model.md. Antes de sugerir agregar una dependencia de generacion de PDF, recuerda que este curso restringe el uso de librerias no vistas en clase, confirma esa restriccion con el equipo antes de asumir que cualquier libreria de PDF esta permitida.
