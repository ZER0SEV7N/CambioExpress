package com.nttdata.cambioexpress.domain.exception;

/** Se lanza cuando una operacion incumple una regla de negocio. */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
