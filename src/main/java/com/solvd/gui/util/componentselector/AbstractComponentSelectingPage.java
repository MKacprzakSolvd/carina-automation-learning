package com.solvd.gui.util.componentselector;

import com.zebrunner.carina.utils.factory.DeviceType;
import com.zebrunner.carina.webdriver.decorator.ExtendedWebElement;
import com.zebrunner.carina.webdriver.gui.AbstractPage;
import com.zebrunner.carina.webdriver.locator.ExtendedElementLocator;
import com.zebrunner.carina.webdriver.locator.ExtendedElementLocatorFactory;
import com.zebrunner.carina.webdriver.locator.internal.LocatingListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.*;
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
                try {
                    if (List.class.isAssignableFrom(field.getType())) {
                        // support list of elements
                        updateCollectionComponent(field);
                    } else if (ExtendedWebElement.class.isAssignableFrom(field.getType())) {
                        // support just singular elements (not lists)
                        updateSingularComponent(field);
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Checks if field needs to be updated, if so, it does so
     *
     * @param listField field of type List<\?>
     */
    private void updateCollectionComponent(Field listField) throws IllegalAccessException {
        Class<?> listElementClass = (Class<?>) getListType(listField);
        Class<?> matchingComponentClass = findMatchingComponentSubclass(listElementClass);
        // overwrite field only if matching class differs from its current list elements class
        if (matchingComponentClass != null && listElementClass != null
                && !listElementClass.equals(matchingComponentClass)) {
            ClassLoader loader = this.getClass().getClassLoader();
            ExtendedElementLocatorFactory locatorFactory = new ExtendedElementLocatorFactory(getDriver(), getDriver());
            ExtendedElementLocator locator = (ExtendedElementLocator) locatorFactory.createLocator(listField);

            Object newValue = Proxy.newProxyInstance(loader, new Class[]{List.class},
                    new LocatingListHandler(locator, listField, matchingComponentClass));
            listField.setAccessible(true);
            listField.set(this, newValue);
        }
    }

    /**
     * Checks if field needs to be updated, if so, it does so
     *
     * @param field field of type ? extends ExtendedWebElement
     */
    private void updateSingularComponent(Field field)
            throws NoSuchMethodException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        field.setAccessible(true);
        Class<?> matchingComponentClass = findMatchingComponentSubclass(field.getType());
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
    }

    /**
     * Tries to find component subclass suitable for current device
     *
     * @return Returns null when if it fails to find suitable subclass
     * It cannot return passed class, only subclass or null
     */
    private Class<?> findMatchingComponentSubclass(Class<?> componentClass) {
        for (Class<?> clazz : REFLECTIONS.getSubTypesOf(componentClass)) {
            if (isComponentSubclassMatching(clazz)) {
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
    private boolean isComponentSubclassMatching(Class<?> clazz) {
        ComponentFor[] componentForAnnotations = clazz.getAnnotationsByType(ComponentFor.class);
        if (this.getClass().isAnnotationPresent(DeviceType.class)
                && componentForAnnotations.length > 0) {
            // get all device types supported by component of type clazz
            List<DeviceType.Type> componentDeviceTypes = Arrays.stream(componentForAnnotations)
                    .map(ComponentFor::value)
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


    /**
     * Copied from Carina ExtendedFieldDecorator
     */
    private Type getListType(Field field) {
        // Type erasure in Java isn't complete. Attempt to discover the generic
        // type of the list.
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return null;
        }
        return ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }
}
