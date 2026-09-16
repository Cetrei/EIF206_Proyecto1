# TODO — Estado del proyecto

## COMPLETADO — Migración a MVC con Modelo Observable (patrón PracticaExamen1)

La migración de `control`+`presentation` al patrón enseñado en clase (Modelo Observable con
`AbstractModel`/`PropertyChangeSupport`, Vista como `PropertyChangeListener`, Control que nunca
pinta la Vista directamente) está terminada. Las 8 funcionalidades del enunciado más
`VentanaPrincipal` siguen el patrón. `ServiceFactory` (calcado de `DaoFactory`) resolvió el bug de
fondo de Services sin estado compartido entre controladores. No queda carpeta `_deprecated/`.

## COMPLETADO — Verificación de rol por controlador (hallazgo de revisión contra la rúbrica)

El enunciado dice explícitamente que Funcionarios, Categorías y Recursos "sólo la podrá ejecutar
un usuario tipo administrador". Antes, esa restricción era únicamente visual. Se corrigió:

- `CategoriaControl`, `RecursoControl` y `FuncionarioControl` verifican
  `SesionControl.obtenerInstancia().esAdministrador()` al inicio de su constructor con
  inyección de dependencias, y lanzan `ReglaDeNegocioException` si el usuario actual no es
  administrador.
- `VentanaPrincipalControl.inicializarPestanas()` ya no construye esos tres controladores cuando
  el usuario no es administrador.
- Tests en `presentation/controller/CategoriaControlTest.java`, `RecursoControlTest.java`,
  `FuncionarioControlTest.java` confirman el rechazo con un `Funcionario` en `SesionControl`.
  El caso complementario (que un `Administrador` NO sea rechazado por rol) también existe en
  esos archivos, con la limitación de la siguiente sección.

Confirmado con `mvn test && mvn verify` real en esta máquina: 141 tests, 0 fallos, BUILD SUCCESS
para ambos, incluyendo la prueba de integración `FuncionarioDaoXmlIT`.

## PENDIENTE — Exportar los `.form` de IntelliJ a código Java plano antes de entregar

Esto **no es solo un paso de empaquetado**: es una dependencia real y ya detectada del código
fuente sobre el runtime del GUI Designer de IntelliJ, más allá de que "en IntelliJ compile y
funcione".

`TabCategorias`, `TabRecursos`, `TabFuncionarios` (y el resto de Vistas con `.form`) inicializan
sus componentes (botones, campos, tabla) dentro de un método `createUIComponents()`. Ese método
NUNCA se llama directamente desde el constructor de la clase: lo invoca `$$$setupUI$$$()`, un
método que el GUI Designer de IntelliJ genera e inyecta en tiempo de edición del `.form`, y que
solo se ejecuta cuando la clase corre bajo ese runtime (dentro de IntelliJ, o en un jar donde ya
se exportó ese código a Java plano). `mvn test`/`mvn verify` en la terminal SÍ compilan y corren
sin IntelliJ abierto, pero JUnit ejecuta el bytecode compilado por `javac` puro, sin ese soporte:
cualquier código que instancie una de estas Vistas directamente (no a través de abrir la ventana
completa en la app real) encuentra sus campos en `null`.

Esto se confirmó al agregar `CategoriaControlTest`/`RecursoControlTest`/`FuncionarioControlTest`
(ver sección anterior): el caso que construye el controlador con una Vista real y un usuario
administrador falla con `NullPointerException` en `botonGuardarReal` — no por un bug de la
lógica de rol, sino porque `TabCategorias`/`TabRecursos`/`TabFuncionarios` nunca llaman a
`createUIComponents()` fuera de IntelliJ. Los tests se ajustaron para no construir esas Vistas
reales y así no depender de esta limitación mientras siga sin resolverse, pero la limitación en
sí solo desaparece exportando los `.form`.

**Pasos:** para cada `.form`, click derecho en el árbol de proyecto de IntelliJ → generar el
código Java del formulario (reemplaza el método `$$$setupUI$$$()` autogenerado, invisible en el
`.java`, por código fuente Java normal y visible que sí llama a `createUIComponents()` desde el
constructor). Después, borrar los `.form` y correr `mvn clean package` para confirmar que compila
y que los tests de `presentation.controller` que instancian una Vista real ya no fallan por NPE,
sin depender en ningún momento del runtime de IntelliJ. Recién después de esto se genera el
comprimido final para el Aula Virtual.

Este es el único pendiente que queda antes de entregar.
