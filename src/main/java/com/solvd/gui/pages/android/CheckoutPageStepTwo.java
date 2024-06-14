package com.solvd.gui.pages.android;

import com.solvd.gui.pages.common.CheckoutPageStepTwoBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = CheckoutPageStepTwoBase.class)
public class CheckoutPageStepTwo extends CheckoutPageStepTwoBase {
    public CheckoutPageStepTwo(WebDriver driver) {
        super(driver);
    }
}
