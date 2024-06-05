package com.solvd.pages;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutPageStepTwo extends AbstractPage {
    @FindBy(css = "#checkout-payment-method-load [type='submit']")
    private ExtendedWebElement placeOrderButton;

    //.checkout-billing-address
    @FindBy(css = "#checkout-payment-method-load .payment-method-content")
    private ExtendedWebElement orderAddressWrapper;

    public CheckoutPageStepTwo(WebDriver driver) {
        super(driver);
        waitTillPageLoads();
    }

    public CheckoutPageStepThree placeOrder() {
        this.placeOrderButton.click();
        return new CheckoutPageStepThree(getDriver());
    }

    private void waitTillPageLoads() {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("body > div.loading-mask")));
        wait.until(ExpectedConditions.elementToBeClickable(this.placeOrderButton));
        wait.until(ExpectedConditions.visibilityOf(this.orderAddressWrapper));
    }
}
