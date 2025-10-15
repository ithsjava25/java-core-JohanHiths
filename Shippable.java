package com.example;

import com.example.Category;
import java.math.BigDecimal;

public interface Shippable {

    BigDecimal weight();


    BigDecimal calculateShippingCost();

    BigDecimal price();
}
