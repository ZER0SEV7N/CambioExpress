package com.nttdata.cambioexpress.domain.port.in;

import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.Operacion;
import com.nttdata.cambioexpress.domain.model.TipoCambio;

import java.math.BigDecimal;
import java.util.List;

public interface CambioUseCase {
  List<TipoCambio> listarTiposCambio();
  TipoCambio obtenerTipoCambio(String moneda);
  Cotizacion cotizar(String moneda, Operacion operacion, BigDecimal monto);
  Cotizacion obtenerCotizacion(String id);
}