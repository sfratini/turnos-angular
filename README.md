## Información general

### Estructura de Carpetas

1. backend: Incluye el código Java que hostea la API. 
2. turnos: App Angular 9 que permite la gestion de turnos. 
3. package.json: Para evitar tener problemas de versiones, el angular-cli fue instalado localente en lugar de global. Única razón por la cual existe en el directorio. 

### Arquitectura y herramientas utilizadas

- El Backend se ha realizado con el framework Java Micronaut, para acelerar el tiempo de desarrollo. Se encuentra basado en una app simple CRUD para gestión de turnos. 
- El frontend se realizó utilizano Angular 9, y el framework Material Angular UI, para la interface web. El resto de los formularios e interacciones son las utilizadas por defecto con Angular. 
- La integración con impresión ZPL se realizó en forma simple mediante un snippet de impresión. Al framework Zebra Browser Print también estaría disponible y se puede integrar a futuro. 
- En este caso, y por simplicidad, no se modificó la base de datos por defecto la cual es una en memoria. Se puede luego fácilmente migrar a otro motor como mySQL, Postgres, o similar. Por esta razón, los datos se pierden una vez se frene el servidor. 

## Backend

### Testing

Existen algunos casos de prueba generados en la ruta `backend/complete/src/test/*`. Para su ejecución se puede ingresar a la carpeta root `backend` desde una consola y ejecutar `./gradlew test`

### Ejecución del servidor

Para iniciar el servidor se puede ejeuctar el comando `./gradlew run` desde la ruta de la carpeta backend. El servidor quedará iniciado a la espera de requests. 

## Frontend

### Ejecución

Para ejecutar el servidor frontend, se debe realizar los siguientes pasos:

1. Ingresar a la carpeta turnos desde una línea de comandos y ejecutar `yarn` o `npm install`, para poder instalar las dependencias. Importante tener instalado a menos node 10.13 o superior. El código fue relizado y probado con node 10.16. 
2. Desde la misma ruta, ejecutar el siguiente comando `../node_modules/@angular/cli/bin/ng serve --open`. También se puede ejecutar sin la ruta absoluta en caso de que el angular-cli se encuentre instalado en la ruta global. El comando compila el código y luego abre un explorador. 


## Comentarios generales de utilización

1. Por defecto y al reiniciar el servidor se pierden los datos al tratarse de una base de datos en memoria. Este comportamiento es esperado. 
2. La impresión de ZPL se puede realizar desde la tabla de turnos. Una posibl mejora sería la de integrar con Zebra Browser Printer. 
3. Al agregar un turno, se abre un modal con validaciones básicas para evitar que el usuario complete datos erroneos. Se pueden cambiar algunos parámetros como evitar que alguien coloque una fecha en el pasado, o mensajes específicos de error, etc. 
4. Al filtrar y buscar no es necesario apretar enter. En cada keystroke se realiza una búsqueda a la base. En una aplicación productiva es probable que este comportamiento no sea el adecuado. Fue implementado de esta forma por agilidad y demostración. 
5. Dado que solo se solicitó el alta y búsqueda, no se puede editar o borrar los turnos. 
6. No se han realizado test para Angular. La ejecución de `ng test` finalizará con error. 