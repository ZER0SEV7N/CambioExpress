package com.nttdata.cambioexpress.application.service;

import com.nttdata.cambioexpress.domain.exception.ResourceNotFoundException;
import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.Operacion;
import com.nttdata.cambioexpress.domain.port.in.CambioUseCase;
import com.nttdata.cambioexpress.infrastructure.adapter.out.memory.InMemoryCotizacionAdapter;
import com.nttdata.cambioexpress.infrastructure.adapter.out.memory.InMemoryTipoCambioAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CambioServiceTest {

  private CambioUseCase service;

  @BeforeEach
  void setUp() {
    service = new CambioService(new InMemoryTipoCambioAdapter(), new InMemoryCotizacionAdapter());
  }

  @Test
  void debeListarTiposDeCambioDisponibles() {
    assertEquals(2, service.listarTiposCambio().size());
  }

  @Test
  void debeCotizarOperacionYGuardarConId() {
    Cotizacion c = service.cotizar("USD", Operacion.COMPRA, new BigDecimal("100"));
    assertEquals("C-0001", c.id());
    assertEquals(new BigDecimal("378.60"), c.montoSoles());
    assertNotNull(service.obtenerCotizacion("C-0001"));
  }

  @Test
  void debeLanzarExcepcionSiMonedaNoExiste() {
    assertThrows(ResourceNotFoundException.class, () -> service.obtenerTipoCambio("GBP"));
  }
}