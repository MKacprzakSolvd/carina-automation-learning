package com.solvd.gui.pages.mobile;

import com.solvd.gui.pages.common.HomePageBase;
import com.solvd.gui.pages.common.SearchPageBase;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class HomePageMobile extends HomePageBase {
    @FindBy(xpath = "//*[@id='search_mini_form']//*[@for='search']")
    private ExtendedWebElement toggleSearchbarButton;

    public HomePageMobile(WebDriver driver) {
        super(driver);
    }

    @Override
    public SearchPageBase searchForProduct(String searchTerm) {
        this.toggleSearchbarButton.click();
        return super.searchForProduct(searchTerm);
    }
}
