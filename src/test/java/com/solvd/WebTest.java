package com.solvd;


import com.solvd.enums.ProductCategory;
import com.solvd.enums.ProductsFilter;
import com.solvd.enums.SortOrder;
import com.solvd.gui.pages.common.*;
import com.solvd.gui.pages.common.components.ProductCardBase;
import com.solvd.gui.util.RandomPicker;
import com.solvd.gui.util.ShippingInfoProvider;
import com.solvd.gui.util.TestWithPropertiesSelector;
import com.solvd.model.Product;
import com.solvd.model.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class WebTest extends TestWithPropertiesSelector {
    // TODO: check if I should add volatile to logger
    private static final Logger LOGGER = LogManager.getLogger(WebTest.class.getName());


    /**
     * Product Search
     * <p>
     * Steps:
     * 1. Open homepage
     * Result: Home page should load
     * 2. Insert "bag" into search field and click search
     * Result: Search page should open, containing non-empty list of items
     */
    @Test
    public void verifyProductSearchTest() {
        // TODO ! add asserts
        // TODO: move url's to separate file
        HomePageBase homePage = initPage(getDriver(), HomePageBase.class);
        homePage.open();
        homePage.assertPageOpened();

        SearchPageBase searchPage = homePage.searchForProduct("bag");
        searchPage.assertPageOpened();
        Assert.assertFalse(searchPage.getProductCards().isEmpty(), "Search for product returned no results.");

        SoftAssert softAssert = new SoftAssert();

        for (var productCard : searchPage.getProductCards()) {
            String productName = productCard.getProductData().getName();
            LOGGER.info("Product name: " + productName);
            softAssert.assertFalse(productName.isBlank(), "Product name is blank.");
        }
    }


    /**
     * Filtering by size and color
     * <p>
     * Steps:
     * 1. Filter products by random size
     * Result: All showed products should be available in the selected size.
     * 2. Filter products by random color
     * Result: All showed products should be available in the selected color and size.
     */
    @Test
    public void verifySizeColorFiltersTest() {
        // open products page
        ProductsPageBase productsPage = initPage(getDriver(), ProductsPageBase.class, ProductCategory.WOMEN_TOPS);
        productsPage.open();
        productsPage.assertPageOpened();

        // filter by random size
        String randomSizeOption = RandomPicker.getRandomElement(
                productsPage.getFilterOptions(ProductsFilter.SIZE)
        );
        productsPage = productsPage.filterBy(ProductsFilter.SIZE, randomSizeOption);
        productsPage.assertPageOpened();

        // make sure every element is available in given size
        // FIXME: check it on all pages
        SoftAssert softAssert = new SoftAssert();
        for (ProductCardBase productCard : productsPage.getProductCards()) {
            softAssert.assertTrue(productCard.isAvailableInSize(randomSizeOption),
                    "Product: %s is not available in size '%s'".formatted(productCard.getProductName(), randomSizeOption));
        }

        // filter by random color
        String randomColorOption = RandomPicker.getRandomElement(
                productsPage.getFilterOptions(ProductsFilter.COLOR)
        );
        productsPage = productsPage.filterBy(ProductsFilter.COLOR, randomColorOption);
        productsPage.assertPageOpened();

        // make sure every element is avaliable in given color and size
        // FIXME: check it on all pages
        // TODO: test for case when no elements found with selected filters
        for (ProductCardBase productCard : productsPage.getProductCards()) {
            softAssert.assertTrue(productCard.isAvailableInSize(randomSizeOption),
                    "Product: %s is not available in size '%s;".formatted(productCard.getProductName(), randomSizeOption));
            softAssert.assertTrue(productCard.isAvailableInColor(randomColorOption),
                    "Product: %s is not available in color '%s'".formatted(productCard.getProductName(), randomColorOption));
        }

        softAssert.assertAll();
    }

    /**
     * Adding and removing items from shopping cart
     * <p>
     * Preconditions:
     * 1. Open https://magento.softwaretestingboard.com/men/tops-men.html
     * <p>
     * Steps:
     * 1. Add two random items to the shopping cart, selecting first available size and color
     * Result: There should appear information that the product was added to the shopping card after each addition
     * 2. Click on the shopping cart icon
     * Result: Shopping cart popup should appear, showing two added products
     * 3. Remove each item from shopping cart
     * Result: Shopping cart should be empty
     */
    @Test(invocationCount = 5)
    public void verifyAddRemoveFromShoppingCartTest() {
        final int PRODUCTS_TO_ADD_TO_CART_NUMBER = 2;
        SoftAssert softAssert = new SoftAssert();

        // open products page
        ProductsPageBase productsPage = initPage(getDriver(), ProductsPageBase.class, ProductCategory.MEN_TOPS);
        productsPage.open();
        productsPage.assertPageOpened();

        // clear shopping cart
        productsPage.getShoppingCart().removeAllFromCart();

        // select two random products
        List<Product> selectedProducts = RandomPicker.getRandomElements(
                productsPage.getProducts(), PRODUCTS_TO_ADD_TO_CART_NUMBER);

        // add them to cart & check if they were added
        for (Product product : selectedProducts) {
            Optional<ProductCardBase> productCard = productsPage.findProductCard(product);
            softAssert.assertTrue(productCard.isPresent(),
                    "Unable to find product card corresponding to product '%s' in products page"
                            .formatted(product.getName()));
            productCard.get().addToCart();
        }
        int itemsInShoppingCart = productsPage.getShoppingCart().getProductsCount();
        softAssert.assertEquals(itemsInShoppingCart, PRODUCTS_TO_ADD_TO_CART_NUMBER,
                "Number of products in shopping cart (%d) doesn't match expected number (%d)."
                        .formatted(itemsInShoppingCart, PRODUCTS_TO_ADD_TO_CART_NUMBER));
        for (Product product : selectedProducts) {
            softAssert.assertTrue(
                    productsPage.getShoppingCart().isProductInCart(product),
                    "Product '%s' was not in the shopping cart".formatted(product.getName()));
        }

        // remove products from card & check if they were removed
        for (Product product : selectedProducts) {
            softAssert.assertTrue(productsPage.getShoppingCart().removeFromCart(product),
                    "Failed to remove product '%s' from shopping cart.".formatted(product.getName()));
        }

        softAssert.assertTrue(productsPage.getShoppingCart().isEmpty(),
                "Shopping cart is not empty.");

        softAssert.assertAll();
    }


    /**
     * Checkout process from products page
     * <p>
     * Preconditions:
     * 1. Open https://magento.softwaretestingboard.com/gear/bags.html
     * <p>
     * Steps:
     * 1. Add random product to card
     * Result: There should appear information that the product was added to the shopping card
     * 2. Open shopping cart popup
     * Result: Shopping cart popup should show, containing one selected item
     * 3. Click proceed to checkout
     * Result: Checkout page should load
     * 4. Expand Order Summary
     * Result: Order summary should contain one selected item
     * 5. Fill in valid Shipping data , select Fixed shipping method and click Next
     * Result: Order Review page should load, containing inserted data and ordered product, with correctly calculated order price
     * 6. Click place order
     * Result: Order Success page should load
     * 7. Click continue shopping
     * Result: Home page should load
     */
    @Test
    public void verifyCheckoutProcessFromProductsPageTest() {
        // open products page
        ProductsPageBase productsPage = initPage(getDriver(), ProductsPageBase.class, ProductCategory.GEAR_BAGS);
        productsPage.open();
        productsPage.assertPageOpened();

        // clear shopping cart
        productsPage.getShoppingCart().removeAllFromCart();

        // select random item, add it to the cart
        ProductCardBase selectedProductCard = RandomPicker.getRandomElement(productsPage.getProductCards());
        Product selectedProduct = selectedProductCard.getProductData();
        selectedProductCard.addToCart();
        // TODO: check if cart contains exactly one product
        assertTrue(productsPage.getShoppingCart().isProductInCart(selectedProduct),
                "Selected product (%s) was not in the cart (on products page)."
                        .formatted(selectedProduct.getName()));

        // go to checkout
        CheckoutPageStepOneBase checkoutPageStepOne = productsPage.getShoppingCart().goToCheckout();
        checkoutPageStepOne.assertPageOpened();

        // complete first step of checkout
        int productsInShoppingCart = checkoutPageStepOne.getProductsCount();
        assertEquals(productsInShoppingCart, 1,
                "%d products in shopping car, while expecting only one, during the first step of checkout."
                        .formatted(productsInShoppingCart));
        assertTrue(checkoutPageStepOne.isProductInCart(selectedProduct),
                "The selected product ('%s') is not in the shopping cart, during the first step of checkout."
                        .formatted(selectedProduct.getName()));

        // TODO read this data from some config
        CheckoutPageStepTwoBase checkoutPageStepTwo = checkoutPageStepOne.goToNextStep(
                ShippingInfoProvider.provideValidShippingInfo());
        checkoutPageStepTwo.assertPageOpened();

        CheckoutPageStepThreeBase checkoutPageStepThree = checkoutPageStepTwo.placeOrder();
        checkoutPageStepThree.assertPageOpened();

        HomePageBase homePage = checkoutPageStepThree.returnToHomePage();
        homePage.assertPageOpened();
    }

    /**
     * Item sorting
     * <p>
     * Preconditions:
     * 1. Open https://magento.softwaretestingboard.com/women/bottoms-women.html
     * <p>
     * Steps:
     * 1. Sort items by product name in ascending order
     * Result: Items should be sorted by product name in ascending order
     * 2. Sort items by product name in descending order
     * Result: Items should be sorted by product name in descending order
     * 3. Sort items by price in ascending order
     * Result: Items should be sorted by price in ascending order
     * 4. Sort items by price in descending order
     * Result: Items should be sorted by price in descending order
     * 5. Go to next page
     * Result: item's prices should continue to descend
     */
    @Test
    public void verifyItemSortingTest() {
        SoftAssert softAssert = new SoftAssert();

        ProductsPageBase productsPage = initPage(getDriver(), ProductsPageBase.class, ProductCategory.WOMEN_BOTTOMS);
        productsPage.open();
        productsPage.assertPageOpened();

        SortOrder[] sortOrdersToCheck = new SortOrder[]{
                SortOrder.BY_NAME_A_TO_Z,
                SortOrder.BY_NAME_Z_TO_A,
                SortOrder.BY_PRICE_ASCENDING,
                SortOrder.BY_PRICE_DESCENDING};

        for (SortOrder sortOrder : sortOrdersToCheck) {
            productsPage = productsPage.setSortOrder(sortOrder);
            softAssert.assertTrue(productsPage.isSortedBy(sortOrder),
                    "Products sorted incorrectly according to sort order '%s'"
                            .formatted(sortOrder));
        }
        // TODO: go to the next page and check sorting (step 5)

        /*
            sorting by price (ascending and descending)
            will fail, page sorts incorrectly (usually last
            items are in the wrong order)
         */
        softAssert.assertAll();
    }


    /**
     * Checkout process from item's details page
     * <p>
     * Preconditions:
     * 1. Open https://magento.softwaretestingboard.com/men/bottoms-men.html
     * <p>
     * Steps:
     * 1. Select random item, and go to its details page
     * Result: Details page of selected item should load, with item details (title, price) matching selected item
     * 2. Select random size, color and quantity (between 1 and 100) and add item to cart
     * Result: There should appear information that the product was added to the shopping card
     * 3. Click on the shopping cart
     * Result: Shopping cart popup should appear, containing one selected item in the chosen color, size and quantity
     * 4. Click proceed to checkout
     * Result: Checkout page should load
     * 5. Expand Order Summary
     * Result: Order summary should contain one selected item in the chosen color, size and quantity
     * 6. Fill in valid Shipping data , select Fixed shipping method and click Next
     * Result: Order Review page should load, containing inserted data and ordered product, with correctly calculated order price
     * 7. Click place order
     * Result: Order Success page should load
     * 8. Click continue shopping
     * Result: Home page should load
     */
    @Test
    public void verifyCheckoutFromItemDetailsPageTest() {
        ProductsPageBase productsPage = initPage(getDriver(), ProductsPageBase.class, ProductCategory.MEN_BOTTOMS);
        productsPage.open();
        productsPage.assertPageOpened();

        // clear shopping card
        productsPage.getShoppingCart().removeAllFromCart();

        // select random product
        ProductCardBase selectedProductCard = RandomPicker.getRandomElement(
                productsPage.getProductCards());
        Product selectedProduct = selectedProductCard.getProductData();

        // open product details page
        ProductDetailsPageBase productDetailsPage = selectedProductCard.goToProductDetailsPage();
        assertTrue(productDetailsPage.isPageForElement(selectedProduct),
                "Opened details page is not for product '%s'.".formatted(selectedProduct.getName()));

        // add product to cart
        productDetailsPage.addToCart();
        // TODO: check if cart contains exactly one product
        assertTrue(productsPage.getShoppingCart().isProductInCart(selectedProduct),
                "Selected product (%s) was not in the cart (on product details page)."
                        .formatted(selectedProduct.getName()));

        // go to checkout
        CheckoutPageStepOneBase checkoutPageStepOne = productsPage.getShoppingCart().goToCheckout();
        checkoutPageStepOne.assertPageOpened();

        // complete first step of checkout
        int productsInShoppingCart = checkoutPageStepOne.getProductsCount();
        assertEquals(productsInShoppingCart, 1,
                "%d products in shopping car, while expecting only one, during the first step of checkout."
                        .formatted(productsInShoppingCart));
        assertTrue(checkoutPageStepOne.isProductInCart(selectedProduct),
                "The selected product ('%s') is not in the shopping cart, during the first step of checkout."
                        .formatted(selectedProduct.getName()));

        // TODO read this data from some config
        CheckoutPageStepTwoBase checkoutPageStepTwo = checkoutPageStepOne.goToNextStep(
                ShippingInfoProvider.provideValidShippingInfo());
        checkoutPageStepTwo.assertPageOpened();

        CheckoutPageStepThreeBase checkoutPageStepThree = checkoutPageStepTwo.placeOrder();
        checkoutPageStepThree.assertPageOpened();

        HomePageBase homePage = checkoutPageStepThree.returnToHomePage();
        homePage.assertPageOpened();
    }


    /**
     * Adding item review
     * <p>
     * Preconditions:
     * 1. Open https://magento.softwaretestingboard.com/gear/fitness-equipment.html
     * <p>
     * Steps:
     * 1. Select random item, and go to its details page
     * Result: Details page of selected item should load, with item details (title, price) matching selected item
     * 2. Click on the reviews tab
     * Result: Reviews tab should open with number of reviews, and its average matching product card
     * 3. Fill in review form with valid data and click submit
     * Result: Information should appear that review was successfully submitted to moderation
     */
    @Test
    public void verifyAddingItemReviewTest() {
        ProductsPageBase productsPage = initPage(getDriver(), ProductsPageBase.class, ProductCategory.GEAR_FITNESS_EQUIPMENT);
        productsPage.open();
        productsPage.assertPageOpened();

        // select random product
        ProductCardBase selectedProductCard = RandomPicker.getRandomElement(
                productsPage.getProductCards());
        Product selectedProduct = selectedProductCard.getProductData();

        // open product details page
        ProductDetailsPageBase productDetailsPage = selectedProductCard.goToProductDetailsPage();
        assertTrue(productDetailsPage.isPageForElement(selectedProduct));

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
