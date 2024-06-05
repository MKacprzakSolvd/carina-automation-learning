package com.solvd;

import com.solvd.components.ProductCard;
import com.solvd.enums.ProductCategory;
import com.solvd.enums.ProductsFilter;
import com.solvd.enums.ShippingMethod;
import com.solvd.enums.SortOrder;
import com.solvd.model.Product;
import com.solvd.model.Review;
import com.solvd.model.ShippingInfo;
import com.solvd.pages.*;
import com.solvd.util.RandomPicker;
import com.zebrunner.carina.core.AbstractTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Optional;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class WebTest extends AbstractTest {
    // TODO: check if I should add volatile to logger
    private static final Logger LOGGER = LogManager.getLogger(WebTest.class.getName());

    @DataProvider()
    public Object[][] provideValidShippingInfo() {
        return new Object[][]{
                {
                        ShippingInfo.builder()
                                .email("a@b.com")
                                .firstName("John")
                                .lastName("Smith")
                                .company("Postal Inc.")
                                .addressLine1("ul. Wielopole 2")
                                .city("Kraków")
                                .province("małopolskie")
                                .postalCode("12-345")
                                .country("Poland")
                                .phoneNumber("123456789")
                                .shippingMethod(ShippingMethod.FIXED)
                                .build()
                }
        };
    }


    /**
     * Verify Product Search
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
        HomePage homePage = new HomePage(getDriver());
        homePage.open();
        homePage.assertPageOpened();
        SearchPage searchPage = homePage.searchForProduct("bag");
        for (var productCard : searchPage.getProductCards()) {
            LOGGER.info("Product name: " + productCard.getProductData().getName());
        }
    }


    @Test
    // TODO: add logging
    // TODO: add test case description (steps, etc)
    public void verifySizeColorFiltersTest() {
        // open products page
        ProductsPage productsPage = new ProductsPage(getDriver(), ProductCategory.WOMEN_TOPS);
        productsPage.open();
        productsPage.assertPageOpened();

        // filter by random size
        String randomSizeOption = RandomPicker.getRandomElement(
                productsPage.getFilterOptions(ProductsFilter.SIZE)
        );
        productsPage = productsPage.filterBy(ProductsFilter.SIZE, randomSizeOption);

        // make sure every element is available in given size
        // FIXME: check it on all pages
        SoftAssert softAssert = new SoftAssert();
        for (ProductCard productCard : productsPage.getProductCards()) {
            softAssert.assertTrue(productCard.isAvailableInSize(randomSizeOption),
                    "Product: %s is not available in size '%s'".formatted(productCard.getProductName(), randomSizeOption));
        }

        // filter by random color
        String randomColorOption = RandomPicker.getRandomElement(
                productsPage.getFilterOptions(ProductsFilter.COLOR)
        );
        productsPage = productsPage.filterBy(ProductsFilter.COLOR, randomColorOption);

        // make sure every element is avaliable in given color and size
        // FIXME: check it on all pages
        // TODO: test for case when no elements found with selected filters
        for (ProductCard productCard : productsPage.getProductCards()) {
            softAssert.assertTrue(productCard.isAvailableInSize(randomSizeOption),
                    "Product: %s is not available in size '%s;".formatted(productCard.getProductName(), randomSizeOption));
            softAssert.assertTrue(productCard.isAvailableInColor(randomColorOption),
                    "Product: %s is not available in color '%s'".formatted(productCard.getProductName(), randomColorOption));
        }

        softAssert.assertAll();
    }

    @Test
    public void verifyAddRemoveFromShoppingCartTest() {
        final int PRODUCTS_TO_ADD_TO_CART_NUMBER = 2;
        SoftAssert softAssert = new SoftAssert();

        // open products page
        ProductsPage productsPage = new ProductsPage(getDriver(), ProductCategory.MEN_TOPS);
        productsPage.open();
        productsPage.assertPageOpened();

        // select two random products
        List<Product> selectedProducts = RandomPicker.getRandomElements(
                productsPage.getProducts(), PRODUCTS_TO_ADD_TO_CART_NUMBER);

        // add them to cart & check if they were added
        for (Product product : selectedProducts) {
            Optional<ProductCard> productCard = productsPage.findProductCard(product);
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


    @Test(dataProvider = "provideValidShippingInfo")
    public void verifyCheckoutProcessFromProductsPageTest(ShippingInfo shippingInfo) {
        // open products page
        ProductsPage productsPage = new ProductsPage(getDriver(), ProductCategory.GEAR_BAGS);
        productsPage.open();
        productsPage.assertPageOpened();

        // select random item, add it to the cart and go to checkout
        ProductCard selectedProductCard = RandomPicker.getRandomElement(productsPage.getProductCards());
        Product selectedProduct = selectedProductCard.getProductData();
        selectedProductCard.addToCart();
        // TODO: check if cart contains exactly one product
        assertTrue(productsPage.getShoppingCart().isProductInCart(selectedProduct),
                "Selected product (%s) was not in the cart (on products page)."
                        .formatted(selectedProduct.getName()));
        CheckoutPageStepOne checkoutPageStepOne = productsPage.getShoppingCart().goToCheckout();

        // complete first step of checkout
        int productsInShoppingCart = checkoutPageStepOne.getProductsCount();
        assertEquals(productsInShoppingCart, 1,
                "%d products in shopping car, while expecting only one, during the first step of checkout."
                        .formatted(productsInShoppingCart));
        assertTrue(checkoutPageStepOne.isProductInCart(selectedProduct),
                "The selected product ('%s') is not in the shopping cart, during the first step of checkout."
                        .formatted(selectedProduct.getName()));

        // TODO read this data from some config
        CheckoutPageStepTwo checkoutPageStepTwo = checkoutPageStepOne.goToNextStep(shippingInfo);

        CheckoutPageStepThree checkoutPageStepThree = checkoutPageStepTwo.placeOrder();
        HomePage homePage = checkoutPageStepThree.returnToHomePage();
    }

    @Test
    public void verifyItemSortingTest() {
        SoftAssert softAssert = new SoftAssert();

        ProductsPage productsPage = new ProductsPage(getDriver(), ProductCategory.WOMEN_BOTTOMS);
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


    @Test(dataProvider = "provideValidShippingInfo")
    public void verifyCheckoutFromItemDetailsPageTest(ShippingInfo shippingInfo) {
        ProductsPage productsPage = new ProductsPage(getDriver(), ProductCategory.MEN_BOTTOMS);
        productsPage.open();
        productsPage.assertPageOpened();

        // select random product
        ProductCard selectedProductCard = RandomPicker.getRandomElement(
                productsPage.getProductCards());
        Product selectedProduct = selectedProductCard.getProductData();

        // open product details page
        ProductDetailsPage productDetailsPage = selectedProductCard.goToProductDetailsPage();
        assertTrue(productDetailsPage.isForElement(selectedProduct));

        // add product to cart
        productDetailsPage.addToCart();
        // TODO: check if cart contains exactly one product
        assertTrue(productsPage.getShoppingCart().isProductInCart(selectedProduct),
                "Selected product (%s) was not in the cart (on product details page)."
                        .formatted(selectedProduct.getName()));

        // go to checkout
        CheckoutPageStepOne checkoutPageStepOne = productsPage.getShoppingCart().goToCheckout();

        // complete first step of checkout
        int productsInShoppingCart = checkoutPageStepOne.getProductsCount();
        assertEquals(productsInShoppingCart, 1,
                "%d products in shopping car, while expecting only one, during the first step of checkout."
                        .formatted(productsInShoppingCart));
        assertTrue(checkoutPageStepOne.isProductInCart(selectedProduct),
                "The selected product ('%s') is not in the shopping cart, during the first step of checkout."
                        .formatted(selectedProduct.getName()));

        // TODO read this data from some config
        CheckoutPageStepTwo checkoutPageStepTwo = checkoutPageStepOne.goToNextStep(shippingInfo);

        CheckoutPageStepThree checkoutPageStepThree = checkoutPageStepTwo.placeOrder();
        HomePage homePage = checkoutPageStepThree.returnToHomePage();
    }


    @Test
    public void verifyAddingItemReviewTest() {
        ProductsPage productsPage = new ProductsPage(getDriver(), ProductCategory.GEAR_FITNESS_EQUIPMENT);
        productsPage.open();
        productsPage.assertPageOpened();

        // select random product
        ProductCard selectedProductCard = RandomPicker.getRandomElement(
                productsPage.getProductCards());
        Product selectedProduct = selectedProductCard.getProductData();

        // open product details page
        ProductDetailsPage productDetailsPage = selectedProductCard.goToProductDetailsPage();
        assertTrue(productDetailsPage.isForElement(selectedProduct));

        Review review = Review.builder()
                .rating(5)
                .userNickname("user")
                .summary("generally ok")
                .reviewContent("product seems to be good and solid while having reasonable price")
                .build();

        // add review
        productDetailsPage = productDetailsPage.addReview(review);

        // check if review was added
        assertTrue(productDetailsPage.isReviewAddedSuccessfullyAlertShown(),
                "Failed to add review, or show alert that it was added");
    }
}
