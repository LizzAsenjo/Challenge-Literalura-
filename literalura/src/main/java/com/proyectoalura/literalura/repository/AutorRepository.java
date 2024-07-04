package com.proyectoalura.literalura.repository;

import com.proyectoalura.literalura.model.Autor;
import com.proyectoalura.literalura.model.AutorDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT libro.titulo FROM Autor autor " +
            "INNER JOIN Libro libro ON autor.libro.id = libro.id " +
            "WHERE autor.nombre = :nombre")
    List<String> libros(String nombre);

    @Query("SELECT DISTINCT NEW com.proyectoalura.literalura.model.AutorDTO(autor.nombre, autor.nacimiento, autor.fallecimiento) " +
            "FROM Autor autor " +
            "JOIN autor.libro libro " +
            "ORDER BY autor.nombre")
    List<AutorDTO> autores();


}
