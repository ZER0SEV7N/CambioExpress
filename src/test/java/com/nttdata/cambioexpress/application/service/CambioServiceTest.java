package com.nttdata.cambioexpress.application.service;

import com.nttdata.cambioexpress.application.port.out.CotizacionRepository;
import com.nttdata.cambioexpress.application.port.out.TipoCambioRepository;
import com.nttdata.cambioexpress.domain.exception.RecursoNoEncontradoException;
import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.Operacion;
import com.nttdata.cambioexpress.domain.model.TipoCambio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del servicio con los puertos simulados. */
@ExtendWith(MockitoExtension.class)
class CambioServiceTest {

    private static final TipoCambio USD =
            new TipoCambio("USD", new BigDecimal("3.712"), new BigDecimal("3.786"));

    @Mock
    private TipoCambioRepository tipoCambioRepository;

    @Mock
    private CotizacionRepository cotizacionRepository;

    @InjectMocks
    private CambioService service;

    @Test
    void listaLosTiposDeCambioDelDia() {
        when(tipoCambioRepository.listar()).thenReturn(List.of(USD));

        List<TipoCambio> tipos = service.listarTiposCambio();

        assertEquals(1, tipos.size());
        assertEquals("USD", tipos.get(0).moneda());
    }

    @Test
    void cotizaYGuardaLaOperacion() {
        when(tipoCambioRepository.buscar("USD")).thenReturn(Optional.of(USD));
        when(cotizacionRepository.siguienteId()).thenReturn("C-0001");
        when(cotizacionRepository.guardar(any(Cotizacion.class))).thenAnswer(inv -> inv.getArgument(0));

        Cotizacion cotizacion = service.cotizar("usd", Operacion.COMPRA, new BigDecimal("100"));

        assertEquals("C-0001", cotizacion.id());
        assertEquals(new BigDecimal("3.786"), cotizacion.tipoCambio());
        assertEquals(new BigDecimal("378.60"), cotizacion.montoSoles());
        verify(cotizacionRepository).guardar(any(Cotizacion.class));
    }

    @Test
    void monedaInexistenteLanzaRecursoNoEncontrado() {
        when(tipoCambioRepository.buscar("JPY")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.cotizar("JPY", Operacion.COMPRA, new BigDecimal("100")));
        verify(cotizacionRepository, never()).guardar(any(Cotizacion.class));
    }
}
