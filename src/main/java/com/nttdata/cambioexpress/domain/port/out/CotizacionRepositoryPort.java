package com.nttdata.cambioexpress.domain.port.out;

import com.nttdata.cambioexpress.domain.model.Cotizacion;
import java.util.Optional;

public interface CotizacionRepositoryPort {
  Cotizacion save(Cotizacion cotizacion);
  Optional<Cotizacion> findById(String id);
  String nextId();
}