# Proyecto Final - Clínica Odontológica "DENTAL U"

Este es el repositorio del proyecto final de Ingeniería de Software. Es un sistema de escritorio para agendar y gestionar citas odontológicas.

## Estructura y Arquitectura
El proyecto está desarrollado en **Java 21** usando **JavaFX** para las interfaces gráficas y **Maven** para manejar las dependencias. También utilizamos **Lombok** para ahorrarnos escribir todo el código repetitivo de getters, setters y constructores.

A nivel de diseño, aplicamos el patrón **MVC** (Modelo-Vista-Controlador):
- **Modelo (`model`)**: Aquí están las clases principales de la lógica de negocio (`Paciente`, `Cita`, `Especialista`, `Usuario` y `ClinicaOdontologica`). La clase `ClinicaOdontologica` actúa como el contenedor principal que guarda las listas de todo.
- **Vistas (`view` en resources)**: Son los archivos `.fxml` con el diseño de las pantallas.
- **Controladores (`controller` y `viewcontroller`)**: Los `viewcontroller` son los que están enlazados al FXML y manejan los clics y eventos de la interfaz. Los `controller` normales sirven de puente para comunicarse con el modelo y mantener la lógica separada.

Además, implementamos el patrón **Singleton** en la clase `ModelFactory`. Esto es clave en el proyecto porque nos asegura que toda la aplicación trabaje sobre una única instancia de los datos en memoria, evitando que se pierda información al cambiar de una ventana a otra.

## Datos de prueba (Datos quemados)
Para la sustentación y pruebas, dejamos varios datos quemados en el método `inicializarDatos()` de la clase `ModelFactory` para no tener que empezar con el sistema vacío:

- **Usuarios para el login**:
  - Usuario: `admin` / Clave: `admin`
  - Usuario: `user` / Clave: `user`
- **Especialistas**: Hay 10 especialistas quemados en el código con diferentes especialidades (Ortodoncia, Periodoncia, Endodoncia, etc.).
- **Citas iniciales**: Dejamos 20 citas ya agendadas de prueba repartidas entre los especialistas.
- **Horarios configurados**: La agenda está armada con fechas del **15 al 18 de mayo de 2026**. Los turnos van desde las 7:00 AM hasta las 12:00 PM, en intervalos de 30 minutos.

## ¿Cómo funciona la lógica del agendamiento?
La parte más importante del sistema es cómo se maneja la disponibilidad:

- **Mapa de horarios**: Cada `Especialista` tiene un atributo `horariosDisponibles` que es un `Map<LocalDate, List<LocalTime>>`. Esto significa que por cada día (fecha), hay una lista de horas libres.
- **Agendar cita**: Cuando desde la vista (`AgendamientoViewController`) se selecciona una hora y se guarda la cita, esa cita entra a la lista general de la clínica. Inmediatamente después, el sistema busca en el mapa del especialista y **elimina** esa hora exacta. Así evitamos que dos pacientes reserven el mismo turno.
- **Cancelar cita**: Al eliminar una cita desde la interfaz, el sistema hace lo inverso: la saca de la lista general y **devuelve** la hora a la lista de disponibilidad del especialista (haciendo un sort para que los botones de horas vuelvan a aparecer en orden cronológico).

## Instalación y Ejecución (.exe)
Para correr el proyecto desde el código fuente, basta con ejecutar la clase `ClinicaOdontologicaApplication` desde el IDE.

Si se va a presentar usando el ejecutable instalado:
1. Descomprimir el archivo `.zip` entregado.
2. Ejecutar el instalador incluido y seguir los pasos.
3. Ir al disco local `C:/` y buscar la carpeta de **Archivos de programa** (o Program Files).
4. Entrar a la carpeta `clinica-odontologia`.
5. Ejecutar el archivo `.exe` que está ahí adentro para abrir la aplicación directamente.
