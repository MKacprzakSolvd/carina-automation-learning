package com.solvd.gui.pages.android;

import com.solvd.gui.pages.common.CheckoutPageStepThreeBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = CheckoutPageStepThreeBase.class)
public class CheckoutPageStepThree extends CheckoutPageStepThreeBase {
    public CheckoutPageStepThree(WebDriver driver) {
        super(driver);
    }
}
