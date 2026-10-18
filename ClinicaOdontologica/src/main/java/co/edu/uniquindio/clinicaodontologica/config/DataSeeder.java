package co.edu.uniquindio.clinicaodontologica.config;

import co.edu.uniquindio.clinicaodontologica.model.entity.ConsumoInsumo;
import co.edu.uniquindio.clinicaodontologica.model.entity.Especialista;
import co.edu.uniquindio.clinicaodontologica.model.enums.RolUsuario;
import co.edu.uniquindio.clinicaodontologica.service.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
  * Clase utilitaria encargada de cargar los datos quemados de prueba al iniciar la aplicación.
  */
public class DataSeeder {

    /**
      * Inicializa todos los datos de prueba en los diferentes módulos del sistema.
      *
      * @param authService            servicio de usuarios
      * @param pacienteService        servicio de pacientes
      * @param especialistaService    servicio de especialistas
      * @param agendamientoService    servicio de agendamiento de citas
      * @param inventarioService      servicio de inventario de insumos
      * @param atencionClinicaService servicio de atención e historia clínica
      */
    public static void cargarDatosIniciales(
            AuthService authService,
            PacienteService pacienteService,
            EspecialistaService especialistaService,
            AgendamientoService agendamientoService,
            InventarioService inventarioService,
            AtencionClinicaService atencionClinicaService) {

        cargarUsuarios(authService);
        cargarInventarioInsumos(inventarioService);
        cargarPacientes(pacienteService);
        List<Especialista> especialistas = cargarEspecialistas(especialistaService);
        cargarCitasYProcedimientosSemilla(agendamientoService, atencionClinicaService, especialistas);
    }

    /**
      * Registra las cuentas de usuario iniciales para el ingreso al sistema.
      *
      * @param authService servicio de autenticación
      */
    private static void cargarUsuarios(AuthService authService) {
        authService.registrarUsuario("admin", "admin", "Administrador General", RolUsuario.ADMINISTRADOR);
        authService.registrarUsuario("user", "user", "Auxiliar Recepción", RolUsuario.AUXILIAR_ADMINISTRATIVO);
        authService.registrarUsuario("doctor", "doctor", "Dr. Especialista Odontólogo", RolUsuario.DOCTOR_ESPECIALISTA);
    }

    /**
      * Carga la lista inicial de insumos odontológicos en el inventario.
      *
      * @param inventarioService servicio de inventario
      */
    private static void cargarInventarioInsumos(InventarioService inventarioService) {
        inventarioService.registrarInsumo("INS-001", "Resina Fotocurada 3M A2", "Restauración y Operatoria", "Jeringa/Porción", 25, 10, 18000);
        inventarioService.registrarInsumo("INS-002", "Anestesia Lidocaína 2% con Epinefrina", "Anestésicos y Farmacología", "Carpule", 14, 10, 4500);
        inventarioService.registrarInsumo("INS-003", "Guantes de Nitrilo Quirúrgicos", "Bioseguridad y Esterilización", "Par", 8, 10, 1500);
        inventarioService.registrarInsumo("INS-004", "Agujas Gingivales Cortas Estériles", "Descartables y Consumibles", "Unidad", 40, 15, 900);
        inventarioService.registrarInsumo("INS-005", "Ácido Grabador Fosfórico 37%", "Cementos y Adhesivos", "Aplicador", 6, 8, 6500);
        inventarioService.registrarInsumo("INS-006", "Ionómero de Vidrio Restaurador", "Restauración y Operatoria", "Dosis", 18, 6, 12000);
        inventarioService.registrarInsumo("INS-007", "Arco NiTi Ortodoncia .016", "Ortodoncia", "Unidad", 12, 8, 9500);
    }

