package com.solvd.gui.util.componentselector;

import com.zebrunner.carina.utils.factory.DeviceType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ComponentForAny.class)
public @interface ComponentFor {
    public DeviceType.Type type();
}
