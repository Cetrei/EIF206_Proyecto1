# Capa de persistencia

Paquete: cr.ac.una.reservas.persistence

Responsable: Companero A.

## Proposito

Esta capa es la unica autorizada a leer y escribir los archivos XML del sistema. Ninguna otra capa debe acceder a archivos directamente. El resto del sistema, principalmente service, interactua con esta capa unicamente a traves de las interfaces descritas abajo, sin conocer que la implementacion usa JAXB ni donde estan los archivos fisicos.

## Tecnologia

Serializacion XML con JAXB, Java Architecture for XML Binding, tal como se vio en clase. Las clases de model en cr.ac.una.reservas.model ya vienen anotadas, o se anotaran en conjunto contigo, con anotaciones como XmlRootElement, XmlElement y XmlElementWrapper para las listas.

El paquete de las anotaciones e interfaces de JAXB es javax.xml.bind, el mismo usado en el ejemplo de clase, no jakarta.xml.bind.

JAXB provee dos clases centrales para el trabajo diario: JAXBContext, que se crea una vez por clase de entidad, y a partir de ahi se obtienen un Marshaller para convertir objetos a XML, y un Unmarshaller para convertir XML de vuelta a objetos, exactamente como en el ejemplo de DemoXml visto en clase.

### Dependencia necesaria, importante

El proyecto usa Java 11 o superior. Desde Java 11, el paquete javax.xml.bind fue retirado del JDK, por lo que el codigo del ejemplo de clase no compila si no se agrega la dependencia correspondiente. Es necesario agregar al proyecto:

javax.xml.bind:jaxb-api

org.glassfish.jaxb:jaxb-runtime

Esto no se considera una libreria externa de conveniencia, es la implementacion del estandar JAXB que se vio en clase y sin ella el ejemplo del profesor tampoco funcionaria en Java 11+. El proyecto ya tiene el pom.xml de Maven con estas dependencias declaradas, no hace falta agregarlas de nuevo.

## Interfaz generica Dao

Definir una interfaz generica que todas las implementaciones deben respetar:

Dao de tipo T y ID, con metodos: buscarPorId(ID id) que devuelve un Optional de T, listarTodos() que devuelve una lista de T, guardar(T entidad) que inserta si no existe o actualiza si ya existe segun su id, eliminar(ID id).

Esta interfaz es el contrato de sustitucion de Liskov del proyecto: cualquier implementacion de Dao, sin importar si guarda en un solo archivo XML o en varios, debe comportarse igual desde la perspectiva de quien la usa. Service nunca debe necesitar saber cual implementacion concreta esta usando.

## Implementaciones esperadas

FuncionarioDao con metodo adicional buscarPorNombre(String nombre) ademas de lo heredado de Dao.

CategoriaDao con metodo adicional buscarPorDescripcion(String texto).

RecursoDao con metodo adicional listarPorCategoria(String idCategoria).

ReservaDao con metodo adicional listarPorFuncionario(String idFuncionario).

Cada uno de estos, con su respectiva implementacion Xml, por ejemplo FuncionarioDaoXml implements FuncionarioDao.

## Estructura de archivos XML sugerida

Un archivo por tipo de entidad, por ejemplo funcionarios.xml, categorias.xml, recursos.xml, reservas.xml, cada uno conteniendo una lista raiz de sus elementos. Ubicarlos en una carpeta data dentro del proyecto, por ejemplo data/funcionarios.xml, para que sea facil de inspeccionar durante el desarrollo y la defensa.

Al guardar una entidad, la implementacion debe leer la lista completa actual del archivo, modificar o agregar el elemento correspondiente por id, y volver a escribir el archivo completo. No es necesario un motor de base de datos ni escritura incremental, dado el tamano esperado de los datos para este proyecto.

## Manejo de errores

Si ocurre un problema de lectura o escritura de archivo, por ejemplo el archivo no existe todavia en el primer arranque, la implementacion debe crear el archivo vacio con la estructura raiz correspondiente en vez de fallar. Si ocurre un error real de formato o de disco, debe lanzarse una excepcion propia, por ejemplo PersistenciaException, para que la capa de service la traduzca a un mensaje de negocio adecuado.

## Como conectas esto con el resto del sistema

Vas a trabajar principalmente contra las clases de model que ya estan definidas en 01_model.md. No necesitas conocer nada de control ni de presentation. Cuando termines una implementacion, avisa para integrarla mediante la fabrica DaoFactory descrita en 02_service.md, que es el unico punto donde se instancian tus clases concretas.

## Notas para quien use IA para generar codigo en esta capa

Genera unicamente clases dentro del paquete persistence. Usa el paquete javax.xml.bind para las anotaciones e interfaces de JAXB, no jakarta.xml.bind. No agregues logica de negocio como validaciones de disponibilidad de recursos, eso no corresponde a esta capa. Respeta los nombres de metodos de las interfaces Dao descritas arriba exactamente, incluyendo mayusculas y tipos de retorno, porque service ya esta escrito o se escribira asumiendo esos nombres tal cual.
