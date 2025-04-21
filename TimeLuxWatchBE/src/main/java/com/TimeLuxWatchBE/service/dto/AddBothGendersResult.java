package com.TimeLuxWatchBE.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO to hold the result of attempting to add both male and female subcategories.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddBothGendersResult {
    private boolean namAdded = false;
    private boolean namExisted = false;
    private boolean nuAdded = false;
    private boolean nuExisted = false;
    private String errorMessage = null; // To capture any unexpected errors during the process

    public boolean isFullySuccessful() {
        return namAdded && nuAdded && errorMessage == null;
    }
     public boolean isPartiallySuccessful() {
        return (namAdded || nuAdded) && !(namAdded && nuAdded) && errorMessage == null;
    }
      public boolean isNoOp() { // Both already existed
        return namExisted && nuExisted && errorMessage == null;
    }
} 