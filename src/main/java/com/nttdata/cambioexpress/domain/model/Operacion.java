package com.nttdata.cambioexpress.domain.model;

/** Tipo de operacion que solicita el cliente. */
public enum Operacion {
    /** El cliente compra la moneda extranjera y paga en soles. */
    COMPRA,
    /** El cliente vende la moneda extranjera y recibe soles. */
    VENTA
}
