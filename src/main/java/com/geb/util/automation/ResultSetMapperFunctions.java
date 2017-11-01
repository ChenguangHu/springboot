package com.geb.util.automation;

import java.sql.ResultSet;
import java.util.SortedMap;

public interface ResultSetMapperFunctions {

    public void init() throws Exception;

    public Object getEntityCache(Class entityClass, ResultSet rs, SortedMap<Integer, String> columns, String[] params) throws Exception;
}
