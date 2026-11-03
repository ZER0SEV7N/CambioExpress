package com.nttdata.cambioexpress.infrastructure.adapter.out.memory;

import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.port.out.CotizacionRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryCotizacionAdapter implements CotizacionRepositoryPort {

  private final Map<String, Cotizacion> almacen = new ConcurrentHashMap<>();
  private final AtomicInteger secuencia = new AtomicInteger(0);

  @Override
  public Cotizacion save(Cotizacion cotizacion) {
    almacen.put(cotizacion.id(), cotizacion);
    return cotizacion;
  }

  @Override
  public Optional<Cotizacion> findById(String id) {
    return Optional.ofNullable(almacen.get(id));
  }

  @Override
  public String nextId() {
    return String.format("C-%04d", secuencia.incrementAndGet());
  }
}