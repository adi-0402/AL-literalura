package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    @Column(name = "idiomas")
    private String idiomas;

    private Double descargas;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)  // Clave foránea
    private Autor autor;

    public Libros(){}

    public Libros(DatosLibros datosLibros, Autor autor){
        this.titulo = datosLibros.titulo();
        this.idiomas = String.join(",", datosLibros.idiomas());
        this.descargas = datosLibros.descargas();
        this.autor = autor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Double getDescargas() {
        return descargas;
    }

    public void setDescargas(Double descargas) {
        this.descargas = descargas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = String.join(",", idiomas); // Convierte la lista en cadena separada por comas
    }

    public List<String> getIdiomasAsList() {
        return List.of(this.idiomas.split(",")); // Convierte la cadena separada por comas en una lista
    }

    @Override
    public String toString() {
        return """
            ====================================
                      DATOS LIBRO REGISTRADO
            ====================================
            Título: %s
            Autor: %s
            Idiomas: %s
            Número de descargas: %.2f
            ====================================
            """.formatted(
                titulo,
                autor != null ? autor.getNombreAutor() : "No disponible",
                idiomas != null ? idiomas : "No disponible",
                descargas != null ? descargas : 0.0
        );
    }

}
