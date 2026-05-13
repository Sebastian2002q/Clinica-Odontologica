package co.edu.uniquindio.clinicaodontologica.factory;

import co.edu.uniquindio.clinicaodontologica.model.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ModelFactory {

    private static ModelFactory instance;

    private ClinicaOdontologica clinica;

    public static ModelFactory getInstance() {
        if (instance == null) {
            instance = new ModelFactory();
        }
        return instance;
    }

    private ModelFactory() {
        clinica = inicializarDatos();
    }

    private static ClinicaOdontologica inicializarDatos() {
        ClinicaOdontologica clinica = new ClinicaOdontologica("DENTAL U");
        Usuario usuario1 = new Usuario("admin", "admin");
        Usuario usuario2 = new Usuario("user", "user");
        clinica.agregarUsuario(usuario1);
        clinica.agregarUsuario(usuario2);

        Map<LocalDate, List<LocalTime>> horariosJuan = new HashMap<>();
        horariosJuan.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosJuan.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosJuan.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosJuan.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosMaria = new HashMap<>();
        horariosMaria.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosMaria.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosMaria.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosMaria.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosCarlos = new HashMap<>();
        horariosCarlos.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosCarlos.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosCarlos.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosCarlos.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosLuna = new HashMap<>();
        horariosLuna.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosLuna.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosLuna.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosLuna.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosPedro = new HashMap<>();
        horariosPedro.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPedro.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPedro.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPedro.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosPatricia = new HashMap<>();
        horariosPatricia.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPatricia.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPatricia.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPatricia.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosPepito = new HashMap<>();
        horariosPepito.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPepito.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPepito.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosPepito.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosSebastian = new HashMap<>();
        horariosSebastian.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosSebastian.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosSebastian.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosSebastian.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosAnderson = new HashMap<>();
        horariosAnderson.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosAnderson.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosAnderson.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosAnderson.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Map<LocalDate, List<LocalTime>> horariosCarla = new HashMap<>();
        horariosCarla.put(LocalDate.of(2026, 5, 15), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosCarla.put(LocalDate.of(2026, 5, 16), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosCarla.put(LocalDate.of(2026, 5, 17), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));
        horariosCarla.put(LocalDate.of(2026, 5, 18), new ArrayList<>(List.of(LocalTime.of(7, 0), LocalTime.of(7, 30), LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 00))));

        Especialista especialista1 = new Especialista("Juan Perez", "Ortodoncia", horariosJuan);
        Especialista especialista2 = new Especialista("María Gómez", "Odontología General", horariosMaria);
        Especialista especialista3 = new Especialista("Carlos Rodriguez", "Periodoncia", horariosCarlos);
        Especialista especialista4 = new Especialista("Luna Gutierrez", "Endodoncia", horariosLuna);
        Especialista especialista5 = new Especialista("Pedro Simon", "Estética Dental", horariosPedro);
        Especialista especialista6 = new Especialista("Patricia Teherán", "Cirugía Maxilofacial", horariosPatricia);
        Especialista especialista7 = new Especialista("Pepito Perez", "Odontopediatría", horariosPepito);
        Especialista especialista8 = new Especialista("Sebastian Quintero", "Ortodoncia", horariosSebastian);
        Especialista especialista9 = new Especialista("Anderson Meneses", "Endodoncia", horariosAnderson);
        Especialista especialista10 = new Especialista("Carla Moñitos", "Periodoncia", horariosCarla);

        clinica.agregarEspecialista(especialista1);
        clinica.agregarEspecialista(especialista2);
        clinica.agregarEspecialista(especialista3);
        clinica.agregarEspecialista(especialista4);
        clinica.agregarEspecialista(especialista5);
        clinica.agregarEspecialista(especialista6);
        clinica.agregarEspecialista(especialista7);
        clinica.agregarEspecialista(especialista8);
        clinica.agregarEspecialista(especialista9);
        clinica.agregarEspecialista(especialista10);

        Cita cita1 = new Cita(new Paciente("Mateo", "111111", "111111"), especialista1, "Consulta general", LocalDate.of(2026, 5, 15), LocalTime.of(7, 0));
        Cita cita2 = new Cita(new Paciente("Sofia", "222222", "222222"), especialista1, "Control de ortodoncia", LocalDate.of(2026, 5, 15), LocalTime.of(7, 30));
        Cita cita3 = new Cita(new Paciente("Lucas", "333333", "333333"), especialista2, "Limpieza dental", LocalDate.of(2026, 5, 15), LocalTime.of(8, 0));
        Cita cita4 = new Cita(new Paciente("Valentina", "444444", "444444"), especialista2, "Dolor dental", LocalDate.of(2026, 5, 16), LocalTime.of(8, 30));
        Cita cita5 = new Cita(new Paciente("Samuel", "555555", "555555"), especialista3, "Revisión de encías", LocalDate.of(2026, 5, 16), LocalTime.of(9, 0));
        Cita cita6 = new Cita(new Paciente("Isabella", "666666", "666666"), especialista3, "Tratamiento periodontal", LocalDate.of(2026, 5, 17), LocalTime.of(9, 30));
        Cita cita7 = new Cita(new Paciente("Daniel", "777777", "777777"), especialista4, "Endodoncia inicial", LocalDate.of(2026, 5, 17), LocalTime.of(10, 0));
        Cita cita8 = new Cita(new Paciente("Camila", "888888", "888888"), especialista4, "Dolor de muela", LocalDate.of(2026, 5, 18), LocalTime.of(10, 30));
        Cita cita9 = new Cita(new Paciente("Nicolas", "999999", "999999"), especialista5, "Blanqueamiento dental", LocalDate.of(2026, 5, 18), LocalTime.of(11, 0));
        Cita cita10 = new Cita(new Paciente("Mariana", "101010", "101010"), especialista5, "Diseño de sonrisa", LocalDate.of(2026, 5, 15), LocalTime.of(11, 30));
        Cita cita11 = new Cita(new Paciente("Emiliano", "1111111", "1111111"), especialista6, "Valoración quirúrgica", LocalDate.of(2026, 5, 15), LocalTime.of(12, 0));
        Cita cita12 = new Cita(new Paciente("Gabriela", "2222222", "2222222"), especialista6, "Extracción dental", LocalDate.of(2026, 5, 16), LocalTime.of(7, 0));
        Cita cita13 = new Cita(new Paciente("Tomas", "3333333", "3333333"), especialista7, "Consulta infantil", LocalDate.of(2026, 5, 16), LocalTime.of(7, 30));
        Cita cita14 = new Cita(new Paciente("Luciana", "4444444", "4444444"), especialista7, "Control odontopediatría", LocalDate.of(2026, 5, 17), LocalTime.of(8, 0));
        Cita cita15 = new Cita(new Paciente("Sebastian", "5555555", "5555555"), especialista8, "Ajuste de brackets", LocalDate.of(2026, 5, 17), LocalTime.of(8, 30));
        Cita cita16 = new Cita(new Paciente("Antonia", "6666666", "6666666"), especialista8, "Revisión de ortodoncia", LocalDate.of(2026, 5, 18), LocalTime.of(9, 0));
        Cita cita17 = new Cita(new Paciente("Martin", "7777777", "7777777"), especialista9, "Tratamiento de conducto", LocalDate.of(2026, 5, 18), LocalTime.of(9, 30));
        Cita cita18 = new Cita(new Paciente("Salome", "8888888", "8888888"), especialista9, "Endodoncia control", LocalDate.of(2026, 5, 15), LocalTime.of(10, 0));
        Cita cita19 = new Cita(new Paciente("Dulce", "9999999", "9999999"), especialista10, "Evaluación periodontal", LocalDate.of(2026, 5, 16), LocalTime.of(10, 30));
        Cita cita20 = new Cita(new Paciente("Emilia", "1212121", "1212121"), especialista10, "Limpieza profunda", LocalDate.of(2026, 5, 17), LocalTime.of(11, 0));
        List<Cita> citasIniciales = List.of(cita1, cita2, cita3, cita4, cita5, cita6, cita7, cita8, cita9, cita10, cita11, cita12, cita13, cita14, cita15, cita16, cita17, cita18, cita19, cita20);
        for (Cita cita : citasIniciales) {

            clinica.agregarCita(cita);

            clinica.eliminarHorarioEspecialista(cita.getEspecialista(), cita.getFecha(), cita.getHora());
        }

        return clinica;
    }

    public boolean verificarCredenciales(String username, String password) {
        return clinica.verificarCredenciales(username, password);
    }

    public List<Especialista> obtenerEspecialistas() {
        return clinica.getEspecialistaList();
    }

    public boolean agregarCita(Cita cita) {
        return clinica.agregarCita(cita);
    }

    public void eliminarHorarioEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        clinica.eliminarHorarioEspecialista(especialista, fecha, hora);
    }

    public List<Cita> obtenerCitasEspecialista(Especialista especialista) {
        return clinica.obtenerCitasEspecialista(especialista);
    }

    public void eliminarEspecialista(Especialista especialista) {
        clinica.eliminarEspecialista(especialista);
    }

    public void agregarEspecialista(Especialista especialista) {
        clinica.agregarEspecialista(especialista);
    }

    public boolean existeCitaEspecialista(Especialista especialista, LocalDate fecha, LocalTime hora) {
        return clinica.existeCitaEspecialista(especialista, fecha, hora);
    }

    public void eliminarCita(Cita citaSeleccionada) {
        clinica.eliminarCita(citaSeleccionada);
    }
}