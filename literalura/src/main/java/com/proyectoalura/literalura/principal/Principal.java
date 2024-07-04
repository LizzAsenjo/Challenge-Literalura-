package com.proyectoalura.literalura.principal;

import com.proyectoalura.literalura.model.*;
import com.proyectoalura.literalura.repository.AutorRepository;
import com.proyectoalura.literalura.repository.LibroRepository;
import com.proyectoalura.literalura.service.ConsumoAPI;
import com.proyectoalura.literalura.service.ConvierteDatos;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<Libro> libros;
    private LibroRepository repositorio;
    private AutorRepository repositoryAutor;

    public Principal(LibroRepository repositorio, AutorRepository repositoryAutor) {
        this.repositorio = repositorio;
        this.repositoryAutor = repositoryAutor;
    }

    public void menuPrincipal(){
        int opcion = -1;

        while (opcion != 0){
            String menu = """
                    -------------------
                    Elija la opción a través de su número:
                    1 -  Buscar libro por título
                    2 -  Listar libros registrados
                    3 -  Listar autores registrados
                    4 -  Listar autores vivos en un determinado año
                    5 -  Listar libros por idioma
                    0 -  Salir
                    -------------------
                            """;
            System.out.println(menu);
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion){
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    mostrarLibros();
                    break;
                case 3:
                    mostrarAutores();
                    break;
                case 4:
                    autoresVivosAnhos();
                    break;
                case 5:
                    mostrarLibrosIdioma();
                    break;
                case 0:
                    System.out.println("----GRACIAS POR SU VISITA------");
                    break;
            }
        }
    }

    private void mostrarLibrosIdioma() {
        System.out.println("Ingrese el idioma para buscar los libros");
        System.out.println("es- español");
        System.out.println("en- ingles");
        System.out.println("fr- francés");
        System.out.println("pt- portugés");
        String idiomaLibro = sc.nextLine();

        List<Libro> libros = repositorio.findAll();

        libros.forEach(libro -> {
            if (libro.getIdioma().equals("["+idiomaLibro+"]")){
                System.out.println("----- LIBRO -----");
                System.out.println("Título: " + libro.getTitulo());

                libro.getAutores().forEach(autor -> {
                    System.out.println("Autor: " + autor.getNombre());
                });

                System.out.println("Idioma: " + libro.getIdioma());
                System.out.println("Número de descargas: " + libro.getTotal_descargas());
            }
        });
        System.out.println("----------------\n\n");
    }

    private void autoresVivosAnhos() {
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar");
        Integer anho = sc.nextInt();
        sc.nextLine();

        List<Autor> autores = repositoryAutor.findAll();

        List<Autor> autoresFiltrados = autores.stream()
                .filter(autor -> autor.getNacimiento() < anho &&
                        (autor.getFallecimiento() == null || autor.getFallecimiento() > anho))
                .collect(Collectors.toList());

        autoresFiltrados.forEach(autor -> {
            System.out.println("Autor:" + autor.getNombre());
            System.out.println("Fecha de nacimiento: " + autor.getNacimiento());
            System.out.println("Fecha de fallecimiento: " + autor.getFallecimiento());

            List<String> libro = repositoryAutor.libros(autor.getNombre());
            System.out.print("Libros: [");
            String librosConcatenados = libro.stream().collect(Collectors.joining(","));

            System.out.println(librosConcatenados + "]\n\n");
        });
    }

    public void mostrarAutores() {
        List<AutorDTO> autoresbd = repositoryAutor.autores();
        autoresbd.forEach(autor -> {
            System.out.println("Autor: " + autor.getNombre());
            System.out.println("Fecha de nacimiento: " + autor.getNacimiento());
            System.out.println("Fecha de fallecimiento: " + autor.getFallecimiento());

            List<String> libros = repositoryAutor.libros(autor.getNombre());
            System.out.print("Libros: [");
            String librosConcatenados = String.join(", ", libros);

            System.out.println(librosConcatenados + "]\n\n");
        });
    }

    private void mostrarLibros() {
        List<Libro> librosbd = repositorio.findAll();

        librosbd.forEach(libro -> {
            System.out.println("----- LIBRO -----");
            System.out.println("Título: " + libro.getTitulo());

            libro.getAutores().forEach(autor ->
                    System.out.println("Autor: " + autor.getNombre())
            );

            System.out.println("Lenguajes: " + libro.getIdioma());
            System.out.println("Número de descargas: " + libro.getTotal_descargas());

            System.out.println("----------------\n\n");
        });
    }

    private ApiResponse getDatosLibro(){
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        String nombreLibro = sc.nextLine();
        String json = consumoAPI.obtenerDatos(URL_BASE + "/?search=" + nombreLibro.replace(" ", "%20"));
        ApiResponse response = conversor.obtenerDatos(json, ApiResponse.class);
        return response;
    }

    private void buscarLibro() {
        ApiResponse response = getDatosLibro();
        List<DatosLibro> libros = response.getLibros();
        if (libros != null && !libros.isEmpty()) {
            System.out.println("----- LIBRO -----");
            libros.stream()
                    .findFirst()
                    .ifPresent(datosLibro -> {
                        System.out.println("Título: " + datosLibro.titulo());
                        datosLibro.autores().forEach(autor ->
                                System.out.println("Autor: " + autor.nombre())
                        );
                        System.out.println("Lenguajes: " + String.join(", ", datosLibro.lenguajes()));
                        System.out.println("Numero de descargas: " + String.join(", ", datosLibro.total_descargas().toString()));

                        //Creamos el objeto de Libro
                        Libro libro = new Libro();
                        libro.setTitulo(datosLibro.titulo());
                        libro.setIdioma(datosLibro.lenguajes().toString());
                        libro.setTotal_descargas(datosLibro.total_descargas());

                        //Crear y asignar la lista de autores
                        List<Autor> autores = datosLibro.autores().stream().map(datosAutor -> {
                            Autor autor = new Autor();
                            autor.setNombre(datosAutor.nombre());
                            autor.setNacimiento(datosAutor.nacimiento());
                            autor.setFallecimiento(datosAutor.fallecimiento());
                            autor.setLibro(libro);
                            return autor;
                        }).collect(Collectors.toList());

                        libro.setAutores(autores);

                        //guardar el libro en la base de datos
                        if (!buscarLibro(libro.getTitulo()))
                            repositorio.save(libro);
                        else
                            System.out.println("El libro ingresado ya existe en la Base de Datos");
                    });

            System.out.println("----------------");
        } else {
            System.out.println("No se encontraron libros.");
        }
    }

    private boolean buscarLibro(String titulo){
        Optional<Libro> libroExiste = repositorio.findByTitulo(titulo);
        if(libroExiste.isEmpty()) {
            return false;
        }else {
            return true;
        }
    }
}
