package com.solvd.gui.pages.ios;

import com.solvd.gui.pages.common.SearchPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.IOS_PHONE, parentClass = SearchPageBase.class)
public class SearchPage extends SearchPageBase {
    public SearchPage(WebDriver driver) {
        super(driver);
    }
}
