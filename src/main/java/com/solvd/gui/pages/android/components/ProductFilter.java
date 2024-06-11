package com.solvd.gui.pages.android.components;

import com.solvd.gui.components.ProductFilterBase;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

public class ProductFilter extends ProductFilterBase {
    // this element is outside ProductFilter element, so xpath starts with //
    @FindBy(xpath = "//*[contains(@class, 'filter-title')]//*[@data-role='title']")
    ExtendedWebElement filtersOpenButton;

    public ProductFilter(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    @Override
    protected void expand() {
        openFiltersPanel();
        super.expand();
    }

    @Override
    protected void collapse() {
        super.collapse();
        closeFiltersPanel();
    }

    private void openFiltersPanel() {
        if (!isFiltersPanelOpened()) {
            this.filtersOpenButton.click();
        }
    }

    private void closeFiltersPanel() {
        if (isFiltersPanelOpened()) {
            this.filtersOpenButton.click();
        }
    }

    private boolean isFiltersPanelOpened() {
        return this.filtersOpenButton.getAttribute("aria-expanded").equals("true");
    }
}
