package com.aluracursos.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibros(
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<DatosAutor> autor,
        @JsonAlias("languages") List<String> idiomas,
        @JsonAlias("download_count") Double descargas
) {
    @Override
    public String toString() {
        return """
            ====================================
                      DATOS LIBRO
            ====================================
            Título: %s
            Autor(es): %s
            Idiomas: %s
            Número de descargas: %.2f
            ====================================
            """.formatted(
                titulo(),
                autor().stream().map(DatosAutor::nombreAutor).reduce((a, b) -> a + ", " + b).orElse("No disponible"),
                String.join(", ", idiomas()),
                descargas()
        );
    }

}
