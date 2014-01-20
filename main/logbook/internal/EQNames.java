package logbook.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Equipment
 *
 */
public final class EQNames {

    /**
     * Equipment names predefined value
     */
    private static final Map<String, String> EQNAME = new ConcurrentHashMap<String, String>() {
        {
            this.put("12cm単装砲", "12cm Single Cannon");
            this.put("12.7cm連装砲", "12.7cm Twin Cannon");
            this.put("10cm連装高角砲", "10cm Twin High-Angle Cannon");
            this.put("14cm単装砲", "14cm Single Cannon");
            this.put("15.5cm三連装砲(主砲)", "15.5cm Three-Barrelled Gun (Main)");
            this.put("20.3cm連装砲", "20.3cm Twin Cannon");
            this.put("35.6cm連装砲", "35.6cm Twin Cannon");
            this.put("41cm連装砲", "41cm Twin Cannon");
            this.put("46cm三連装砲", "46cm Triple Cannon");
            this.put("12.7cm連装高角砲", "12.7cm Twin High-Angle Cannon");
            this.put("15.2cm単装砲", "15.2cm Single Cannon");
            this.put("15.5cm三連装副砲", "15.5cm Triple Cannon (Secondary)");
            this.put("61cm三連装魚雷", "61cm Triple Torpedo");
            this.put("61cm四連装魚雷", "61cm Quad Torpedo");
            this.put("61cm四連装(酸素)魚雷", "61cm Quad (Oxygen) Torpedo");
            this.put("九七式艦攻", "Type 97 Torpedo Bomber");
            this.put("天山", "Tenzan");
            this.put("流星", "Ryuusei");
            this.put("九六式艦戦", "Type 96 Fighter");
            this.put("零式艦戦21型", "Type 21 Zero Fighter");
            this.put("零式艦戦52型", "Type 52 Zero Fighter");
            this.put("烈風", "Reppu");
            this.put("九九式艦爆", "Type 99 Bomber");
            this.put("彗星", "Suisei");
            this.put("零式水上偵察機", "Type 0 Recon Seaplane");
            this.put("瑞雲", "Zuiun");
            this.put("13号対空電探", "Type 13 Air RADAR");
            this.put("22号対水上電探", "Type 22 Surface RADAR");
            this.put("33号対水上電探", "Type 33 Surface RADAR");
            this.put("21号対空電探", "Type 21 Air RADAR");
            this.put("32号対水上電探", "Type 32 Surface RADAR");
            this.put("14号対空電探", "Type 14 Air RADAR");
            this.put("改良型艦本式タービン", "Improved Steam Turbine");
            this.put("強化型艦本式缶", "Enhanced Steam Turbine");
            this.put("三式弾", "Type 3 Shell");
            this.put("九一式徹甲弾", "Type 91 AP Shell");
            this.put("7.7mm機銃", "7.7mm Gun");
            this.put("12.7mm単装機銃", "12.7mm Gun");
            this.put("25mm連装機銃", "25mm Dual Gun");
            this.put("25mm三連装機銃", "25mm Triple Gun");
            this.put("甲標的", "Type A Ko-hyoteki");
            this.put("応急修理要員", "Repair Team");
            this.put("応急修理女神", "Repair Goddess");
            this.put("九四式爆雷投射機", "Type 94 Depth Charge");
            this.put("三式爆雷投射機", "Type 3 Depth Charge");
            this.put("九三式水中聴音機", "Type 93 SONAR");
            this.put("三式水中探信儀", "Type 3 SONAR");
            this.put("12.7cm単装高角砲", "12.7 cm Single High-Angle Cannon");
            this.put("25mm単装機銃", "25mm Single Gun");
            this.put("20.3cm(3号)連装砲", "20.3cm(no.3) Dual Cannon");
            this.put("12cm30連装噴進砲", "12cm 30-tube Rocket Launcher");
            this.put("流星改", "Ryuusei Kai");
            this.put("烈風改", "Reppu Kai");
            this.put("彩雲", "Saiun");
            this.put("紫電改二", "Shiden Kai 2");
            this.put("震電改", "Shinden Kai");
            this.put("彗星一二型甲", "Suisei Model 12A");
            this.put("61cm五連装(酸素)魚雷", "61cm Quintuple (Oxygen) Torpedo");
            this.put("零式水上観測機", "Zero Observation Seaplane");
            this.put("零式艦戦62型", "Type 62 Zero Fighter-Bomber");
            this.put("二式艦上偵察機", "Type 2 Recon Plane");
            this.put("試製晴嵐", "Prototype Seiran");
            this.put("12.7cm連装砲B型改二", "12.7cm Twin Cannon Type B Kai2");
            this.put("Ju87C改", "Ju 87C Kai");
            this.put("15.2cm連装砲", "15.2cm Twin Cannon");
            this.put("8cm高角砲", "8cm High-Angle Cannon");
            this.put("53cm艦首(酸素)魚雷", "53cm Hull-mount (Oxygen) Torpedo");
            this.put("大発動艇", "Daihatsu-Class Landing Craft");
            this.put("カ号観測機", "Type Ka Liaison Aircraft");
            this.put("三式指揮連絡機(対潜)", "Type 3 Liaison Aircraft");
            this.put("10cm連装高角砲(砲架)", "Improved 10cm High-angle Cannon");
            this.put("増設バルジ(中型艦)", "Anti-Torpedo Bulge (M)");
            this.put("増設バルジ(大型艦)", "Anti-Torpedo Bulge (L)");
            this.put("74", "UNKNOWN");
            this.put("75", "UNKNOWN");
            this.put("76", "UNKNOWN");
            this.put("77", "UNKNOWN");
            this.put("78", "UNKNOWN");
            this.put("79", "UNKNOWN");
            this.put("80", "UNKNOWN");
            this.put("81", "UNKNOWN");
            this.put("82", "UNKNOWN");
            this.put("83", "UNKNOWN");
            this.put("84", "UNKNOWN");
            this.put("85", "UNKNOWN");
            this.put("86", "UNKNOWN");
            this.put("87", "UNKNOWN");
            this.put("88", "UNKNOWN");
            this.put("89", "UNKNOWN");
            this.put("90", "UNKNOWN");
            this.put("91", "UNKNOWN");
            this.put("92", "UNKNOWN");
            this.put("93", "UNKNOWN");
            this.put("94", "UNKNOWN");
            this.put("95", "UNKNOWN");
            this.put("96", "UNKNOWN");
            this.put("97", "UNKNOWN");
            this.put("98", "UNKNOWN");
            this.put("99", "UNKNOWN");
            this.put("100", "UNKNOWN");
            this.put("101", "UNKNOWN");
            this.put("102", "UNKNOWN");
            this.put("103", "UNKNOWN");
            this.put("104", "UNKNOWN");
            this.put("105", "UNKNOWN");
            this.put("106", "UNKNOWN");
            this.put("107", "UNKNOWN");
            this.put("108", "UNKNOWN");
            this.put("109", "UNKNOWN");
            this.put("110", "UNKNOWN");
        }
    };

    /**
     * Get the equipment name
     * 
     * @param jpname JP equipment name
     * @return English name
     */
    public static String get(String jpname) {
        return EQNAME.get(jpname);
    }
}
