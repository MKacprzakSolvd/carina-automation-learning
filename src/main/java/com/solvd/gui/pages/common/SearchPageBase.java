package com.solvd.gui.pages.common;

import com.solvd.gui.pages.common.components.ProductCardBase;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Collections;
import java.util.List;

public abstract class SearchPageBase extends AbstractPage {
    // TODO make ProductCard implement ExtendedWebElement
    @FindBy(css = ".products .product-items .product-item")
    private List<ProductCardBase> productCards;

    public SearchPageBase(WebDriver driver) {
        super(driver);
        // ending page url with '?' is workaround for carina but when comparing url ending with /
        setPageURL("catalogsearch/result/?");
    }

    public List<ProductCardBase> getProductCards() {
        System.out.println(this.productCards.size());
        return Collections.unmodifiableList(this.productCards);
    }
}
