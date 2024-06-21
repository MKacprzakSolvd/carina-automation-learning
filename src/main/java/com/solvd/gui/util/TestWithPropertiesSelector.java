package com.solvd.gui.util;

import com.zebrunner.carina.core.AbstractTest;
import com.zebrunner.carina.webdriver.core.capability.CapabilitiesLoader;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;

import java.util.Locale;
import java.util.Optional;

public abstract class TestWithPropertiesSelector extends AbstractTest {
    // TODO find better name for this parameter
    public final String PARAMETER_NAME = "testPropertiesType";

    @BeforeTest
    public void loadAppropriatePropertiesFile(ITestContext context) {
        CapabilitiesLoader capabilitiesLoader = new CapabilitiesLoader();
        Optional<String> testPropertiesType = getTestPropertiesType(context);
        System.out.printf("DETECTED PROPERTIES: %s%n", testPropertiesType.orElse("NOT FOUND"));
        testPropertiesType.ifPresent(
                propType -> capabilitiesLoader.loadCapabilities("_config-%s.properties".formatted(propType))
        );
    }

    private Optional<String> getTestPropertiesType(ITestContext context) {
        return Optional.ofNullable(context.getCurrentXmlTest().getParameter(PARAMETER_NAME))
                .map(str -> str.toLowerCase(Locale.ENGLISH));
    }
}
