package com.solvd.gui.pages.android;

import com.solvd.gui.pages.common.ProductDetailsPageBase;
import com.zebrunner.carina.utils.android.AndroidService;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = ProductDetailsPageBase.class)
public class ProductDetailsPage extends ProductDetailsPageBase {
    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected ProductDetailsPageBase submitReview() {
        AndroidService.getInstance().hideKeyboard();
        return super.submitReview();
    }
}
