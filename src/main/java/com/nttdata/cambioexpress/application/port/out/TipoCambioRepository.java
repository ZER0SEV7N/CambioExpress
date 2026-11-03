package com.nttdata.cambioexpress.application.port.out;

import com.nttdata.cambioexpress.domain.model.TipoCambio;

import java.util.List;
import java.util.Optional;

/** Fuente de los tipos de cambio del dia (puerto de salida). */
public interface TipoCambioRepository {

    List<TipoCambio> listar();

    Optional<TipoCambio> buscar(String moneda);
}
