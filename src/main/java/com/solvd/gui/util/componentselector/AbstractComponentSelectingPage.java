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


// FIXME: figure out how to avoid double initialization of components (first it creates base class,
//       then it overrides it with appropriate children class) (maybe create @SelectingFindBy annotation)
public abstract class AbstractComponentSelectingPage extends AbstractPage {
    private static final Logger LOGGER = LogManager.getLogger(AbstractComponentSelectingPage.class.getName());

    public AbstractComponentSelectingPage(WebDriver driver) {
        super(driver);
        updateComponents();
    }

    /**
     * updates components to appropriate children class
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
            // TODO: go over each class in hierarchy
            if (field.isAnnotationPresent(AutoSelectComponent.class)) {
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

    private boolean isComponentClassMatching(Class<?> clazz) {
        if (this.getClass().isAnnotationPresent(DeviceType.class)
                && clazz.isAnnotationPresent(ComponentFor.class)) {
            DeviceType.Type componentDeviceType = clazz.getAnnotation(ComponentFor.class).type();
            DeviceType.Type pageDeviceType = this.getClass().getAnnotation(DeviceType.class).pageType();
            return componentDeviceType == pageDeviceType
                    && componentDeviceType != null;
        }
        return false;
    }

    protected void initNewExtendedWebElement(ExtendedWebElement elementToInit, ExtendedWebElement oldElement) {
        elementToInit.setBy(oldElement.getBy());
        elementToInit.setName(oldElement.getName());
    }
}
