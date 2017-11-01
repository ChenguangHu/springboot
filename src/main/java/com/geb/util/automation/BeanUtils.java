package com.geb.util.automation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.Locale.ENGLISH;

public class BeanUtils {

    private final ConcurrentMap<Class, ConcurrentMap<String, Property>> cache = new ConcurrentHashMap<Class, ConcurrentMap<String, Property>>();

    private static class Property {

        private Class clazz;
        private String name;
        private Method read;
        private Method write;
        private Field field;

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + (this.clazz != null ? this.clazz.hashCode() : 0);
            hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Property other = (Property) obj;
            if (this.clazz != other.clazz && (this.clazz == null || !this.clazz.equals(other.clazz))) {
                return false;
            }
            return !((this.name == null) ? (other.name != null) : !this.name.equals(other.name));
        }

        public Method getRead() throws IllegalAccessException {
            if (read == null) {
                throw new IllegalAccessException(String.format("%s jiabo %s read ", clazz.getName(), name));
            }
            return read;
        }

        public Method getWrite() throws IllegalAccessException {
            if (write == null) {
                throw new IllegalAccessException(String.format("%s jiabo%s write jiabo", clazz.getName(), name));
            }
            return write;
        }
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    public Object getPropertyKey(Object bean, String property) throws IntrospectionException {
        if (bean == null) {
            throw new NullPointerException();
        }
        return _getProperty(bean, property, true);
    }

    public boolean isExistsProperty(Object bean, String property) throws IntrospectionException {
        return getPropertyKey(bean, property) != null;
    }

    private Field findField(String property, Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (property.equals(f.getName())) {
                return f;
            }
        }
        for (Field f : clazz.getFields()) {
            if (property.equals(f.getName())) {
                return f;
            }
        }
        return null;
    }

    private Property _getProperty(Object bean, String property, boolean maybeNull) throws IntrospectionException {
        if (bean == null) {
            throw new NullPointerException();
        }
        Class<? extends Object> clazz = bean.getClass();
        ConcurrentMap<String, Property> propertyMap = cache.get(clazz);
        if (propertyMap == null) {
            propertyMap = LangUitls.putIfAbsent(cache, clazz, new ConcurrentHashMap<String, Property>());
        }
        Property p = propertyMap.get(property);
        if (p == null) {
            BeanInfo info = Introspector.getBeanInfo(clazz, Introspector.IGNORE_ALL_BEANINFO);
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            for (PropertyDescriptor t : pds) {
                if (property.equals(t.getName())) {
                    p = new Property();
                    p.clazz = clazz;
                    p.name = t.getName();
                    p.read = t.getReadMethod();
                    p.write = t.getWriteMethod();
                    if (p.read != null && p.write != null) {
                        p.field = findField(property, clazz);
                        p = LangUitls.putIfAbsent(propertyMap, p.name, p);
                    } else {
                        p = null;
                    }
                    break;
                }
            }
            if (p == null && !maybeNull) {
                throw new RuntimeException(String.format("%s jiabo%s jiabo", clazz.getName(), property));
            }
        }
        return p;
    }

    public Type getGenericPropertyType(Object bean, String property) throws IntrospectionException, IllegalAccessException {
        Property p = _getProperty(bean, property, false);
        Method m = p.getRead();
        return m.getGenericReturnType();
    }

    public Class getPropertyType(Object bean, String property) throws IntrospectionException, IllegalAccessException {
        Property p = _getProperty(bean, property, false);
        Method m = p.getRead();
        return m.getReturnType();
    }

    public Class getNullablePropertyType(Object bean, String property) throws IntrospectionException, IllegalAccessException {
        Property p = _getProperty(bean, property, true);
        if (p == null) {
            return null;
        }
        Method m = p.getRead();
        return m.getReturnType();
    }

    public Object getProperty(Object bean, String property) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Property p = _getProperty(bean, property, false);
        Method m = p.getRead();
        return m.invoke(bean);
    }

    public void setProperty(Object bean, String property, Object value) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Property p = _getProperty(bean, property, false);
        Method m = p.getWrite();
        m.invoke(bean, value);
    }

    public <T extends Annotation> T getAnnotation(Object bean, String property, Class<T> annotationClass) throws IntrospectionException {
        Property p = _getProperty(bean, property, true);
        if (p == null) {
            return null;
        }
        T t;
        if (p.field != null) {
            t = p.field.getAnnotation(annotationClass);
            if (t != null) {
                return t;
            }
        }
        return p.read.getAnnotation(annotationClass);
    }
}
