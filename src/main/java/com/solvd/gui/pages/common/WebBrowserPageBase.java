package com.solvd.gui.pages.common;

import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.openqa.selenium.WebDriver;

public abstract class WebBrowserPageBase extends AbstractPage {
    public WebBrowserPageBase(WebDriver driver) {
        super(driver);
    }

    public abstract void openUrlInNewTab(String url);
}
