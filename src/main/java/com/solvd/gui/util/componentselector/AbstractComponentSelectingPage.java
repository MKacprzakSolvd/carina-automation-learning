package com.solvd.gui.util.componentselector;

import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;


// FIXME: figure out how to avoid double initialization of components (first it creates base class,
//       then it overrides it with appropriate children class) (maybe create @SelectingFindBy annotation)
public abstract class AbstractComponentSelectingPage extends AbstractPage {
    private static final Logger LOGGER = LogManager.getLogger(AbstractComponentSelectingPage.class.getName());

    public AbstractComponentSelectingPage(WebDriver driver) {
        super(driver);
        updateComponents();
    }

    /**
     * updates components from this class and all parent classes
     */
    private void updateComponents() {
        Class<?> classInHierarchy = this.getClass();
        while (classInHierarchy != null) {
            updateComponentsForClass(classInHierarchy);
            classInHierarchy = classInHierarchy.getSuperclass();
        }
    }

    /**
     * update objects components that reside in specified class
     */
    private void updateComponentsForClass(Class<?> clazz) {
        // TODO: add logging
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(AutoSelectComponent.class)) {
                // TODO add support for collections
                try {
                    field.setAccessible(true);
                    Class<?> matchingComponentClass = findMatchingComponentClass(field);
                    // overwrite field only if matching class differs from its current class
                    if (matchingComponentClass != null && !field.getType().equals(matchingComponentClass)) {
                        // get constructor
                        // TODO: use constructorUtils from Apache Commons
                        //       like here: com/zebrunner/carina/webdriver/decorator/ExtendedWebElement.java::clone
                        Constructor<?> constructor = matchingComponentClass.getDeclaredConstructor(WebDriver.class, SearchContext.class);
                        ExtendedWebElement oldValue = (ExtendedWebElement) field.get(this);
                        ExtendedWebElement newValue = (ExtendedWebElement) constructor.newInstance(oldValue.getDriver(), oldValue.getSearchContext());
                        initNewExtendedWebElement(newValue, oldValue);
                        // assign new value
                        field.set(this, newValue);
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Class<?> findMatchingComponentClass(Field field) {
        for (Class<?> clazz : REFLECTIONS.getSubTypesOf(field.getType())) {
            if (isComponentClassMatching(clazz)) {
                return clazz;
            }
        }
        return null;
    }

    /**
     * Checks if specific component subtype is suitable for this page.
     * I.e. if I pass class of ShoppingCart component for android, and this object is
     * an instance of page for android, then this method will return true.
     *
     * @param clazz class (or subclass) of some component on this page
     */
    private boolean isComponentClassMatching(Class<?> clazz) {
        ComponentFor[] componentForAnnotations = clazz.getAnnotationsByType(ComponentFor.class);
        if (this.getClass().isAnnotationPresent(DeviceType.class)
                && componentForAnnotations.length > 0) {
            // get all device types supported by component of type clazz
            List<DeviceType.Type> componentDeviceTypes = Arrays.stream(componentForAnnotations)
                    .map(ComponentFor::type)
                    .toList();
            // get device type this page is designed for
            DeviceType.Type pageDeviceType = this.getClass().getAnnotation(DeviceType.class).pageType();
            return componentDeviceTypes.contains(pageDeviceType);
        }
        return false;
    }

    protected void initNewExtendedWebElement(ExtendedWebElement elementToInit, ExtendedWebElement oldElement) {
        elementToInit.setBy(oldElement.getBy());
        elementToInit.setName(oldElement.getName());
    }
}
