package com.solvd.gui.util;

import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;

/**
 * hacky way to use waitForJSToLoad in ExtendedWebElement
 */
public class JSWaiter {
    private final JSWaiterInner jsWaiterInner;

    public JSWaiter(WebDriver driver) {
        jsWaiterInner = new JSWaiterInner(driver);
    }

    public void waitForJSToLoad() {
        this.jsWaiterInner.waitForJSToLoad();
    }

    /**
     * use nested classes instead of inheritance to avoid exposing all AbstractPage functions
     */
    private static class JSWaiterInner extends AbstractPage {
        public JSWaiterInner(WebDriver driver) {
            super(driver);
        }
    }
}
