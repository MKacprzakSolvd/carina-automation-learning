package com.solvd.gui.components;

import com.solvd.gui.pages.common.CheckoutPageStepOneBase;
import com.solvd.model.Product;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractUIObject;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

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

    // TODO: replace with custom element
    @FindBy(css = ".product-item-details .product-item-name a")
    private List<ExtendedWebElement> productNamesElements;
    @FindBy(css = ".product-item-details .delete")
    private List<ExtendedWebElement> productRemoveButtons;

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
        for (ExtendedWebElement productNameElement : this.productNamesElements) {
            if (productNameElement.getText().equals(product.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * tries to remove product  from cart
     * return true on succesfull removal,
     * false on fail (i.e. product not in cart)
     */
    public boolean removeFromCart(Product product) {
        // TODO: rewrite with findProductCard when implemented
        if (!isProductInCart(product)) {
            return false;
        }
        for (int i = 0; i < this.productNamesElements.size(); i++) {
            if (product.getName().equals(this.productNamesElements.get(i).getText())) {
                this.productRemoveButtons.get(i).click();
                break;
            }
        }

        // Confirm removal in modal
        WebElement removalConfirmationButton = getDriver().findElement(this.removeProductConfirmationButton);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(visibilityOf(removalConfirmationButton));
        removalConfirmationButton.click();
        waitTillProductRemovedFromCart(product);
        return true;
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
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until(not(attributeContains(this.cartCounterWrapper, "class", CART_UPDATE_INDICATING_CLASS)));
    }

    protected void waitTillProductRemovedFromCart(Product product) {
        // TODO: check whether nested waits are a problem
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        wait.until((driver) -> !isProductInCart(product));
    }
}

