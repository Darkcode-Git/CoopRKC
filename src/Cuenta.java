import java.util.Objects;

/**
 * Clase abstracta base para todas las cuentas.
 * Implementa abstracción y define el contrato común.
 */
public abstract class Cuenta {
    protected final String numeroCuenta;
    protected double saldo;

    public Cuenta(String numeroCuenta, double saldoInicial) {
        this.numeroCuenta = Objects.requireNonNull(numeroCuenta,
                "El número de cuenta no puede ser nulo");

        if (numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede estar vacío");
        }

        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }

        this.saldo = saldoInicial;
    }

    /**
     * Método para depositar dinero
     */
    public void depositar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser mayor a 0");
        }
        this.saldo += monto;
    }

    /**
     * Método para retirar dinero con validación de saldo
     */
    public void retirar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a retirar debe ser mayor a 0");
        }

        if (saldo < monto) {
            throw new IllegalArgumentException(
                    String.format("Saldo insuficiente. Saldo actual: $%.2f, Monto solicitado: $%.2f",
                            saldo, monto));
        }

        this.saldo -= monto;
    }

    // Método abstracto para aplicar políticas específicas
    public abstract void aplicarComision();

    // Getters con encapsulamiento
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    @Override
    public String toString() {
        return String.format("Cuenta{numero='%s', saldo=%.2f}", numeroCuenta, saldo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cuenta cuenta = (Cuenta) obj;
        return Objects.equals(numeroCuenta, cuenta.numeroCuenta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroCuenta);
    }
}