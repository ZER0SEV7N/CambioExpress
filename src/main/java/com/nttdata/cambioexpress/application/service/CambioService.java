package com.nttdata.cambioexpress.application.service;

import com.nttdata.cambioexpress.application.port.in.CambioUseCase;
import com.nttdata.cambioexpress.application.port.out.CotizacionRepository;
import com.nttdata.cambioexpress.application.port.out.TipoCambioRepository;
import com.nttdata.cambioexpress.domain.exception.RecursoNoEncontradoException;
import com.nttdata.cambioexpress.domain.model.Cotizacion;
import com.nttdata.cambioexpress.domain.model.Operacion;
import com.nttdata.cambioexpress.domain.model.TipoCambio;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

/** Implementa los casos de uso usando los puertos de salida. */
@Service
public class CambioService implements CambioUseCase {

    private final TipoCambioRepository tipoCambioRepository;
    private final CotizacionRepository cotizacionRepository;

    public CambioService(TipoCambioRepository tipoCambioRepository,
                         CotizacionRepository cotizacionRepository) {
        this.tipoCambioRepository = tipoCambioRepository;
        this.cotizacionRepository = cotizacionRepository;
    }

    @Override
    public List<TipoCambio> listarTiposCambio() {
        return tipoCambioRepository.listar();
    }

    @Override
    public TipoCambio obtenerTipoCambio(String moneda) {
        String codigo = moneda == null ? "" : moneda.toUpperCase(Locale.ROOT);
        return tipoCambioRepository.buscar(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el tipo de cambio para " + moneda));
    }

    @Override
    public Cotizacion cotizar(String moneda, Operacion operacion, BigDecimal monto) {
        if (operacion == null) {
            throw new IllegalArgumentException("La operacion es obligatoria: COMPRA o VENTA");
        }
        TipoCambio tipoCambio = obtenerTipoCambio(moneda);
        BigDecimal montoSoles = tipoCambio.convertir(operacion, monto);
        Cotizacion cotizacion = new Cotizacion(
                cotizacionRepository.siguienteId(),
                tipoCambio.moneda(),
                operacion,
                monto.setScale(2, RoundingMode.HALF_UP),
                tipoCambio.tipoAplicado(operacion),
                montoSoles);
        return cotizacionRepository.guardar(cotizacion);
    }

    @Override
    public Cotizacion obtenerCotizacion(String id) {
        return cotizacionRepository.buscar(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cotizacion " + id));
    }
}
