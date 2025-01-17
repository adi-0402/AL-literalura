package com.aluracursos.literalura.repository;
import java.util.Optional;
import com.aluracursos.literalura.model.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends JpaRepository<Libros, Long> {
    Libros findByTituloIgnoreCase(String tituloLibro);
}
