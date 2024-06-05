package com.solvd.components;

import com.solvd.enums.ProductCategory;
import com.solvd.pages.ProductsPage;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

// TODO: consider moving to ProductsPage (as static nested class)
public class ProductFilter extends AbstractUIObject {
    @FindBy(className = "swatch-option")
    private List<ExtendedWebElement> options;

    @FindBy(className = "filter-options-title")
    private ExtendedWebElement title;


    public ProductFilter(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public List<String> getOptions() {
        // expand options to avoid waiting for visibility added by Carina
        expand();
        return this.options.stream()
                .map(webElement -> webElement.getAttribute("option-label"))
                .toList();
    }

    /**
     * expand filter section, allowing to select option
     * does anything only if filter is not expanded
     */
    private void expand() {
        if (!isExpanded()) {
            this.title.click();
        }
    }

    private boolean isExpanded() {
        return getRootExtendedElement().getAttribute("class").contains("active");
    }

    public ProductsPage filterBy(String option, ProductCategory productCategory) {
        for (ExtendedWebElement optionElement : options) {
            if (optionElement.getAttribute("option-label").equals(option)) {
                expand();
                optionElement.click();
                return new ProductsPage(getDriver(), productCategory);
            }
        }
        throw new IllegalArgumentException("Option not found: " + option);
    }
}
