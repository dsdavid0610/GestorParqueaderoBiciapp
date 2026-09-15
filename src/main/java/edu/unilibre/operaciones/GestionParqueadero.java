package edu.unilibre.operaciones;

import edu.unilibre.datos.Bicicleta;
import edu.unilibre.datos.MetodoPago;
import edu.unilibre.datos.Parqueadero;
import edu.unilibre.datos.ReporteDiario;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author  : Vinni 2026
 *
 * Capa de operaciones/reglas de negocio del parqueadero de
 * bicicletas. Usa los métodos obtener/establecer de las clases
 * encapsuladas de "datos" para registrar ingresos, salidas, pagos
 * y el reporte del día, siguiendo los requerimientos Rq01 a Rq05.
 */
public class GestionParqueadero {

    /** Valor cobrado por cada minuto que la bicicleta permanece en el parqueadero (Rq03). */
    public static final double TARIFA_POR_MINUTO = 1000.0;

    private final ReporteDiario reporteDelDia = new ReporteDiario(LocalDate.now());

    /** Rq01: agrega una bicicleta identificándola con la cédula del dueño, el número de serie y el color. */
    public Bicicleta registrarIngreso(Parqueadero parqueadero, String cedulaDueño,
                                       String numeroSerie, String tipoBicicleta, String color) {
        if (parqueadero == null) {
            throw new IllegalStateException("Debe existir un parqueadero inicializado.");
        }
        if (cedulaDueño == null || cedulaDueño.isBlank()) {
            throw new IllegalArgumentException("La cédula del dueño es obligatoria.");
        }
        if (numeroSerie == null || numeroSerie.isBlank()) {
            throw new IllegalArgumentException("El número de serie de la bicicleta es obligatorio.");
        }
        if (tipoBicicleta == null || tipoBicicleta.isBlank()) {
            throw new IllegalArgumentException("El tipo de bicicleta es obligatorio.");
        }
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("El color de la bicicleta es obligatorio.");
        }
        // Rq02: máximo 20 cupos.
        if (parqueadero.obtenerCantidadOcupados() >= Parqueadero.CAPACIDAD_MAXIMA) {
            throw new IllegalStateException(
                    "El parqueadero ya alcanzó su capacidad máxima de " + Parqueadero.CAPACIDAD_MAXIMA + " cupos.");
        }

        Bicicleta bicicleta = new Bicicleta(cedulaDueño, numeroSerie, tipoBicicleta, color, LocalDateTime.now());
        boolean agregada = parqueadero.agregarBicicleta(bicicleta);
        if (!agregada) {
            throw new IllegalStateException("No hay cupos disponibles en este momento.");
        }
        return bicicleta;
    }

    /** Rq03/Rq04: calcula el valor a cobrar (a $1.000 por minuto) identificando la bicicleta por la cédula del dueño. */
    public Bicicleta registrarSalida(Parqueadero parqueadero, String cedulaDueño) {
        if (parqueadero == null) {
            throw new IllegalStateException("Debe existir un parqueadero inicializado.");
        }
        if (cedulaDueño == null || cedulaDueño.isBlank()) {
            throw new IllegalArgumentException("La cédula del dueño es obligatoria.");
        }

        Bicicleta encontrada = buscarBicicletaParqueada(parqueadero, cedulaDueño);
        if (encontrada == null) {
            throw new IllegalArgumentException(
                    "No hay ninguna bicicleta parqueada con la cédula " + cedulaDueño + ".");
        }

        encontrada.establecerHoraSalida(LocalDateTime.now());

        long minutos = Duration.between(encontrada.obtenerHoraIngreso(), encontrada.obtenerHoraSalida()).toMinutes();
        if (minutos < 1) {
            minutos = 1; // se cobra mínimo 1 minuto, aunque la salida sea casi inmediata
        }
        double valor = minutos * TARIFA_POR_MINUTO;
        encontrada.establecerValorCobrado(valor);

        return encontrada;
    }

    /** Rq03: registra el método de pago elegido por el dueño, libera el cupo y actualiza el reporte del día. */
    public double registrarPago(Parqueadero parqueadero, String cedulaDueño, MetodoPago metodoPago) {
        if (parqueadero == null) {
            throw new IllegalStateException("Debe existir un parqueadero inicializado.");
        }
        if (cedulaDueño == null || cedulaDueño.isBlank()) {
            throw new IllegalArgumentException("La cédula del dueño es obligatoria.");
        }
        if (metodoPago == null) {
            throw new IllegalArgumentException("Debe elegir un método de pago.");
        }

        Bicicleta encontrada = buscarBicicletaPendientePago(parqueadero, cedulaDueño);
        if (encontrada == null) {
            throw new IllegalArgumentException(
                    "No hay ninguna bicicleta pendiente de pago con la cédula " + cedulaDueño + ".");
        }

        encontrada.establecerMetodoPago(metodoPago);
        encontrada.establecerPagada(true);

        boolean liberado = parqueadero.retirarBicicleta(encontrada);
        if (!liberado) {
            throw new IllegalStateException("No se pudo liberar el cupo de esa bicicleta.");
        }

        reporteDelDia.registrarPago(encontrada.obtenerValorCobrado());
        return encontrada.obtenerValorCobrado();
    }

    /** Rq05: reporte diario con ganancias y bicicletas retiradas/cobradas. */
    public ReporteDiario obtenerReporteDelDia() {
        return reporteDelDia;
    }

    private Bicicleta buscarBicicletaParqueada(Parqueadero parqueadero, String cedulaDueño) {
        for (Bicicleta b : parqueadero.obtenerBicicletas()) {
            if (b != null && b.estaEnParqueadero() && cedulaDueño.equals(b.obtenerCedulaDueño())) {
                return b;
            }
        }
        return null;
    }

    private Bicicleta buscarBicicletaPendientePago(Parqueadero parqueadero, String cedulaDueño) {
        for (Bicicleta b : parqueadero.obtenerBicicletas()) {
            if (b != null && b.estaPendientePago() && cedulaDueño.equals(b.obtenerCedulaDueño())) {
                return b;
            }
        }
        return null;
    }
}
