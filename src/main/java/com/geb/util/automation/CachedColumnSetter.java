package com.geb.util.automation;

import java.sql.ResultSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.collections.keyvalue.MultiKey;

public class CachedColumnSetter implements ColumnSetter {

    protected final ConcurrentMap<Object, ColumnSetter> cache = new ConcurrentHashMap<>();
    private static final ColumnSetter NO = new ColumnSetter() {
        @Override
        public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
            return false;
        }
    };
    private final ColumnSetter[] columnSetters;

    public CachedColumnSetter(ColumnSetter... columnSetters) {
        this.columnSetters = columnSetters.clone();
    }

    @Override
    public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception {
        Object key = new MultiKey(entity.getClass(), columnName);
        ColumnSetter setter = cache.get(key);
        if (setter == null) {
            for (ColumnSetter t : columnSetters) {
                if (t.set(ctx, rs, entity, columnName, columnIndex)) {
                    cache.putIfAbsent(key, t);
                    return true;
                }
            }
            cache.putIfAbsent(key, NO);
            return false;
        } else {
            return setter.set(ctx, rs, entity, columnName, columnIndex);
        }
    }
}
