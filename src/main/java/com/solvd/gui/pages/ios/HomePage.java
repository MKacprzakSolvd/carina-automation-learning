package com.solvd.gui.pages.ios;

import com.solvd.gui.pages.common.HomePageBase;
import com.solvd.gui.pages.common.SearchPageBase;
import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@DeviceType(pageType = DeviceType.Type.IOS_PHONE, parentClass = HomePageBase.class)
public class HomePage extends HomePageBase {
    @FindBy(xpath = "//*[@id='search_mini_form']//*[@for='search']")
    private ExtendedWebElement toggleSearchbarButton;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public SearchPageBase searchForProduct(String searchTerm) {
        this.toggleSearchbarButton.click();
        return super.searchForProduct(searchTerm);
    }
}
