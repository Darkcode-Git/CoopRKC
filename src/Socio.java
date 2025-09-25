import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Representa un socio de la cooperativa.
 * Aplica principios de encapsulamiento y inmutabilidad.
 */
public final class Socio {
    private final String nombre;
    private final String cedula;
    private final List<Cuenta> cuentas;

    public Socio(String nombre, String cedula) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.cedula = Objects.requireNonNull(cedula, "La cédula no puede ser nula");

        if (nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (cedula.trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula no puede estar vacía");
        }

        this.cuentas = new ArrayList<>();
    }

    /**
     * Agrega una cuenta al socio con validación de duplicados
     */
    public void agregarCuenta(Cuenta cuenta) {
        Objects.requireNonNull(cuenta, "La cuenta no puede ser nula");

        if (cuentas.stream()
                .anyMatch(c -> c.getNumeroCuenta().equals(cuenta.getNumeroCuenta()))) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta con el número: " + cuenta.getNumeroCuenta());
        }

        cuentas.add(cuenta);
    }

    /**
     * Obtiene stream de cuentas para programación funcional
     */
    public Stream<Cuenta> streamCuentas() {
        return cuentas.stream();
    }

    /**
     * Calcula saldo total usando streams
     */
    public double calcularSaldoTotal() {
        return cuentas.stream()
                .mapToDouble(Cuenta::getSaldo)
                .sum();
    }

    // Getters con encapsulamiento
    public String getNombre() {
        return nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public List<Cuenta> getCuentas() {
        return Collections.unmodifiableList(cuentas);
    }

    @Override
    public String toString() {
        return String.format("Socio{nombre='%s', cedula='%s', cuentas=%d}",
                nombre, cedula, cuentas.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Socio socio = (Socio) obj;
        return Objects.equals(cedula, socio.cedula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cedula);
    }
}