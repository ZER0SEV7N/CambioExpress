package com.nttdata.cambioexpress.domain.model;

import com.nttdata.cambioexpress.domain.exception.ReglaDeNegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Tipo de cambio de una moneda frente al sol.
 * compra: precio al que la casa compra la moneda; venta: precio al que la vende.
 */
public record TipoCambio(String moneda, BigDecimal compra, BigDecimal venta) {

    /** Monto maximo permitido por operacion. */
    public static final BigDecimal LIMITE_POR_OPERACION = new BigDecimal("10000.00");

    private static final int ESCALA_SOLES = 2;

    public TipoCambio {
        if (moneda == null || !moneda.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("La moneda debe tener 3 letras en mayusculas");
        }
        if (compra == null || compra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El tipo de compra debe ser mayor que cero");
        }
        if (venta == null || venta.compareTo(compra) <= 0) {
            throw new IllegalArgumentException("El tipo de venta debe ser mayor que el de compra");
        }
    }

    /**
     * Convierte un monto en moneda extranjera a soles.
     * COMPRA aplica el tipo de venta de la casa; VENTA aplica el tipo de compra.
     */
    public BigDecimal convertir(Operacion operacion, BigDecimal monto) {
        Objects.requireNonNull(operacion, "La operacion es obligatoria");
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaDeNegocioException("El monto debe ser mayor que cero");
        }
        if (monto.compareTo(LIMITE_POR_OPERACION) > 0) {
            throw new ReglaDeNegocioException(
                    "El monto supera el límite de 10000.00 por operación");
        }
        return monto.multiply(tipoAplicado(operacion)).setScale(ESCALA_SOLES, RoundingMode.HALF_UP);
    }

    /** Tipo que se aplica segun la operacion del cliente. */
    public BigDecimal tipoAplicado(Operacion operacion) {
        return operacion == Operacion.COMPRA ? venta : compra;
    }
}
