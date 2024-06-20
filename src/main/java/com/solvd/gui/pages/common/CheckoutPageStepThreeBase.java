package com.solvd.gui.pages.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public abstract class CheckoutPageStepThreeBase extends AbstractPage {
    @FindBy(css = "#maincontent .action.continue")
    private ExtendedWebElement continueShoppingButton;

    public CheckoutPageStepThreeBase(WebDriver driver) {
        super(driver);
        setPageURL("checkout/onepage/success/");
        setUiLoadedMarker(this.continueShoppingButton);
    }

    public HomePageBase returnToHomePage() {
//        waitTillPageLoads();
        this.continueShoppingButton.click();
        return initPage(getDriver(), HomePageBase.class);
    }

    private void waitTillPageLoads() {
        waitUntil(elementToBeClickable(this.continueShoppingButton), 10);
    }
}
