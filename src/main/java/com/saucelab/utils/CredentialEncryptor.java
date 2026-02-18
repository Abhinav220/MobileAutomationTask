package com.saucelab.utils;

import java.util.Scanner;

/**
 * Utility class to encrypt Xray and Jira credentials for secure storage.
 * 
 * Usage:
 *   java -cp target/classes com.saucelab.utils.CredentialEncryptor
 * 
 * Or run as main class:
 *   mvn exec:java -Dexec.mainClass="com.saucelab.utils.CredentialEncryptor"
 */
public class CredentialEncryptor {
    
    private static final String DEFAULT_SECRET_KEY = "SauceLabAutomation";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("  Credential Encryption Utility");
        System.out.println("========================================\n");
        
        System.out.println("This utility encrypts Xray and Jira credentials for secure storage.");
        System.out.println("Encrypted values can be stored in config.properties.\n");
        
        // Get secret key
        System.out.print("Enter encryption secret key (press Enter for default '" + DEFAULT_SECRET_KEY + "'): ");
        String secretKey = scanner.nextLine().trim();
        if (secretKey.isEmpty()) {
            secretKey = DEFAULT_SECRET_KEY;
        }
        
        System.out.println("\n--- Xray Credentials ---");
        
        // Encrypt Xray Client ID
        System.out.print("Enter Xray Client ID: ");
        String clientId = scanner.nextLine();
        if (!clientId.isEmpty()) {
            String encryptedClientId = EncryptionUtils.encrypt(clientId, secretKey);
            System.out.println("Encrypted Xray Client ID:");
            System.out.println("xray.client.id.encrypted=" + encryptedClientId);
        }
        
        // Encrypt Xray Client Secret
        System.out.print("\nEnter Xray Client Secret: ");
        String clientSecret = scanner.nextLine();
        if (!clientSecret.isEmpty()) {
            String encryptedClientSecret = EncryptionUtils.encrypt(clientSecret, secretKey);
            System.out.println("Encrypted Xray Client Secret:");
            System.out.println("xray.client.secret.encrypted=" + encryptedClientSecret);
        }
        
        System.out.println("\n--- Jira Credentials ---");
        
        // Encrypt Jira Email
        System.out.print("Enter Jira Email: ");
        String jiraEmail = scanner.nextLine();
        if (!jiraEmail.isEmpty()) {
            String encryptedEmail = EncryptionUtils.encrypt(jiraEmail, secretKey);
            System.out.println("Encrypted Jira Email:");
            System.out.println("jira.email.encrypted=" + encryptedEmail);
        }
        
        // Encrypt Jira Token
        System.out.print("\nEnter Jira API Token: ");
        String jiraToken = scanner.nextLine();
        if (!jiraToken.isEmpty()) {
            String encryptedToken = EncryptionUtils.encrypt(jiraToken, secretKey);
            System.out.println("Encrypted Jira Token:");
            System.out.println("jira.token.encrypted=" + encryptedToken);
        }
        
        System.out.println("\n--- Configuration ---");
        System.out.println("Add the encrypted values above to your config.properties file.");
        System.out.println("Also add:");
        System.out.println("encryption.secret.key=" + secretKey);
        
        System.out.println("\n========================================");
        System.out.println("  Encryption Complete");
        System.out.println("========================================\n");
        
        scanner.close();
    }
    
    /**
     * Encrypts a single credential value.
     * Useful for programmatic encryption.
     */
    public static String encryptCredential(String plainText, String secretKey) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            secretKey = DEFAULT_SECRET_KEY;
        }
        return EncryptionUtils.encrypt(plainText, secretKey);
    }
    
    /**
     * Decrypts a single credential value.
     * Useful for verification.
     */
    public static String decryptCredential(String encryptedText, String secretKey) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            secretKey = DEFAULT_SECRET_KEY;
        }
        return EncryptionUtils.decrypt(encryptedText, secretKey);
    }
}
