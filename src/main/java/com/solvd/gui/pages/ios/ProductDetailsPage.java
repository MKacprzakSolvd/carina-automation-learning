package com.solvd.gui.pages.ios;

import com.solvd.gui.pages.common.ProductDetailsPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.utils.ios.IOSUtils;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.IOS_PHONE, parentClass = ProductDetailsPageBase.class)
public class ProductDetailsPage extends ProductDetailsPageBase implements IOSUtils {
    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected ProductDetailsPageBase submitReview() {
        hideKeyboard();
        return super.submitReview();
    }
}
