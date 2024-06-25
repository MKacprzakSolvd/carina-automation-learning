package com.solvd.gui.pages.common.components;

import com.solvd.gui.pages.common.CheckoutPageStepOneBase;
import com.solvd.gui.util.JSWaiter;
import com.solvd.model.Product;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class ShoppingCartBase extends AbstractUIObject {
    // class (added to cartCounterWrapper)  indicating that cart content is updating
    private static final String CART_UPDATE_INDICATING_CLASS = "_block-content-loading";

    @FindBy(css = ".showcart .counter")
    private ExtendedWebElement cartCounterWrapper;

    // counter displayed on the cart icon, always visible
    @FindBy(css = ".showcart .counter-number")
    private ExtendedWebElement cartCounter;

    @FindBy(className = "showcart")
    private ExtendedWebElement openButton;

    @FindBy(id = "ui-id-1")
    private ExtendedWebElement contentWrapper;

    @FindBy(css = ".subtotal .price")
    private ExtendedWebElement itemsPriceIndicator;

    @FindBy(id = "top-cart-btn-checkout")
    private ExtendedWebElement toCheckoutButton;

    @FindBy(css = ".product-item-details")
    private List<ShoppingCartProduct> shoppingCartProducts;

    // confirmation button from modal. It it outside of shopping cart root element
    private By removeProductConfirmationButton = By.cssSelector(".modals-wrapper .action-accept");

    public ShoppingCartBase(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
    }

    public boolean isOpened() {
        return this.contentWrapper.isDisplayed();
    }

    public void open() {
        if (!isOpened()) {
            this.openButton.click();
        }
    }

    public boolean isEmpty() {
        waitTillCartUpdates();
        return !this.cartCounter.isDisplayed();
    }

    public int getProductsCount() {
        waitTillCartUpdates();
        if (isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(this.cartCounter.getText());
        }
    }

    public boolean isProductInCart(Product product) {
        waitTillCartUpdates();
        open();
        if (isEmpty()) {
            return false;
        }
        // TODO: implement Optional<ProductCartCard> findProductCard() method
        //       and use it here and in remove from cart (and find better name for it)
        for (ShoppingCartProduct shoppingCartProduct : this.shoppingCartProducts) {
            if (shoppingCartProduct.getProductName().equals(product.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to find product from shopping cart by name
     */
    public Optional<Product> findProductByName(String productName) {
        open();
        return this.shoppingCartProducts.stream()
                .filter(shoppingCartProduct -> shoppingCartProduct.getProductName().equals(productName))
                .map(ShoppingCartProduct::toProduct)
                .findAny();
    }

    /**
     * Tries to find product from shopping cart by name, which it gets from passed product
     * Same as calling findProductByName(product.getName())
     */
    public Optional<Product> findProductByName(Product product) {
        return findProductByName(product.getName());
    }

    /**
     * tries to remove product  from cart
     * return true on successful removal,
     * false on fail (i.e. product not in cart)
     */
    public boolean removeFromCart(Product product) {
        // TODO: rewrite with findProductCard when implemented
        if (!isProductInCart(product)) {
            return false;
        }
        // find product by name and remove for cart
        this.shoppingCartProducts.stream()
                .filter(shoppingCartProduct -> shoppingCartProduct.getProductName().equals(product.getName()))
                .findAny()
                .get()
                .clickRemove();

        // Confirm removal in modal
        WebElement removalConfirmationButton = getDriver().findElement(this.removeProductConfirmationButton);
        waitUntil(visibilityOf(removalConfirmationButton), 10);
        removalConfirmationButton.click();
        waitTillProductRemovedFromCart(product);
        return true;
    }

    public void removeAllFromCart() {
        while (!this.shoppingCartProducts.isEmpty()) {
            String productToRemove = this.shoppingCartProducts.getFirst().getProductName();
            removeFromCart(Product.builder().name(productToRemove).build());
        }
    }

    // TODO: make this function return CheckoutPage

    /**
     * Proceeds to checkout page. Cart must have some items, otherwise
     * illegal state exception will be thrown
     */
    public CheckoutPageStepOneBase goToCheckout() {
        if (isEmpty()) {
            throw new IllegalStateException("Shopping cart must have at least one product to go to checkout.");
        }

        open();
        this.toCheckoutButton.click();
        return initPage(getDriver(), CheckoutPageStepOneBase.class);
    }

    protected void waitTillCartUpdates() {
        waitUntil(not(attributeContains(this.cartCounterWrapper, "class", CART_UPDATE_INDICATING_CLASS)), 10);

        JSWaiter jsWaiter = new JSWaiter(getDriver());
        jsWaiter.waitForJSToLoad();
    }

    protected void waitTillProductRemovedFromCart(Product product) {
        // TODO: check whether nested waits are a problem
        waitUntil((driver) -> !isProductInCart(product), 10);
    }


    public static class ShoppingCartProduct extends AbstractUIObject {
        @FindBy(css = ".product-item-name a")
        private ExtendedWebElement productName;

        @FindBy(css = ".price-excluding-tax .price")
        private ExtendedWebElement productPrice;

        @FindBy(css = ".delete")
        private ExtendedWebElement productDeleteButton;

        public ShoppingCartProduct(WebDriver driver) {
            super(driver);
        }

        public ShoppingCartProduct(WebDriver driver, SearchContext searchContext) {
            super(driver, searchContext);
        }

        public String getProductName() {
            return this.productName.getText();
        }

        public BigDecimal getProductPrice() {
            return new BigDecimal(this.productPrice.getText().substring(1));
        }

        public void clickRemove() {
            this.productDeleteButton.click();
        }

        public Product toProduct() {
            return Product.builder()
                    .name(getProductName())
                    .price(getProductPrice())
                    .build();
        }
    }
}

