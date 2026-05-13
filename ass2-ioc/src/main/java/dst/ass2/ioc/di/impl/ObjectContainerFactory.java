package dst.ass2.ioc.di.impl;

import dst.ass2.ioc.di.IObjectContainer;
import dst.ass2.ioc.di.IObjectContainerFactory;
import dst.ass2.ioc.di.InjectionException;
import dst.ass2.ioc.di.InvalidDeclarationException;
import dst.ass2.ioc.di.ObjectCreationException;
import dst.ass2.ioc.di.TypeConversionException;
import dst.ass2.ioc.di.annotation.Component;
import dst.ass2.ioc.di.annotation.Initialize;
import dst.ass2.ioc.di.annotation.Inject;
import dst.ass2.ioc.di.annotation.Property;
import dst.ass2.ioc.di.annotation.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ObjectContainerFactory implements IObjectContainerFactory {

    @Override
    public IObjectContainer newObjectContainer(Properties properties) {
        return new ObjectContainer(properties);
    }

//    private static final class ObjectContainer implements IObjectContainer {
//
//        private final Properties properties;
//        private final ConcurrentMap<Class<?>, Object> singletons = new ConcurrentHashMap<>();
//
//        private ObjectContainer(Properties properties) {
//            this.properties = properties;
//        }
//
//        @Override
//        public Properties getProperties() {
//            return properties;
//        }
//
//        @Override
//        public <T> T getObject(Class<T> type) throws InjectionException {
//            validateComponent(type);
//
//            Component component = type.getAnnotation(Component.class);
//            if (component.scope() == Scope.PROTOTYPE) {
//                return createObject(type, snapshotProperties());
//            }
//
//            Object singleton = singletons.get(type);
//            if (singleton == null) {
//                synchronized (singletons) {
//                    singleton = singletons.get(type);
//                    if (singleton == null) {
//                        singleton = createObject(type, snapshotProperties());
//                        singletons.put(type, singleton);
//                    }
//                }
//            }
//            return type.cast(singleton);
//        }
//
//        private <T> T getObject(Class<T> type, Properties propertySnapshot) throws InjectionException {
//            validateComponent(type);
//
//            Component component = type.getAnnotation(Component.class);
//            if (component.scope() == Scope.PROTOTYPE) {
//                return createObject(type, propertySnapshot);
//            }
//
//            Object singleton = singletons.get(type);
//            if (singleton == null) {
//                synchronized (singletons) {
//                    singleton = singletons.get(type);
//                    if (singleton == null) {
//                        singleton = createObject(type, propertySnapshot);
//                        singletons.put(type, singleton);
//                    }
//                }
//            }
//            return type.cast(singleton);
//        }
//
//        private Properties snapshotProperties() {
//            Properties snapshot = new Properties();
//            synchronized (properties) {
//                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
//                    snapshot.put(entry.getKey(), entry.getValue());
//                }
//            }
//            return snapshot;
//        }
//
//        private <T> T createObject(Class<T> type, Properties propertySnapshot) {
//            validateInstantiable(type);
//
//            T instance = instantiate(type);
//            injectFields(instance, propertySnapshot);
//            initialize(instance);
//            return instance;
//        }
//
//        private void validateComponent(Class<?> type) {
//            if (type == null || !type.isAnnotationPresent(Component.class)) {
//                throw new InvalidDeclarationException("Type is not annotated with @Component: " + type);
//            }
//        }
//
//        private void validateInstantiable(Class<?> type) {
//            int modifiers = type.getModifiers();
//            if (type.isInterface() || Modifier.isAbstract(modifiers)) {
//                throw new ObjectCreationException("Component type cannot be instantiated: " + type.getName());
//            }
//        }
//
//        private <T> T instantiate(Class<T> type) {
//            try {
//                Constructor<T> constructor = type.getDeclaredConstructor();
//                constructor.setAccessible(true);
//                return constructor.newInstance();
//            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
//                throw new ObjectCreationException("Could not instantiate component: " + type.getName(), e);
//            } catch (InvocationTargetException e) {
//                throw new ObjectCreationException("Constructor failed for component: " + type.getName(), e.getCause());
//            }
//        }
//
//        private void injectFields(Object instance, Properties propertySnapshot) {
//            for (Field field : allFields(instance.getClass())) {
//                if (field.isAnnotationPresent(Property.class)) {
//                    injectProperty(instance, field, field.getAnnotation(Property.class), propertySnapshot);
//                }
//                if (field.isAnnotationPresent(Inject.class)) {
//                    injectDependency(instance, field, field.getAnnotation(Inject.class), propertySnapshot);
//                }
//            }
//        }
//
//        private void injectDependency(Object instance, Field field, Inject inject, Properties propertySnapshot) {
//            try {
//                Class<?> targetType = inject.targetType() == Void.class ? field.getType() : inject.targetType();
//                if (!field.getType().isAssignableFrom(targetType)) {
//                    throw new InvalidDeclarationException(
//                        "Target type " + targetType.getName() + " is not assignable to " + field.getType().getName()
//                    );
//                }
//
//                Object dependency = getObject(targetType, propertySnapshot);
//                field.setAccessible(true);
//                field.set(instance, dependency);
//            } catch (InjectionException e) {
//                if (!inject.optional()) {
//                    throw e;
//                }
//            } catch (IllegalAccessException e) {
//                if (!inject.optional()) {
//                    throw new InjectionException("Could not inject field: " + field.getName(), e);
//                }
//            }
//        }
//
//        private void injectProperty(Object instance, Field field, Property property, Properties propertySnapshot) {
//            String value = propertySnapshot.getProperty(property.value());
//            if (value == null) {
//                throw new ObjectCreationException("Missing property: " + property.value());
//            }
//
//            Object convertedValue = convertPropertyValue(value, field.getType(), property.value());
//            try {
//                field.setAccessible(true);
//                field.set(instance, convertedValue);
//            } catch (IllegalAccessException e) {
//                throw new InjectionException("Could not inject property field: " + field.getName(), e);
//            }
//        }
//
//        private Object convertPropertyValue(String value, Class<?> targetType, String key) {
//            try {
//                if (targetType == String.class) {
//                    return value;
//                } else if (targetType == Byte.class || targetType == byte.class) {
//                    return Byte.valueOf(value);
//                } else if (targetType == Short.class || targetType == short.class) {
//                    return Short.valueOf(value);
//                } else if (targetType == Integer.class || targetType == int.class) {
//                    return Integer.valueOf(value);
//                } else if (targetType == Long.class || targetType == long.class) {
//                    return Long.valueOf(value);
//                } else if (targetType == Float.class || targetType == float.class) {
//                    return Float.valueOf(value);
//                } else if (targetType == Double.class || targetType == double.class) {
//                    return Double.valueOf(value);
//                } else if (targetType == Boolean.class || targetType == boolean.class) {
//                    return Boolean.valueOf(value);
//                } else if (targetType == Character.class || targetType == char.class) {
//                    if (value.length() != 1) {
//                        throw new IllegalArgumentException("Expected a single character");
//                    }
//                    return value.charAt(0);
//                }
//            } catch (NumberFormatException e) {
//                throw new TypeConversionException("Could not convert property " + key + " to " + targetType.getName(), e);
//            } catch (IllegalArgumentException e) {
//                throw new TypeConversionException("Could not convert property " + key + " to " + targetType.getName(), e);
//            }
//
//            throw new TypeConversionException("Unsupported property type for " + key + ": " + targetType.getName());
//        }
//
//        private void initialize(Object instance) {
//            List<Method> initializeMethods = initializeMethods(instance.getClass());
//            for (Method method : initializeMethods) {
//                if (method.getParameterCount() != 0) {
//                    throw new InvalidDeclarationException("@Initialize method must not have parameters: " + method.getName());
//                }
//            }
//
//            for (Method method : initializeMethods) {
//                try {
//                    method.setAccessible(true);
//                    method.invoke(instance);
//                } catch (IllegalAccessException | IllegalArgumentException e) {
//                    throw new ObjectCreationException("Could not invoke initialize method: " + method.getName(), e);
//                } catch (InvocationTargetException e) {
//                    throw new ObjectCreationException("Initialize method failed: " + method.getName(), e.getCause());
//                }
//            }
//        }
//
//        private List<Method> initializeMethods(Class<?> type) {
//            Map<String, Method> selectedMethods = new LinkedHashMap<>();
//            List<Class<?>> hierarchy = hierarchy(type);
//            Collections.reverse(hierarchy);
//
//            for (Class<?> current : hierarchy) {
//                for (Method method : current.getDeclaredMethods()) {
//                    if (method.isAnnotationPresent(Initialize.class)) {
//                        selectedMethods.putIfAbsent(methodSignature(method), method);
//                    }
//                }
//            }
//
//            List<Method> methods = new ArrayList<>(selectedMethods.values());
//            methods.sort(Comparator.comparingInt(method -> hierarchy.indexOf(method.getDeclaringClass())));
//            return methods;
//        }
//
//        private String methodSignature(Method method) {
//            return method.getName() + Arrays.toString(method.getParameterTypes());
//        }
//
//        private List<Field> allFields(Class<?> type) {
//            List<Class<?>> hierarchy = hierarchy(type);
//            List<Field> fields = new ArrayList<>();
//            for (Class<?> current : hierarchy) {
//                Collections.addAll(fields, current.getDeclaredFields());
//            }
//            return fields;
//        }
//
//        private List<Method> allMethods(Class<?> type) {
//            List<Class<?>> hierarchy = hierarchy(type);
//            List<Method> methods = new ArrayList<>();
//            for (Class<?> current : hierarchy) {
//                Collections.addAll(methods, current.getDeclaredMethods());
//            }
//            return methods;
//        }
//
//        private List<Class<?>> hierarchy(Class<?> type) {
//            List<Class<?>> hierarchy = new ArrayList<>();
//            Class<?> current = type;
//            while (current != null && current != Object.class) {
//                hierarchy.add(current);
//                current = current.getSuperclass();
//            }
//            Collections.reverse(hierarchy);
//            return hierarchy;
//        }
//    }
}
