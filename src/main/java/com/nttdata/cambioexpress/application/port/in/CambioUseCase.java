package com.nttdata.cambioexpress.application.port.in;

import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.Operacion;
import com.nttdata.cambioexpress.domain.model.TipoCambio;

import java.math.BigDecimal;
import java.util.List;

/** Casos de uso de CambioExpress (puerto de entrada). */
public interface CambioUseCase {

    List<TipoCambio> listarTiposCambio();

    TipoCambio obtenerTipoCambio(String moneda);

    Cotizacion cotizar(String moneda, Operacion operacion, BigDecimal monto);

    Cotizacion obtenerCotizacion(String id);
}
