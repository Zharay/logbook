package logbook.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 遠征
 *
 */
public final class Deck {

    /**
     * 遠征プリセット値
     */
    private static final Map<String, String> DECK = new ConcurrentHashMap<String, String>() {
        {
                this.put("1", "Expedition 1");
                this.put("2", "Expedition 2");
                this.put("3", "Expedition 3");
                this.put("4", "Expedition 4");
                this.put("5", "Expedition 5");
                this.put("6", "Expedition 6");
                this.put("7", "Expedition 7");
                this.put("8", "Expedition 8");
                this.put("9", "Expedition 9");
                this.put("10", "Expedition 10");
                this.put("11", "Expedition 11");
                this.put("12", "Expedition 12");
                this.put("13", "Expedition 13");
                this.put("14", "Expedition 14");
                this.put("15", "Expedition 15");
                this.put("16", "Expedition 16");
                this.put("17", "Expedition 17");
                this.put("18", "Expedition 18");
                this.put("19", "Expedition 19");
                this.put("20", "Expedition 20");
                this.put("21", "<UNKNOWN>");
                this.put("22", "<UNKNOWN>");
                this.put("23", "<UNKNOWN>");
                this.put("24", "<UNKNOWN>");
                this.put("25", "Expedition 25");
                this.put("26", "Expedition 26");
                this.put("27", "Expedition 27");
                this.put("28", "Expedition 28");
                this.put("29", "Expedition 29");
                this.put("30", "Expedition 30");
                this.put("31", "<UNKNOWN>");
                this.put("32", "<UNKNOWN>");
                this.put("33", "Vanguard Support");
                this.put("34", "Decisive Battle Support");
                this.put("35", "Expedition 35");
                this.put("36", "Expedition 36");
                this.put("37", "<UNKNOWN>");
                this.put("38", "<UNKNOWN>");
                this.put("39", "<UNKNOWN>");
                this.put("40", "<UNKNOWN>");
                this.put("41", "<UNKNOWN>");
                this.put("42", "<UNKNOWN>");
                this.put("43", "<UNKNOWN>");
                this.put("44", "<UNKNOWN>");
                this.put("45", "<UNKNOWN>");
                this.put("46", "<UNKNOWN>");
                this.put("47", "<UNKNOWN>");
                this.put("48", "<UNKNOWN>");
                this.put("49", "<UNKNOWN>");
                this.put("50", "<UNKNOWN>");
                this.put("51", "<UNKNOWN>");
                this.put("52", "<UNKNOWN>");
                this.put("53", "<UNKNOWN>");
                this.put("54", "<UNKNOWN>");
                this.put("55", "<UNKNOWN>");
                this.put("56", "<UNKNOWN>");
                this.put("57", "<UNKNOWN>");
                this.put("58", "<UNKNOWN>");
                this.put("59", "<UNKNOWN>");
                this.put("60", "<UNKNOWN>");
                this.put("61", "<UNKNOWN>");
                this.put("62", "<UNKNOWN>");
                this.put("63", "<UNKNOWN>");
                this.put("64", "<UNKNOWN>");
                this.put("65", "<UNKNOWN>");
                this.put("66", "<UNKNOWN>");
                this.put("67", "<UNKNOWN>");
                this.put("68", "<UNKNOWN>");
                this.put("69", "<UNKNOWN>");
                this.put("70", "<UNKNOWN>");
                this.put("71", "<UNKNOWN>");
                this.put("72", "<UNKNOWN>");
                this.put("73", "<UNKNOWN>");
                this.put("74", "<UNKNOWN>");
                this.put("75", "<UNKNOWN>");
                this.put("76", "<UNKNOWN>");
                this.put("77", "<UNKNOWN>");
                this.put("78", "<UNKNOWN>");
                this.put("79", "<UNKNOWN>");
                this.put("80", "<UNKNOWN>");
                this.put("81", "<UNKNOWN>");
                this.put("82", "<UNKNOWN>");
                this.put("83", "<UNKNOWN>");
                this.put("84", "<UNKNOWN>");
                this.put("85", "<UNKNOWN>");
                this.put("86", "<UNKNOWN>");
                this.put("87", "<UNKNOWN>");
                this.put("88", "<UNKNOWN>");
                this.put("89", "<UNKNOWN>");
                this.put("90", "<UNKNOWN>");
                this.put("91", "<UNKNOWN>");
                this.put("92", "<UNKNOWN>");
                this.put("93", "<UNKNOWN>");
                this.put("94", "<UNKNOWN>");
                this.put("95", "<UNKNOWN>");
                this.put("96", "<UNKNOWN>");
                this.put("97", "<UNKNOWN>");
                this.put("98", "<UNKNOWN>");
                this.put("99", "<UNKNOWN>");
                this.put("100", "<UNKNOWN>");
                this.put("101", "<UNKNOWN>");
                this.put("102", "<UNKNOWN>");
                this.put("103", "<UNKNOWN>");
                this.put("104", "<UNKNOWN>");
                this.put("105", "<UNKNOWN>");
                this.put("106", "<UNKNOWN>");
                this.put("107", "<UNKNOWN>");
                this.put("108", "<UNKNOWN>");
                this.put("109", "Vanguard Support");
                this.put("110", "Decisive Battle Support");
        }
    };

    /**
     * 遠征を取得します
     * 
     * @param id ID
     * @return 遠征
     */
    public static String get(String id) {
        return DECK.get(id);
    }
}
