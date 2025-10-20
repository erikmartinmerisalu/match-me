package com.matchme.config;

import com.matchme.entity.User;
import com.matchme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.annotation.PostConstruct;

@Component
@DependsOn({"dotenvConfig", "initializeDotenvProperties"}) // Ensure these run first
public class AdminUserInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        // Small delay to ensure environment is fully initialized
        try {
            Thread.sleep(100); // 100ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Get properties directly from Environment after it's been configured
        String adminEmail = environment.getProperty("ADMIN_EMAIL");
        String adminPassword = environment.getProperty("ADMIN_PASSWORD");
        String createDefaultAdminStr = environment.getProperty("CREATE_DEFAULT_ADMIN");
        boolean createDefaultAdmin = Boolean.parseBoolean(createDefaultAdminStr);
        
        // Debug: print all environment properties that start with "ADMIN" or "CREATE"
        System.out.println("=== Environment Properties ===");
        for (String propertyName : new String[]{"ADMIN_EMAIL", "ADMIN_PASSWORD", "CREATE_DEFAULT_ADMIN"}) {
            String value = environment.getProperty(propertyName);
            System.out.println("🔍 " + propertyName + " = " + value);
        }
        System.out.println("==============================");
        
        System.out.println("🔧 AdminUserInitializer - Email: " + adminEmail + ", Create: " + createDefaultAdmin);
        
        if (createDefaultAdmin && adminEmail != null && !adminEmail.isEmpty() && 
            adminPassword != null && !adminPassword.isEmpty()) {
            createAdminUserIfNotExists(adminEmail, adminPassword);
        } else {
            System.out.println("⚠️  Admin creation skipped - check .env file configuration");
        }
    }

    private void createAdminUserIfNotExists(String adminEmail, String adminPassword) {
        if (userService.findByEmail(adminEmail).isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setIsAdmin(true);
            adminUser.setIsFake(false);
            
            userService.save(adminUser);
            System.out.println("✅ Admin user created: " + adminEmail);
        } else {
            System.out.println("ℹ️  Admin user already exists: " + adminEmail);
        }
    }
}