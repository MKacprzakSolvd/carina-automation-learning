package com.solvd.gui.pages.ios;

import com.solvd.gui.pages.common.CheckoutPageStepOneBase;
import com.solvd.gui.pages.mobile.CheckoutPageStepOneMobile;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@DeviceType(pageType = DeviceType.Type.IOS_PHONE, parentClass = CheckoutPageStepOneBase.class)
public class CheckoutPageStepOne extends CheckoutPageStepOneMobile {

    public CheckoutPageStepOne(WebDriver driver) {
        super(driver);
    }

    @Override
    public int getProductsCount() {
        waitTillPageLoads();
        openCartContents();
        waitUntil(visibilityOf(this.productsInCartCount), 10);
        int productsCount = super.getProductsCount();
        closeCartContents();
        return productsCount;
    }
}
