/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.dto;

import java.util.Calendar;
import java.util.Date;

import javax.json.JsonObject;

import logbook.internal.MapNames;
import logbook.internal.Ship;

/**
 * 海戦とドロップした艦娘を表します
 */
public final class BattleResultDto extends AbstractDto {

    /** 日付 */
    private final Date battleDate;

    /** 海域名 */
    private final String questName;

    /** ランク */
    private final String rank;

    /** 敵艦隊名 */
    private final String enemyName;

    /** ドロップフラグ */
    private final boolean dropFlag;

    /** 艦種 */
    private final String dropType;

    /** 艦名 */
    private final String dropName;

    /** 戦闘詳細 */
    private final BattleDto battle;

    /**
     * コンストラクター
     * 
     * @param object JSON Object
     * @param battle 戦闘詳細
     */
    public BattleResultDto(JsonObject object, BattleDto battle) {

        this.battleDate = Calendar.getInstance().getTime();
        String jpname = object.getString("api_quest_name");
        this.questName = MapNames.get(jpname);
        this.rank = object.getString("api_win_rank");
        this.enemyName = object.getJsonObject("api_enemy_info").getString("api_deck_name");
        this.dropFlag = object.containsKey("api_get_ship");
        if (this.dropFlag) {
            String id = object.getJsonObject("api_get_ship").getJsonNumber("api_ship_id").toString();
            ShipInfoDto intname = Ship.get(id);
            this.dropType = intname.getType();
            this.dropName = intname.getName();
        } else {
            this.dropType = "";
            this.dropName = "";
        }

        this.battle = battle;
    }

    /**
     * @return 日付
     */
    public Date getBattleDate() {
        return this.battleDate;
    }

    /**
     * @return 海域名
     */
    public String getQuestName() {
        return this.questName;
    }

    /**
     * @return ランク
     */
    public String getRank() {
        return this.rank;
    }

    /**
     * @return 敵艦隊名
     */
    public String getEnemyName() {
        return this.enemyName;
    }

    /**
     * @return ドロップフラグ
     */
    public boolean isDropFlag() {
        return this.dropFlag;
    }

    /**
     * @return 艦種
     */
    public String getDropType() {
        return this.dropType;
    }

    /**
     * @return 艦名
     */
    public String getDropName() {
        return this.dropName;
    }

    /**
     * @return 戦闘詳細
     */
    public BattleDto getBattleDto() {
        return this.battle;
    }
}
