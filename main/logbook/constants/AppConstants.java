/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.constants;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;

import org.eclipse.swt.graphics.RGB;

/**
 * アプリケーションで使用する共通の定数クラス
 *
 */
public class AppConstants {

    /** バージョン */
    public static final String VERSION = "0.5.12 r40";

    /** ホームページ */
    public static final URI HOME_PAGE_URI = URI.create("http://kancolle.sanaechan.net/");

    /** アップデートチェック先 */
    public static final URI UPDATE_CHECK_URI = URI
            .create("https://raw.github.com/Zharay/logbook/master/checkversion.txt");

    /** GitHub Translation */
    public static final URI GITHUB_PAGE_URI = URI.create("https://github.com/Zharay/logbook");

    /** 日付書式 */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 日付書式 */
    public static final String DATE_SHORT_FORMAT = "HH:mm:ss";

    /** 疲労赤色 */
    public static final int COND_RED = 19;

    /** 疲労オレンジ色 */
    public static final int COND_ORANGE = 29;

    /** 疲労緑色 */
    public static final int COND_GREEN = 50;

    /** 遠征色 */
    public static final RGB MISSION_COLOR = new RGB(102, 51, 255);

    /** 入渠色 */
    public static final RGB NDOCK_COLOR = new RGB(0, 102, 153);

    /** 疲労赤色 */
    public static final RGB COND_RED_COLOR = new RGB(255, 16, 0);

    /** 疲労オレンジ色 */
    public static final RGB COND_ORANGE_COLOR = new RGB(255, 140, 0);

    /** 疲労緑色 */
    public static final RGB COND_GREEN_COLOR = new RGB(0, 128, 0);

    /** 5分前 */
    public static final RGB TIME_IN_5_MIN = new RGB(255, 215, 0);

    /** 10分前 */
    public static final RGB TIME_IN_10_MIN = new RGB(255, 239, 153);

    /** 20分前 */
    public static final RGB TIME_IN_20_MIN = new RGB(255, 247, 203);

    /** テーブル行(偶数行)背景色 */
    public static final RGB ROW_BACKGROUND = new RGB(246, 246, 246);

    /** 小破(75%) */
    public static final float SLIGHT_DAMAGE = 0.75f;

    /** 中破(50%) */
    public static final float HALF_DAMAGE = 0.5f;

    /** 大破(25%) */
    public static final float BADLY_DAMAGE = 0.25f;

    /** 補給(少) */
    public static final float LOW_SUPPLY = 0.77f;

    /** 補給(空) */
    public static final float EMPTY_SUPPLY = 0.33f;

    /** 文字コード(Shift_JIS) */
    public static final Charset CHARSET = Charset.forName("MS932");

    /** アプリケーション設定ファイル  */
    public static final File APP_CONFIG_FILE = new File("./config/internal.xml");

    /** 艦娘設定ファイル  */
    public static final File SHIP_CONFIG_FILE = new File("./config/ship.xml");

    /** 建造ドック設定ファイル  */
    public static final File KDOCK_CONFIG_FILE = new File("./config/kdock.xml");

    /** 所有艦娘グループ設定ファイル  */
    public static final File GROUP_CONFIG_FILE = new File("./config/group.xml");

    /** 保有資材:燃料 */
    public static final int MATERIAL_FUEL = 1;

    /** 保有資材:弾薬 */
    public static final int MATERIAL_AMMO = 2;

    /** 保有資材:鋼材 */
    public static final int MATERIAL_METAL = 3;

    /** 保有資材:ボーキサイト */
    public static final int MATERIAL_BAUXITE = 4;

    /** 保有資材:バーナー */
    public static final int MATERIAL_BURNER = 5;

    /** 保有資材:高速修復材 */
    public static final int MATERIAL_BUCKET = 6;

    /** 保有資材:開発資材 */
    public static final int MATERIAL_RESEARCH = 7;

    /** /resources/icon/add.png */
    public static final String R_ICON_ADD = "/resources/icon/add.png";

    /** /resources/icon/delete.png */
    public static final String R_ICON_DELETE = "/resources/icon/delete.png";

    /** /resources/icon/error.png */
    public static final String R_ICON_ERROR = "/resources/icon/error.png";

    /** /resources/icon/exclamation.png */
    public static final String R_ICON_EXCLAMATION = "/resources/icon/exclamation.png";

    /** /resources/icon/folder_star.png */
    public static final String R_ICON_FOLDER_STAR = "/resources/icon/folder_star.png";

    /** /resources/icon/star.png */
    public static final String R_ICON_STAR = "/resources/icon/star.png";

    /** /resources/hpgauge/0.png */
    public static final String R_HPGAUGE_0 = "/resources/hpgauge/0.png";

    /** /resources/hpgauge/1.png */
    public static final String R_HPGAUGE_1 = "/resources/hpgauge/1.png";

    /** /resources/hpgauge/2.png */
    public static final String R_HPGAUGE_2 = "/resources/hpgauge/2.png";