    /**
      * Registra los pacientes iniciales de prueba con su respectiva historia clínica.
      *
      * @param pacienteService servicio de pacientes
      */
    private static void cargarPacientes(PacienteService pacienteService) {
        String[][] datosPacientes = {
                {"111111", "Mateo Gómez", "3101111111", "mateo@correo.com"},
                {"222222", "Sofía López", "3102222222", "sofia@correo.com"},
                {"333333", "Lucas Martínez", "3103333333", "lucas@correo.com"},
                {"444444", "Valentina Díaz", "3104444444", "valentina@correo.com"},
                {"555555", "Samuel Herrera", "3105555555", "samuel@correo.com"},
                {"666666", "Isabella Torres", "3106666666", "isabella@correo.com"},
                {"777777", "Daniel Castro", "3107777777", "daniel@correo.com"},
                {"888888", "Camila Vargas", "3108888888", "camila@correo.com"},
                {"999999", "Nicolás Rojas", "3109999999", "nicolas@correo.com"},
                {"101010", "Mariana Ríos", "3111010101", "mariana@correo.com"},
                {"1111111", "Emiliano Mendoza", "3121111111", "emiliano@correo.com"},
                {"2222222", "Gabriela Silva", "3122222222", "gabriela@correo.com"},
                {"3333333", "Tomás Navarro", "3123333333", "tomas@correo.com"},
                {"4444444", "Luciana Ortiz", "3124444444", "luciana@correo.com"},
                {"5555555", "Sebastián Quintero", "3125555555", "sebastian@correo.com"},
                {"6666666", "Antonia Morales", "3126666666", "antonia@correo.com"},
                {"7777777", "Martín Pineda", "3127777777", "martin@correo.com"},
                {"8888888", "Salomé Cardona", "3128888888", "salome@correo.com"},
                {"9999999", "Dulce María Cano", "3129999999", "dulce@correo.com"},
                {"1212121", "Emilia Giraldo", "3131212121", "emilia@correo.com"}
        };
        for (String[] fila : datosPacientes) {
            pacienteService.registrarPaciente(fila[0], fila[1], fila[2], fila[3]);
        }
    }

