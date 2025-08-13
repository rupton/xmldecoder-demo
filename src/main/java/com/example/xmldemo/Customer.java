package com.example.xmldemo;
public class Customer implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String email;
    private String id;
    
    // Default constructor required for JavaBeans
    public Customer() {}
    
    public Customer(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }
    
    // Standard JavaBean getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    @Override
    public String toString() {
        return "Customer{name='" + name + "', email='" + email + "', id='" + id + "'}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return java.util.Objects.equals(name, customer.name) &&
               java.util.Objects.equals(email, customer.email) &&
               java.util.Objects.equals(id, customer.id);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, email, id);
    }
}