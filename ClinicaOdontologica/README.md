# Proyecto Final - Clínica Odontológica "DENTAL U"

Este es el repositorio del proyecto final de Ingeniería de Software. Es un sistema de escritorio para agendar y gestionar citas odontológicas, historias clínicas, pagos e inventario de insumos.

## Estructura y Arquitectura
El proyecto está desarrollado en **Java 21** usando **JavaFX** para las interfaces gráficas y **Maven** para manejar las dependencias. También utilizamos **Lombok** para ahorrarnos escribir todo el código repetitivo de getters, setters y constructores.

A nivel de diseño, pasamos la estructura al patrón **MVP** (Modelo-Vista-Presentador) separando también los repositorios y servicios por módulos:
- **Modelo (`model`)**: Aquí están las clases principales del negocio (`Paciente`, `Cita`, `Especialista`, `Agenda`, `BloqueHorario`, `HistoriaClinica`, `ProcedimientoClinico`, `Insumo`, `Pago`, `Caja`, `Recordatorio` y `Usuario`) junto con los `enums`.
- **Repositorios (`repository`) y Servicios (`service`)**: Los repositorios manejan las listas en memoria de cada módulo y los servicios tienen las validaciones del negocio (verificar que el paciente esté registrado antes de agendar, validar horarios disponibles, actualizar la historia clínica y descontar insumos).
- **Vistas (`view`) y Presentadores (`presenter`)**: Las interfaces gráficas están en `view/fx` implementando los contratos de `view/contract`, y los `presenter` sirven de puente entre la vista y los servicios para mantener la lógica separada de los componentes visuales.

Además, implementamos `ServiceLocator` (con patrón **Singleton**) y `DataSeeder` en el paquete `config`. Esto nos asegura que toda la aplicación trabaje sobre una única instancia de los datos en memoria, evitando que se pierda información al cambiar de una sección a otra.

## Datos de prueba (Datos quemados)
Para la sustentación y pruebas, dejamos varios datos quemados en la clase `DataSeeder` para no tener que empezar con el sistema vacío:

- **Usuarios para el login**:
  - Usuario: `admin` / Clave: `admin`
  - Usuario: `user` / Clave: `user`
  - Usuario: `doctor` / Clave: `doctor`
- **Pacientes e Inventario**: Hay 10 pacientes registrados de prueba con su historia clínica creada y 7 insumos odontológicos iniciales en bodega.
- **Especialistas**: Hay 10 especialistas quemados en el código con diferentes especialidades (Ortodoncia, Periodoncia, Endodoncia, Cirugía Oral, etc.).
- **Citas iniciales**: Dejamos citas ya agendadas de prueba repartidas entre los especialistas.
- **Horarios configurados**: La agenda está configurada por bloques horarios desde las 7:00 AM hasta las 12:00 PM y en la tarde desde las 2:00 PM, en intervalos de 30 minutos.

## ¿Cómo funciona la lógica del agendamiento?
La parte más importante del sistema es cómo se maneja la disponibilidad y el flujo de la cita:

- **Bloques de horario**: Cada `Especialista` tiene una `Agenda` con sus fechas y bloques de hora (`BloqueHorario`). En la pantalla los turnos disponibles se ven en verde y los que ya están reservados se ven en rojo.
- **Agendar cita**: Primero se valida que la cédula del paciente exista en el sistema. Cuando se selecciona una hora libre y se guarda la cita, el sistema marca ese bloque como ocupado en la agenda del especialista. Así evitamos que dos pacientes reserven el mismo turno.
- **Cancelar o reprogramar cita**: Al cancelar o cambiar una cita desde la interfaz, el sistema hace lo inverso: libera la hora anterior en la agenda del especialista para que vuelva a aparecer disponible.
- **Atención e insumos**: Cuando se registra un procedimiento en la historia clínica, el sistema actualiza el expediente del paciente y descuenta automáticamente del inventario las unidades de insumos que se usaron en la consulta.

## Instalación y Ejecución
Para correr el proyecto desde el código fuente, basta con abrir la carpeta `ClinicaOdontologica` en el IDE y ejecutar la clase `ClinicaOdontologicaApplication` (o `Launcher`).
