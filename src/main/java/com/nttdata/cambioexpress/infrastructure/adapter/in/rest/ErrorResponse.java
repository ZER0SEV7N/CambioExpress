package com.nttdata.cambioexpress.infrastructure.adapter.in.rest;

/** Formato comun de error. */
public record ErrorResponse(int status, String mensaje) {
}
