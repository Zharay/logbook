package logbook.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 艦種
 * 
 */
public class ShipStyle {

    /**
     * 艦種プリセット値
     */
    private static final Map<String, String> SHIPSTYLE = new ConcurrentHashMap<String, String>() {
        {
            this.put("1", "LCS");
            this.put("2", "DD");
            this.put("3", "CL");
            this.put("4", "CLT");
            this.put("5", "CA");
            this.put("6", "CAV");
            this.put("7", "CVL");
            this.put("8", "BB");
            this.put("9", "BB");
            this.put("10", "BBV");
            this.put("11", "CV");
            this.put("12", "BBD");
            this.put("13", "SS");
            this.put("14", "SSV");
            this.put("15", "AP");
            this.put("16", "AV");
            this.put("17", "LHA");
            this.put("18", "CVB");
        }
    };

    /**
     * 艦種を取得します
     * 
     * @param id
     * @return 艦種
     */
    public static String get(String id) {
        return SHIPSTYLE.get(id);
    }
}