    /** /resources/hpgauge/3.png */
    public static final String R_HPGAUGE_3 = "/resources/hpgauge/3.png";

    /** /resources/hpgauge/4.png */
    public static final String R_HPGAUGE_4 = "/resources/hpgauge/4.png";

    /** /resources/hpgauge/5.png */
    public static final String R_HPGAUGE_5 = "/resources/hpgauge/5.png";

    /** /resources/hpgauge/6.png */
    public static final String R_HPGAUGE_6 = "/resources/hpgauge/6.png";

    /** /resources/hpgauge/7.png */
    public static final String R_HPGAUGE_7 = "/resources/hpgauge/7.png";

    /** /resources/hpgauge/8.png */
    public static final String R_HPGAUGE_8 = "/resources/hpgauge/8.png";

    /** /resources/hpgauge/9.png */
    public static final String R_HPGAUGE_9 = "/resources/hpgauge/9.png";

    /** /resources/hpgauge/10.png */
    public static final String R_HPGAUGE_10 = "/resources/hpgauge/10.png";

    /** /resources/hpgauge/11.png */
    public static final String R_HPGAUGE_11 = "/resources/hpgauge/11.png";

    /** /resources/hpgauge/12.png */
    public static final String R_HPGAUGE_12 = "/resources/hpgauge/12.png";

    /** /resources/hpgauge/13.png */
    public static final String R_HPGAUGE_13 = "/resources/hpgauge/13.png";

    /** /resources/hpgauge/14.png */
    public static final String R_HPGAUGE_14 = "/resources/hpgauge/14.png";

    /** /resources/hpgauge/15.png */
    public static final String R_HPGAUGE_15 = "/resources/hpgauge/15.png";

    /** /resources/hpgauge/16.png */
    public static final String R_HPGAUGE_16 = "/resources/hpgauge/16.png";

    /** /resources/hpgauge/17.png */
    public static final String R_HPGAUGE_17 = "/resources/hpgauge/17.png";

    /** /resources/hpgauge/18.png */
    public static final String R_HPGAUGE_18 = "/resources/hpgauge/18.png";

    /** /resources/hpgauge/19.png */
    public static final String R_HPGAUGE_19 = "/resources/hpgauge/19.png";

    /** /resources/hpgauge/20.png */
    public static final String R_HPGAUGE_20 = "/resources/hpgauge/20.png";

    /** /resources/hpgauge/21.png */
    public static final String R_HPGAUGE_21 = "/resources/hpgauge/21.png";

    /** /resources/hpgauge/22.png */
    public static final String R_HPGAUGE_22 = "/resources/hpgauge/22.png";

    /** /resources/hpgauge/23.png */
    public static final String R_HPGAUGE_23 = "/resources/hpgauge/23.png";

    /** /resources/hpgauge/24.png */
    public static final String R_HPGAUGE_24 = "/resources/hpgauge/24.png";

    /** /resources/hpgauge/25.png */
    public static final String R_HPGAUGE_25 = "/resources/hpgauge/25.png";

    /** /resources/hpgauge/26.png */
    public static final String R_HPGAUGE_26 = "/resources/hpgauge/26.png";

    /** /resources/hpgauge/27.png */
    public static final String R_HPGAUGE_27 = "/resources/hpgauge/27.png";

    /** /resources/hpgauge/28.png */
    public static final String R_HPGAUGE_28 = "/resources/hpgauge/28.png";

    /** /resources/hpgauge/29.png */
    public static final String R_HPGAUGE_29 = "/resources/hpgauge/29.png";

    /** /resources/hpgauge/30.png */
    public static final String R_HPGAUGE_30 = "/resources/hpgauge/30.png";

    /** /resources/hpgauge/31.png */
    public static final String R_HPGAUGE_31 = "/resources/hpgauge/31.png";

    /** /resources/hpgauge/32.png */
    public static final String R_HPGAUGE_32 = "/resources/hpgauge/32.png";

    /** /resources/hpgauge/33.png */
    public static final String R_HPGAUGE_33 = "/resources/hpgauge/33.png";

    /** /resources/hpgauge/34.png */
    public static final String R_HPGAUGE_34 = "/resources/hpgauge/34.png";

    /** /resources/hpgauge/35.png */
    public static final String R_HPGAUGE_35 = "/resources/hpgauge/35.png";

    /** /resources/hpgauge/36.png */
    public static final String R_HPGAUGE_36 = "/resources/hpgauge/36.png";

    /** /resources/hpgauge/37.png */
    public static final String R_HPGAUGE_37 = "/resources/hpgauge/37.png";

    /** /resources/hpgauge/38.png */
    public static final String R_HPGAUGE_38 = "/resources/hpgauge/38.png";

    /** /resources/hpgauge/39.png */
    public static final String R_HPGAUGE_39 = "/resources/hpgauge/39.png";

    /** /resources/hpgauge/40.png */
    public static final String R_HPGAUGE_40 = "/resources/hpgauge/40.png";

    /** /resources/hpgauge/41.png */
    public static final String R_HPGAUGE_41 = "/resources/hpgauge/41.png";

