package com.solvd.pages;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class HomePage extends AbstractPage {
    @FindBy(id = "search")
    private ExtendedWebElement searchInputField;
    @FindBy(xpath = "//*[@id='search_mini_form']//button[@type='submit']")
    private ExtendedWebElement searchSubmitButton;

    public HomePage(WebDriver driver) {
        super(driver);
        // TODO add some verification that this is home page
        setPageURL("");

    }

    public SearchPage searchForProduct(String searchTerm) {
        this.searchInputField.type(searchTerm);
        // TODO replace with searchInputField.submit()
        this.searchSubmitButton.click();
        return new SearchPage(getDriver());
    }
}
