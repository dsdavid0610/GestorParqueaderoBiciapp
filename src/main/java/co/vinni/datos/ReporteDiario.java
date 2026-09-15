package co.vinni.datos;

import java.time.LocalDate;

/**
 * @author  : Vinni 2026
 *
 * Clase encapsulada: atributos privados con acceso mediante
 * métodos "obtener" (getters) en español. Acumula, para un día
 * en concreto, cuántas bicicletas pagaron y salieron, y cuánto
 * dinero total se recaudó.
 */
public class ReporteDiario {

    private final LocalDate fecha;
    private int cantidadBicicletas;
    private double valorTotalIngresado;

    public ReporteDiario(LocalDate fecha) {
        this.fecha = fecha;
        this.cantidadBicicletas = 0;
        this.valorTotalIngresado = 0.0;
    }

    public LocalDate obtenerFecha() {
        return fecha;
    }

    public int obtenerCantidadBicicletas() {
        return cantidadBicicletas;
    }

    public double obtenerValorTotalIngresado() {
        return valorTotalIngresado;
    }

    /**
     * Registra un pago ya confirmado: suma una bicicleta más al
     * conteo del día y acumula el valor cobrado.
     */
    public void registrarPago(double valorCobrado) {
        this.cantidadBicicletas++;
        this.valorTotalIngresado += valorCobrado;
    }
}
