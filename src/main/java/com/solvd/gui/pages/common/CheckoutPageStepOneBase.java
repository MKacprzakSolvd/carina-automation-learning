package com.solvd.gui.pages.common;

import com.solvd.model.Product;
import com.solvd.model.ShippingInfo;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public abstract class CheckoutPageStepOneBase extends AbstractPage {
    @FindBy(id = "customer-email")
    private ExtendedWebElement emailField;

    // name fields
    @FindBy(xpath = "//*[@name='shippingAddress.firstname']//*[@name='firstname']")
    private ExtendedWebElement firstNameField;
    @FindBy(xpath = "//*[@name='shippingAddress.lastname']//*[@name='lastname']")
    private ExtendedWebElement lastNameField;

    @FindBy(xpath = "//*[@name='shippingAddress.company']//*[@name='company']")
    private ExtendedWebElement companyField;

    // address fields
    @FindBy(xpath = "//*[@name='shippingAddress.street.0']//*[@name='street[0]']")
    private ExtendedWebElement addressLine1Field;
    @FindBy(xpath = "//*[@name='shippingAddress.street.1']//*[@name='street[1]']")
    private ExtendedWebElement addressLine2Field;
    @FindBy(xpath = "//*[@name='shippingAddress.street.2']//*[@name='street[2]']")
    private ExtendedWebElement addressLine3Field;

    @FindBy(xpath = "//*[@name='shippingAddress.city']//*[@name='city']")
    private ExtendedWebElement cityField;

    // state/province is either dropdown or field, depending on country
    @FindBy(xpath = "//*[@name='shippingAddress.region_id']//*[@name='region_id']")
    private ExtendedWebElement provinceDropdown;
    @FindBy(xpath = "//*[@name='shippingAddress.region']//*[@name='region']")
    private ExtendedWebElement provinceField;

    @FindBy(xpath = "//*[@name='shippingAddress.postcode']//*[@name='postcode']")
    private ExtendedWebElement postalCodeField;

    @FindBy(xpath = "//*[@name='shippingAddress.country_id']//*[@name='country_id']")
    private ExtendedWebElement countryDropdown;

    @FindBy(xpath = "//*[@name='shippingAddress.telephone']//*[@name='telephone']")
    private ExtendedWebElement phoneNumberField;

    // shipping rates
    @FindBy(xpath = "//*[@id='checkout-shipping-method-load']//*[@value='flatrate_flatrate']")
    private ExtendedWebElement fixedRateShippingMethodRadio;
    @FindBy(xpath = "//*[@id='checkout-shipping-method-load']//*[@value='tablerate_bestway']")
    private ExtendedWebElement tableRateShippingMethodRadio;

    @FindBy(xpath = "//*[@id='shipping-method-buttons-container']//*[@type='submit']")
    private ExtendedWebElement goToNextStepButton;

    @FindBy(css = "#opc-sidebar .title[data-role='title']")
    private ExtendedWebElement toggleProductListButton;

    @FindBy(css = "#opc-sidebar .items-in-cart [role='heading'] > span:first-child")
    protected ExtendedWebElement productsInCartCount;

    @FindBy(css = "#opc-sidebar .minicart-items-wrapper")
    private ExtendedWebElement productsWrapper;

    // TODO replace with custom element
    @FindBy(css = "#opc-sidebar .product-item-name")
    private List<ExtendedWebElement> productNames;

    public CheckoutPageStepOneBase(WebDriver driver) {
        super(driver);
        setPageURL("checkout/#shipping");
        waitForJSToLoad(30);
    }

    // TODO create class for address with builder and just pass it in
    public CheckoutPageStepTwoBase goToNextStep(ShippingInfo shippingInfo) {
        this.emailField.type(shippingInfo.getEmail());
        this.firstNameField.type(shippingInfo.getFirstName());
        this.lastNameField.type(shippingInfo.getLastName());
        this.companyField.type(shippingInfo.getCompany());
        this.addressLine1Field.type(shippingInfo.getAddressLine1());
        this.addressLine2Field.type(shippingInfo.getAddressLine2());
        this.addressLine3Field.type(shippingInfo.getAddressLine3());
        this.cityField.type(shippingInfo.getCity());
        this.postalCodeField.type(shippingInfo.getPostalCode());
        this.phoneNumberField.type(shippingInfo.getPhoneNumber());

        // select country
        // TODO change this to select by value instead of selected text
        this.countryDropdown.select(shippingInfo.getCountry());

        // select province
        // FIXME add support for case when you need to insert province
        //       instead selecting from dropdown (when provinceField is visible)
        //       this depends on selected country
        // TODO maybe change this to select province by value??
        this.provinceDropdown.select(shippingInfo.getProvince());

        // FIXME add checking whether shipping method is available
        switch (shippingInfo.getShippingMethod()) {
            case FIXED -> this.fixedRateShippingMethodRadio.click();
            case TABLE_RATE -> this.tableRateShippingMethodRadio.click();
        }

        // go to next step
        this.goToNextStepButton.click();

        return initPage(getDriver(), CheckoutPageStepTwoBase.class);
    }

    public int getProductsCount() {
        // TODO check if it will work without waitUntil on mobile
        //      this wait shouldn't do anything - it's worse than carina build-in checks
        //waitUntil(visibilityOf(this.productsInCartCount), 10);
        return Integer.parseInt(this.productsInCartCount.getText());
    }

    public boolean isProductInCart(Product product) {
        // TODO implement comparison based on all available data (price, color, etc)
        openProductsList();
        waitForJSToLoad(30);
        return productNames.stream()
                .map(webElement -> webElement.getText())
                .anyMatch(title -> title.equals(product.getName()));
    }

    private boolean isProductsListOpened() {
        return this.productsWrapper.isDisplayed();
    }

    private void openProductsList() {
        waitForJSToLoad(30);
        if (!isProductsListOpened()) {
            // TODO make this better, maybe just create element checkout loader
            waitUntil(not(presenceOfElementLocated(By.id("checkout-loader"))), 10);
            this.toggleProductListButton.click();
        }
    }
}
