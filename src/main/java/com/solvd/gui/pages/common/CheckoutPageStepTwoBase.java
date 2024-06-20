package com.solvd.gui.pages.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

public abstract class CheckoutPageStepTwoBase extends AbstractPage {
    @FindBy(css = "#checkout-payment-method-load [type='submit']")
    private ExtendedWebElement placeOrderButton;

    //.checkout-billing-address
    @FindBy(css = "#checkout-payment-method-load .payment-method-content")
    private ExtendedWebElement orderAddressWrapper;

    public CheckoutPageStepTwoBase(WebDriver driver) {
        super(driver);
        //waitForJSToLoad();
        setPageURL("checkout/#payment");
        waitTillPageLoads();
    }

    public CheckoutPageStepThreeBase placeOrder() {
        waitForJSToLoad();
        this.placeOrderButton.click();
        return initPage(getDriver(), CheckoutPageStepThreeBase.class);
    }

    private void waitTillPageLoads() {
        waitUntil(invisibilityOfElementLocated(By.cssSelector("body > div.loading-mask")), 10);
    }
}
