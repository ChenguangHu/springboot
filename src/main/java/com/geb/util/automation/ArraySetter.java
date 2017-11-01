package com.geb.util.automation;

import java.sql.ResultSet;
import java.util.List;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ArraySetter extends ByPropertyTypeSetter {

    @Override
    protected Object createValue(GlobalContext ctx, ResultSet rs, Object entity, String columnName, int columnIndex, Property property) throws Exception {
        if (String[].class == property.type) {
            String xml = rs.getString(columnIndex);
            if (rs.wasNull()) {
                return null;
            }
            String[] array;
            Element eArray = DocumentHelper.parseText(xml).getRootElement();
            List<Element> eValues = eArray.elements("Value");
            array = new String[eValues.size()];
            int i = 0;
            for (Element eValue : eValues) {
                array[i++] = eValue.getText();
            }
            return array;
        } else {
            return INVALID_VALUE;
        }
    }

}
