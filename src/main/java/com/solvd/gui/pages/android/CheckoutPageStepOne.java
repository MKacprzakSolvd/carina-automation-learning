package com.solvd.gui.pages.android;

import com.solvd.gui.pages.common.CheckoutPageStepOneBase;
import com.solvd.model.Product;
import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Arrays;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

@DeviceType(pageType = DeviceType.Type.ANDROID_PHONE, parentClass = CheckoutPageStepOneBase.class)
public class CheckoutPageStepOne extends CheckoutPageStepOneBase {
    private final String CART_CONTENT_WRAPPER_OPEN_CLASS = "_show";

    @FindBy(css = ".minicart-wrapper .showcart")
    private ExtendedWebElement openCartContentsButton;

    @FindBy(xpath = "//*[contains(@class,'opc-summary-wrapper')]//*[@data-role='closeBtn']")
    private ExtendedWebElement closeCartContentsButton;

    @FindBy(className = "opc-summary-wrapper")
    private ExtendedWebElement cartContentsWrapper;

    @FindBy(id = "checkout-loader")
    private ExtendedWebElement checkoutLoader;

    public CheckoutPageStepOne(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isProductInCart(Product product) {
        waitTillPageLoads();
        openCartContents();
        boolean productInCart = super.isProductInCart(product);
        closeCartContents();
        return productInCart;
    }

    @Override
    public int getProductsCount() {
        waitTillPageLoads();
        openCartContents();
        int productsCount = super.getProductsCount();
        closeCartContents();
        return productsCount;
    }

    private boolean isCartContentsOpened() {
        return Arrays.asList(
                this.cartContentsWrapper.getAttribute("class").split(" ")
        ).contains(CART_CONTENT_WRAPPER_OPEN_CLASS);
    }

    private void openCartContents() {
        if (!isCartContentsOpened()) {
            waitForJSToLoad();
            waitUntil(elementToBeClickable(this.openCartContentsButton), 10);
            this.openCartContentsButton.click();
            waitForJSToLoad();
        }
    }

    private void closeCartContents() {
        if (isCartContentsOpened()) {
            waitForJSToLoad();
            this.closeCartContentsButton.click();
            waitForJSToLoad();
        }
    }

    private void waitTillPageLoads() {
        this.checkoutLoader.waitUntilElementDisappear(20);
        waitForJSToLoad();
    }
}
