package com.nttdata.cambioexpress.domain.model;

import com.nttdata.cambioexpress.domain.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class TipoCambio {

  private static final BigDecimal LIMITE_OPERACION = new BigDecimal("10000.00");

  private final String moneda;
  private final BigDecimal compra;
  private final BigDecimal venta;

  public TipoCambio(String moneda, BigDecimal compra, BigDecimal venta) {
    if (moneda == null || !moneda.matches("^[A-Z]{3}$")) {
      throw new IllegalArgumentException("La moneda debe tener 3 letras mayúsculas (ej. USD, EUR)");
    }
    if (compra == null || compra.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("El tipo de compra debe ser mayor que cero");
    }
    if (venta == null || venta.compareTo(compra) <= 0) {
      throw new IllegalArgumentException("El tipo de venta debe ser mayor que el de compra");
    }
    this.moneda = moneda;
    this.compra = compra;
    this.venta = venta;
  }

  public BigDecimal convertir(Operacion operacion, BigDecimal monto) {
    Objects.requireNonNull(operacion, "La operación es obligatoria");
    Objects.requireNonNull(monto, "El monto es obligatorio");

    if (monto.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BusinessRuleException("El monto debe ser mayor que cero");
    }
    if (monto.compareTo(LIMITE_OPERACION) > 0) {
      throw new BusinessRuleException("El monto supera el límite de 10000.00 por operación");
    }

    BigDecimal tasaAplicada = (operacion == Operacion.COMPRA) ? this.venta : this.compra;
    return monto.multiply(tasaAplicada).setScale(2, RoundingMode.HALF_UP);
  }

  public BigDecimal tasaPara(Operacion operacion) {
    return (operacion == Operacion.COMPRA) ? this.venta : this.compra;
  }

  public String getMoneda() {
    return moneda;
  }

  public BigDecimal getCompra() {
    return compra;
  }

  public BigDecimal getVenta() {
    return venta;
  }
}