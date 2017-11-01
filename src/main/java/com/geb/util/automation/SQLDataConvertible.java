package com.geb.util.automation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLDataConvertible {

    void toSQLData(PreparedStatement pstmt, int index) throws SQLException;

    boolean fromSQLData(ResultSet rs, int index) throws SQLException;
}
