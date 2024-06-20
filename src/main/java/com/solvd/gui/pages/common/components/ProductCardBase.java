package com.solvd.gui.pages.common.components;

import com.solvd.gui.pages.common.ProductDetailsPageBase;
import com.solvd.model.Product;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.math.BigDecimal;
import java.util.List;

public class ProductCardBase extends AbstractUIObject {
    @FindBy(xpath = ".//*[contains(@class,'product-item-name')]//a[contains(@class,'product-item-link')]")
    private ExtendedWebElement productName;
    @FindBy(className = "price")
    private ExtendedWebElement price;
    @FindBy(className = "product-image-photo")
    private ExtendedWebElement image;
    @FindBy(xpath = ".//a[contains(@class,'product-item-photo')]")
    private ExtendedWebElement link;

    @FindBy(xpath = ".//*[@attribute-code='size']//*[@option-label]")
    private List<ExtendedWebElement> avaliableSizes;
    @FindBy(xpath = ".//*[@attribute-code='color']//*[@option-label]")
    private List<ExtendedWebElement> avaliableColors;

    @FindBy(css = ".product-item-details .tocart")
    private ExtendedWebElement addToCartButton;

    public ProductCardBase(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    // FIXME: add available colors and sizes to product data
    public Product getProductData() {
        return Product.builder()
                .name(getProductName())
                .price(getPrice())
                .build();
    }

    public String getProductName() {
        return this.productName.getText();
    }

    public BigDecimal getPrice() {
        return new BigDecimal(extractPrice(this.price.getText()));
    }

    public String getLink() {
        return this.link.getAttribute("href");
    }

    public List<String> getAvaliableSizes() {
        return this.avaliableSizes.stream()
                .map(webElement -> webElement.getAttribute("option-label"))
                .toList();
    }

    public boolean isAvailableInSize(String size) {
        return getAvaliableSizes().contains(size);
    }

    public List<String> getAvaliableColors() {
        return this.avaliableColors.stream()
                .map(webElement -> webElement.getAttribute("option-label"))
                .toList();
    }

    public boolean isAvailableInColor(String color) {
        return getAvaliableColors().contains(color);
    }

    // TODO: add option to specify size and color
    public void addToCart() {
        // hover over product cart to show add to cart button
        // FIXME it needs hover in desktop to avoid clicking on a banner (check is scrollTo solved problem)
        this.addToCartButton.scrollTo();
        // select first size and color (if colors available)
        if (!this.avaliableSizes.isEmpty()) {
            this.avaliableSizes.getFirst().click();
        }
        if (!this.avaliableColors.isEmpty()) {
            this.avaliableColors.getFirst().click();
        }
        // click it
        this.addToCartButton.click();
    }

    public ProductDetailsPageBase goToProductDetailsPage() {
        // center on the image before clicking to avoid image being covered
        // by the ad banner in the lower right part of the screen
        this.image.scrollTo();
        this.image.click();
        return initPage(getDriver(), ProductDetailsPageBase.class);
    }

    /**
     * extract price from string containing price
     */
    private static String extractPrice(String priceString) {
        // TODO find better way of extracting price - this might fail for different currency
        //      tip: you can extract price from data-price-amount property (of price wrapper)
        // split string with price by $, and return last part
        String[] parts = priceString.split("\\$");
        return parts[parts.length - 1];
    }
}
