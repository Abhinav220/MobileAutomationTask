package com.saucelab.utils;

import com.saucelab.config.ConfigLoader;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static com.saucelab.utils.EncryptionUtils.decrypt;

/**
 * Simplified Xray integration utility for reporting test results to Xray Cloud.
 */
public class XrayLogger {
    
    private static String testExecutionKey = null;
    private static String bearerToken = null;
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    
    /**
     * Checks if Xray integration is enabled.
     */
    public static boolean isXrayEnabled() {
        return Boolean.parseBoolean(ConfigLoader.getProperty("xray.enabled", "false"));
    }
    
    /**
     * Sets the test execution key.
     */
    public static void setTestExecutionKey(String key) {
        testExecutionKey = key;
        System.out.println("[XRAY] Test Execution Key set to: " + key);
    }
    
    /**
     * Gets the current test execution key.
     */
    public static String getTestExecutionKey() {
        return testExecutionKey;
    }
    
    /**
     * Initializes the test execution key from config or creates a new one.
     */
    public static void initializeTestExecutionKey() {
        if (!isXrayEnabled()) {
            System.out.println("[XRAY] Xray integration is disabled. Skipping initialization.");
            return;
        }
        
        // Check system property first
        String execId = System.getProperty("execId");
        if (execId != null && !execId.trim().isEmpty()) {
            setTestExecutionKey(execId);
            return;
        }
        
        // Check config property
        String configExecId = ConfigLoader.getProperty("xray.exec.id");
        if (configExecId != null && !configExecId.trim().isEmpty()) {
            setTestExecutionKey(configExecId);
            return;
        }
        
        System.out.println("[XRAY] No test execution key provided. Xray reporting will be skipped.");
    }
    
    /**
     * Gets the secret key for decryption from config or uses default.
     */
    private static String getSecretKey() {
        return ConfigLoader.getEncryptionSecretKey();
    }
    
    /**
     * Authenticates with Xray Cloud API and returns a bearer token.
     */
    public static String authenticate() throws IOException {
        if (bearerToken != null) {
            return bearerToken;
        }
        
        String encryptedClientId = ConfigLoader.getProperty("xray.client.id.encrypted");
        String encryptedClientSecret = ConfigLoader.getProperty("xray.client.secret.encrypted");
        
        // Try encrypted first, then fallback to plain text
        String clientId = encryptedClientId != null ? decrypt(encryptedClientId, getSecretKey()) : 
                ConfigLoader.getProperty("xray.client.id");
        String clientSecret = encryptedClientSecret != null ? decrypt(encryptedClientSecret, getSecretKey()) : 
                ConfigLoader.getProperty("xray.client.secret");
        
        String authEndpoint = ConfigLoader.getProperty("xray.auth.endpoint", 
                "https://xray.cloud.getxray.app/api/oauth/token");
        
        if (clientId == null || clientSecret == null) {
            throw new IOException("Xray client ID or secret not configured");
        }
        
        JSONObject body = new JSONObject();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .timeout(Duration.ofSeconds(30))
                .build();
        
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new IOException("Failed to authenticate with Xray: HTTP " + response.statusCode());
            }
            