    /** /resources/hpgauge/42.png */
    public static final String R_HPGAUGE_42 = "/resources/hpgauge/42.png";

    /** /resources/hpgauge/43.png */
    public static final String R_HPGAUGE_43 = "/resources/hpgauge/43.png";

    /** /resources/hpgauge/44.png */
    public static final String R_HPGAUGE_44 = "/resources/hpgauge/44.png";

    /** /resources/hpgauge/45.png */
    public static final String R_HPGAUGE_45 = "/resources/hpgauge/45.png";

    /** /resources/hpgauge/46.png */
    public static final String R_HPGAUGE_46 = "/resources/hpgauge/46.png";

    /** /resources/hpgauge/47.png */
    public static final String R_HPGAUGE_47 = "/resources/hpgauge/47.png";

    /** /resources/hpgauge/48.png */
    public static final String R_HPGAUGE_48 = "/resources/hpgauge/48.png";

    /** /resources/hpgauge/49.png */
    public static final String R_HPGAUGE_49 = "/resources/hpgauge/49.png";

    /** /resources/hpgauge/50.png */
    public static final String R_HPGAUGE_50 = "/resources/hpgauge/50.png";

    /** HPゲージイメージ */
    public static final String[] R_HPGAUGE_IMAGES = { AppConstants.R_HPGAUGE_0, AppConstants.R_HPGAUGE_1,
            AppConstants.R_HPGAUGE_2, AppConstants.R_HPGAUGE_3, AppConstants.R_HPGAUGE_4, AppConstants.R_HPGAUGE_5,
            AppConstants.R_HPGAUGE_6, AppConstants.R_HPGAUGE_7, AppConstants.R_HPGAUGE_8, AppConstants.R_HPGAUGE_9,
            AppConstants.R_HPGAUGE_10, AppConstants.R_HPGAUGE_11, AppConstants.R_HPGAUGE_12, AppConstants.R_HPGAUGE_13,
            AppConstants.R_HPGAUGE_14, AppConstants.R_HPGAUGE_15, AppConstants.R_HPGAUGE_16, AppConstants.R_HPGAUGE_17,
            AppConstants.R_HPGAUGE_18, AppConstants.R_HPGAUGE_19, AppConstants.R_HPGAUGE_20, AppConstants.R_HPGAUGE_21,
            AppConstants.R_HPGAUGE_22, AppConstants.R_HPGAUGE_23, AppConstants.R_HPGAUGE_24, AppConstants.R_HPGAUGE_25,
            AppConstants.R_HPGAUGE_26, AppConstants.R_HPGAUGE_27, AppConstants.R_HPGAUGE_28, AppConstants.R_HPGAUGE_29,
            AppConstants.R_HPGAUGE_30, AppConstants.R_HPGAUGE_31, AppConstants.R_HPGAUGE_32, AppConstants.R_HPGAUGE_33,
            AppConstants.R_HPGAUGE_34, AppConstants.R_HPGAUGE_35, AppConstants.R_HPGAUGE_36, AppConstants.R_HPGAUGE_37,
            AppConstants.R_HPGAUGE_38, AppConstants.R_HPGAUGE_39, AppConstants.R_HPGAUGE_40, AppConstants.R_HPGAUGE_41,
            AppConstants.R_HPGAUGE_42, AppConstants.R_HPGAUGE_43, AppConstants.R_HPGAUGE_44, AppConstants.R_HPGAUGE_45,
            AppConstants.R_HPGAUGE_46, AppConstants.R_HPGAUGE_47, AppConstants.R_HPGAUGE_48, AppConstants.R_HPGAUGE_49,
            AppConstants.R_HPGAUGE_50 };

    /** 艦隊タブの艦娘ラベルに設定するツールチップテキスト */
    public static final String TOOLTIP_FLEETTAB_SHIP = "HP:{0}/{1} Fuel:{2}/{3} Ammo:{4}/{5}\nNext:{6}exp";

    /** メッセージ 出撃できます。 */
    public static final String MESSAGE_GOOD = "Ready to sortie!";

    /** メッセージ {0} 出撃はできません。 */
    public static final String MESSAGE_BAD = "{0}";

    /** メッセージ 大破している艦娘がいます  */
    public static final String MESSAGE_BADLY_DAMAGE = "There are severely damaged ships.";

    /** メッセージ 入渠中の艦娘がいます  */
    public static final String MESSAGE_BATHWATER = "Ships are being repaired.";

    /** メッセージ 遠征中です。  */
    public static final String MESSAGE_MISSION = "On an expedition.";

    /** メッセージ 疲労している艦娘がいます */
    public static final String MESSAGE_COND = "\nMorale will be fully recovered at {0}";

    /** メッセージ 制空値:{0} */
    public static final String MESSAGE_SEIKU = "\nAir Superiority: {0}";

    /** メッセージ 大破している艦娘がいます、進撃はできません。 */
    public static final String MESSAGE_STOP_SORTIE = "There are severely damaged ships. Stop the sortie!";
}
