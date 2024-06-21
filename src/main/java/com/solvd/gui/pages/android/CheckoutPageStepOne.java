package com.solvd.gui.pages.android;

import com.solvd.gui.pages.common.CheckoutPageStepOneBase;
import com.solvd.gui.pages.mobile.CheckoutPageStepOneMobile;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = CheckoutPageStepOneBase.class)
public class CheckoutPageStepOne extends CheckoutPageStepOneMobile {

    public CheckoutPageStepOne(WebDriver driver) {
        super(driver);
    }

    @Override
    public int getProductsCount() {
        waitTillPageLoads();
        openCartContents();
        int productsCount = super.getProductsCount();
        closeCartContents();
        return productsCount;
    }
}
