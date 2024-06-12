package com.solvd.gui.pages.desktop;

import com.solvd.enums.ProductCategory;
import com.solvd.gui.pages.common.ProductsPageBase;
import com.solvd.gui.pages.common.components.ProductFilterBase;
import com.solvd.gui.util.componentselector.AutoSelectComponent;
import com.zebrunner.carina.utils.factory.DeviceType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

@DeviceType(pageType = DeviceType.Type.DESKTOP, parentClass = ProductsPageBase.class)
public class ProductsPage extends ProductsPageBase {
    @FindBy(xpath = SIZE_FILTER_XPATH)
    private ProductFilterBase sizeFilter;
    @FindBy(xpath = COLOR_FILTER_XPATH)
    private ProductFilterBase colorFilter;
    @FindBy(xpath = COLOR_FILTER_XPATH)
    @AutoSelectComponent
    private ProductFilterBase androidColorFilter;

    public ProductsPage(WebDriver driver, ProductCategory productCategory) {
        super(driver, productCategory);
    }

    @Override
    protected ProductFilterBase getSizeFilter() {
        return this.sizeFilter;
    }

    @Override
    protected ProductFilterBase getColorFilter() {
        return this.colorFilter;
    }
}
