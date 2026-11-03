package com.nttdata.cambioexpress.infrastructure.adapter.in.rest;

import com.nttdata.cambioexpress.application.port.in.CambioUseCase;
import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.TipoCambio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** API REST de CambioExpress (adaptador de entrada). */
@RestController
@RequestMapping("/api")
public class CambioController {

    private final CambioUseCase cambioUseCase;

    public CambioController(CambioUseCase cambioUseCase) {
        this.cambioUseCase = cambioUseCase;
    }

    @GetMapping("/tipos-cambio")
    public List<TipoCambio> listarTiposCambio() {
        return cambioUseCase.listarTiposCambio();
    }

    @GetMapping("/tipos-cambio/{moneda}")
    public TipoCambio obtenerTipoCambio(@PathVariable String moneda) {
        return cambioUseCase.obtenerTipoCambio(moneda);
    }

    @PostMapping("/cotizaciones")
    public ResponseEntity<Cotizacion> cotizar(@RequestBody CotizacionRequest request) {
        Cotizacion cotizacion = cambioUseCase.cotizar(request.moneda(), request.operacion(), request.monto());
        return ResponseEntity.created(URI.create("/api/cotizaciones/" + cotizacion.id())).body(cotizacion);
    }

    @GetMapping("/cotizaciones/{id}")
    public Cotizacion obtenerCotizacion(@PathVariable String id) {
        return cambioUseCase.obtenerCotizacion(id);
    }
}
