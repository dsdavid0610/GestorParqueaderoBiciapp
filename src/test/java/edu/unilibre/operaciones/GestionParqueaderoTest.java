package edu.unilibre.operaciones;

import edu.unilibre.datos.Bicicleta;
import edu.unilibre.datos.MetodoPago;
import edu.unilibre.datos.Parqueadero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GestionParqueaderoTest {

    private GestionParqueadero servicio;
    private Parqueadero parqueadero;

    @BeforeEach
    public void setUp() {
        servicio = new GestionParqueadero();
        parqueadero = new Parqueadero();
    }

    @Test
    public void registrarIngresoOk() {
        Bicicleta bici = servicio.registrarIngreso(parqueadero, "123456789", "SER-001", "Montañera", "Rojo");

        assertEquals("123456789", bici.obtenerCedulaDueño());
        assertEquals("SER-001", bici.obtenerNumeroSerie());
        assertEquals("Montañera", bici.obtenerTipoBicicleta());
        assertEquals("Rojo", bici.obtenerColor());
        assertNotNull(bici.obtenerHoraIngreso());
        assertTrue(bici.estaEnParqueadero());
        assertEquals(1, parqueadero.obtenerCantidadOcupados());
    }

    @Test
    public void registrarIngresoConParqueaderoLlenoLanzaExcepcion() {
        for (int i = 0; i < Parqueadero.CAPACIDAD_MAXIMA; i++) {
            servicio.registrarIngreso(parqueadero, "cedula" + i, "serie" + i, "Urbana", "Azul");
        }

        assertThrows(IllegalStateException.class,
                () -> servicio.registrarIngreso(parqueadero, "cedulaExtra", "serieExtra", "Urbana", "Azul"));
    }

    @Test
    public void registrarSalidaCobraPorCadaMinutoTranscurrido() {
        // Caso del ejemplo real: si la bicicleta dura 15 minutos, debe cobrar 15 x $1.000 = $15.000.
        Bicicleta biciQuinceMin = servicio.registrarIngreso(parqueadero, "111111111", "SER-111", "Montañera", "Negro");
        biciQuinceMin.establecerHoraIngreso(LocalDateTime.now().minusMinutes(15));

        Bicicleta salidaQuinceMin = servicio.registrarSalida(parqueadero, "111111111");

        assertEquals(15 * GestionParqueadero.TARIFA_POR_MINUTO, salidaQuinceMin.obtenerValorCobrado());
        assertSame(biciQuinceMin, salidaQuinceMin);
        assertTrue(salidaQuinceMin.estaPendientePago());
    }

    @Test
    public void registrarPagoConMetodoLiberaCupoYActualizaElReporteDelDia() {
        Bicicleta bici = servicio.registrarIngreso(parqueadero, "333333333", "SER-333", "Urbana", "Verde");
        bici.establecerHoraIngreso(LocalDateTime.now().minusMinutes(10));
        servicio.registrarSalida(parqueadero, "333333333");

        assertEquals(1, parqueadero.obtenerCantidadOcupados());

        double valorPagado = servicio.registrarPago(parqueadero, "333333333", MetodoPago.NEQUI);

        double valorEsperado = 10 * GestionParqueadero.TARIFA_POR_MINUTO;
        assertEquals(valorEsperado, valorPagado);
        assertEquals(MetodoPago.NEQUI, bici.obtenerMetodoPago());
        assertTrue(bici.estaPagada());
        assertEquals(0, parqueadero.obtenerCantidadOcupados());
        assertEquals(1, servicio.obtenerReporteDelDia().obtenerCantidadBicicletas());
        assertEquals(valorEsperado, servicio.obtenerReporteDelDia().obtenerValorTotalIngresado());
    }
}
