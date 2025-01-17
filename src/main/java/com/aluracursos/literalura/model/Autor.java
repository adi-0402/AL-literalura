package com.aluracursos.literalura.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombreAutor;

    private Integer anoNacimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Libros> libros;

    public Autor(){}

    public Autor(DatosAutor datosAutor){
        this.nombreAutor = datosAutor.nombreAutor();
        this.anoNacimiento = datosAutor.anoNacimiento();
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public Integer getAnoNacimiento() {
        return anoNacimiento;
    }

    public void setAnoNacimiento(Integer anoNacimiento) {
        this.anoNacimiento = anoNacimiento;
    }

    @Override
    public String toString(){
        return """
                ===================================
                           DATOS AUTOR
                ===================================        
                """ + "\nNombre: " + nombreAutor + "\nAño de nacimiento: " + anoNacimiento +
                "\n===================================";
    }
}
