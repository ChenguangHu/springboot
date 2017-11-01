package com.geb.util.automation;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DirectResultSetter implements ColumnSetter {

    protected ConcurrentMap<Object, Object> cache = new ConcurrentHashMap<>();
    private static final Object INVALID_OBJECT = new Object();
    private static final Class[] PARAMS = {ResultSet.class, int.class};

    @Override
    public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
        BeanUtils b = ctx.getBeanUtils();
        Object key = b.getPropertyKey(entity, columnName);
        if (key == null) {
            return false;
        }
        Object m = cache.get(key);
        if (m == null) {
            try {
                m = entity.getClass().getMethod("set" + BeanUtils.capitalize(columnName), PARAMS);
            } catch (NoSuchMethodException ex) {
                m = INVALID_OBJECT;
            }
            m = LangUitls.getNotNull(cache.putIfAbsent(key, m), m);
        }
        if (m instanceof Method) {
            ((Method) m).invoke(entity, rs, columnIndex);
            return true;
        } else if (m == INVALID_OBJECT) {
            return false;
        } else {
            throw new RuntimeException();
        }
    }

}
