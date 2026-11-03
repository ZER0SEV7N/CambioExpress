package com.nttdata.cambioexpress.application.port.out;

import com.nttdata.cambioexpress.domain.model.Cotizacion;

import java.util.Optional;

/** Almacen de cotizaciones (puerto de salida). */
public interface CotizacionRepository {

    String siguienteId();

    Cotizacion guardar(Cotizacion cotizacion);

    Optional<Cotizacion> buscar(String id);
}
