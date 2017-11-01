package com.geb.util.automation;

import java.sql.ResultSet;

public interface ColumnSetter {

    public boolean set(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex) throws Exception;
}
