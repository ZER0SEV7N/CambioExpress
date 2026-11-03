package com.nttdata.cambioexpress.domain.model;

import com.nttdata.cambioexpress.domain.exception.ReglaDeNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Pruebas de la regla principal: TipoCambio.convertir. */
class TipoCambioTest {

    /** USD: la casa compra a 3.712 y vende a 3.786. */
    private final TipoCambio usd = new TipoCambio("USD", new BigDecimal("3.712"), new BigDecimal("3.786"));

    @Test
    @DisplayName("1. Compra de dolares usa el tipo de venta: 100 -> 378.60")
    void compraDeDolaresUsaElTipoDeVenta() {
        // Arrange
        BigDecimal monto = new BigDecimal("100");

        // Act
        BigDecimal soles = usd.convertir(Operacion.COMPRA, monto);

        // Assert
        assertEquals(new BigDecimal("378.60"), soles);
    }

    @Test
    @DisplayName("2. Venta de dolares usa el tipo de compra: 100 -> 371.20")
    void ventaDeDolaresUsaElTipoDeCompra() {
        // Arrange
        BigDecimal monto = new BigDecimal("100");

        // Act
        BigDecimal soles = usd.convertir(Operacion.VENTA, monto);

        // Assert
        assertEquals(new BigDecimal("371.20"), soles);
    }

    @Test
    @DisplayName("3. Redondea a 2 decimales: COMPRA 10.55 -> 39.94")
    void redondeaADosDecimales() {
        // Arrange
        BigDecimal monto = new BigDecimal("10.55");

        // Act
        BigDecimal soles = usd.convertir(Operacion.COMPRA, monto);

        // Assert
        assertEquals(new BigDecimal("39.94"), soles);
        assertEquals(2, soles.scale());
    }

    @Test
    @DisplayName("4. Monto sobre el limite de 10000.00 lanza ReglaDeNegocioException")
    void montoSobreElLimiteLanzaExcepcion() {
        // Arrange
        BigDecimal monto = new BigDecimal("10000.01");

        // Act
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> usd.convertir(Operacion.COMPRA, monto));

        // Assert
        assertEquals("El monto supera el límite de 10000.00 por operación", ex.getMessage());
    }

    @Test
    @DisplayName("5. Monto en cero lanza ReglaDeNegocioException")
    void montoEnCeroLanzaExcepcion() {
        // Arrange
        BigDecimal monto = BigDecimal.ZERO;

        // Act
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class,
                () -> usd.convertir(Operacion.VENTA, monto));

        // Assert
        assertEquals("El monto debe ser mayor que cero", ex.getMessage());
    }

    @Test
    @DisplayName("6. Datos invalidos al construir TipoCambio lanzan IllegalArgumentException")
    void datosInvalidosAlConstruirLanzanExcepcion() {
        // Arrange
        BigDecimal compra = new BigDecimal("3.712");
        BigDecimal venta = new BigDecimal("3.786");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new TipoCambio("usd", compra, venta));
        assertThrows(IllegalArgumentException.class, () -> new TipoCambio("USD", BigDecimal.ZERO, venta));
        assertThrows(IllegalArgumentException.class, () -> new TipoCambio("USD", compra, compra));
    }

    @Test
    @DisplayName("7. El limite exacto de 10000.00 si se permite")
    void montoEnElLimiteExactoSePermite() {
        // Arrange
        BigDecimal monto = new BigDecimal("10000.00");

        // Act
        BigDecimal soles = usd.convertir(Operacion.COMPRA, monto);

        // Assert
        assertEquals(new BigDecimal("37860.00"), soles);
    }
}
