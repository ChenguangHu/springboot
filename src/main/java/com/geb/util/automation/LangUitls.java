package com.geb.util.automation;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.LoggerFactory;

public class LangUitls {

    public static final FastDateFormat F_DATETIME = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    private static final FastDateFormat F_DAILY_FILE = FastDateFormat.getInstance("yyMMdd");

    public static <E> E getNotNull(E nullable, E defaultValue) {
        return nullable == null ? defaultValue : nullable;
    }

    public static <K, V> V putIfAbsent(ConcurrentMap<K, V> map, K key, V value) {
        V v = map.putIfAbsent(key, value);
        return v == null ? value : v;
    }

    public static File dailyFile(File dir, Date date) {
        File f = new File(dir, F_DAILY_FILE.format(date));
        if (!f.exists() && !f.mkdirs() && !f.exists()) {
            throw new RuntimeException(String.format("Can't create folder [%s]", f.getAbsolutePath()));
        }
        return f;
    }

    public static void closeObject(Object obj) {
        if (obj == null) {
            return;
        }
        try {
            if (obj instanceof Closeable) {
                ((Closeable) obj).close();
            } else if (obj instanceof ResultSet) {
                ((ResultSet) obj).close();
            } else if (obj instanceof Statement) {
                ((Statement) obj).close();
            } else if (obj instanceof Connection) {
                ((Connection) obj).close();
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (IOException ex) {
            LoggerFactory.getLogger(LangUitls.class).error(null, ex);
        } catch (SQLException ex) {
            LoggerFactory.getLogger(LangUitls.class).error(null, ex);
        }
    }

    public static boolean loopLimit(int i, int max) {
        if (i <= max) {
            return true;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 楠岃瘉閾惰鍗″彿
     *
     * @param number
     * @return
     */
    public static boolean isLuhn(String number) {
        if (number == null || number.isEmpty()) {
            return false;
        }
        int s = 0;
        for (int i = 0, n = number.length(); i < n; i++) {
            char c = number.charAt(n - 1 - i);
            if (c < '0' || c > '9') {
                return false;
            }
            if ((i & 1) == 0) {
                s += c - '0';
            } else {
                int a = (c - '0') * 2;
                s += a > 9 ? a - 9 : a;
            }
        }
        return s % 10 == 0;
    }

    /**
     * 璁＄畻鍦扮悆涓婁换鎰忎袱鐐�缁忕含搴�璺濈
     *
     * @param lng1 绗竴鐐圭粡搴�	* @param lat1 绗竴鐐圭含搴�	* @param lng2 绗簩鐐圭粡搴�	*
     * @param lat2 绗簩鐐圭含搴�	* @return 杩斿洖璺濈 鍗曚綅锛氱背
     */
    public static double distance(double lng1, double lat1, double lng2, double lat2) {
        double a, b, R;
        R = 6378137; // 鍦扮悆鍗婂緞
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (lng1 - lng2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }
}
