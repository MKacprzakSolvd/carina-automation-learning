package com.solvd.pages;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutPageStepThree extends AbstractPage {
    @FindBy(css = "#maincontent .action.continue")
    private ExtendedWebElement continueShoppingButton;

    public CheckoutPageStepThree(WebDriver driver) {
        super(driver);
    }

    public HomePage returnToHomePage() {
        waitTillPageLoads();
        this.continueShoppingButton.click();
        return new HomePage(getDriver());
    }

    private void waitTillPageLoads() {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(this.continueShoppingButton));
    }
}
