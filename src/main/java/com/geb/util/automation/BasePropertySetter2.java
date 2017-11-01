package com.geb.util.automation;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BasePropertySetter2 extends ByPropertyTypeSetter {

    static final Map<Class, Method> RESULT_SET_GET_METHODS;

    static {
        Map<Class, String> map = new HashMap<Class, String>();
        map.put(BigDecimal.class, "getBigDecimal");
        map.put(InputStream.class, "getBinaryStream");
        map.put(Blob.class, "getBlob");
        map.put(Boolean.class, "getBoolean");
        map.put(Boolean.TYPE, "getBoolean");
        map.put(Byte.class, "getByte");
        map.put(Byte.TYPE, "getByte");
        map.put(byte[].class, "getBytes");
        map.put(Reader.class, "getCharacterStream");
        map.put(Clob.class, "getClob");
        map.put(java.sql.Date.class, "getDate");
        map.put(Double.TYPE, "getDouble");
        map.put(Double.class, "getDouble");
        map.put(Float.class, "getFloat");
        map.put(Float.TYPE, "getFloat");
        map.put(Integer.class, "getInt");
        map.put(Integer.TYPE, "getInt");
        map.put(Long.class, "getLong");
        map.put(Long.TYPE, "getLong");
        map.put(String.class, "getString");
        map.put(java.sql.Time.class, "getTime");
        map.put(java.sql.Timestamp.class, "getTimestamp");
        //
        map.put(java.util.Date.class, "getTimestamp");
        map.put(Object.class, "getObject");
        //
        RESULT_SET_GET_METHODS = new HashMap<Class, Method>(map.size());
        try {
            for (Map.Entry<Class, String> t : map.entrySet()) {
                Method m = ResultSet.class.getMethod(t.getValue(), Integer.TYPE);
                RESULT_SET_GET_METHODS.put(t.getKey(), m);
            }
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<Class, Method> getResultSetGetMethods() {
        return Collections.<Class, Method>unmodifiableMap(RESULT_SET_GET_METHODS);
    }

    static Object invoke(ResultSet rs, int columnIndex, Class type) throws SQLException {
        Method m = RESULT_SET_GET_METHODS.get(type);
        if (m == null) {
            return INVALID_VALUE;
        }
        try {
            Object value = m.invoke(rs, columnIndex);
            if (rs.wasNull() && !type.isPrimitive()) {
                value = null;
            }
            return value;
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw ResultSetMapper.wrapSQLException("type:" + type, ex);
        }
    }

    @Override
    protected Object createValue(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex, Property property) throws Exception {
        return invoke(rs, columnIndex, property.type);
    }

}
