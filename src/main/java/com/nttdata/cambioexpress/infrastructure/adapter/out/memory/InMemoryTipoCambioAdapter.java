package com.nttdata.cambioexpress.infrastructure.adapter.out.memory;

import com.nttdata.cambioexpress.domain.model.TipoCambio;
import com.nttdata.cambioexpress.domain.port.out.TipoCambioRepositoryPort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTipoCambioAdapter implements TipoCambioRepositoryPort {

  private final Map<String, TipoCambio> datos = Map.of(
          "USD", new TipoCambio("USD", new BigDecimal("3.712"), new BigDecimal("3.786")),
          "EUR", new TipoCambio("EUR", new BigDecimal("4.010"), new BigDecimal("4.095"))
  );

  @Override
  public List<TipoCambio> findAll() {
    return List.of(datos.get("USD"), datos.get("EUR"));
  }

  @Override
  public Optional<TipoCambio> findByMoneda(String moneda) {
    return Optional.ofNullable(datos.get(moneda));
  }
}