package com.solvd.gui.util;

import com.solvd.enums.ShippingMethod;
import com.solvd.model.ShippingInfo;

public class ShippingInfoProvider {
    public static ShippingInfo provideValidShippingInfo() {
        return ShippingInfo.builder()
                .email("a@b.com")
                .firstName("John")
                .lastName("Smith")
                .company("Postal Inc.")
                .addressLine1("ul. Wielopole 2")
                .city("Kraków")
                .province("małopolskie")
                .postalCode("12-345")
                .country("Poland")
                .phoneNumber("123456789")
                .shippingMethod(ShippingMethod.FIXED)
                .build();
    }
}
