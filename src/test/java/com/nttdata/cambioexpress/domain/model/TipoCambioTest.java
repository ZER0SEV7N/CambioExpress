package com.nttdata.cambioexpress.domain.model;

import com.nttdata.cambioexpress.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TipoCambioTest {

  private TipoCambio tipoCambioUsd;

  @BeforeEach
  void setUp() {
    // USD: compra 3.712, venta 3.786
    tipoCambioUsd = new TipoCambio("USD", new BigDecimal("3.712"), new BigDecimal("3.786"));
  }

  @Test
  @DisplayName("1. Compra de dólares usa el tipo de venta")
  void compraDolaresUsaTipoVenta() {
    BigDecimal resultado = tipoCambioUsd.convertir(Operacion.COMPRA, new BigDecimal("100"));
    assertEquals(new BigDecimal("378.60"), resultado);
  }

  @Test
  @DisplayName("2. Venta de dólares usa el tipo de compra")
  void ventaDolaresUsaTipoCompra() {
    BigDecimal resultado = tipoCambioUsd.convertir(Operacion.VENTA, new BigDecimal("100"));
    assertEquals(new BigDecimal("371.20"), resultado);
  }

  @Test
  @DisplayName("3. Redondea a 2 decimales")
  void redondeaADosDecimales() {
    BigDecimal resultado = tipoCambioUsd.convertir(Operacion.COMPRA, new BigDecimal("10.55"));
    assertEquals(new BigDecimal("39.94"), resultado);
    assertEquals(2, resultado.scale());
  }

  @Test
  @DisplayName("4. Monto sobre el límite lanza BusinessRuleException")
  void montoSobreElLimiteLanzaExcepcion() {
    BigDecimal montoExcedido = new BigDecimal("10000.01");
    BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> tipoCambioUsd.convertir(Operacion.COMPRA, montoExcedido)
    );
    assertEquals("El monto supera el límite de 10000.00 por operación", ex.getMessage());
  }

  @Test
  @DisplayName("5. Monto en cero lanza BusinessRuleException")
  void montoEnCeroLanzaExcepcion() {
    BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> tipoCambioUsd.convertir(Operacion.VENTA, BigDecimal.ZERO)
    );
    assertEquals("El monto debe ser mayor que cero", ex.getMessage());
  }

  @Test
  @DisplayName("6. Datos inválidos al construir TipoCambio lanza IllegalArgumentException")
  void datosInvalidosAlConstruirLanzaExcepcion() {
    BigDecimal compraValida = new BigDecimal("3.712");
    BigDecimal ventaValida = new BigDecimal("3.786");

    // Moneda en minúsculas
    assertThrows(IllegalArgumentException.class, () -> new TipoCambio("usd", compraValida, ventaValida));

    BigDecimal compraCero = BigDecimal.ZERO;

    // Compra en 0
    assertThrows(IllegalArgumentException.class, () -> new TipoCambio("USD", compraCero, ventaValida));

    BigDecimal compraMayor = new BigDecimal("3.786");
    BigDecimal ventaMenor = new BigDecimal("3.712");

    // Venta igual o menor que compra
    assertThrows(IllegalArgumentException.class, () -> new TipoCambio("USD", compraMayor, ventaMenor));
  }
}