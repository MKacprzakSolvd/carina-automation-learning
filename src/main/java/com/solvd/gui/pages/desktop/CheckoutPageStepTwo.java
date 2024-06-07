package com.solvd.gui.pages.desktop;

import com.solvd.gui.pages.common.CheckoutPageStepTwoBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.DESKTOP, parentClass = CheckoutPageStepTwoBase.class)
public class CheckoutPageStepTwo extends CheckoutPageStepTwoBase {
    public CheckoutPageStepTwo(WebDriver driver) {
        super(driver);
    }
}
