package com.geb.util.automation;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.Properties;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

@Intercepts(
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}))
//@Component
public class AutoResultSetMapper implements Interceptor {

    protected GlobalContext globalContext;//须在这个实体类里面修实体类默认的包
    private volatile boolean init;

    public AutoResultSetMapper(GlobalContext globalContext) {
        this.globalContext = globalContext;
    }

    private void tryInit() throws Exception {
        if (!init) {
            synchronized (this) {
                if (!init) {
                    ResultSetMapperFunctions funs = globalContext.getFunctions();
                    if (funs != null) {
                        funs.init();
                    }
                    init = true;
                }
            }
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        tryInit();
        ResultSet rs = ((Statement) invocation.getArgs()[0]).getResultSet();
        if (rs.getMetaData().getColumnLabel(1).startsWith(":")) {
            Object result = new ResultSetMapper(globalContext).mapping(rs);
            if (result == null) {
                return Collections.emptyList();
            } else {
                return result;
            }
        } else {
            return invocation.proceed();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
