package co.vinni.datos;

import java.util.Arrays;

/**
 * @author  : Vinni 2026
 *
 * Clase encapsulada: atributo privado con acceso mediante métodos
 * "obtener" (getters) en español. El arreglo de bicicletas se
 * maneja de forma controlada dentro de la propia clase para no
 * exponer su referencia interna (encapsulamiento real, no solo
 * sintáctico).
 */
public class Parqueadero {

    /** Cupos totales disponibles para bicicletas. */
    public static final int CAPACIDAD_MAXIMA = 20;

    private Bicicleta[] bicicletas;

    public Parqueadero() {
        this.bicicletas = new Bicicleta[CAPACIDAD_MAXIMA];
    }

    /**
     * Devuelve una copia defensiva del arreglo de cupos para que
     * quien la consulte no pueda modificar el estado interno
     * directamente. Los elementos (bicicletas) sí son los mismos
     * objetos, para que la capa de operaciones pueda actualizarlos.
     */
    public Bicicleta[] obtenerBicicletas() {
        return Arrays.copyOf(bicicletas, bicicletas.length);
    }

    /**
     * Intenta ubicar una bicicleta en el primer cupo libre.
     *
     * @return true si se pudo ubicar, false si el parqueadero ya
     *         está lleno.
     */
    public boolean agregarBicicleta(Bicicleta bicicleta) {
        for (int i = 0; i < bicicletas.length; i++) {
            if (bicicletas[i] == null) {
                bicicletas[i] = bicicleta;
                return true;
            }
        }
        return false;
    }

    /**
     * Libera el cupo que ocupaba la bicicleta indicada (se usa
     * cuando el dueño ya pagó y se retira definitivamente).
     *
     * @return true si se encontró y liberó el cupo, false si esa
     *         bicicleta no estaba en el parqueadero.
     */
    public boolean retirarBicicleta(Bicicleta bicicleta) {
        for (int i = 0; i < bicicletas.length; i++) {
            if (bicicletas[i] == bicicleta) {
                bicicletas[i] = null;
                return true;
            }
        }
        return false;
    }

    /** Cuenta cuántos cupos están ocupados actualmente. */
    public int obtenerCantidadOcupados() {
        int cantidad = 0;
        for (Bicicleta b : bicicletas) {
            if (b != null) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /** Cuántos cupos libres quedan. */
    public int obtenerCantidadDisponibles() {
        return CAPACIDAD_MAXIMA - obtenerCantidadOcupados();
    }
}
