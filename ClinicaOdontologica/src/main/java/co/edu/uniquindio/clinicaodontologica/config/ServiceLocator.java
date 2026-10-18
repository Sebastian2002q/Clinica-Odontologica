package co.edu.uniquindio.clinicaodontologica.config;

import co.edu.uniquindio.clinicaodontologica.repository.*;
import co.edu.uniquindio.clinicaodontologica.service.*;
import lombok.Getter;

/**
  * Clase encargada de centralizar la creación de los repositorios y servicios
  * del sistema bajo una misma instancia en memoria (patrón Singleton).
  */
@Getter
public class ServiceLocator {

    /**
      * Instancia única del contenedor de servicios para toda la aplicación.
      */
    private static ServiceLocator instance;

    /**
      * Repositorio en memoria de los usuarios del sistema.
      */
    private final UsuarioRepository usuarioRepository;

    /**
      * Repositorio en memoria de los pacientes registrados.
      */
    private final PacienteRepository pacienteRepository;

    /**
      * Repositorio en memoria de los especialistas y sus agendas.
      */
    private final EspecialistaRepository especialistaRepository;

    /**
      * Repositorio en memoria de las citas agendadas.
      */
    private final CitaRepository citaRepository;

    /**
      * Repositorio en memoria de las historias clínicas.
      */
    private final HistoriaClinicaRepository historiaClinicaRepository;

    /**
      * Repositorio en memoria de los insumos odontológicos.
      */
    private final InventarioRepository inventarioRepository;

    /**
      * Repositorio en memoria de la caja y pagos registrados.
      */
    private final CajaRepository cajaRepository;

    /**
      * Repositorio en memoria de los recordatorios enviados.
      */
    private final RecordatorioRepository recordatorioRepository;

    /**
      * Servicio de autenticación y gestión de usuarios.
      */
    private final AuthService authService;

    /**
      * Servicio de lógica de negocio para el registro y consulta de pacientes.
      */
    private final PacienteService pacienteService;

    /**
      * Servicio de lógica de negocio para la gestión de especialistas.
      */
    private final EspecialistaService especialistaService;

    /**
      * Servicio de lógica de negocio para el agendamiento, cancelación y reprogramación de citas.
      */
    private final AgendamientoService agendamientoService;

    /**
      * Servicio de lógica de negocio para el control de inventario de insumos.
      */
    private final InventarioService inventarioService;

    /**
      * Servicio de lógica de negocio para el registro de procedimientos en la historia clínica.
      */
    private final AtencionClinicaService atencionClinicaService;

    /**
      * Servicio de lógica de negocio para el registro de pagos y consulta de caja.
      */
    private final FacturacionService facturacionService;

    /**
      * Servicio de lógica de negocio para el envío de recordatorios de citas.
      */
    private final RecordatorioService recordatorioService;

    /**
      * Constructor por defecto que inicializa los servicios con los datos quemados de prueba.
      */
    public ServiceLocator() {
        this(true);
    }

    /**
      * Constructor que inicializa todos los repositorios y servicios de la clínica.
      *
      * @param cargarSemilla indica si se deben cargar los datos de prueba al iniciar
      */
    public ServiceLocator(boolean cargarSemilla) {
        this.usuarioRepository = new UsuarioRepository();
        this.pacienteRepository = new PacienteRepository();
        this.especialistaRepository = new EspecialistaRepository();
        this.citaRepository = new CitaRepository();
        this.historiaClinicaRepository = new HistoriaClinicaRepository();
        this.inventarioRepository = new InventarioRepository();
        this.cajaRepository = new CajaRepository();
        this.recordatorioRepository = new RecordatorioRepository();

        this.authService = new AuthService(usuarioRepository);
        this.pacienteService = new PacienteService(pacienteRepository, historiaClinicaRepository);
        this.especialistaService = new EspecialistaService(especialistaRepository, citaRepository);
        this.agendamientoService = new AgendamientoService(citaRepository, pacienteRepository, especialistaRepository);
        this.inventarioService = new InventarioService(inventarioRepository);
        this.atencionClinicaService = new AtencionClinicaService(historiaClinicaRepository, citaRepository, inventarioService);
        this.facturacionService = new FacturacionService(cajaRepository, citaRepository, historiaClinicaRepository);
        this.recordatorioService = new RecordatorioService(citaRepository, recordatorioRepository);

        if (cargarSemilla) {
            DataSeeder.cargarDatosIniciales(
                    authService,
                    pacienteService,
                    especialistaService,
                    agendamientoService,
                    inventarioService,
                    atencionClinicaService
            );
        }
    }

    /**
      * Obtiene la instancia única de ServiceLocator para compartir los datos entre ventanas.
      *
      * @return instancia única de ServiceLocator
      */
    public static synchronized ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator(true);
        }
        return instance;
    }
}
