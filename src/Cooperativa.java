import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;


 //Clase principal de la cooperativa con programación funcional integrada
 
public final class Cooperativa {
    private static final Logger LOGGER = Logger.getLogger(Cooperativa.class.getName());

    private final String nombre;
    private final String nit;
    private final List<Socio> socios;
    private final List<Cuenta> cuentas;
    private final Map<String, Socio> indiceCedulas;
    private final Map<String, Cuenta> indiceNumeroCuentas;

    public Cooperativa(String nombre, String nit) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.nit = Objects.requireNonNull(nit, "El NIT no puede ser nulo");

        this.socios = new CopyOnWriteArrayList<>();
        this.cuentas = new CopyOnWriteArrayList<>();
        this.indiceCedulas = new ConcurrentHashMap<>();
        this.indiceNumeroCuentas = new ConcurrentHashMap<>();

        LOGGER.log(Level.INFO, "Cooperativa creada: {0} (NIT: {1})", new Object[]{nombre, nit});
    }

    public void listarSociosRegistrados() {
        System.out.println("\n=== SOCIOS REGISTRADOS (Programación Funcional) ===");
        socios.stream()
                .map(Socio::getNombre)
                .sorted()
                .forEach(nombre -> System.out.println("• " + nombre));
    }

    public Stream<String> streamNombresSocios() {
        return socios.stream()
                .map(Socio::getNombre);
    }

    public List<Cuenta> filtrarCuentasPorSaldoMinimo(double saldoMinimo) {
        return cuentas.stream()
                .filter(cuenta -> cuenta.getSaldo() > saldoMinimo)
                .sorted(Comparator.comparingDouble(Cuenta::getSaldo).reversed())
                .collect(Collectors.toUnmodifiableList());
    }

    public void mostrarCuentasConSaldoMayor(double saldoMinimo) {
        System.out.printf("\n=== CUENTAS CON SALDO > $%.2f ===\n", saldoMinimo);
        cuentas.stream()
                .filter(cuenta -> cuenta.getSaldo() > saldoMinimo)
                .sorted(Comparator.comparingDouble(Cuenta::getSaldo).reversed())
                .forEach(cuenta -> System.out.printf("• %s: $%.2f - Propietario: %s\n",
                        cuenta.getNumeroCuenta(),
                        cuenta.getSaldo(),
                        obtenerPropietarioCuenta(cuenta)));
    }

    public double obtenerSumaTotalSaldos() {
        return cuentas.stream()
                .mapToDouble(Cuenta::getSaldo)
                .sum();
    }

    public void mostrarCalculosTotales() {
        System.out.println("\n=== CÁLCULOS TOTALES (Programación Funcional) ===");
        double totalSaldos = obtenerSumaTotalSaldos();
        System.out.printf("Total saldos: $%.2f%n", totalSaldos);
    }

    public void registrarSocio(Socio socio) {
        Objects.requireNonNull(socio, "El socio no puede ser nulo");
        String cedula = socio.getCedula();

        if (indiceCedulas.containsKey(cedula)) {
            throw new IllegalArgumentException("Ya existe un socio registrado con la cédula: " + cedula);
        }

        socios.add(socio);
        indiceCedulas.put(cedula, socio);
        LOGGER.log(Level.INFO, "Socio registrado: {0} (Cédula: {1})", new Object[]{socio.getNombre(), cedula});
    }

    public void agregarCuenta(Cuenta cuenta) {
        Objects.requireNonNull(cuenta, "La cuenta no puede ser nula");
        String numeroCuenta = cuenta.getNumeroCuenta();

        if (indiceNumeroCuentas.containsKey(numeroCuenta)) {
            throw new IllegalArgumentException("Ya existe una cuenta con el número: " + numeroCuenta);
        }

        cuentas.add(cuenta);
        indiceNumeroCuentas.put(numeroCuenta, cuenta);
        LOGGER.log(Level.INFO, "Cuenta agregada: {0}", numeroCuenta);
    }

    public void aplicarInteresesCuentasAhorro() {
        System.out.println("\n=== APLICANDO INTERESES ===");
        long cuentasAfectadas = cuentas.stream()
                .filter(CuentaAhorros.class::isInstance)
                .map(CuentaAhorros.class::cast)
                .peek(cuenta -> {
                    double saldoAnterior = cuenta.getSaldo();
                    cuenta.aplicarIntereses();
                    System.out.printf("Cuenta %s: $%.2f → $%.2f (Interés: %.2f%%)%n",
                            cuenta.getNumeroCuenta(),
                            saldoAnterior,
                            cuenta.getSaldo(),
                            cuenta.getTasaInteres() * 100);
                })
                .count();
        System.out.printf("Intereses aplicados a %d cuenta(s) de ahorro.%n", cuentasAfectadas);
    }

    public Socio buscarSocioPorCedula(String cedula) {
        return indiceCedulas.get(cedula);
    }

    public Cuenta buscarCuenta(String numeroCuenta) {
        return indiceNumeroCuentas.get(numeroCuenta);
    }

    private String obtenerPropietarioCuenta(Cuenta cuenta) {
        return socios.stream()
                .filter(socio -> socio.getCuentas().contains(cuenta))
                .map(Socio::getNombre)
                .findFirst()
                .orElse("Propietario no encontrado");
    }

    public void generarReporteCompleto(double saldoMinimo) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("       REPORTE COMPLETO DE LA COOPERATIVA");
        System.out.println("=".repeat(60));

        DoubleSummaryStatistics estadisticas = cuentas.stream()
                .mapToDouble(Cuenta::getSaldo)
                .summaryStatistics();

        System.out.printf("""
                        📊 ESTADÍSTICAS:
                        • Total de socios: %d
                        • Total de cuentas: %d
                        • Saldo total: $%.2f
                        • Saldo promedio: $%.2f
                        • Saldo máximo: $%.2f
                        • Saldo mínimo: $%.2f
                        %n""",
                socios.size(),
                cuentas.size(),
                estadisticas.getSum(),
                estadisticas.getAverage(),
                estadisticas.getMax(),
                estadisticas.getMin());

        listarSociosRegistrados();
        mostrarCuentasConSaldoMayor(saldoMinimo);
        mostrarCalculosTotales();
    }

    public String getNombre() {
        return nombre;
    }

    public String getNit() {
        return nit;
    }

    public List<Socio> getSocios() {
        return Collections.unmodifiableList(socios);
    }

    public List<Cuenta> getCuentas() {
        return Collections.unmodifiableList(cuentas);
    }
}
