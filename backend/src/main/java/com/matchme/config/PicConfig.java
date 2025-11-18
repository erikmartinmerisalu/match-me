// package com.matchme.config;

// import java.nio.file.Paths;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class PicConfig implements WebMvcConfigurer {

//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         String absolutePath = Paths.get("uploads").toAbsolutePath().toString();
//         registry.addResourceHandler("/uploads/**")
//                 .addResourceLocations("file:" + absolutePath + "/");
//     }

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**")
//                 .allowedOrigins(
//                     "http://localhost:5173", 
//                     "http://127.0.0.1:5173", 
//                     "http://localhost:3000"
//                 )
//                 .allowedMethods("*")
//                 .allowedHeaders("*")
//                 .allowCredentials(true);
//     }
// }

