package com.nttdata.cambioexpress.domain.port.out;

import com.nttdata.cambioexpress.domain.model.TipoCambio;
import java.util.List;
import java.util.Optional;

public interface TipoCambioRepositoryPort {
  List<TipoCambio> findAll();
  Optional<TipoCambio> findByMoneda(String moneda);
}