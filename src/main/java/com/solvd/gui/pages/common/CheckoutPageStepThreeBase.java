package com.solvd.gui.pages.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class CheckoutPageStepThreeBase extends AbstractPage {
    @FindBy(css = "#maincontent .action.continue")
    private ExtendedWebElement continueShoppingButton;

    public CheckoutPageStepThreeBase(WebDriver driver) {
        super(driver);
        waitTillPageLoads();
    }

    public HomePageBase returnToHomePage() {
        this.continueShoppingButton.click();
        return initPage(getDriver(), HomePageBase.class);
    }

    private void waitTillPageLoads() {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        //this.continueShoppingButton.waitUntil(ExpectedConditions.elementToBeClickable(this.continueShoppingButton), 10);
        wait.until(ExpectedConditions.elementToBeClickable(this.continueShoppingButton));
    }
}
