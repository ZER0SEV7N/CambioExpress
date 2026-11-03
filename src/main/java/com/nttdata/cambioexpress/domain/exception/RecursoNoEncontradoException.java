package com.nttdata.cambioexpress.domain.exception;

/** Se lanza cuando no existe la moneda o la cotizacion solicitada. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
