package com.solvd.pages;

import com.solvd.components.ProductCard;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Collections;
import java.util.List;

public class SearchPage extends AbstractPage {
    // TODO make ProductCard implement ExtendedWebElement
    @FindBy(css = ".product-items .product-item")
    private List<ProductCard> productCards;

    public SearchPage(WebDriver driver) {
        super(driver);
        setPageURL("catalogsearch/result/");
    }

    public List<ProductCard> getProductCards() {
        return Collections.unmodifiableList(this.productCards);
    }
}
