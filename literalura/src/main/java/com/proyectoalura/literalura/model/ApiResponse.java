package com.proyectoalura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {
    private int count;
    private String next;
    private String previous;
    @JsonAlias("results")
    private List<DatosLibro> libros;

    // Getters and Setters
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<DatosLibro> getLibros() {
        return libros;
    }

    public void setLibros(List<DatosLibro> libros) {
        this.libros = libros;
    }
}
