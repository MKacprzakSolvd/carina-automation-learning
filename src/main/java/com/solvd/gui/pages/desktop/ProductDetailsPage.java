package com.solvd.gui.pages.desktop;

import com.solvd.gui.pages.common.ProductDetailsPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.DESKTOP, parentClass = ProductDetailsPageBase.class)
public class ProductDetailsPage extends ProductDetailsPageBase {
    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }
}
