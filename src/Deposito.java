import java.util.Objects;
import java.util.logging.Logger;

public final class Deposito implements Transaccion {
    private static final Logger LOGGER = Logger.getLogger(Deposito.class.getName());

    private final Cuenta cuenta;
    private final double monto;
    private final String tipo = "DEPOSITO";

    public Deposito(Cuenta cuenta, double monto) {
        this.cuenta = Objects.requireNonNull(cuenta, "La cuenta no puede ser nula");
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del depósito debe ser mayor a 0");
        }
        this.monto = monto;
    }

    @Override
    public void ejecutar() throws IllegalArgumentException {
        try {
            double saldoAnterior = cuenta.getSaldo();
            cuenta.depositar(monto);

            LOGGER.info(String.format("Depósito ejecutado: Cuenta %s, Monto: %.2f, Saldo anterior: %.2f, Nuevo saldo: %.2f",
                    cuenta.getNumeroCuenta(), monto, saldoAnterior, cuenta.getSaldo()));

        } catch (Exception e) {
            LOGGER.severe("Error al ejecutar depósito: " + e.getMessage());
            throw new IllegalArgumentException("No se pudo ejecutar el depósito: " + e.getMessage(), e);
        }
    }

    @Override
    public String getTipo() {
        return tipo;
    }

    @Override
    public double getMonto() {
        return monto;
    }

    @Override
    public Cuenta getCuenta() {
        return cuenta;
    }

    @Override
    public String toString() {
        return String.format("Deposito{cuenta='%s', monto=%.2f}",
                cuenta.getNumeroCuenta(), monto);
    }
}