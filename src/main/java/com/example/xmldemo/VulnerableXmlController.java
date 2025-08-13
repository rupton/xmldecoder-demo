package com.example.xmldemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
@RestController
public class VulnerableXmlController {

    public static void main(String[] args) {
        SpringApplication.run(VulnerableXmlController.class, args);
        System.out.println("=== VULNERABLE XMLDecoder Demo Server Started ===");
        System.out.println("WARNING: This is for educational purposes only!");
        System.out.println("Server running at: http://localhost:8080");
    }

    // Vulnerable endpoint that uses XMLDecoder
    @PostMapping("/api/customer/import")
    public ResponseEntity<Map<String, Object>> importCustomer(@RequestBody String xmlData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Received XML data:");
            System.out.println(xmlData);
            System.out.println("--- Processing with XMLDecoder ---");
            
            // VULNERABLE CODE: Never do this in real applications!
            InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes());
            XMLDecoder decoder = new XMLDecoder(inputStream);
            
            Object result = decoder.readObject();  // RCE vulnerability here!
            decoder.close();
            
            response.put("status", "success");
            response.put("message", "Customer data processed successfully");
            response.put("data", result.toString());
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error processing XML: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }

    // Safe endpoint for comparison (uses simple string processing)
    @PostMapping("/api/customer/safe-import")
    public ResponseEntity<Map<String, Object>> safeImportCustomer(@RequestBody String xmlData) {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("Safe processing of XML data:");
        System.out.println(xmlData);
        
        // Safe processing - just treat as string data
        response.put("status", "success");
        response.put("message", "XML received safely (not deserialized)");
        response.put("length", xmlData.length());
        
        return ResponseEntity.ok(response);
    }

    // Demonstrate legitimate XMLDecoder usage
    @PostMapping("/api/customer/export")
    public ResponseEntity<String> exportCustomer(@RequestParam String name, 
                                                 @RequestParam String email, 
                                                 @RequestParam String id) {
        try {
            Customer customer = new Customer(name, email, id);
            
            // Use XMLEncoder to serialize the customer object
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(outputStream);
            encoder.writeObject(customer);
            encoder.close();
            
            String xmlData = outputStream.toString();
            System.out.println("Exported customer as XML:");
            System.out.println(xmlData);
            
            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml")
                    .body(xmlData);
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error exporting customer: " + e.getMessage());
        }
    }

    // Show how legitimate XMLDecoder would process the exported data
    @PostMapping("/api/customer/legitimate-import")
    public ResponseEntity<Map<String, Object>> legitimateImportCustomer(@RequestBody String xmlData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("Processing legitimate XMLDecoder data:");
            System.out.println(xmlData);
            
            // This is the intended use case - deserializing objects you previously serialized
            InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes());
            XMLDecoder decoder = new XMLDecoder(inputStream);
            
            Object result = decoder.readObject();
            decoder.close();
            
            if (result instanceof Customer) {
                Customer customer = (Customer) result;
                response.put("status", "success");
                response.put("message", "Customer imported successfully");
                response.put("customer", Map.of(
                    "name", customer.getName(),
                    "email", customer.getEmail(),
                    "id", customer.getId()
                ));
            } else {
                response.put("status", "error");
                response.put("message", "Expected Customer object, got: " + result.getClass().getName());
            }
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error processing XML: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public String home() {
        return "<html><body>" +
               "<h1>XMLDecoder Vulnerability Demo</h1>" +
               "<p><strong>WARNING:</strong> This is for educational purposes only!</p>" +
               "<h2>Test Endpoints:</h2>" +
               "<ul>" +
               "<li><code>POST /api/customer/import</code> - Vulnerable XMLDecoder endpoint</li>" +
               "<li><code>POST /api/customer/legitimate-import</code> - Legitimate XMLDecoder usage</li>" +
               "<li><code>POST /api/customer/export</code> - Export customer as XMLEncoder format</li>" +
               "<li><code>POST /api/customer/safe-import</code> - Safe endpoint for comparison</li>" +
               "</ul>" +
               "<h2>Legitimate Workflow:</h2>" +
               "<pre>" +
               "# 1. Export a customer object\n" +
               "curl -X POST 'http://localhost:8080/api/customer/export?name=John&email=john@example.com&id=123'\n\n" +
               "# 2. Import the exported XML (legitimate use)\n" +
               "curl -X POST http://localhost:8080/api/customer/legitimate-import \\\n" +
               "  -H 'Content-Type: application/xml' \\\n" +
               "  -d '[output from export]'" +
               "</pre>" +
               "<h2>Attack Example:</h2>" +
               "<pre>" +
               "curl -X POST http://localhost:8080/api/customer/import \\\n" +
               "  -H 'Content-Type: application/xml' \\\n" +
               "  -d '&lt;java&gt;&lt;object class=\"java.lang.String\"&gt;&lt;string&gt;Hello World&lt;/string&gt;&lt;/object&gt;&lt;/java&gt;'" +
               "</pre>" +
               "</body></html>";
    }
}
