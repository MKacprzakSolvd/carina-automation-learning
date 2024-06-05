package com.solvd.pages;

import com.solvd.components.ProductCard;
import com.solvd.components.ProductFilter;
import com.solvd.components.ShoppingCart;
import com.solvd.enums.ProductCategory;
import com.solvd.enums.ProductsFilter;
import com.solvd.enums.SortOrder;
import com.solvd.model.Product;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.*;

public class ProductsPage extends AbstractPage {
    ProductCategory productCategory;

    @FindBy(css = ".products .product-items .product-item")
    private List<ProductCard> productCards;

    @FindBy(xpath = "//*[contains(@class,'page-header')]//*[@data-block='minicart']")
    private ShoppingCart shoppingCart;

    // TODO: extract string 'Size' and 'Color' from this and move it somewhere else (as constant)?
    // select filter block that have 'Size' in title
    @FindBy(xpath = "//*[@id='layered-filter-block']" +
            "//*[contains(@class,'filter-options-item')]" +
            "[.//*[contains(@class,'filter-options-title')][text()='Size']]")
    private ProductFilter sizeFilter;

    // select filter block that have 'Color' in title
    @FindBy(xpath = "//*[@id='layered-filter-block']" +
            "//*[contains(@class,'filter-options-item')]" +
            "[.//*[contains(@class,'filter-options-title')][text()='Color']]")
    private ProductFilter colorFilter;

    // maps from filters enum to filter object
    private Map<ProductsFilter, ProductFilter> filtersMap = new EnumMap<>(ProductsFilter.class);

    // there are two elements with id 'sorter', so this locator is required
    @FindBy(css = "#authenticationPopup + .toolbar-products #sorter")
    private ExtendedWebElement sortTypeSelector;
    @FindBy(css = "#authenticationPopup + .toolbar-products [data-role='direction-switcher']")
    private ExtendedWebElement sortDirectionSelector;


    public ProductsPage(WebDriver driver, ProductCategory productCategory) {
        super(driver);
        this.productCategory = productCategory;
        setPageURL(productCategory.getRelativeUrl());

        // TODO: find a better solution for this mapping, currently it's easy
        //       to forget about adding a new mapping when adding filter
        this.filtersMap.put(ProductsFilter.SIZE, this.sizeFilter);
        this.filtersMap.put(ProductsFilter.COLOR, this.colorFilter);
    }


    private ProductCategory getProductCategory() {
        return productCategory;
    }

    public List<ProductCard> getProductCards() {
        return Collections.unmodifiableList(this.productCards);
    }

    /**
     * finds product card corresponding to passed product
     */
    public Optional<ProductCard> findProductCard(Product product) {
        for (ProductCard productCard : this.productCards) {
            // TODO: create method representsProduct(Product) in ProductCard and use it here
            if (productCard.getProductName().equals(product.getName())) {
                return Optional.of(productCard);
            }
        }
        return Optional.empty();
    }

    public List<Product> getProducts() {
        return this.productCards.stream()
                .map(productCard -> productCard.getProductData())
                .toList();
    }

    public ShoppingCart getShoppingCart() {
        return this.shoppingCart;
    }

    // FIXME: add support for case where filter is used (and thus inaccessible)
    public List<String> getFilterOptions(ProductsFilter productsFilter) {
        return this.filtersMap.get(productsFilter).getOptions();
    }

    // FIXME: add support for case where filter is used (and thus inaccessible)
    public ProductsPage filterBy(ProductsFilter productsFilter, String option) {
        return this.filtersMap.get(productsFilter).filterBy(option, getProductCategory());
    }

    public SortOrder getSortOrder() {
        return SortOrder.valueOf(
                this.sortTypeSelector.getSelectedValue(),
                // here desc indicates that clicking will change direction to descending
                // so when data-value == desc, the order is ascending
                this.sortDirectionSelector.getAttribute("data-value").equals("desc")
        );
    }

    public ProductsPage setSortOrder(SortOrder sortOrder) {
        // TODO: figure out how to select sort order by value instead of by selected text
        //       (to get value use getAttribute("value"), but I don't know how to select
        //       from selection by value with Carina
        ProductsPage productsPage = this;
        SortOrder currentSortOrder = getSortOrder();

        // select correct sort type
        if (!currentSortOrder.getValue().equals(sortOrder.getValue())) {
            this.sortTypeSelector.select(sortOrder.getValue());
            productsPage = new ProductsPage(getDriver(), getProductCategory());
        }

        // select correct sort direction
        if (currentSortOrder.isAscending() != sortOrder.isAscending()) {
            this.sortDirectionSelector.click();
            productsPage = new ProductsPage(getDriver(), getProductCategory());
        }

        return productsPage;
    }

    // TODO improve this method (make it more readable, implement comparing inside Product)
    //      now it just compares base on price
    public boolean isSortedBy(SortOrder sortOrder) {
        List<Product> products = getProducts();
        List<Product> sortedProducts = products.stream()
                .sorted(sortOrder.getComparator())
                .toList();
        for (int i = 0; i < sortedProducts.size(); i++) {
            if (!sortedProducts.get(i).getName().equals(
                    products.get(i).getName())) {
                return false;
            }
        }
        return true;
    }

    // TODO: implement
    // isFilterApplied
    // getAppliedFilters
}
