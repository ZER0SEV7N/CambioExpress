package com.nttdata.cambioexpress.infrastructure.adapter.out.memoria;

import com.nttdata.cambioexpress.application.port.out.TipoCambioRepository;
import com.nttdata.cambioexpress.domain.model.TipoCambio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Tipos de cambio del dia, en memoria. */
@Component
public class TipoCambioMemoriaAdapter implements TipoCambioRepository {

    private final List<TipoCambio> tiposDelDia = List.of(
            new TipoCambio("USD", new BigDecimal("3.712"), new BigDecimal("3.786")),
            new TipoCambio("EUR", new BigDecimal("4.021"), new BigDecimal("4.135")));

    @Override
    public List<TipoCambio> listar() {
        return tiposDelDia;
    }

    @Override
    public Optional<TipoCambio> buscar(String moneda) {
        return tiposDelDia.stream().filter(t -> t.moneda().equals(moneda)).findFirst();
    }
}
