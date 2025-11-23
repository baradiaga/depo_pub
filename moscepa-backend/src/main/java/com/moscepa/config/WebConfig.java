// Fichier 2/2 : src/main/java/com/moscepa/config/WebConfig.java (Version Finale et Propre)

package com.moscepa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration Web MVC pour servir des ressources statiques comme les fichiers uploadés.
 * La configuration CORS est gérée de manière centralisée et sécurisée dans SecurityConfig.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Cette méthode rend le contenu du dossier spécifié par 'file.upload-dir'
     * accessible publiquement via l'URL '/uploads/**'.
     * Par exemple, un fichier 'image.jpg' dans le dossier sera accessible à
     * http://localhost:8080/uploads/image.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry ) {
        // On s'assure que le chemin se termine bien par un slash pour que le mapping fonctionne
        String uploadPath = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
