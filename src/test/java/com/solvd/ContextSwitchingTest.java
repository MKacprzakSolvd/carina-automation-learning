package com.solvd;

import com.solvd.enums.ProductCategory;
import com.solvd.gui.pages.common.*;
import com.solvd.gui.pages.common.components.ProductCardBase;
import com.solvd.gui.util.MobileContextUtils;
import com.solvd.gui.util.RandomPicker;
import com.solvd.model.Product;
import com.solvd.model.Review;
import com.zebrunner.carina.core.AbstractTest;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class ContextSwitchingTest extends AbstractTest {
    private static final Logger LOGGER = LogManager.getLogger(ContextSwitchingTest.class.getName());

    @Test
    public void verifyContextSwitchingTest() {
        HomePageBase homePage = initPage(getDriver(), HomePageBase.class);
        homePage.open();
        homePage.assertPageOpened();

        MobileContextUtils contextHelper = new MobileContextUtils();
        for (String context : contextHelper.getContexts()) {
            System.out.println(context);
        }
        contextHelper.switchMobileContext(MobileContextUtils.View.NATIVE);

//        getDriver().findElement(By.xpath("//*[@id='com.android.chrome:id/tab_switcher_button']")).click();
//        getDriver().findElement(By.xpath("//*[@id='tab_switcher_button']")).click();
        ExtendedWebElement element = new ExtendedWebElement(getDriver(), getDriver());
//        element.setBy(By.xpath("//*[contains(@id,'tab_switcher_button')]"));
        element.setBy(By.xpath("//android.widget.ImageButton[@content-desc=\"Switch or close tabs\"]"));
        element.click();
        SearchPageBase searchPage = homePage.searchForProduct("bag");
        //searchPage.assertPageOpened();
        for (var productCard : searchPage.getProductCards()) {
            LOGGER.info("Product name: " + productCard.getProductData().getName());
        }
    }


    @Test
    public void verifyAddingItemReviewInNewCardTest() {
        ProductsPageBase productsPage = initPage(getDriver(), ProductsPageBase.class, getDriver(), ProductCategory.GEAR_FITNESS_EQUIPMENT);
        productsPage.open();
        productsPage.assertPageOpened();

        // select random product
        ProductCardBase selectedProductCard = RandomPicker.getRandomElement(
                productsPage.getProductCards());
        Product selectedProduct = selectedProductCard.getProductData();
        String selectedProductDetailsLink = selectedProductCard.getLink();

        // open product details in new browser tab
        WebBrowserPageBase webBrowserPage = initPage(getDriver(), WebBrowserPageBase.class);
        webBrowserPage.openUrlInNewTab(selectedProductDetailsLink);

        // open product details page
        ProductDetailsPageBase productDetailsPage = initPage(getDriver(), ProductDetailsPageBase.class);
        assertTrue(productDetailsPage.isPageForElement(selectedProduct),
                "Opened details page is not for product '%s'.".formatted(selectedProduct.getName()));

        Review review = Review.builder()
                .rating(5)
                .userNickname("user")
                .summary("generally ok")
                .reviewContent("product seems to be good and solid while having reasonable price")
                .build();

        // add review
        productDetailsPage = productDetailsPage.addReview(review);
        //productDetailsPage.assertPageOpened();

        // check if review was added
        assertTrue(productDetailsPage.isReviewAddedSuccessfullyAlertShown(),
                "Failed to add review, or show alert that it was added");
    }
}
