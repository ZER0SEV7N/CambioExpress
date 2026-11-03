package com.nttdata.cambioexpress.infrastructure.adapter.out.memoria;

import com.nttdata.cambioexpress.application.port.out.CotizacionRepository;
import com.nttdata.cambioexpress.domain.model.Cotizacion;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Cotizaciones guardadas en memoria; el id tiene el formato C-0001. */
@Component
public class CotizacionMemoriaAdapter implements CotizacionRepository {

    private final Map<String, Cotizacion> cotizaciones = new ConcurrentHashMap<>();
    private final AtomicInteger secuencia = new AtomicInteger();

    @Override
    public String siguienteId() {
        return String.format("C-%04d", secuencia.incrementAndGet());
    }

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        cotizaciones.put(cotizacion.id(), cotizacion);
        return cotizacion;
    }

    @Override
    public Optional<Cotizacion> buscar(String id) {
        return Optional.ofNullable(cotizaciones.get(id));
    }
}
