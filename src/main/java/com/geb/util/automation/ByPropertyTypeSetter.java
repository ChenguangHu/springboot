package com.geb.util.automation;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections.keyvalue.MultiKey;

public abstract class ByPropertyTypeSetter implements ColumnSetter {

    private static final Pattern P_COLUMN = Pattern.compile("_(\\w)");
    protected static final Object INVALID_VALUE = new Object();
    protected ConcurrentMap<Object, Property> propertyCache = new ConcurrentHashMap<>();

    protected abstract static class Property {

        protected final Class<?> type;
        protected final String propertyName;

        public Property(Class<?> type, String propertyName) {
            this.type = type;
            this.propertyName = propertyName;
        }

        public abstract void setValue(Object entity, Object value) throws Exception;
    }

    private static class BeanProperty extends Property {

        private final BeanUtils b;

        public BeanProperty(Class<?> type, String propertyName, BeanUtils b) {
            super(type, propertyName);
            this.b = b;
        }

        @Override
        public void setValue(Object entity, Object value) throws Exception {
            b.setProperty(entity, this.propertyName, value);
        }

    }

    private static class FieldProperty extends Property {

        private final Field f;

        public FieldProperty(Class<?> type, String propertyName, Field f) {
            super(type, propertyName);
            this.f = f;
        }

        @Override
        public void setValue(Object entity, Object value) throws Exception {
            f.set(entity, value);
        }

    }

    private Property findBeanProperty(Object entity, String propertyName, BeanUtils b) throws Exception {
        Class<?> type = b.getNullablePropertyType(entity, propertyName);
        if (type != null) {
            return new BeanProperty(type, propertyName, b);
        }
        return null;
    }

    private Property findFieldProperty(Object entity, String propertyName) throws Exception {
        for (Field f : entity.getClass().getFields()) {
            int mod = f.getModifiers();
            if (Modifier.isFinal(mod) || Modifier.isStatic(mod) || !Modifier.isPublic(mod)) {
                continue;
            }
            if (f.getName().equals(propertyName)) {
                return new FieldProperty(f.getType(), propertyName, f);
            }
        }
        return null;
    }

    private String createPropertyName(int i, String columnName) {
        switch (i) {
            case 0:
                return columnName;
            case 1:
                Matcher m = P_COLUMN.matcher(columnName);
                StringBuffer sb = null;
                while (m.find()) {
                    if (sb == null) {
                        sb = new StringBuffer();
                    }
                    m.appendReplacement(sb, m.group(1).toUpperCase());
                }
                if (sb != null) {
                    m.appendTail(sb);
                    return sb.toString();
                }
                return null;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public final boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
        MultiKey key = new MultiKey(entity.getClass(), columnName);
        Property p = propertyCache.get(key);
        if (p == null) {
            for (int i = 0; i < 2; i++) {
                String propertyName = createPropertyName(i, columnName);
                if (propertyName != null) {
                    if ((p = findBeanProperty(entity, propertyName, ctx.getBeanUtils())) != null || (p = findFieldProperty(entity, propertyName)) != null) {
                        break;
                    }
                }
            }
            if (p == null) {
                return false;
            }
            p = LangUitls.putIfAbsent(propertyCache, key, p);
        }
        Object value = createValue(ctx, rs, entity, columnName, columnIndex, p);
        if (value == INVALID_VALUE) {
            return false;
        }
        p.setValue(entity, value);
        return true;
    }

    protected abstract Object createValue(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex, Property property) throws Exception;
}
