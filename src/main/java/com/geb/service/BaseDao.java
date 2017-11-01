package com.geb.service;

import com.geb.util.ApplicationException;
import com.geb.ext.Pagination;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

public class BaseDao {

    static final Logger log = LoggerFactory.getLogger(BaseDao.class);
    protected final String HCG_ID;
    @Autowired
    protected SqlSessionFactory sqlSessionFactory;

    public BaseDao() {
        HCG_ID = getClass().getName() + ".";
    }

    public BaseDao(SqlSessionFactory sqlSessionFactory) {
        this();
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

//记录sql异常
    protected int hasError(SqlSession session, String statement, Object parameter, int errorCode) throws PersistenceException {
        try {
            return session.update(statement, parameter);
        } catch (PersistenceException ex) {
            Throwable t = ex.getCause();
            if (t instanceof SQLException) {
                if (((SQLException) t).getErrorCode() == errorCode) {
                    return -1;
                }
            }
            throw ex;
        }
    }
//开始通用数据库操作

    //分页查询
    protected Pagination selectPageList(String sqlMapId, int pageSize, int pageNum, Object param) throws ApplicationException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Map map = new HashMap();
            if (param != null) {
                if (param instanceof Map) {
                    map.putAll((Map) param);
                } else if (param instanceof List) {
                    map.put("list", param);
                }else{
                    map.put("param", param);
                }
            }
            map.put("is_count", true);
            map.put("is_limit", false);
            Map m = session.selectOne(sqlMapId, map);
            int count = Integer.parseInt(m.get("pagination_count").toString());
            Pagination p = new Pagination(pageSize, pageNum, count);
            p.setMaxPageNum((int) Math.ceil((double)count/(double)pageSize));
            map.put("is_count", false);
            map.put("is_limit", true);
            map.put("pageSize", p.getPageSize());
            map.put("pageLimit", p.getPageNum() < 1 ? 0 : (p.getPageNum() - 1) * p.getPageSize());
            Object result = session.selectList(sqlMapId, map);
            map.clear();
            p.setData(result);
            return p;
        } catch (Exception e) {
            throw new ApplicationException("数据库分页查询对象[mapper:" + sqlMapId + "]\n 日志：" + e.getMessage(), -9, e);
        } finally {
            session.close();
        }
    }

    //查询所有,可通过条件
    protected List selectList(String sqlMapId, Object param) throws ApplicationException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Map map = new HashMap();
            if (param != null) {
                if (param instanceof Map) {
                    map.putAll((Map) param);
                } else if (param instanceof List) {
                    map.put("list", param);
                }else{
                    return session.selectList(sqlMapId, param);
                }
                return session.selectList(sqlMapId, map);
            } else {
                return session.selectList(sqlMapId);
            }
        } catch (Exception e) {
            throw new ApplicationException("数据库查询List对象[mapper:" + sqlMapId + "]\n 日志：" + e.getMessage(), -9, e);
        } finally {
            session.close();
        }
    }

    //根据ID查一个
    protected Object selectObject(String sqlMapId, Object param) throws ApplicationException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.selectOne(sqlMapId, param);
        } catch (Exception e) {
            throw new ApplicationException("数据库查询一个对象[mapper:" + sqlMapId + "]\n 日志：" + e.getMessage(), -9, e);
        } finally {
            session.close();
        }
    }

  

    //新增一个
    protected int insertOne(String sqlMapId, Object param) throws ApplicationException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            return session.insert(sqlMapId, param == null ? null : param);
        } catch (Exception e) {
            throw new ApplicationException("数据新增一个对象[mapper:" + sqlMapId + "]\n 日志：" + e.getMessage(), -9, e);
        } finally {
            session.close();
        }
    }
    
     //新增一些
    protected int insertOne(String sqlMapId, List<Object> param) throws ApplicationException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Map map=new HashMap();
            map.put("list",param);
            return session.insert(sqlMapId, map);
        } catch (Exception e) {
            throw new ApplicationException("数据新增一个对象[mapper:" + sqlMapId + "]\n 日志：" + e.getMessage(), -9, e);
        } finally {
            session.close();
        }
    }

    //删除一个对象通过条件
    protected int deleteOne(String sqlMapId, Object param) throws ApplicationException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            int status = 0;
            status = session.delete(sqlMapId, param);
            return status;
        } catch (Exception e) {
            throw new ApplicationException("数据删除对象[mapper:" + sqlMapId + "]\n 日志：" + e.getMessage(), -9, e);
        } finally {
            session.close();
        }
    }

    //更新对象通过条件
    protected int updateOne(String sqlMapId, Object param) throws ApplicationException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            int status = 0;
            status = session.update(sqlMapId, param);
            return status;
        } catch (Exception e) {
            throw new ApplicationException("数据更新对象[mapper:" + sqlMapId + "]\n 日志：" + e.getMessage(), -9, e);
        } finally {
            session.close();
        }
    }

}
