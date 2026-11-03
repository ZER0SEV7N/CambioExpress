package com.nttdata.cambioexpress.infrastructure.adapter.in.rest;

import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.TipoCambio;
import com.nttdata.cambioexpress.domain.port.in.CambioUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    Cotizacion creada = cambioUseCase.cotizar(request.moneda(), request.operacion(), request.monto());
    return ResponseEntity
            .created(URI.create("/api/cotizaciones/" + creada.id()))
            .body(creada);
  }

  @GetMapping("/cotizaciones/{id}")
  public Cotizacion obtenerCotizacion(@PathVariable String id) {
    return cambioUseCase.obtenerCotizacion(id);
  }
}