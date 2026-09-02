# Extraccion de datos de reserva mediante IA

Paquete: com.una.reservas.ai

Responsable: Companero B.

## Proposito

El enunciado permite que el usuario escriba una frase en lenguaje natural describiendo la actividad, fecha, horas y recursos que necesita, y que el sistema use un modelo de lenguaje para extraer esos datos y llenar el formulario de reserva automaticamente, dejando al usuario la posibilidad de corregir antes de confirmar.

## Interfaz esperada

Ya estan creadas en el paquete ai las clases DatosReservaExtraidos y la interfaz ExtractorReserva, con exactamente esta forma:

ExtractorReserva tiene un unico metodo, extraer(String frase, List de Categoria categoriasDisponibles), que devuelve un DatosReservaExtraidos.

DatosReservaExtraidos tiene los campos actividad, fecha (LocalDate), horaInicio (LocalTime), horaFin (LocalTime) e idsCategoriasIdentificadas (lista de String, los ids de las categorias identificadas dentro de categoriasDisponibles). Cualquier campo que no se logre identificar en la frase queda nulo o vacio, y el usuario lo completa manualmente en el formulario.

No es necesario crear estas dos clases, ya existen. Se implementa contra ellas tal cual estan.

## Implementaciones esperadas

GeminiExtractorReserva, que llama a la API de Gemini de Google, enviando la frase del usuario junto con la lista de categorias disponibles, pidiendole al modelo que devuelva los datos en un formato estructurado, idealmente JSON, para poder parsearlo facilmente.

ReglasExtractorReserva, una implementacion de respaldo basada en reglas simples de texto, por ejemplo buscar patrones de fecha, patrones de hora, y coincidencia de palabras contra los nombres de las categorias disponibles. No necesita ser sofisticada, es un respaldo para cuando la API no esta disponible.

## Comportamiento esperado del servicio que las usa

Se recomienda un ExtractorReservaService que intenta primero con GeminiExtractorReserva, y si ocurre un error, por ejemplo sin conexion, sin llave configurada, tiempo de espera agotado, o error de la API, cae automaticamente a ReglasExtractorReserva sin que el usuario vea un error tecnico, como mucho un aviso de que se uso el modo basico de extraccion. Esto sigue el patron Strategy, el resto del sistema solo conoce la interfaz ExtractorReserva y no necesita saber cual de las dos implementaciones respondio.

## Sobre la API de Gemini

Gemini ofrece un nivel gratuito con limite de solicitudes razonable para un proyecto academico, suficiente para las pruebas de este proyecto y para la defensa. Se recomienda usar el modelo de la familia Gemini Flash por ser mas rapido y con mayor cuota gratuita, dejando el modelo Pro solo si se necesita mayor precision. La llave de API no debe subirse al repositorio, debe leerse de una variable de entorno o de un archivo de configuracion local que este listado en el gitignore del proyecto.

## Formato de intercambio con el modelo

Se recomienda pedirle al modelo explicitamente que responda solo con un objeto JSON con las claves actividad, fecha, horaInicio, horaFin y categorias, sin texto adicional antes ni despues, para poder parsear la respuesta de forma directa. Incluir en la instruccion al modelo la lista de categorias disponibles para que solo pueda elegir entre esas.

## Como se conecta con el resto del sistema

La capa de control, al presionar el boton de extraer, llama a ExtractorReservaService con la frase escrita por el usuario y la lista de categorias que provee CategoriaService, ver 02_service.md, y recibe de vuelta un DatosReservaExtraidos que usa para prellenar los campos del formulario de reserva en presentation. Esta capa no decide si la reserva se confirma o no, solo propone datos.

## Notas para quien use IA para generar codigo en esta capa

Genera unicamente clases dentro del paquete ai. No manejes aqui la logica de si hay disponibilidad de recursos, eso es responsabilidad de ReservaService en la capa de service. Si necesitas la forma exacta de Categoria, revisa 01_model.md.
