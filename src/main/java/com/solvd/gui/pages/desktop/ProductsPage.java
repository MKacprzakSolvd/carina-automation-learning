package com.solvd.gui.pages.desktop;

import com.solvd.enums.ProductCategory;
import com.solvd.gui.pages.common.ProductsPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.DESKTOP, parentClass = ProductsPageBase.class)
public class ProductsPage extends ProductsPageBase {
    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    public ProductsPage(WebDriver driver, String relativeUrl) {
        super(driver, relativeUrl);
    }

    public ProductsPage(WebDriver driver, ProductCategory productCategory) {
        super(driver, productCategory);
    }
}