            // Token is returned as a quoted string
            bearerToken = response.body().replaceAll("\"", "");
            System.out.println("[XRAY] Successfully authenticated with Xray Cloud");
            return bearerToken;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Authentication interrupted", e);
        }
    }
    
    /**
     * Logs a test execution result to Xray.
     */
    public static void logTestExecution(String testKey, String status, String comment) {
        if (!isXrayEnabled()) {
            return;
        }
        
        if (testExecutionKey == null) {
            System.err.println("[XRAY] Test execution key not set. Cannot log test result.");
            return;
        }
        
        try {
            String token = authenticate();
            String executionEndpoint = ConfigLoader.getProperty("xray.execution.endpoint",
                    "https://xray.cloud.getxray.app/api/v2/import/execution");
            
            JSONObject payload = buildTestExecutionPayload(testExecutionKey, testKey, status, comment);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(executionEndpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .timeout(Duration.ofSeconds(30))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("[XRAY] Test result logged: " + testKey + " -> " + status);
            } else {
                System.err.println("[XRAY] Failed to log test result: HTTP " + response.statusCode() + 
                        " - " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("[XRAY] Error logging test execution: " + e.getMessage());
        }
    }
    
    /**
     * Builds the test execution payload for Xray Cloud API.
     */
    private static JSONObject buildTestExecutionPayload(String execKey, String testKey, String status, String comment) {
        JSONObject payload = new JSONObject();
        payload.put("testExecutionKey", execKey);
        
        JSONObject test = new JSONObject();
        test.put("testKey", testKey);
        test.put("status", status.toUpperCase());
        if (comment != null && !comment.isEmpty()) {
            test.put("comment", comment);
        }
        
        JSONObject tests = new JSONObject();
        tests.put("test", test);
        payload.put("tests", new JSONObject[]{tests});
        
        return payload;
    }
    
    /**
     * Attaches a report file to the test execution in Xray.
     */
    public static void attachReportToTestExecution(String filePath) {
        if (!isXrayEnabled()) {
            return;
        }
        
        if (testExecutionKey == null) {
            System.err.println("[XRAY] Test execution key not set. Cannot attach report.");
            return;
        }
        
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                System.err.println("[XRAY] Report file not found: " + filePath);
                return;
            }
            
            // Use Jira REST API for attachments
            try {
                JiraCredentials credentials = loadJiraCredentials();
                attachToJira(testExecutionKey, filePath, credentials.jiraUrl, credentials.email, credentials.token);
            } catch (Exception e) {
                System.err.println("[XRAY] Jira credentials not configured or invalid. Cannot attach report: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("[XRAY] Error attaching report: " + e.getMessage());
        }
    }
    
    /**
     * Loads Jira credentials, decrypting if encrypted.
     */
    private static JiraCredentials loadJiraCredentials() {
        String encryptedEmail = ConfigLoader.getProperty("jira.email.encrypted");
        String encryptedToken = ConfigLoader.getProperty("jira.token.encrypted");
        
        String email = encryptedEmail != null ? decrypt(encryptedEmail, getSecretKey()) : 
                ConfigLoader.getProperty("jira.email");
        String token = encryptedToken != null ? decrypt(encryptedToken, getSecretKey()) : 
                ConfigLoader.getProperty("jira.token");
        String jiraUrl = ConfigLoader.getProperty("jira.url");
        
        if (email == null || token == null || jiraUrl == null) {
            throw new RuntimeException("Jira credentials not configured");
        }
        
        return new JiraCredentials(jiraUrl, email, token);
    }
    
    /**
     * Helper class to hold Jira credentials.
     */
    private static class JiraCredentials {
        final String jiraUrl;
        final String email;
        final String token;
        
        JiraCredentials(String jiraUrl, String email, String token) {
            this.jiraUrl = jiraUrl;
            this.email = email;
            this.token = token;
        }
    }
    
    /**
     * Attaches a file to a Jira issue using Jira REST API.
     */
    private static void attachToJira(String issueKey, String filePath, String jiraUrl, String email, String token) {
        try {
            java.io.File file = new java.io.File(filePath);
            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
            
            String auth = Base64.getEncoder().encodeToString((email + ":" + token).getBytes(StandardCharsets.UTF_8));
            
            if (!jiraUrl.endsWith("/")) {
                jiraUrl += "/";
            }
            
            String attachUrl = jiraUrl + "rest/api/3/issue/" + issueKey + "/attachments";
            
            // Create multipart form data
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            String fileName = file.getName();
            
            StringBuilder multipart = new StringBuilder();
            multipart.append("--").append(boundary).append("\r\n");
            multipart.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
            multipart.append("Content-Type: application/octet-stream\r\n\r\n");
            
            byte[] boundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
            byte[] headerBytes = multipart.toString().getBytes(StandardCharsets.UTF_8);
            
            byte[] body = new byte[headerBytes.length + fileBytes.length + boundaryBytes.length];
            System.arraycopy(headerBytes, 0, body, 0, headerBytes.length);
            System.arraycopy(fileBytes, 0, body, headerBytes.length, fileBytes.length);
            System.arraycopy(boundaryBytes, 0, body, headerBytes.length + fileBytes.length, boundaryBytes.length);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(attachUrl))
                    .header("Authorization", "Basic " + auth)
                    .header("X-Atlassian-Token", "no-check")
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .timeout(Duration.ofSeconds(60))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("[XRAY] Report attached to test execution: " + issueKey);
            } else {
                System.err.println("[XRAY] Failed to attach report: HTTP " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("[XRAY] Error attaching to Jira: " + e.getMessage());
        }
    }
}
