# Usar una imagen base con JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Crear un directorio para la aplicación
RUN mkdir /app

# Establecer el directorio de trabajo en /app
WORKDIR /app

# Copiar el archivo JAR de la aplicación en el contenedor
COPY target/*.jar /app/app.jar

# Exponer el puerto en el que la aplicación Spring Boot escucha
EXPOSE 8083

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
