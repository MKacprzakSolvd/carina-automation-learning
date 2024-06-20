package com.solvd.gui.pages.ios;

import com.solvd.enums.ProductCategory;
import com.solvd.gui.pages.common.ProductsPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.IOS_PHONE, parentClass = ProductsPageBase.class)
public class ProductsPage extends ProductsPageBase {
    public ProductsPage(WebDriver driver, ProductCategory productCategory) {
        super(driver, productCategory);
    }
}
