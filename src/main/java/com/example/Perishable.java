package com.example;

import com.example.Category;
import java.time.LocalDate;
//
public interface Perishable {
    LocalDate getExpiryDate();

/// /
    default boolean isExpired() {
        return getExpiryDate().isBefore(LocalDate.now());
    }


    LocalDate expirationDate();
}
