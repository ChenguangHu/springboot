package com.geb.util.automation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapSetter implements ColumnSetter {

    private static final Map<String, Class> RESULT_SET_VALUE_TYPE;
    private static final Pattern P_COLUMN_NAME = Pattern.compile("(\\w+)(?:\\x28([\\w\\.\\[\\];]+)\\x29)?");

    static {
        Map<Class, Method> map = BasePropertySetter2.RESULT_SET_GET_METHODS;
        RESULT_SET_VALUE_TYPE = new HashMap<String, Class>(map.size());
        for (Class clazz : map.keySet()) {
            String name = clazz.getName();
            String simpleName = clazz.getSimpleName();
            RESULT_SET_VALUE_TYPE.put(name, clazz);
            if (!RESULT_SET_VALUE_TYPE.containsKey(simpleName)) {
                RESULT_SET_VALUE_TYPE.put(simpleName, clazz);
            }
        }
    }

    @Override
    public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
        if (!(entity instanceof Map)) {
            return false;
        }
        Map map = (Map) entity;
        Matcher m = P_COLUMN_NAME.matcher(columnName);
        if (!m.matches()) {
            throw new SQLException(String.format("%s 涓嶅尮閰�%s", columnName, P_COLUMN_NAME));
        }
        String type = m.group(2);
        Object value;
        if (type == null) {
            value = rs.getObject(columnIndex);
        } else {
            Class clazz = RESULT_SET_VALUE_TYPE.get(type);
            if (clazz == null) {
                throw new SQLException("鏃犳晥鐨勭被鍨嬶細" + type);
            }
            value = BasePropertySetter2.invoke(rs, columnIndex, clazz);
        }
        map.put(m.group(1), value);
        return true;
    }

}
