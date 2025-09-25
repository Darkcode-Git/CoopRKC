import java.util.Objects;
import java.util.logging.Logger;

public final class Retiro implements Transaccion {
    private static final Logger LOGGER = Logger.getLogger(Retiro.class.getName());

    private final Cuenta cuenta;
    private final double monto;
    private final String tipo = "RETIRO";

    public Retiro(Cuenta cuenta, double monto) {
        this.cuenta = Objects.requireNonNull(cuenta, "La cuenta no puede ser nula");
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto del retiro debe ser mayor a 0");
        }
        this.monto = monto;
    }

    @Override
    public void ejecutar() throws IllegalArgumentException {
        try {
            // Validar saldo suficiente antes del retiro
            if (cuenta.getSaldo() < monto) {
                throw new IllegalArgumentException(
                        String.format("Saldo insuficiente. Saldo actual: %.2f, Monto solicitado: %.2f",
                                cuenta.getSaldo(), monto));
            }

            double saldoAnterior = cuenta.getSaldo();
            cuenta.retirar(monto);

            LOGGER.info(String.format("Retiro ejecutado: Cuenta %s, Monto: %.2f, Saldo anterior: %.2f, Nuevo saldo: %.2f",
                    cuenta.getNumeroCuenta(), monto, saldoAnterior, cuenta.getSaldo()));

        } catch (IllegalArgumentException e) {
            LOGGER.warning("Retiro rechazado: " + e.getMessage());
            throw e; // Re-lanzar la excepción para que sea manejada por el llamador
        } catch (Exception e) {
            LOGGER.severe("Error inesperado al ejecutar retiro: " + e.getMessage());
            throw new IllegalArgumentException("No se pudo ejecutar el retiro: " + e.getMessage(), e);
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
        return String.format("Retiro{cuenta='%s', monto=%.2f}",
                cuenta.getNumeroCuenta(), monto);
    }
}