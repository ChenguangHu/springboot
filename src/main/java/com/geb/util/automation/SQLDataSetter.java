package com.geb.util.automation;

import java.sql.ResultSet;

public class SQLDataSetter extends ByPropertyTypeSetter {

    @Override
    protected Object createValue(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex, Property property) throws Exception {
        if (SQLDataConvertible.class.isAssignableFrom(property.type)) {
            Object value = property.type.newInstance();
            if (!((SQLDataConvertible) value).fromSQLData(rs, columnIndex)) {
                value = null;
            }
            return value;
        } else {
            return INVALID_VALUE;
        }
    }

}
