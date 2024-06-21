package com.solvd.gui.pages.android;

import com.solvd.gui.pages.common.HomePageBase;
import com.solvd.gui.pages.mobile.HomePageMobile;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = HomePageBase.class)
public class HomePage extends HomePageMobile {
    public HomePage(WebDriver driver) {
        super(driver);
    }
}
