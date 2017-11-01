package com.geb.util.automation;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;

public class SerializableSetter extends ByPropertyTypeSetter {

    @Override
    protected Object createValue(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex, Property property) throws Exception {
        if (Serializable.class.isAssignableFrom(property.type)) {
            InputStream in = rs.getBinaryStream(columnIndex);
            if (in == null) {
                return null;
            }
            try {
                return SerializationUtils.deserialize(in);
            } finally {
                IOUtils.closeQuietly(in);
            }
        } else {
            return INVALID_VALUE;
        }
    }

}
