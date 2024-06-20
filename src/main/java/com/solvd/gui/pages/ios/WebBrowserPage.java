package com.solvd.gui.pages.ios;

import com.solvd.gui.pages.common.WebBrowserPageBase;
import com.solvd.gui.util.MobileContextUtils;
import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import java.util.Set;

@DeviceType(pageType = DeviceType.Type.IOS_PHONE, parentClass = WebBrowserPageBase.class)
public class WebBrowserPage extends WebBrowserPageBase {

    @FindBy(xpath = "//android.widget.ImageButton[@content-desc=\"Switch or close tabs\"]")
    private ExtendedWebElement switchTabs;

    @FindBy(xpath = "//android.widget.ImageView[@content-desc=\"New tab\"]")
    private ExtendedWebElement newTabButton;

    public WebBrowserPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void openUrlInNewTab(String url) {
        Set<String> windowHandlesBeforeNewTab = getDriver().getWindowHandles();

        // open new tab
        MobileContextUtils contextHelper = new MobileContextUtils();
        MobileContextUtils.View previousContext = contextHelper.getCurrentContext();
        contextHelper.switchMobileContext(MobileContextUtils.View.NATIVE);
        switchTabs.click();
        newTabButton.click();

        contextHelper.switchMobileContext(previousContext);

        // make driver use new tab
        Set<String> windowHandlesAfterNewTab = getDriver().getWindowHandles();
        String newWindowHandle = windowHandlesAfterNewTab.stream()
                .filter(str -> !windowHandlesBeforeNewTab.contains(str))
                .findFirst().get();
        getDriver().switchTo().window(newWindowHandle);

        //open url
        getDriver().get(url);
    }
}
