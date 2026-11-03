package com.nttdata.cambioexpress.application.service;

import com.nttdata.cambioexpress.domain.exception.ResourceNotFoundException;
import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.Operacion;
import com.nttdata.cambioexpress.domain.model.TipoCambio;
import com.nttdata.cambioexpress.domain.port.in.CambioUseCase;
import com.nttdata.cambioexpress.domain.port.out.CotizacionRepositoryPort;
import com.nttdata.cambioexpress.domain.port.out.TipoCambioRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CambioService implements CambioUseCase {

  private final TipoCambioRepositoryPort tipoCambioRepository;
  private final CotizacionRepositoryPort cotizacionRepository;

  public CambioService(TipoCambioRepositoryPort tipoCambioRepository,
                       CotizacionRepositoryPort cotizacionRepository) {
    this.tipoCambioRepository = tipoCambioRepository;
    this.cotizacionRepository = cotizacionRepository;
  }

  @Override
  public List<TipoCambio> listarTiposCambio() {
    return tipoCambioRepository.findAll();
  }

  @Override
  public TipoCambio obtenerTipoCambio(String moneda) {
    String codigo = moneda == null ? "" : moneda.toUpperCase();
    return tipoCambioRepository.findByMoneda(codigo)
            .orElseThrow(() -> new ResourceNotFoundException("Moneda no soportada: " + moneda));
  }

  @Override
  public Cotizacion cotizar(String moneda, Operacion operacion, BigDecimal monto) {
    TipoCambio tc = obtenerTipoCambio(moneda);
    BigDecimal montoSoles = tc.convertir(operacion, monto);
    BigDecimal tasa = tc.tasaPara(operacion);
    String id = cotizacionRepository.nextId();

    Cotizacion cotizacion = new Cotizacion(
            id,
            tc.getMoneda(),
            operacion,
            monto.setScale(2, RoundingMode.HALF_UP),
            tasa,
            montoSoles
    );
    return cotizacionRepository.save(cotizacion);
  }

  @Override
  public Cotizacion obtenerCotizacion(String id) {
    return cotizacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada: " + id));
  }
}