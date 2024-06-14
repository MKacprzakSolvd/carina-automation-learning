package com.solvd.gui.pages.common;

import com.solvd.gui.pages.common.components.ShoppingCartBase;
import com.solvd.model.Product;
import com.solvd.model.Review;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.math.BigDecimal;
import java.util.List;

public abstract class ProductDetailsPageBase extends AbstractPage {
    @FindBy(xpath = "//*[contains(@class,'page-header')]//*[@data-block='minicart']")
    private ShoppingCartBase shoppingCart;

    @FindBy(xpath = "//*[@role='alert']/*/*")
    private List<ExtendedWebElement> alerts;

    @FindBy(xpath = "//*[contains(@class,'product-info-main')]//*[@itemprop='name']")
    private ExtendedWebElement productName;
    @FindBy(xpath = "//*[contains(@class,'product-info-main')]//*[contains(@class,'price-wrapper')]")
    private ExtendedWebElement productPrice;

    @FindBy(xpath = "//*[@id='product-options-wrapper']" +
            "//*[@attribute-code='size']//*[contains(@class,'swatch-option')]")
    private List<ExtendedWebElement> productSizes;
    @FindBy(xpath = "//*[@id='product-options-wrapper']" +
            "//*[@attribute-code='color']//*[contains(@class,'swatch-option')]")
    private List<ExtendedWebElement> productColors;

    @FindBy(css = ".product-add-form [type='submit']")
    private ExtendedWebElement addToCartButton;

    @FindBy(id = "tab-label-reviews")
    private ExtendedWebElement reviewsTab;
    @FindBy(id = "tab-label-reviews-title")
    private ExtendedWebElement reviewsTabTitle;

    @FindBy(xpath = "//*[contains(@class,'review-control-vote')]//*[contains(@class,'rating-')]")
    private List<ExtendedWebElement> reviewRatings;
    @FindBy(id = "nickname_field")
    private ExtendedWebElement reviewNickname;
    @FindBy(id = "summary_field")
    private ExtendedWebElement reviewSummary;
    @FindBy(id = "review_field")
    private ExtendedWebElement reviewReviewText;
    @FindBy(xpath = "//*[@id='review-form']//*[@type='submit']")
    private ExtendedWebElement reviewSubmitButton;


    public ProductDetailsPageBase(WebDriver driver) {
        super(driver);
    }


    public ShoppingCartBase getShoppingCart() {
        return this.shoppingCart;
    }

    public String getProductName() {
        return this.productName.getText();
    }

    public BigDecimal getProductPrice() {
        return new BigDecimal(
                this.productPrice.getAttribute("data-price-amount"));
    }

    public boolean isForElement(Product product) {
        // TODO improve (move comparison to Product class)
        return product.getName().equals(getProductName())
                // warning: you cannot replace compareTo with equals
                //          because for equals scale have to be the same
                && product.getPrice().compareTo(getProductPrice()) == 0;
    }

    // TODO add option to select color and size
    public void addToCart() {
        // select first color and size
        this.productSizes.getFirst().click();
        this.productColors.getFirst().click();
        this.addToCartButton.click();
    }

    public ProductDetailsPageBase addReview(Review review) {
        // open ratings tab
        this.reviewsTabTitle.click();

        // fill in the form
        // click on star corresponding to selected rating
        ExtendedWebElement selectedRating = this.reviewRatings.get(review.getRating() - 1);
        // FIXME: find a better alternative - currently it doesn't simulate user clicking on it
        selectedRating.clickByJs();

        this.reviewNickname.type(review.getUserNickname());
        this.reviewSummary.type(review.getSummary());
        this.reviewReviewText.type(review.getReviewContent());

        return submitReview();
    }

    /**
     * submits review
     * this process differs on desktop and mobile,
     * so this method is overridden for mobile page
     *
     * @return new details page (submitting refreshes page)
     */
    protected ProductDetailsPageBase submitReview() {
        this.reviewSubmitButton.click();

        return initPage(getDriver(), ProductDetailsPageBase.class);
    }

    /**
     * informs whether alert that review was added successfully is shown
     */
    public boolean isReviewAddedSuccessfullyAlertShown() {
        waitForJSToLoad();
        for (ExtendedWebElement message : this.alerts) {
            if (message.getText().equals("You submitted your review for moderation.")) {
                return true;
            }
        }
        return false;
    }

    // add goToDetailsPage to ProductCard class
    // goToCheckout()
    // boolean addReview()
}