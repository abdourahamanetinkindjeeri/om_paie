package com.odc.om.paie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class PaieApplication {

	public static void main(String[] args) {
		// Charger les variables d'environnement depuis le fichier .env
		try {
			Path envPath = Paths.get(".env");
			if (Files.exists(envPath)) {
				Files.lines(envPath).forEach(line -> {
					if (line.contains("=") && !line.trim().startsWith("#")) {
						String[] parts = line.split("=", 2);
						if (parts.length == 2) {
							System.setProperty(parts[0].trim(), parts[1].trim());
						}
					}
				});
			}
		} catch (Exception e) {
			// Ignorer les erreurs de chargement du .env
		}

		SpringApplication.run(PaieApplication.class, args);
	}

}
