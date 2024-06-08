# Arquitectura y Diseño de Sistemas

## TP General

1 - Clean Code / SOLID:
- Analizar la clase SongRepository. Explicar qué principios SOLID no se cumplen y por qué.
- Limpiar la clase SongRepository, implementar la solución de los problemas descritos anteriormente.

2 - Implementar los test que cubran los casos de la clase SongRepository.

3 - La arquitectura del proyecto, cumple con MVC? Justificar.


## Solucion

### Ejercicio 1

#### Single Responsibility Principle:
No se cumple porque no tiene 1 sola responsabilidad.
No se cumple ni a nivel de clase ni a nivel de funcion, ya que
tiene más de una responsabilidad.

La clase está accediendo a la caché, a la base de datos,
al servicio de spotify, y a wikipedia.

A nivel de función no lo cumple porque no tiene el mismo nivel de abstracción

Se puede solucionar creando mas funciones, modularizar, aplicar clean code

#### Open/Closed Principle
La clase no cumple con este principio.
Si queremos modificar un servicio, hay que modificar la clase.
Y si quisieramos agregar otro servicio, tambien hay que modificar la clase.

Se puede solucionar implementando el patrón de diseño Broker

#### Liskov Substitution Principle
Este principio nos dice que las subclases deberian poder ser sustituidas
por sus clases bases.
En este caso, no tenemos subclases.

#### Interface Segregation Principle
Este principio nos dice que es mejor tener muchas interfaces especificas,
en vez de tener una sola de propósito general.

En este caso como no tenemos interfaces, no se aplica este principio.

#### Dependency Inversion Principle
Este principio nos dice que debemos depender de abstracciones y no de clases concretas.

En este caso, dependemos de spotifyTrackService y SpotifySqlDBImpl.


