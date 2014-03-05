package logbook.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Map
 *
 */
public final class MapNames {

    /**
     * Map names predefined value
     */
    private static final Map<String, String> MAPNAME = new ConcurrentHashMap<String, String>() {
        {
            this.put("鎮守府正面海域", "1-1");
            this.put("南西諸島沖", "1-2");
            this.put("製油所地帯沿岸", "1-3");
            this.put("南西諸島防衛線", "1-4");
            this.put("カムラン半島", "2-1");
            this.put("バシー島沖", "2-2");
            this.put("東部オリョール海", "2-3");
            this.put("沖ノ島海域", "2-4");
            this.put("モーレイ海哨戒", "3-1");
            this.put("キス島沖", "3-2");
            this.put("アルフォンシーノ方面", "3-3");
            this.put("北方海域全域", "3-4");
            this.put("ジャム島攻略作戦", "4-1");
            this.put("カレー洋制圧戦", "4-2");
            this.put("リランカ島空襲", "4-3");
            this.put("カスガダマ沖海戦", "4-4");
            this.put("南方海域前面", "5-1");
            this.put("珊瑚諸島沖", "5-2");
            this.put("サブ島沖海域", "5-3");
            this.put("観音崎沖", "E-1 Arpeggio");
            this.put("硫黄島周辺海域", "E-2 Arpeggio");
            this.put("中部太平洋海域", "E-3 Arpeggio");
            this.put("サーモン海域", "5-4");
            this.put("24", "<UNKNOWN>");
            this.put("25", "<UNKNOWN>");
            this.put("26", "<UNKNOWN>");
            this.put("27", "<UNKNOWN>");
            this.put("28", "<UNKNOWN>");
            this.put("29", "<UNKNOWN>");
            this.put("30", "<UNKNOWN>");
        }
    };

    /**
     * Get the map code
     * 
     * @param jpname JP Map name
     * @return Map Code
     */
    public static String get(String jpname) {
        return MAPNAME.get(jpname);
    }
}
