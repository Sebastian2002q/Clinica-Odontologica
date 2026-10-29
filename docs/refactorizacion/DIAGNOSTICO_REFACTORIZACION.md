# Diagnóstico Técnico y Plan de Refactorización

**Asignatura:** Ingeniería de Software II  
**Institución:** Universidad del Quindío  
**Fecha:** Octubre 2026  
**Equipo de Trabajo:**
* Anderson Meneses
* Sebastián Quintero Flórez

**Repositorio Oficial:** [Clinica-Odontologica (GitHub)](https://github.com/Sebastian2002q/Clinica-Odontologica.git)  
**Rama:** `main`

---

## 1. Contexto

* **Nombre del Proyecto:** Clínica Odontológica "DENTAL U" (Sistema Integral de Gestión Odontológica).
* **Tecnología y Entorno:** Java 21 LTS, JavaFX 21, Apache Maven 3.9+, Project Lombok, JUnit 5 Jupiter.
* **Arquitectura Documentada vs Real:** Arquitectura en capas desacoplada basada en el patrón **MVP (Modelo - Vista - Presentador)**. La capa de presentación cuenta con contratos puros (`view/contract`) y vistas pasivas JavaFX (`view/fx`); la lógica de aplicación reside en servicios de dominio (`service`); la persistencia en memoria se administra mediante repositorios dedicados (`repository`); las estructuras del negocio están encapsuladas en entidades y enumeraciones (`model/entity`, `model/enums`); y la resolución de dependencias opera mediante el patrón Singleton en `ServiceLocator`.
* **Funcionalidad Seleccionada:** **Gestión del Ciclo de Vida del Agendamiento de Citas (Reserva, Validación de Agenda, Reprogramación y Cancelación).**  
  *Justificación:* Es la transacción central del sistema que involucra reglas de negocio concurrentes y la interacción orquestada de múltiples entidades: `Cita`, `Paciente`, `Especialista`, `Agenda` y `BloqueHorario`.
* **Requisitos Asociados:**
  * **F-02 (Agendar Cita):** Asignación de citas médicas asociando paciente, doctor, fecha, hora y motivo, con matriz interactiva de disponibilidad semaforizada (Verde = Libre, Rojo = Ocupado).
  * **REQ004 (Cancelación y Reprogramación):** Liberación de turnos médicos y reasignación de bloques horarios en la agenda.
  * **RN-001 (Validación de Paciente Registrado):** Restricción obligatoria de existencia previa del paciente antes de permitir el agendamiento.
  * **RN-002 (Disponibilidad de Agenda Médica):** Bloqueo estricto ante solapamientos de horario o selección de bloques ya ocupados.

---

## 2. Arquitectura Real y Flujo de la Funcionalidad

### Mapa de Componentes Participantes

1. **Capa de Vista (UI):**
   * `co.edu.uniquindio.clinicaodontologica.view.contract.IAgendamientoView`: Interfaz que define el contrato pasivo de la pantalla de citas.
   * `co.edu.uniquindio.clinicaodontologica.view.fx.AgendamientoFxView`: Implementación visual en JavaFX que dibuja los paneles, calendarios y formularios.
2. **Capa de Presentación:**
   * `co.edu.uniquindio.clinicaodontologica.presenter.AgendamientoPresenter`: Intermediario encargado de procesar los eventos de usuario, invocar los servicios y solicitar actualizaciones a la vista.
3. **Capa de Lógica de Negocio (Servicios):**
   * `co.edu.uniquindio.clinicaodontologica.service.AgendamientoService`: Orquestador de las transacciones de agendamiento, verificación de bloqueos, reprogramación y cancelación.
   * `co.edu.uniquindio.clinicaodontologica.service.EspecialistaService`: Consulta y gestión de los médicos especialistas.
   * `co.edu.uniquindio.clinicaodontologica.service.PacienteService`: Validación de pacientes registrados.
4. **Capa de Persistencia (Repositorios en Memoria):**
   * `co.edu.uniquindio.clinicaodontologica.repository.CitaRepository`: Almacén en memoria de citas con soporte de filtros por estado y especialista.
   * `co.edu.uniquindio.clinicaodontologica.repository.PacienteRepository`: Consulta de pacientes por cédula.
   * `co.edu.uniquindio.clinicaodontologica.repository.EspecialistaRepository`: Consulta de doctores por ID.
5. **Capa de Dominio (Entidades):**
   * `co.edu.uniquindio.clinicaodontologica.model.entity.Cita`: Entidad con identificador, paciente, doctor, fecha, hora y estado (`EstadoCita`).
   * `co.edu.uniquindio.clinicaodontologica.model.entity.Agenda`: Estructura interna del especialista que mapea fecha y hora a disponibilidad booleana.
   * `co.edu.uniquindio.clinicaodontologica.model.entity.BloqueHorario`: Value Object representativo de cada franja temporal.

### Flujo de Ejecución de la Transacción de Cancelación

```
[Usuario] ──(Clic en Cancelar Cita)──► [AgendamientoFxView]
                                             │
                                             ▼
                                  [AgendamientoPresenter]
                                             │
                                             ▼
                                  [AgendamientoService.cancelarCita]
                                             │
                   ┌─────────────────────────┴─────────────────────────┐
                   ▼                                                   ▼
         [Cita.cancelar()]                                  [Agenda.liberarBloque()]
                   │                                                   │
                   ▼                                                   ▼
         [CitaRepository.guardar()] (Refactorizado)          [Estado -> Libre (Verde)]
```

---

## 3. Hallazgos Técnicos

### Ficha Técnica H-01
* **ID:** H-01
* **Tipo:** Defecto funcional / Deuda técnica
* **Ubicación:** `ClinicaOdontologica/src/main/java/co/edu/uniquindio/clinicaodontologica/service/AgendamientoService.java`, clase `AgendamientoService`, método `cancelarCita(String idCita)`, líneas 116–125.
* **Evidencia de Código (Antes):**
```java
public void cancelarCita(String idCita) {
    Cita cita = citaRepository.buscarPorId(idCita)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la cita " + idCita));
    if (cita.getEstado() == EstadoCita.PAGADA || cita.getEstado() == EstadoCita.ATENDIDA) {
        throw new IllegalStateException("No se puede cancelar una cita que ya fue atendida o pagada.");
    }
    cita.cancelar();
    cita.getEspecialista().getAgenda().liberarBloque(cita.getFecha(), cita.getHora());
    citaRepository.eliminar(idCita); // <--- Línea 124 original: borrado físico destructivo
}
```
* **Observación Técnica:**  
  La entidad `Cita` maneja un ciclo de vida con estados (`AGENDADA`, `ATENDIDA`, `PAGADA`, `CANCELADA`) y la llamada `cita.cancelar()` actualiza el estado interno a `EstadoCita.CANCELADA`. Por su parte, `CitaRepository` posee métodos diseñados explícitamente para soportar borrado lógico: `listarActivas()` excluye citas canceladas (`filter(c -> c.getEstado() != EstadoCita.CANCELADA)`), mientras que `listarTodas()` documenta retornar el histórico completo incluyendo canceladas.  
  Sin embargo, en la línea 124 original, `AgendamientoService` ejecutaba un borrado físico mediante `citaRepository.eliminar(idCita)`. Esto destruía el objeto del mapa en memoria, invalidando la llamada `cita.cancelar()`, eliminando toda trazabilidad histórica e impidiendo generar reportes de auditoría sobre inasistencias o cancelaciones.
* **Criterio Afectado:** Consistencia del modelo de dominio, trazabilidad de datos y coherencia entre capas.
* **Impacto y Riesgo:**  
  *Impacto:* Alto. Provoca pérdida definitiva de registros de auditoría y genera contradicción entre la lógica del repositorio y la del servicio.  
  *Riesgo:* Inconsistencia de datos ante reportes gerenciales de cancelaciones.
* **Acción Propuesta:** Reemplazar `citaRepository.eliminar(idCita)` por la conservación de la entidad cancelada en el repositorio (`citaRepository.guardar(cita)` manteniendo borrado lógico).
* **Verificación:** Prueba unitaria automatizada (`AgendamientoServiceTest`) que agenda una cita, la cancela y verifica que `buscarPorId(idCita)` retorne la cita en estado `CANCELADA`, que no aparezca en `listarActivas()` y que sí figure en `listarTodas()`.
* **Estado:** **Implementado y verificado con pruebas unitarias.**

---

### Ficha Técnica H-02
* **ID:** H-02
* **Tipo:** Riesgo arquitectónico / Deuda técnica
* **Ubicación:** `ClinicaOdontologica/src/main/java/co/edu/uniquindio/clinicaodontologica/view/fx/AgendamientoFxView.java`, clase `AgendamientoFxView`, constructor, líneas 42–51.
* **Evidencia de Código:**
```java
public AgendamientoFxView(Runnable onDataChangedCallback) {
    this.onDataChangedCallback = onDataChangedCallback;
    ServiceLocator loc = ServiceLocator.getInstance();
    this.presenter = new AgendamientoPresenter(
            this,
            loc.getAgendamientoService(),
            loc.getEspecialistaService(),
            loc.getPacienteService()
    );
}
```
* **Observación Técnica:**  
  En el patrón MVP (Passive View), la vista debe ser totalmente pasiva y no debe tener la responsabilidad de instanciar presentadores ni localizar servicios de infraestructura. `AgendamientoFxView` se acopla directamente a la implementación global `ServiceLocator.getInstance()`.
* **Criterio Afectado:** Principio de Inversión de Dependencias (DIP - SOLID) y Separación de Capas.
* **Impacto y Riesgo:**  
  *Impacto:* Medio. Impide realizar pruebas de la vista con presentadores simulados (mocks) y genera acoplamiento entre UI e infraestructura.
* **Acción Propuesta:** Sobrecargar el constructor de la vista para permitir la inyección externa del presentador: `public AgendamientoFxView(AgendamientoPresenter presenter, Runnable callback)`.
* **Verificación:** Instanciación de `AgendamientoFxView` pasando un presentador mock sin requerir la inicialización de `ServiceLocator`.
* **Estado:** Pendiente (documentado como deuda técnica).

---

### Ficha Técnica H-03
* **ID:** H-03
* **Tipo:** Oportunidad de mejora / Deuda técnica
* **Ubicación:** `ClinicaOdontologica/src/main/java/co/edu/uniquindio/clinicaodontologica/presenter/AgendamientoPresenter.java`, clase `AgendamientoPresenter`, método `seleccionarEspecialista(Especialista)`, línea 75.
* **Evidencia de Código:**
```java
public void seleccionarEspecialista(Especialista especialista) {
    if (especialista == null) {
        return;
    }
    List<LocalDate> fechas = especialista.getAgenda().obtenerFechasConfiguradas();
    view.mostrarFechasAgenda(fechas);
    view.mostrarCitasEspecialista(agendamientoService.listarCitasPorEspecialista(especialista));
...
```
* **Observación Técnica:**  
  El presentador navega la estructura interna de la entidad: `especialista.getAgenda().obtenerFechasConfiguradas()`. Esto viola el Principio de Mínimo Conocimiento (Ley de Deméter), acoplando al presentador con la existencia y estructura del objeto `Agenda` en lugar de interactuar a través de un método directo del especialista o del servicio de especialistas ya inyectado.
* **Criterio Afectado:** Ley de Deméter, Encapsulamiento y Cohesión.
* **Impacto y Riesgo:**  
  *Impacto:* Bajo. Cambios en la representación de la agenda dentro de la entidad `Especialista` propagan modificaciones innecesarias en la capa de presentación.
* **Acción Propuesta:** Delegar la consulta mediante un método en `Especialista.java` (`obtenerFechasAgenda()`) o a través de `EspecialistaService`.
* **Verificación:** Ejecución de pruebas unitarias verificando que las fechas retornadas coincidan antes y después de la delegación.
* **Estado:** Pendiente (documentado como deuda técnica).

---

## 4. Priorización de Hallazgos

| ID | Hallazgo | Impacto (1–3) | Riesgo (1–3) | Esfuerzo (1–3) | Prioridad | Selección y Justificación |
|---|---|:---:|:---:|:---:|:---:|---|
| **H-01** | Borrado físico en cancelación de citas | **3** | **3** | **1** | **Alta** | **Seleccionado e intervenido en Sesión 2.** Representa un defecto funcional directo que destruía la trazabilidad histórica del sistema. Su corrección es acotada, de alto impacto correctivo y fue verificada antes y después. |
| **H-02** | Acoplamiento de la vista a `ServiceLocator` | 2 | 2 | 2 | Media | Requiere modificar la jerarquía de instanciación en `MainDashboardFxView` y varios constructores de vistas. Se deja documentado como deuda técnica para futuros sprints. |
| **H-03** | Invasión de estructura interna (Ley de Deméter) | 1 | 1 | 1 | Baja | Es una mejora de encapsulamiento estético que no soluciona anomalías funcionales observables. |

---

## 5. Intervención Implementada (Sesión 2)

* **Hallazgo Intervenido:** `H-01` (Preservación de Trazabilidad Histórica en Cancelación de Citas).
* **Archivos Afectados:**
  * `ClinicaOdontologica/src/main/java/co/edu/uniquindio/clinicaodontologica/service/AgendamientoService.java`
  * `ClinicaOdontologica/pom.xml` (incorporación de dependencia JUnit 5 Jupiter en scope test)
  * `ClinicaOdontologica/src/test/java/co/edu/uniquindio/clinicaodontologica/service/AgendamientoServiceTest.java` (batería de pruebas automatizadas)

### Comparación de Código Antes / Después

```java
// ==================== DISEÑO ANTERIOR (Líneas 122-125) ====================
cita.cancelar();
cita.getEspecialista().getAgenda().liberarBloque(cita.getFecha(), cita.getHora());
citaRepository.eliminar(idCita); // ¡Defecto! Borrado físico que destruía el registro

// ==================== DISEÑO DESPUÉS (Refactorizado) ====================
cita.cancelar();
cita.getEspecialista().getAgenda().liberarBloque(cita.getFecha(), cita.getHora());
citaRepository.guardar(cita); // ¡Solución! Borrado lógico preservando historial y estado CANCELADA
```

---

## 6. Verificación y Evidencias de Comprobación

Para validar la conservación del comportamiento observable y la corrección rigurosa del hallazgo H-01, se creó la clase de pruebas unitarias automatizadas:  
[`AgendamientoServiceTest.java`](../../ClinicaOdontologica/src/test/java/co/edu/uniquindio/clinicaodontologica/service/AgendamientoServiceTest.java).

### 6.1. Evidencia Antes de la Refactorización (Falla Comprobada)

Al ejecutar la prueba con el código original (`citaRepository.eliminar`), el test falló demostrando la existencia real del defecto:

```text
[INFO] Running co.edu.uniquindio.clinicaodontologica.service.AgendamientoServiceTest
[ERROR] Tests run: 2, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.106 s <<< FAILURE!
[ERROR] testCancelarCitaPreservaTrazabilidadHistoricaYLiberaHorario -- Time elapsed: 0.066 s <<< FAILURE!
org.opentest4j.AssertionFailedError: H-01: La cita no debe ser eliminada físicamente; debe persistirse en el repositorio para auditoría. 
==> expected: <true> but was: <false>
	at co.edu.uniquindio.clinicaodontologica.service.AgendamientoServiceTest.testCancelarCitaPreservaTrazabilidadHistoricaYLiberaHorario(AgendamientoServiceTest.java:120)
[INFO] Results:
[ERROR] Failures: 1
[INFO] BUILD FAILURE
```

### 6.2. Evidencia Después de la Refactorización (Aprobación Total)

Tras aplicar el cambio acotado (`citaRepository.guardar(cita)`), se ejecutó nuevamente la suite completa:

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running co.edu.uniquindio.clinicaodontologica.service.AgendamientoServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.118 s -- in co.edu.uniquindio.clinicaodontologica.service.AgendamientoServiceTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  8.167 s
[INFO] Finished at: 2026-10-29T16:37:33-05:00
```

### 6.3. Conservación del Comportamiento Observable
* La interfaz gráfica sigue mostrando el bloque liberado en color **Verde** inmediatamente después de cancelar.
* La lista de citas activas del especialista deja de mostrar la cita cancelada (comportamiento esperado por el usuario).
* El repositorio ahora conserva el registro con `EstadoCita.CANCELADA`, habilitando reportes gerenciales históricos y auditorías clínicas.

---

## 7. Cierre y Lecciones Aprendidas

* **Lecciones Técnicas:**
  * El diseño de repositorios con borrado lógico (`soft delete`) requiere estricta coherencia en la capa de servicios; invocar métodos de eliminación física desvirtúa los métodos de consulta histórica.
  * Escribir pruebas unitarias automatizadas antes de refactorizar permite tener una red de seguridad inamovible (TDD inverso / Characterization Tests) para demostrar sin ambigüedades la corrección del defecto.
* **Pregunta de Cierre de la Guía:** *¿Qué práctica del proceso de desarrollo habría permitido detectar estos problemas antes de entregar la aplicación?*  
  * **Respuesta:** La implementación temprana de **pruebas de integración automatizadas sobre el ciclo de vida completo de las entidades** y la práctica formal de **revisiones de código por pares (Code Reviews)** con listas de verificación arquitectónicas. Las pruebas de ciclo de vida habrían evidenciado que una entidad que transiciona al estado `CANCELADA` no puede desvanecerse de la persistencia histórica si el requerimiento exige auditoría y trazabilidad.

---

## 8. Trazabilidad Git (Commits de la Actividad)

* **Commit 1 (Diagnóstico Técnico Inicial):**  
  Hash: `afdab60` — Mensaje: `docs: registrar diagnostico tecnico`
* **Commit 2 (Refactorización y Pruebas Unitarias):**  
  Hash: `4036e0b` — Mensaje: `refactor: preservar trazabilidad en cancelacion de citas`
* **Commit 3 (Documentación de Verificación y Cierre):**  
  Hash: `199963b` — Mensaje: `docs: documentar verificacion de refactorizacion`
