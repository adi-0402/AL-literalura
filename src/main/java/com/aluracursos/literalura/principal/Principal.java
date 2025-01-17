package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.model.*;
import com.aluracursos.literalura.repository.AutorRepository;
import com.aluracursos.literalura.repository.LibroRepository;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private final String URL = "https://gutendex.com/books/?search=";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            var menu = """
                    Elija una opcion:
                    1 - Buscar libro por titulo
                    2 - Mostrar libros registrados
                    3 - Mostrar autores registrados
                    4 - Mostrar autores vivos en un determinado año
                    5 - Mostrar libros por idioma

                    0 - Salir
                    """;

            System.out.println(menu);
            try {
                System.out.print("Ingrese su opcion: ");
                opcion = teclado.nextInt();
                teclado.nextLine(); // Limpia el buffer

                switch (opcion) {
                    case 1 -> buscarLibroPorTitulo();
                    case 2 -> mostrarLibros();
                    case 3 -> mostrarAutores();
                    case 4 -> mostrarAutoresVivos();
                    case 5 -> mostrarLibrosPorIdioma();
                    case 0 -> System.out.println("Cerrando aplicacion...");
                    default -> System.out.println("Opción inválida. Intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número válido.");
                teclado.nextLine(); // Limpia el buffer
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.print("Escriba el nombre del libro a buscar: ");
        var tituloLibro = teclado.nextLine();

        // Buscar si el libro ya está en la base de datos
        Libros libroExistente = libroRepository.findByTituloIgnoreCase(tituloLibro);
        if (libroExistente != null) {
            System.out.println("El libro ya está registrado en la base de datos:");
            System.out.println(libroExistente);
            return;
        }

        // Consumir la API para buscar el libro
        var json = consumoAPI.obtenerDatos(URL + tituloLibro.replace(" ", "%20"));
        if (json == null || json.isEmpty()) {
            System.out.println("Error al conectar con la API. Por favor, inténtelo más tarde.");
            return;
        }

        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.libro().stream()
                .filter(l -> l.titulo().equalsIgnoreCase(tituloLibro))
                .findFirst();

        if (libroBuscado.isPresent()) {
            DatosLibros datosLibros = libroBuscado.get();
            System.out.println("Libro encontrado en la API:");
            System.out.println(datosLibros); // Llama automáticamente al toString de DatosLibros

            Autor autor = obtenerAutor(datosLibros.autor());
            Libros libro = new Libros(datosLibros, autor);

            try {
                libroRepository.save(libro);
                System.out.println("El libro ha sido registrado en la base de datos.");
            } catch (DataIntegrityViolationException e) {
                System.out.println("Error: El libro ya existe en la base de datos.");
            }
        } else {
            System.out.println("Libro no encontrado en la API.");
        }

    }

    private void mostrarLibros() {
        List<Libros> librosBase = libroRepository.findAll();
        if (librosBase.isEmpty()) {
            System.out.println("No hay libros registrados en la base de datos.");
        } else {
            librosBase.forEach(System.out::println);
        }
    }

    private void mostrarAutores() {
        List<Autor> autoresBase = autorRepository.findAll();
        if (autoresBase.isEmpty()) {
            System.out.println("No hay autores registrados en la base de datos.");
        } else {
            autoresBase.forEach(System.out::println);
        }
    }

    private void mostrarAutoresVivos() {
        System.out.print("Ingrese el año para buscar autores vivos: ");
        int anio;
        try {
            // Asegurarse de que el año ingresado sea un número entero
            anio = Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un año válido.");
            return; // Salir del método si el año no es válido
        }

        // Construir la URL con parámetros de consulta
        String url = String.format("https://gutendex.com/books/?author_year_start=%d&author_year_end=%d", anio, anio);

        // Consumir la API
        var json = consumoAPI.obtenerDatos(url);
        if (json == null || json.isEmpty()) {
            System.out.println("Error al conectar con la API o no se encontraron autores vivos.");
            return;
        }

        // Convertir los datos obtenidos
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        List<DatosLibros> libros = datosBusqueda.libro();

        if (libros.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + anio + ".");
        } else {
            System.out.println("Autores vivos en el año " + anio + ":");
            libros.stream()
                    .flatMap(libro -> libro.autor().stream()) // Obtener todos los autores
                    .distinct() // Evitar autores repetidos
                    .forEach(autor -> {
                        System.out.printf("Autor: %s (Año de nacimiento: %d)%n", autor.nombreAutor(), autor.anoNacimiento());
                    });
        }
    }



    private void mostrarLibrosPorIdioma() {
        System.out.print("Ingrese el idioma (EN, ES, FR, PT): ");
        String idioma = teclado.nextLine().trim().toUpperCase(); // Elimina espacios y asegura mayúsculas

        List<Libros> librosPorIdioma = libroRepository.findAll().stream()
                .filter(libro -> libro.getIdiomasAsList().stream()
                        .anyMatch(idiomaLibro -> idiomaLibro.equalsIgnoreCase(idioma))) // Compara cada idioma de la lista
                .toList();

        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma " + idioma + ".");
        } else {
            System.out.println("Libros encontrados en el idioma " + idioma + ":");
            librosPorIdioma.forEach(System.out::println);
        }
    }

    private Autor obtenerAutor(List<DatosAutor> datosAutores) {
        String nombreAutor = datosAutores.get(0).nombreAutor();
        Optional<Autor> autorExistente = autorRepository.findByNombreAutorContainingIgnoreCase(nombreAutor);

        if (autorExistente.isPresent()) {
            return autorExistente.get();
        } else {
            Autor autor = new Autor(datosAutores.get(0));
            return autorRepository.save(autor);
        }
    }
}
