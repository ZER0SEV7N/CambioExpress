package com.nttdata.cambioexpress.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CambioControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void listarTiposCambioRetorna200() throws Exception {
    mockMvc.perform(get("/api/tipos-cambio"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void obtenerUsdRetorna200() throws Exception {
    mockMvc.perform(get("/api/tipos-cambio/USD"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.moneda").value("USD"))
            .andExpect(jsonPath("$.compra").value(3.712))
            .andExpect(jsonPath("$.venta").value(3.786));
  }

  @Test
  void obtenerMonedaInexistenteRetorna404() throws Exception {
    mockMvc.perform(get("/api/tipos-cambio/JPY"))
            .andExpect(status().isNotFound());
  }

  @Test
  void cotizarCompraUsdRetorna201() throws Exception {
    String body = """
                { "moneda": "USD", "operacion": "COMPRA", "monto": 100 }
                """;
    mockMvc.perform(post("/api/cotizaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.montoSoles").value(378.60));
  }

  @Test
  void cotizarVentaUsdRetorna201() throws Exception {
    String body = """
                { "moneda": "USD", "operacion": "VENTA", "monto": 100 }
                """;
    mockMvc.perform(post("/api/cotizaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.montoSoles").value(371.20));
  }

  @Test
  void cotizarMontoInvalidoRetorna400() throws Exception {
    String body = """
                { "moneda": "USD", "operacion": "COMPRA", "monto": 20000 }
                """;
    mockMvc.perform(post("/api/cotizaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isBadRequest());
  }

  @Test
  void consultarCotizacionExistenteRetorna200() throws Exception {
    String body = """
                { "moneda": "EUR", "operacion": "COMPRA", "monto": 50 }
                """;
    String location = mockMvc.perform(post("/api/cotizaciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader("Location");

    mockMvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.moneda").value("EUR"));
  }

  @Test
  void consultarCotizacionInexistenteRetorna404() throws Exception {
    mockMvc.perform(get("/api/cotizaciones/C-9999"))
            .andExpect(status().isNotFound());
  }
}