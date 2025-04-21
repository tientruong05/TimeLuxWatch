package com.TimeLuxWatchBE.payload.request;

// Using Lombok for boilerplate code (getters, setters, etc.)
// Make sure Lombok dependency is included in your pom.xml and enabled in your IDE
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategorySaveRequest {
    private String categoryName;
    private String subCategoriesName;
    private int status;

    // Lombok will generate getters and setters
    // public String getCategoryName() { ... }
    // public void setCategoryName(String categoryName) { ... }
    // etc.
} 