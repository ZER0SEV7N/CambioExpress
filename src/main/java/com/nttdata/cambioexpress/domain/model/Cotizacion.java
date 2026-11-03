package com.nttdata.cambioexpress.domain.model;

import java.math.BigDecimal;

/** Resultado de cotizar una operacion de compra o venta. */
public record Cotizacion(String id,
                         String moneda,
                         Operacion operacion,
                         BigDecimal monto,
                         BigDecimal tipoCambio,
                         BigDecimal montoSoles) {
}
