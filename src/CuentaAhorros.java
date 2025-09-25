/**
 * Cuenta de ahorros con tasa de interés.
 * Hereda de Cuenta e implementa polimorfismo.
 */
public final class CuentaAhorros extends Cuenta {
    private final double tasaInteres;
    private static final double COMISION_MANEJO = 5000.0;

    public CuentaAhorros(String numeroCuenta, double saldoInicial, double tasaInteres) {
        super(numeroCuenta, saldoInicial);

        if (tasaInteres < 0 || tasaInteres > 1) {
            throw new IllegalArgumentException(
                    "La tasa de interés debe estar entre 0 y 1 (0% y 100%)");
        }

        this.tasaInteres = tasaInteres;
    }

    /**
     * Aplica intereses a la cuenta
     */
    public void aplicarIntereses() {
        double intereses = saldo * tasaInteres;
        saldo += intereses;
    }

    /**
     * Implementación polimórfica de aplicar comisión
     */
    @Override
    public void aplicarComision() {
        if (saldo >= COMISION_MANEJO) {
            saldo -= COMISION_MANEJO;
        }
    }

    /**
     * Override del método retirar para aplicar reglas específicas de cuenta de ahorros
     */
    @Override
    public void retirar(double monto) {
        // Validación adicional para cuenta de ahorros
        double saldoMinimo = 50000.0;

        if ((saldo - monto) < saldoMinimo) {
            throw new IllegalArgumentException(
                    String.format("El retiro dejaría un saldo menor al mínimo permitido ($%.2f). " +
                                    "Saldo actual: $%.2f, Monto solicitado: $%.2f",
                            saldoMinimo, saldo, monto));
        }

        super.retirar(monto);
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    @Override
    public String toString() {
        return String.format("CuentaAhorros{numero='%s', saldo=%.2f, tasaInteres=%.3f}",
                numeroCuenta, saldo, tasaInteres);
    }
}