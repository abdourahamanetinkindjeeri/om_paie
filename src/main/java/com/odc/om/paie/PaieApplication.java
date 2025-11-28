package com.odc.om.paie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@EnableAsync
public class PaieApplication {

	public static void main(String[] args) {
		// Charger les variables d'environnement depuis le fichier .env si présent
		loadEnvFile();

		SpringApplication.run(PaieApplication.class, args);
	}

	private static void loadEnvFile() {
		try {
			Path envPath = Paths.get(".env");
			if (Files.exists(envPath) && Files.isReadable(envPath)) {
				Files.lines(envPath)
					.filter(line -> line.contains("=") && !line.trim().startsWith("#"))
					.forEach(line -> {
						String[] parts = line.split("=", 2);
						if (parts.length == 2) {
							String key = parts[0].trim();
							String value = parts[1].trim();
							// Ne pas écraser les variables d'environnement système
							if (System.getenv(key) == null) {
								System.setProperty(key, value);
							}
						}
					});
				System.out.println(".env file loaded successfully");
			} else {
				System.out.println(".env file not found or not readable, using system environment variables");
			}
		} catch (Exception e) {
			System.err.println("Error loading .env file: " + e.getMessage() + ", using system environment variables");
		}
	}

}
