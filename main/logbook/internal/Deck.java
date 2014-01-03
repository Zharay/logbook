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
                this.put("1", "Practice Sail");
                this.put("2", "Long Distance Practice Sail");
                this.put("3", "Guard Duty");
                this.put("4", "ASW Lookout Mission");
                this.put("5", "Naval Escort");
                this.put("6", "Air Defence Marksmanship Practice");
                this.put("7", "Naval Review Rehearsal");
                this.put("8", "Naval Review");
                this.put("9", "Tanker Escort");
                this.put("10", "Reconnaissance Enforcement");
                this.put("11", "Bauxite Transportation");
                this.put("12", "Resource Transportation");
                this.put("13", "Operation Tokyo Express");
                this.put("14", "包囲陸戦隊撤収作戦");
                this.put("15", "Assisting Decoy Mobile Troops");
                this.put("16", "艦隊決戦援護作戦");
                this.put("17", "Scouting out Enemy Territory");
                this.put("18", "Transporting Aircraft");
                this.put("19", "Operation Kita");
                this.put("20", "Submarine Patrol");
                this.put("21", "<UNKNOWN>");
                this.put("22", "<UNKNOWN>");
                this.put("23", "<UNKNOWN>");
                this.put("24", "<UNKNOWN>");
                this.put("25", "通商破壊作戦");
                this.put("26", "敵母港空襲作戦");
                this.put("27", "潜水艦通商破壊作戦");
                this.put("28", "西方海域封鎖作戦");
                this.put("29", "Submarine Deployment Practice");
                this.put("30", "Submarine Deployment");
                this.put("31", "<UNKNOWN>");
                this.put("32", "<UNKNOWN>");
                this.put("33", "Vanguard Support Mission");
                this.put("34", "艦隊決戦支援任務");
                this.put("35", "Operation MO");
                this.put("36", "Construction of Seaplane Base");
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
                this.put("109", "Vanguard Support Mission");
                this.put("110", "艦隊決戦支援任務");
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
