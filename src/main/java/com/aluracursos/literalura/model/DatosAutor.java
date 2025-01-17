package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosAutor(
        @JsonAlias("name") String nombreAutor,
        @JsonAlias("birth_year") Integer anoNacimiento
) {
    public Object toEntity() {
        return new Autor(this);
    }
    @Override
    public String toString() {
        return String.format("Autor: %s (Año de nacimiento: %d)", nombreAutor(), anoNacimiento() != null ? anoNacimiento() : "No disponible");
    }

}
