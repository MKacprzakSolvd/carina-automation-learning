package com.solvd.gui.pages.common;

import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public abstract class HomePageBase extends AbstractPage {
    @FindBy(id = "search")
    private ExtendedWebElement searchInputField;

    public HomePageBase(WebDriver driver) {
        super(driver);
        setPageURL("");

    }

    public SearchPageBase searchForProduct(String searchTerm) {
        this.searchInputField.type(searchTerm + "\n");
        return initPage(getDriver(), SearchPageBase.class);
    }
}
