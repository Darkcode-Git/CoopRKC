import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;

/**
 * Clase principal que demuestra la funcionalidad del sistema de la Cooperativa
 * Optimizada para JDK 24
 */
public class CooperativaSistemaGestor {
    private static final Logger LOGGER = Logger.getLogger(CooperativaSistemaGestor.class.getName());
    private static final double SALDO_MINIMO_FILTRO = 500000.0;

    public static void main(String[] args) {
        try {
            // Crear cooperativa mock para demostración
            var cooperativa = new CooperativaMock();
            inicializarCooperativa(cooperativa);
            realizarOperaciones(cooperativa);
            mostrarReportes(cooperativa);
            realizarValidaciones(cooperativa);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error crítico en la aplicación", e);
            System.exit(1);
        }
    }

    /**
     * Inicializa la cooperativa con datos de prueba
     */
    private static void inicializarCooperativa(CooperativaMock cooperativa) {
        if (cooperativa == null) {
            throw new IllegalArgumentException("La cooperativa no puede ser nula");
        }

        try {
            // Registro de socios usando records
            record DatosSocio(String nombre, String cedula, double tasaInteres, String numeroCuenta, double deposito) {}

            var datosSocios = List.of(
                    new DatosSocio("Ana Gómez", "1001", 0.02, "AH-1001-1", 600000),
                    new DatosSocio("Carlos Pérez", "1002", 0.03, "AH-1002-1", 200000),
                    new DatosSocio("María López", "1003", 0.015, "AH-1003-1", 800000),
                    new DatosSocio("Carlos Pérez", "1002", 0.03, "AH-1002-2", 400000)
            );

            datosSocios.forEach(datos -> {
                try {
                    var socio = Optional.ofNullable(cooperativa.buscarSocioPorCedula(datos.cedula()))
                            .orElseGet(() -> {
                                var nuevoSocio = new Socio(datos.nombre(), datos.cedula());
                                cooperativa.registrarSocio(nuevoSocio);
                                return nuevoSocio;
                            });

                    // Crear cuenta con 3 parámetros: número, saldo inicial y tasa de interés
                    var cuenta = new CuentaAhorros(datos.numeroCuenta(), datos.deposito(), datos.tasaInteres());
                    socio.agregarCuenta(cuenta);
                    cooperativa.agregarCuenta(cuenta);

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error al procesar socio: " + datos.nombre(), e);
                }
            });

            LOGGER.info("Inicialización de la cooperativa completada exitosamente");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error durante la inicialización", e);
            throw new RuntimeException("Error de inicialización", e);
        }
    }

    /**
     * Realiza operaciones de retiro y aplicación de intereses
     */
    private static void realizarOperaciones(CooperativaMock cooperativa) {
        try {
            // Realizar retiros usando records
            record OperacionRetiro(String numeroCuenta, double monto) {}

            var retiros = List.of(
                    new OperacionRetiro("AH-1002-1", 50000),
                    new OperacionRetiro("AH-1001-1", 100000) // Ajustado para evitar saldo insuficiente
            );

            retiros.forEach(retiro -> {
                try {
                    var cuenta = cooperativa.buscarCuenta(retiro.numeroCuenta());
                    if (cuenta != null) {
                        new Retiro(cuenta, retiro.monto()).ejecutar();
                        LOGGER.info("Retiro ejecutado: " + retiro.monto());
                    } else {
                        LOGGER.warning("Cuenta no encontrada: " + retiro.numeroCuenta());
                    }
                } catch (Exception e) {
                    LOGGER.warning("Error en retiro: " + e.getMessage());
                }
            });

            // Aplicar intereses usando streams
            cooperativa.todasLasCuentas().stream()
                    .filter(CuentaAhorros.class::isInstance)
                    .map(CuentaAhorros.class::cast)
                    .forEach(CuentaAhorros::aplicarIntereses);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error durante las operaciones", e);
        }
    }

    /**
     * Muestra reportes de la cooperativa
     */
    private static void mostrarReportes(CooperativaMock cooperativa) {
        try {
            // Usando text blocks (JDK 15+)
            System.out.println("""
                
                === REPORTES DE LA COOPERATIVA ===
                """);

            // Listar socios usando method reference
            System.out.println("Socios registrados:");
            cooperativa.listarSocios().stream()
                    .map(Socio::getNombre)
                    .forEach(nombre -> System.out.printf("- %s%n", nombre));

            // Filtrar cuentas usando streams
            System.out.println("\nCuentas con saldo mayor a " + String.format("%.2f", SALDO_MINIMO_FILTRO) + ":");
            cooperativa.todasLasCuentas().stream()
                    .filter(cuenta -> cuenta.getSaldo() > SALDO_MINIMO_FILTRO)
                    .forEach(cuenta -> System.out.printf("Cuenta: %s, Saldo: %.2f%n",
                            cuenta.getNumeroCuenta(), cuenta.getSaldo()));

            // Calcular total usando streams
            double total = cooperativa.todasLasCuentas().stream()
                    .mapToDouble(Cuenta::getSaldo)
                    .sum();
            System.out.printf("%nTotal en la cooperativa: %.2f%n", total);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error mostrando reportes", e);
        }
    }

    /**
     * Realiza validaciones del sistema
     */
    private static void realizarValidaciones(CooperativaMock cooperativa) {
        try {
            // Intentar crear cuenta duplicada
            var socio = cooperativa.buscarSocioPorCedula("1002");
            if (socio != null) {
                var cuentaDuplicada = new CuentaAhorros("AH-1002-1", 0.0, 0.01);
                socio.agregarCuenta(cuentaDuplicada);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.info("Validación exitosa - prevención de cuenta duplicada: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error inesperado en validaciones", e);
        }
    }

    /**
     * Clase mock para simular la funcionalidad de Cooperativa
     */
    static class CooperativaMock {
        private final List<Socio> socios = new ArrayList<>();
        private final List<Cuenta> cuentas = new ArrayList<>();

        public void registrarSocio(Socio socio) {
            socios.add(socio);
        }

        public void agregarCuenta(Cuenta cuenta) {
            cuentas.add(cuenta);
        }

        public Socio buscarSocioPorCedula(String cedula) {
            return socios.stream()
                    .filter(s -> s.getCedula().equals(cedula))
                    .findFirst()
                    .orElse(null);
        }

        public Cuenta buscarCuenta(String numeroCuenta) {
            return cuentas.stream()
                    .filter(c -> c.getNumeroCuenta().equals(numeroCuenta))
                    .findFirst()
                    .orElse(null);
        }

        public List<Socio> listarSocios() {
            return new ArrayList<>(socios);
        }

        public List<Cuenta> todasLasCuentas() {
            return new ArrayList<>(cuentas);
        }
    }
}