    /**
      * Genera el mapa de horarios disponibles por defecto desde las 7:00 AM hasta las 12:00 PM
      * para los próximos 10 días.
      *
      * @return mapa con las fechas y su lista de horas libres
      */
    public static Map<LocalDate, List<LocalTime>> construirHorariosBase() {
        List<LocalTime> franjas = List.of(
                LocalTime.of(7, 0), LocalTime.of(7, 30),
                LocalTime.of(8, 0), LocalTime.of(8, 30),
                LocalTime.of(9, 0), LocalTime.of(9, 30),
                LocalTime.of(10, 0), LocalTime.of(10, 30),
                LocalTime.of(11, 0), LocalTime.of(11, 30),
                LocalTime.of(12, 0)
        );
        Map<LocalDate, List<LocalTime>> mapa = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i < 10; i++) {
            mapa.put(hoy.plusDays(i), new ArrayList<>(franjas));
        }
        return mapa;
    }

    /**
      * Registra los 10 especialistas iniciales de prueba junto con sus horarios disponibles.
      *
      * @param especialistaService servicio de especialistas
      * @return lista de especialistas creados
      */
    private static List<Especialista> cargarEspecialistas(EspecialistaService especialistaService) {
        String[][] datosEspecialistas = {
                {"Juan Perez", "Ortodoncia", "TP-10201", "Consultorio 101"},
                {"María Gómez", "Odontología General", "TP-10202", "Consultorio 102"},
                {"Carlos Rodriguez", "Periodoncia", "TP-10203", "Consultorio 103"},
                {"Luna Gutierrez", "Endodoncia", "TP-10204", "Consultorio 104"},
                {"Pedro Simon", "Estética Dental", "TP-10205", "Consultorio 105"},
                {"Patricia Teherán", "Cirugía Maxilofacial", "TP-10206", "Consultorio 201"},
                {"Pepito Perez", "Odontopediatría", "TP-10207", "Consultorio 202"},
                {"Sebastian Quintero", "Ortodoncia", "TP-10208", "Consultorio 203"},
                {"Anderson Meneses", "Endodoncia", "TP-10209", "Consultorio 204"},
                {"Carla Moñitos", "Periodoncia", "TP-10210", "Consultorio 205"}
        };

        List<Especialista> creados = new ArrayList<>();
        for (String[] esp : datosEspecialistas) {
            creados.add(especialistaService.registrarEspecialista(
                    esp[0], esp[1], esp[2], esp[3], construirHorariosBase()
            ));
        }
        return creados;
    }

    /**
      * Crea las citas iniciales de prueba repartidas entre los especialistas y registra un procedimiento inicial.
      *
      * @param agendamientoService    servicio de agendamiento
      * @param atencionClinicaService servicio de atención clínica
      * @param esp                    lista de especialistas registrados
      */
    private static void cargarCitasYProcedimientosSemilla(
            AgendamientoService agendamientoService,
            AtencionClinicaService atencionClinicaService,
            List<Especialista> esp) {

        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);
        LocalDate pasadoManana = hoy.plusDays(2);
        LocalDate dia3 = hoy.plusDays(3);

        var cita1 = agendamientoService.agendarCita("111111", esp.get(0).getIdDoctor(), hoy, LocalTime.of(7, 0), "Consulta general y valoración");
        agendamientoService.agendarCita("222222", esp.get(0).getIdDoctor(), hoy, LocalTime.of(7, 30), "Control de ortodoncia");

        agendamientoService.agendarCita("444444", esp.get(1).getIdDoctor(), hoy, LocalTime.of(8, 30), "Dolor dental molar inferior");
        agendamientoService.agendarCita("333333", esp.get(1).getIdDoctor(), manana, LocalTime.of(8, 0), "Limpieza dental ultrasónica");

        agendamientoService.agendarCita("555555", esp.get(2).getIdDoctor(), hoy, LocalTime.of(9, 0), "Revisión de encías");
        agendamientoService.agendarCita("666666", esp.get(2).getIdDoctor(), manana, LocalTime.of(9, 30), "Tratamiento periodontal");
        agendamientoService.agendarCita("777777", esp.get(3).getIdDoctor(), manana, LocalTime.of(10, 0), "Endodoncia inicial");
        agendamientoService.agendarCita("888888", esp.get(3).getIdDoctor(), pasadoManana, LocalTime.of(10, 30), "Dolor de muela");
        agendamientoService.agendarCita("999999", esp.get(4).getIdDoctor(), hoy, LocalTime.of(11, 0), "Blanqueamiento dental");
        agendamientoService.agendarCita("101010", esp.get(4).getIdDoctor(), manana, LocalTime.of(11, 30), "Diseño de sonrisa");
        agendamientoService.agendarCita("1111111", esp.get(5).getIdDoctor(), hoy, LocalTime.of(12, 0), "Valoración quirúrgica");
        agendamientoService.agendarCita("2222222", esp.get(5).getIdDoctor(), manana, LocalTime.of(7, 0), "Extracción dental");
        agendamientoService.agendarCita("3333333", esp.get(6).getIdDoctor(), pasadoManana, LocalTime.of(7, 30), "Consulta infantil");
        agendamientoService.agendarCita("4444444", esp.get(6).getIdDoctor(), dia3, LocalTime.of(8, 0), "Control odontopediatría");
        agendamientoService.agendarCita("5555555", esp.get(7).getIdDoctor(), hoy, LocalTime.of(8, 30), "Ajuste de brackets");
        agendamientoService.agendarCita("6666666", esp.get(7).getIdDoctor(), manana, LocalTime.of(9, 0), "Revisión de ortodoncia");
        agendamientoService.agendarCita("7777777", esp.get(8).getIdDoctor(), pasadoManana, LocalTime.of(9, 30), "Tratamiento de conducto");
        agendamientoService.agendarCita("8888888", esp.get(8).getIdDoctor(), dia3, LocalTime.of(10, 0), "Endodoncia control");
        agendamientoService.agendarCita("9999999", esp.get(9).getIdDoctor(), hoy, LocalTime.of(10, 30), "Evaluación periodontal");
        agendamientoService.agendarCita("1212121", esp.get(9).getIdDoctor(), manana, LocalTime.of(11, 0), "Limpieza profunda");

        atencionClinicaService.registrarProcedimiento(
                cita1.getIdCita(),
                "K02.1 - Caries de la dentina",
                "Obturación dental con resina fotocurada",
                "Se remueve tejido cariado en molar 36, se aplica grabado ácido y restauración con resina A2 sin complicaciones.",
                120000,
                List.of(new ConsumoInsumo("INS-001", "Resina Fotocurada 3M A2", 1, "Jeringa/Porción"))
        );
    }
}
