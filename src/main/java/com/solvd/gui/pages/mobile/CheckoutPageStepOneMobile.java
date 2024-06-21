package com.solvd.gui.pages.mobile;

import com.solvd.gui.pages.common.CheckoutPageStepOneBase;
import com.solvd.model.Product;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Arrays;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class CheckoutPageStepOneMobile extends CheckoutPageStepOneBase {
    protected final String CART_CONTENT_WRAPPER_OPEN_CLASS = "_show";

    @FindBy(css = ".minicart-wrapper .showcart")
    protected ExtendedWebElement openCartContentsButton;

    @FindBy(xpath = "//*[contains(@class,'opc-summary-wrapper')]//*[@data-role='closeBtn']")
    protected ExtendedWebElement closeCartContentsButton;

    @FindBy(className = "opc-summary-wrapper")
    protected ExtendedWebElement cartContentsWrapper;

    @FindBy(id = "checkout-loader")
    protected ExtendedWebElement checkoutLoader;


    public CheckoutPageStepOneMobile(WebDriver driver) {
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

    protected boolean isCartContentsOpened() {
        return Arrays.asList(
                this.cartContentsWrapper.getAttribute("class").split(" ")
        ).contains(CART_CONTENT_WRAPPER_OPEN_CLASS);
    }

    protected void openCartContents() {
        if (!isCartContentsOpened()) {
            waitForJSToLoad();
            waitUntil(elementToBeClickable(this.openCartContentsButton), 10);
            this.openCartContentsButton.click();
            waitForJSToLoad();
        }
    }

    protected void closeCartContents() {
        if (isCartContentsOpened()) {
            waitForJSToLoad();
            waitUntil(elementToBeClickable(this.closeCartContentsButton), 10);
            this.closeCartContentsButton.click();
            this.cartContentsWrapper.waitUntilElementDisappear(10);
            waitForJSToLoad();
        }
    }

    protected void waitTillPageLoads() {
        this.checkoutLoader.waitUntilElementDisappear(20);
        waitForJSToLoad();
    }
}
