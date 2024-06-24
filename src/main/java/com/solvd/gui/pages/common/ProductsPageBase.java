package com.solvd.gui.pages.common;

import com.solvd.enums.ProductCategory;
import com.solvd.enums.ProductsFilter;
import com.solvd.enums.SortOrder;
import com.solvd.gui.pages.common.components.ProductCardBase;
import com.solvd.gui.pages.common.components.ProductFilterBase;
import com.solvd.gui.pages.common.components.ShoppingCartBase;
import com.solvd.gui.util.componentselector.AbstractComponentSelectingPage;
import com.solvd.gui.util.componentselector.AutoSelectComponent;
import com.solvd.model.Product;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class ProductsPageBase extends AbstractComponentSelectingPage {
    @Getter
    private String relativeUrl;

    @FindBy(css = ".products .product-items .product-item")
    private List<ProductCardBase> productCards;

    // this element is used as load marker, and > selector is used in order to
    // distinguish between search and products page
    @FindBy(css = ".main > .products.wrapper")
    private ExtendedWebElement productsWrapper;

    // TODO: extract string 'Size' and 'Color' from this and move it somewhere else (as constant)?
    // select filter block that have 'Size' in title
    @FindBy(xpath = "//*[@id='layered-filter-block']"
            + "//*[contains(@class,'filter-options-item')]"
            + "[.//*[contains(@class,'filter-options-title')][text()='Size']]")
    @AutoSelectComponent
    private ProductFilterBase sizeFilter;

    // select filter block that have 'Color' in title
    @FindBy(xpath = "//*[@id='layered-filter-block']"
            + "//*[contains(@class,'filter-options-item')]"
            + "[.//*[contains(@class,'filter-options-title')][text()='Color']]")
    @AutoSelectComponent
    private ProductFilterBase colorFilter;

    @Getter
    @FindBy(xpath = "//*[contains(@class,'page-header')]//*[@data-block='minicart']")
    private ShoppingCartBase shoppingCart;

    // there are two elements with id 'sorter', so this locator is required
    @FindBy(css = "#authenticationPopup + .toolbar-products #sorter")
    private ExtendedWebElement sortTypeSelector;
    @FindBy(css = "#authenticationPopup + .toolbar-products [data-role='direction-switcher']")
    private ExtendedWebElement sortDirectionSelector;


    public ProductsPageBase(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(this.productsWrapper);
    }

    public ProductsPageBase(WebDriver driver, String relativeUrl) {
        this(driver);
        if (relativeUrl == null) {
            throw new IllegalArgumentException("relativeUrl cannot be null");
        }
        this.relativeUrl = relativeUrl;
        setPageURL(this.relativeUrl);
    }


    public ProductsPageBase(WebDriver driver, ProductCategory productCategory) {
        this(driver,
                // hacky way to check for null pointer
                Optional.ofNullable(
                        productCategory.getRelativeUrl()
                ).orElseThrow(() -> new IllegalArgumentException("productCategory cannot be null")));
    }


    protected ProductFilterBase getFilter(ProductsFilter productsFilter) {
        return switch (productsFilter) {
            case COLOR -> this.colorFilter;
            case SIZE -> this.sizeFilter;
            default -> throw new IllegalArgumentException("Unknown enum value: " + productsFilter.name());
        };
    }

    public List<ProductCardBase> getProductCards() {
        return Collections.unmodifiableList(this.productCards);
    }

    /**
     * finds product card corresponding to passed product
     */
    public Optional<ProductCardBase> findProductCard(Product product) {
        for (ProductCardBase productCard : this.productCards) {
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

    // FIXME: add support for case where filter is used (and thus inaccessible)
    public List<String> getFilterOptions(ProductsFilter productsFilter) {
        return getFilter(productsFilter).getOptions();
    }

    // FIXME: add support for case where filter is used (and thus inaccessible)
    public ProductsPageBase filterBy(ProductsFilter productsFilter, String option) {
        return getFilter(productsFilter).filterBy(option, this.relativeUrl);
    }

    public SortOrder getSortOrder() {
        return SortOrder.valueOf(
                this.sortTypeSelector.getSelectedValue(),
                // here desc indicates that clicking will change direction to descending
                // so when data-value == desc, the order is ascending
                this.sortDirectionSelector.getAttribute("data-value").equals("desc")
        );
    }

    public ProductsPageBase setSortOrder(SortOrder sortOrder) {
        // TODO: figure out how to select sort order by value instead of by selected text
        //       (to get value use getAttribute("value"), but I don't know how to select
        //       from selection by value with Carina
        ProductsPageBase productsPage = this;
        SortOrder currentSortOrder = getSortOrder();

        // select correct sort type
        if (!currentSortOrder.getValue().equals(sortOrder.getValue())) {
            this.sortTypeSelector.select(sortOrder.getValue());
            productsPage = initPage(getDriver(), ProductsPageBase.class, this.relativeUrl);
        }

        // select correct sort direction
        if (currentSortOrder.isAscending() != sortOrder.isAscending()) {
            this.sortDirectionSelector.click();
            productsPage = initPage(getDriver(), ProductsPageBase.class, this.relativeUrl);
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
