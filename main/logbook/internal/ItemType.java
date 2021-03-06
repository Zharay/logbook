package logbook.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * アイテム種別
 *
 */
public class ItemType {

    /**
     * アイテム種別プリセット値
     */
    private static final Map<String, String> ITEMTYPE = new ConcurrentHashMap<String, String>() {
        {
            this.put("1", "Main Cannon");
            this.put("2", "Main Cannon");
            this.put("3", "Main Cannon");
            this.put("4", "Secondary Cannon");
            this.put("5", "Torpedo");
            this.put("6", "Fighter");
            this.put("7", "Dive Bomber");
            this.put("8", "Torpedo Bomber");
            this.put("9", "Recon Plane");
            this.put("10", "Recon Seaplane");
            this.put("11", "Air RADAR");
            this.put("12", "AA Ammo");
            this.put("13", "AP Ammo");
            this.put("14", "Damage Control");
            this.put("15", "AA Gun");
            this.put("16", "High-Angle Gun");
            this.put("17", "Depth Charge");
            this.put("18", "SONAR");
            this.put("19", "Engine");
            this.put("20", "Landing Craft");
            this.put("21", "Autogyro");
            this.put("22", "Liaison Airplane");
            this.put("23", "Additional Armor");
            this.put("24", "Searchlight");
            this.put("25", "Transport Support");
            this.put("26", "Ship Repair Facility");
        }
    };

    /**
     * アイテム種別を取得します
     * 
     * @param type ID
     * @return アイテム種別
     */
    public static String get(String type) {
        return ITEMTYPE.get(type);
    }
}
