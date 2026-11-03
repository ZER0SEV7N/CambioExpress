package com.nttdata.cambioexpress.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Pruebas de integracion de la API (las ejecuta Failsafe en la fase verify). */
@SpringBootTest
@AutoConfigureMockMvc
class CambioControllerIT {

    @Autowired
    private MockMvc mvc;

    private String cotizar(String moneda, String operacion, String monto) {
        return "{\"moneda\":\"" + moneda + "\",\"operacion\":\"" + operacion + "\",\"monto\":" + monto + "}";
    }

    @Test
    void listaLosTiposDeCambioDelDia() throws Exception {
        mvc.perform(get("/api/tipos-cambio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void obtieneElTipoDeCambioDelDolar() throws Exception {
        mvc.perform(get("/api/tipos-cambio/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compra").value(3.712))
                .andExpect(jsonPath("$.venta").value(3.786));
    }

    @Test
    void monedaInexistenteResponde404() throws Exception {
        mvc.perform(get("/api/tipos-cambio/JPY"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cotizaUnaCompraYResponde201() throws Exception {
        mvc.perform(post("/api/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cotizar("USD", "COMPRA", "100")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/cotizaciones/C-\\d{4}")))
                .andExpect(jsonPath("$.tipoCambio").value(3.786))
                .andExpect(jsonPath("$.montoSoles").value(378.60));
    }

    @Test
    void cotizaUnaVentaConElTipoDeCompra() throws Exception {
        mvc.perform(post("/api/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cotizar("USD", "VENTA", "100")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoCambio").value(3.712))
                .andExpect(jsonPath("$.montoSoles").value(371.20));
    }

    @Test
    void montoSobreElLimiteResponde400() throws Exception {
        mvc.perform(post("/api/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cotizar("USD", "COMPRA", "10000.01")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El monto supera el límite de 10000.00 por operación"));
    }

    @Test
    void consultaUnaCotizacionCreada() throws Exception {
        String location = mvc.perform(post("/api/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cotizar("EUR", "COMPRA", "50")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moneda").value("EUR"));
    }

    @Test
    void cotizacionInexistenteResponde404() throws Exception {
        mvc.perform(get("/api/cotizaciones/C-9999"))
                .andExpect(status().isNotFound());
    }
}
