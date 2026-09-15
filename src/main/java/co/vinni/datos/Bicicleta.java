package co.vinni.datos;

import java.time.LocalDateTime;

/**
 * @author  : Vinni 2026
 *
 * Clase encapsulada: atributos privados con acceso mediante
 * métodos "obtener" (getters) y "establecer" (setters) en español.
 *
 * Representa una bicicleta mientras está dentro del parqueadero.
 * Se identifica con la cédula del dueño, el número de serie de la
 * bicicleta y su color (Rq01), y guarda además el tipo de
 * bicicleta que pide el Home del proyecto.
 */
public class Bicicleta {

    private String cedulaDueño;
    private String numeroSerie;
    private String tipoBicicleta;
    private String color;
    private LocalDateTime horaIngreso;
    private LocalDateTime horaSalida;
    private double valorCobrado;
    private MetodoPago metodoPago;
    private boolean pagada;

    public Bicicleta() {
    }

    public Bicicleta(String cedulaDueño, String numeroSerie, String tipoBicicleta,
                      String color, LocalDateTime horaIngreso) {
        this.cedulaDueño = cedulaDueño;
        this.numeroSerie = numeroSerie;
        this.tipoBicicleta = tipoBicicleta;
        this.color = color;
        this.horaIngreso = horaIngreso;
        this.pagada = false;
    }

    public String obtenerCedulaDueño() {
        return cedulaDueño;
    }

    public void establecerCedulaDueño(String cedulaDueño) {
        this.cedulaDueño = cedulaDueño;
    }

    public String obtenerNumeroSerie() {
        return numeroSerie;
    }

    public void establecerNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String obtenerTipoBicicleta() {
        return tipoBicicleta;
    }

    public void establecerTipoBicicleta(String tipoBicicleta) {
        this.tipoBicicleta = tipoBicicleta;
    }

    public String obtenerColor() {
        return color;
    }

    public void establecerColor(String color) {
        this.color = color;
    }

    public LocalDateTime obtenerHoraIngreso() {
        return horaIngreso;
    }

    public void establecerHoraIngreso(LocalDateTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public LocalDateTime obtenerHoraSalida() {
        return horaSalida;
    }

    public void establecerHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public double obtenerValorCobrado() {
        return valorCobrado;
    }

    public void establecerValorCobrado(double valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public MetodoPago obtenerMetodoPago() {
        return metodoPago;
    }

    public void establecerMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public boolean estaPagada() {
        return pagada;
    }

    public void establecerPagada(boolean pagada) {
        this.pagada = pagada;
    }

    /** true mientras la bicicleta sigue físicamente parqueada (aún no se le registra salida). */
    public boolean estaEnParqueadero() {
        return horaSalida == null;
    }

    /** true cuando ya salió pero el dueño todavía no ha pagado (cupo aún no liberado). */
    public boolean estaPendientePago() {
        return horaSalida != null && !pagada;
    }

    @Override
    public String toString() {
        return "Bicicleta{cédula=" + cedulaDueño + ", serie=" + numeroSerie + ", ingreso=" + horaIngreso + "}";
    }
}
