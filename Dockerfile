# Étape 1 : Build avec Maven et JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copier le pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source et builder le jar
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image finale avec JRE 21
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le jar généré
COPY --from=build /app/target/*.jar app.jar

# Exposer le port par défaut
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java","-jar","app.jar"]

