package xyz.femdev.femutils.java.module;

import xyz.femdev.femutils.java.module.annotations.FInject;
import xyz.femdev.femutils.java.module.annotations.FSingleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflection-based injector using @FInject and @FSingleton.
 */
public final class DefaultInjector implements Injector {
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public <T> T getInstance(Class<T> type) throws ModuleInstantiationException {
        try {
            if (type.isAnnotationPresent(FSingleton.class)) {
                //noinspection unchecked
                return (T) singletons.computeIfAbsent(type, this::createNewInstanceUnchecked);
            }
            return createNewInstance(type);
        } catch (ModuleInstantiationException e) {
            throw e;
        } catch (Exception e) {
            throw new ModuleInstantiationException(
                    "Failed to create instance of " + type.getName(), e
            );
        }
    }

    private <T> T createNewInstanceUnchecked(Class<?> clazz) {
        try {
            //noinspection unchecked
            return createNewInstance((Class<T>) clazz);
        } catch (ModuleInstantiationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T createNewInstance(Class<T> type) throws Exception {
        for (Constructor<?> ctor : type.getDeclaredConstructors()) {
            if (ctor.isAnnotationPresent(FInject.class)) {
                ctor.setAccessible(true);
                Object[] params = resolveParameters(ctor.getParameterTypes());
                //noinspection unchecked
                return (T) ctor.newInstance(params);
            }
        }
        Constructor<T> noArg = type.getDeclaredConstructor();
        noArg.setAccessible(true);
        T instance = noArg.newInstance();

        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(FInject.class)) {
                field.setAccessible(true);
                Object dependency = getInstance(field.getType());
                field.set(instance, dependency);
            }
        }
        return instance;
    }

    private Object[] resolveParameters(Class<?>[] paramTypes) {
        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = getInstance(paramTypes[i]);
        }
        return params;
    }
}
