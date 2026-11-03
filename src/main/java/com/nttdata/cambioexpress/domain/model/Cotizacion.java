package com.nttdata.cambioexpress.domain.model;

import java.math.BigDecimal;

public record Cotizacion(
        String id,
        String moneda,
        Operacion operacion,
        BigDecimal monto,
        BigDecimal tipoCambio,
        BigDecimal montoSoles
) {}