package com.solvd.gui.pages.desktop;

import com.solvd.gui.pages.common.CheckoutPageStepOneBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.DESKTOP, parentClass = CheckoutPageStepOne.class)
public class CheckoutPageStepOne extends CheckoutPageStepOneBase {
    public CheckoutPageStepOne(WebDriver driver) {
        super(driver);
    }
}
