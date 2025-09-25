public interface Transaccion {
    void ejecutar() throws IllegalArgumentException;
    String getTipo();
    double getMonto();
    Cuenta getCuenta();
}