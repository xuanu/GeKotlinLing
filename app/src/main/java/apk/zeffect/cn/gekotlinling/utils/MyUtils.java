package apk.zeffect.cn.gekotlinling.utils;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * <pre>
 *      author  ：zzx
 *      e-mail  ：zhengzhixuan18@gmail.com
 *      time    ：2017/12/15
 *      desc    ：
 *      version:：1.0
 * </pre>
 *
 * @author zzx
 */

public class MyUtils {
    public static Map<String, String> sortMapByKey(Map<String, String> paramMap) {
        if ((paramMap == null) || (paramMap.isEmpty())) {
            return null;
        }
        TreeMap localTreeMap = new TreeMap(new MapKeyComparator());
        localTreeMap.putAll(paramMap);
        return localTreeMap;
    }

    public static class MapKeyComparator
            implements Comparator<String> {
        public int compare(String paramString1, String paramString2) {
            return paramString1.compareTo(paramString2);
        }
    }
}
