package com.solvd.gui.util.componentselector;

import com.zebrunner.carina.utils.factory.DeviceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentFor {
    // TODO: replace with set
    public DeviceType.Type type();
}
