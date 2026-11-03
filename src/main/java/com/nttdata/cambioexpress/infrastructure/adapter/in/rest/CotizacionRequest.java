package com.nttdata.cambioexpress.infrastructure.adapter.in.rest;

import com.nttdata.cambioexpress.domain.model.Operacion;

import java.math.BigDecimal;

/** Cuerpo de POST /api/cotizaciones. */
public record CotizacionRequest(String moneda, Operacion operacion, BigDecimal monto) {
}
