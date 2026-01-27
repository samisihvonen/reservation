package com.example.backend.config;

import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;

/**
 * Configuration class to load environment variables from .env files
 * 
 * Load order:
 * 1. .env.{profile} (e.g., .env.local, .env.staging, .env.production)
 * 2. .env (default)
 * 3. System environment variables (fallback)
 */
@Configuration
public class EnvironmentConfig {
    
    static {
        loadEnvironmentVariables();
    }
    
    /**
     * Load environment variables from .env files
     * Tries profile-specific file first, then falls back to default .env
     */
    private static void loadEnvironmentVariables() {
        String activeProfile = System.getProperty("spring.profiles.active", "local");
        String profileSpecificFile = ".env." + activeProfile;
        
        // Try to load profile-specific .env file
        if (new File(profileSpecificFile).exists()) {
            try {
                Dotenv.configure()
                    .directory(System.getProperty("user.dir"))
                    .filename(profileSpecificFile)
                    .load();
                System.out.println("✓ Loaded environment from: " + profileSpecificFile);
            } catch (Exception e) {
                System.err.println("✗ Failed to load " + profileSpecificFile + ": " + e.getMessage());
                loadDefaultEnv();
            }
        } else {
            // If profile-specific doesn't exist, try default .env
            loadDefaultEnv();
        }
    }
    
    /**
     * Load default .env file if it exists
     */
    private static void loadDefaultEnv() {
        if (new File(".env").exists()) {
            try {
                Dotenv.configure()
                    .directory(System.getProperty("user.dir"))
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();
                System.out.println("✓ Loaded environment from: .env");
            } catch (Exception e) {
                System.err.println("✗ Failed to load .env: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ No .env file found. Using system environment variables.");
        }
    }
}