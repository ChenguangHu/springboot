package com.geb.util.automation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "GlobalContext")
public class GlobalContext {

    protected BeanUtils beanUtils;
    //这里给定默认的实体类包的目录
    protected String defaultEntityPackage = "com.geb.entity";
    protected ColumnSetter[] columnSetters;
    private ResultSetMapperFunctions functions;

    public GlobalContext() {
        beanUtils = new BeanUtils();
        columnSetters = new ColumnSetter[]{
            new CachedColumnSetter(
            new MapSetter(),
            new DirectResultSetter(),
            new ArraySetter(), new SQLDataSetter(), new BasePropertySetter2(), new SerializableSetter()
            )};
    }

    public BeanUtils getBeanUtils() {
        return beanUtils;
    }

    public void setBeanUtils(BeanUtils beanUtils) {
        this.beanUtils = beanUtils;
    }

    public String getDefaultEntityPackage() {
        return defaultEntityPackage;
    }

    public void setDefaultEntityPackage(String defaultEntityPackage) {
        this.defaultEntityPackage = defaultEntityPackage;
    }

    public ColumnSetter[] getColumnSetters() {
        return columnSetters;
    }

    public void setColumnSetters(ColumnSetter[] columnSetters) {
        this.columnSetters = columnSetters;
    }

    public ResultSetMapperFunctions getFunctions() {
        return functions;
    }

    public void setFunctions(ResultSetMapperFunctions functions) {
        this.functions = functions;
    }

}
