package com.proyectoalura.literalura;

import com.proyectoalura.literalura.principal.Principal;
import com.proyectoalura.literalura.repository.AutorRepository;
import com.proyectoalura.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private LibroRepository repository;

	@Autowired
	private AutorRepository repositoryAutor;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository, repositoryAutor);
		principal.menuPrincipal();
	}
}
