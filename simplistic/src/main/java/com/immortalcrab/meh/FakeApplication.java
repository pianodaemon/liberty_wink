package com.immortalcrab.meh;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class FakeApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(PlatformResource.class);
        return classes;
    }

}
