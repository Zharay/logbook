/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.data.context;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.config.GlobalConfig;
import logbook.data.Data;
import logbook.data.DataQueue;
import logbook.dto.AwaitingDecision;
import logbook.dto.BattleDto;
import logbook.dto.BattleResultDto;
import logbook.dto.CreateItemDto;
import logbook.dto.DeckMissionDto;
import logbook.dto.DockDto;
import logbook.dto.GetShipDto;
import logbook.dto.ItemDto;
import logbook.dto.MissionResultDto;
import logbook.dto.NdockDto;
import logbook.dto.ResourceDto;
import logbook.dto.ShipDto;
import logbook.dto.ShipInfoDto;
import logbook.gui.logic.CreateReportLogic;
import logbook.internal.Deck;
import logbook.internal.Ship;
import logbook.internal.ShipStyle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 遠征・入渠などの情報を管理します
 *
 */
public final class GlobalContext {
    /** ロガー */
    private static final Logger LOG = LogManager.getLogger(GlobalContext.class);

    /** ログに表示する日付書式 */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(GlobalConfig.DATE_SHORT_FORMAT);

    /** 装備Map */
    private static Map<Long, ItemDto> itemMap = new ConcurrentSkipListMap<Long, ItemDto>();

    /** 艦娘Map */
    private static Map<Long, ShipDto> shipMap = new ConcurrentSkipListMap<Long, ShipDto>();

    /** 秘書艦 */
    private static ShipDto secretary;

    /** 建造 */
    private static List<GetShipDto> getShipList = new ArrayList<GetShipDto>();

    /** 建造(艦娘名の確定待ち) */
    private static Queue<AwaitingDecision> getShipQueue = new ArrayBlockingQueue<AwaitingDecision>(10);

    /** 建造(投入資源) */
    private static Map<String, ResourceDto> getShipResource = new HashMap<String, ResourceDto>();

    /** 開発 */
    private static List<CreateItemDto> createItemList = new ArrayList<CreateItemDto>();

    /** 開発(装備名の確定待ち) */
    private static Queue<CreateItemDto> createItemQueue = new ArrayBlockingQueue<CreateItemDto>(10);

    /** 海戦・ドロップ */
    private static List<BattleResultDto> battleResultList = new ArrayList<BattleResultDto>();

    /** 遠征結果 */
    private static List<MissionResultDto> missionResultList = new ArrayList<MissionResultDto>();

    /** 司令部Lv */
    private static int hqLevel;

    /** 最大保有可能 艦娘数 */
    private static int maxChara;

    /** 最大保有可能 装備数 */
    private static int maxSlotitem;

    /** 最後に建造を行った建造ドック */
    private static String lastBuildKdock;

    /** 戦闘詳細 */
    private static Queue<BattleDto> battleList = new ArrayBlockingQueue<BattleDto>(1);

    /** 遠征リスト */
    private static DeckMissionDto[] deckMissions = new DeckMissionDto[] { DeckMissionDto.EMPTY, DeckMissionDto.EMPTY,
            DeckMissionDto.EMPTY };

    /** ドック */
    private static Map<String, DockDto> dock = new HashMap<String, DockDto>();

    /** 入渠リスト */
    private static NdockDto[] ndocks = new NdockDto[] { NdockDto.EMPTY, NdockDto.EMPTY, NdockDto.EMPTY,
            NdockDto.EMPTY };

    /** ログキュー */
    private static Queue<String> consoleQueue = new ArrayBlockingQueue<String>(10);

    /**
     * @return 装備Map
     */
    public static Map<Long, ItemDto> getItemMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    /**
     * @return 艦娘Map
     */
    public static Map<Long, ShipDto> getShipMap() {
        return Collections.unmodifiableMap(shipMap);
    }

    /**
     * @return 秘書艦
     */
    public static ShipDto getSecretary() {
        return secretary;
    }

    /**
     * @return 司令部Lv
     */
    public static int hqLevel() {
        return hqLevel;
    }

    /**
     * @return 最大保有可能 艦娘数
     */
    public static int maxChara() {
        return maxChara;
    }

    /**
     * @return 最大保有可能 装備数
     */
    public static int maxSlotitem() {
        return maxSlotitem;
    }

    /**
     * @return 建造艦娘List
     */
    public static List<GetShipDto> getGetshipList() {
        return Collections.unmodifiableList(getShipList);
    }

    /**
     * @return 開発アイテムList
     */
    public static List<CreateItemDto> getCreateItemList() {
        return Collections.unmodifiableList(createItemList);
    }

    /**
     * @return 海戦・ドロップList
     */
    public static List<BattleResultDto> getBattleResultList() {
        return Collections.unmodifiableList(battleResultList);
    }

    /**
     * @return 遠征結果
     */
    public static List<MissionResultDto> getMissionResultList() {
        return Collections.unmodifiableList(missionResultList);
    }

    /**
     * @return 遠征リスト
     */
    public static DeckMissionDto[] getDeckMissions() {
        return deckMissions;
    }

    /**
     * @return 入渠リスト
     */
    public static NdockDto[] getNdocks() {
        return ndocks;
    }

    /**
     * @return ドック
     */
    public static DockDto getDock(String id) {
        return dock.get(id);
    }

    /**
     * @return ログメッセージ
     */
    public static String getConsoleMessage() {
        return consoleQueue.poll();
    }

    /**
     * 情報を更新します
     * 
     * @return 更新する情報があった場合trueを返します
     */
    public static boolean updateContext() {
        boolean update = false;
        Data data;
        while ((data = DataQueue.poll()) != null) {
            update = true;
            // json保存設定
            if (GlobalConfig.getStoreJson()) {
                doStoreJson(data);
            }
            switch (data.getDataType()) {
            // 保有装備
            case SLOTITEM_MEMBER:
                doSlotitemMember(data);
                break;
            // 保有艦
            case SHIP3:
                doShip3(data);
                break;
            // 保有艦
            case SHIP2:
                doShip2(data);
                break;
            // 基本
            case BASIC:
                doBasic(data);
                break;
            // 遠征
            case DECK_PORT:
                doDeckPort(data);
                break;
            // 遠征(帰還)
            case MISSION_RESULT:
                doMissionResult(data);
                break;
            // 入渠
            case NDOCK:
                doNdock(data);
                break;
            // 建造
            case CREATESHIP:
                doCreateship(data);
                break;
            // 建造ドック
            case KDOCK:
                doKdock(data);
                break;
            // 建造(入手)
            case GETSHIP:
                doGetship(data);
                break;
            // 装備開発
            case CREATEITEM:
                doCreateitem(data);
                break;
            // 海戦
            case BATTLE:
            case BATTLE_MIDNIGHT:
            case BATTLE_SP_MIDNIGHT:
            case BATTLE_NIGHT_TO_DAY:
                doBattle(data);
                break;
            // 海戦結果
            case BATTLERESULT:
                doBattleresult(data);
                break;
            // 艦隊
            case DECK:
                doDeck(data);
                break;
            // 艦娘一覧
            case SHIP_MASTER:
                doShipMaster(data);
                break;
            default:
                break;
            }
        }
        return update;
    }

    /**
     * JSONオブジェクトを保存する
     * @param data
     */
    private static void doStoreJson(Data data) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss.SSS");
            Date time = Calendar.getInstance().getTime();
            // ファイル名
            String fname = new StringBuilder().append(format.format(time)).append("_").append(data.getDataType())
                    .append(".json").toString();
            // ファイルパス
            File file = new File(FilenameUtils.concat(GlobalConfig.getStoreJsonPath(), fname));

            FileUtils.write(file, data.getJsonObject().toString());
        } catch (IOException e) {
            LOG.warn("JSONオブジェクトを保存するに失敗しました", e);
            LOG.warn(data);
        }

    }

    /**
     * 海戦情報を更新します
     * @param data
     */
    private static void doBattle(Data data) {
        try {
            if (battleList.size() == 0) {
                JsonObject apidata = data.getJsonObject().getJsonObject("api_data");
                battleList.add(new BattleDto(apidata));

                addConsole("海戦情報を更新しました");
            }
        } catch (Exception e) {
            LOG.warn("海戦情報を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 海戦情報を更新します
     * @param data
     */
    private static void doBattleresult(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");
            BattleResultDto dto = new BattleResultDto(apidata, battleList.poll());
            battleResultList.add(dto);
            CreateReportLogic.storeBattleResultReport(dto);

            addConsole("海戦情報を更新しました");
        } catch (Exception e) {
            LOG.warn("海戦情報を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 建造(投入資源)情報を更新します
     * @param data
     */
    private static void doCreateship(Data data) {
        try {
            String kdockid = data.getField("api_kdock_id");
            // 投入資源
            ResourceDto resource = new ResourceDto(
                    data.getField("api_large_flag"),
                    data.getField("api_item1"),
                    data.getField("api_item2"),
                    data.getField("api_item3"),
                    data.getField("api_item4"),
                    data.getField("api_item5"),
                    secretary, hqLevel
                    );
            lastBuildKdock = kdockid;
            getShipResource.put(kdockid, resource);
            GlobalConfig.setCreateShipResource(kdockid, resource);

            addConsole("建造(投入資源)情報を更新しました");
        } catch (Exception e) {
            LOG.warn("建造(投入資源)情報を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 建造を更新します
     * @param data
     */
    private static void doKdock(Data data) {
        try {
            // 建造ドックの空きをカウントします
            if (lastBuildKdock != null) {
                ResourceDto resource = getShipResource.get(lastBuildKdock);
                if (resource != null) {
                    int freecount = 0;
                    JsonArray apidata = data.getJsonObject().getJsonArray("api_data");
                    for (int i = 0; i < apidata.size(); i++) {
                        int state = ((JsonObject) apidata.get(i)).getJsonNumber("api_state").intValue();
                        if (state == 0) {
                            freecount++;
                        }
                    }
                    // 建造ドックの空きをセットします
                    resource.setFreeDock(Integer.toString(freecount));
                    GlobalConfig.setCreateShipResource(lastBuildKdock, resource);
                }
            }
            addConsole("建造を更新しました");
        } catch (Exception e) {
            LOG.warn("建造を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 建造(入手)情報を更新します
     * @param data
     */
    private static void doGetship(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");
            long shipid = apidata.getJsonNumber("api_id").longValue();
            String dock = data.getField("api_kdock_id");

            getShipQueue.add(new AwaitingDecision(shipid, dock));

            addConsole("建造(入手)情報を更新しました");
        } catch (Exception e) {
            LOG.warn("建造(入手)情報を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 装備開発情報を更新します
     * 
     * @param data
     */
    private static void doCreateitem(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");

            // 投入資源
            ResourceDto resources = new ResourceDto(data.getField("api_item1"), data.getField("api_item2"),
                    data.getField("api_item3"), data.getField("api_item4"), secretary, hqLevel);

            CreateItemDto item = new CreateItemDto(apidata, resources);
            if (item.isCreateFlag()) {
                createItemQueue.add(item);
            } else {
                createItemList.add(item);
                CreateReportLogic.storeCreateItemReport(item);
            }

            addConsole("装備開発情報を更新しました");
        } catch (Exception e) {
            LOG.warn("装備開発情報を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 保有装備を更新します
     * 
     * @param data
     */
    private static void doSlotitemMember(Data data) {
        try {
            JsonArray apidata = data.getJsonObject().getJsonArray("api_data");
            // 破棄
            itemMap.clear();
            for (int i = 0; i < apidata.size(); i++) {
                JsonObject object = (JsonObject) apidata.get(i);
                ItemDto item = new ItemDto(object);
                itemMap.put(Long.valueOf(item.getId()), item);
            }
            // 確定待ちの開発装備がある場合、装備の名前を確定させます
            CreateItemDto createitem;
            while ((createitem = createItemQueue.poll()) != null) {
                ItemDto item = itemMap.get(Long.valueOf(createitem.getId()));
                if (item != null) {
                    createitem.setName(item.getName());
                    createitem.setType(item.getType());
                    createItemList.add(createitem);
                    CreateReportLogic.storeCreateItemReport(createitem);
                } else {
                    createItemQueue.add(createitem);
                }
            }

            addConsole("保有装備情報を更新しました");
        } catch (Exception e) {
            LOG.warn("保有装備を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 保有艦娘を更新します
     * 
     * @param data
     */
    private static void doShip3(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");

            JsonArray shipdata = apidata.getJsonArray("api_ship_data");
            // 情報を破棄
            shipMap.clear();
            for (int i = 0; i < shipdata.size(); i++) {
                ShipDto ship = new ShipDto((JsonObject) shipdata.get(i));
                shipMap.put(Long.valueOf(ship.getId()), ship);
            }

            // 艦隊を設定
            doDeck(apidata.getJsonArray("api_deck_data"));

            // 確定待ちの艦娘がある場合、艦娘の名前を確定させます
            AwaitingDecision shipInfo;
            while ((shipInfo = getShipQueue.poll()) != null) {
                ShipDto getShip = shipMap.get(Long.valueOf(shipInfo.getShipid()));
                if (getShip != null) {
                    // 投入資源を取得する
                    ResourceDto resource = getShipResource.get(shipInfo.getDock());
                    if (resource == null) {
                        resource = GlobalConfig.getCreateShipResource(shipInfo.getDock());
                    }
                    GetShipDto dto = new GetShipDto(getShip, resource);
                    getShipList.add(dto);
                    CreateReportLogic.storeCreateShipReport(dto);
                    // 投入資源を除去する
                    getShipResource.remove(shipInfo.getDock());
                    GlobalConfig.removeCreateShipResource(shipInfo.getDock());
                } else {
                    getShipQueue.add(shipInfo);
                }
            }

            addConsole("保有艦娘情報を更新しました");
        } catch (Exception e) {
            LOG.warn("保有艦娘を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 保有艦娘を更新します
     * 
     * @param data
     */
    private static void doShip2(Data data) {
        try {
            JsonArray apidata = data.getJsonObject().getJsonArray("api_data");
            // 情報を破棄
            shipMap.clear();
            for (int i = 0; i < apidata.size(); i++) {
                ShipDto ship = new ShipDto((JsonObject) apidata.get(i));
                shipMap.put(Long.valueOf(ship.getId()), ship);
            }

            // 艦隊を設定
            doDeck(data.getJsonObject().getJsonArray("api_data_deck"));

            // 確定待ちの艦娘がある場合、艦娘の名前を確定させます
            AwaitingDecision shipInfo;
            while ((shipInfo = getShipQueue.poll()) != null) {
                ShipDto getShip = shipMap.get(Long.valueOf(shipInfo.getShipid()));
                if (getShip != null) {
                    // 投入資源を取得する
                    ResourceDto resource = getShipResource.get(shipInfo.getDock());
                    if (resource == null) {
                        resource = GlobalConfig.getCreateShipResource(shipInfo.getDock());
                    }
                    GetShipDto dto = new GetShipDto(getShip, resource);
                    getShipList.add(dto);
                    CreateReportLogic.storeCreateShipReport(dto);
                    // 投入資源を除去する
                    getShipResource.remove(shipInfo.getDock());
                    GlobalConfig.removeCreateShipResource(shipInfo.getDock());
                } else {
                    getShipQueue.add(shipInfo);
                }
            }

            addConsole("保有艦娘情報を更新しました");
        } catch (Exception e) {
            LOG.warn("保有艦娘を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 艦隊を更新します
     * 
     * @param data
     */
    private static void doDeck(Data data) {
        try {
            JsonArray apidata = data.getJsonObject().getJsonArray("api_data");
            // 艦隊IDをクリアします
            for (DockDto dockdto : dock.values()) {
                for (ShipDto ship : dockdto.getShips()) {
                    ship.setFleetid("");
                }
            }
            doDeck(apidata);
            addConsole("艦隊を更新しました");
        } catch (Exception e) {
            LOG.warn("艦隊を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 艦隊を設定します
     * 
     * @param apidata
     */
    private static void doDeck(JsonArray apidata) {
        dock.clear();
        for (int i = 0; i < apidata.size(); i++) {
            JsonObject jsonObject = (JsonObject) apidata.get(i);
            String fleetid = Long.toString(jsonObject.getJsonNumber("api_id").longValue());
            String name = jsonObject.getString("api_name");
            JsonArray apiship = jsonObject.getJsonArray("api_ship");

            DockDto dockdto = new DockDto(fleetid, name);
            dock.put(fleetid, dockdto);

            for (int j = 0; j < apiship.size(); j++) {
                Long shipid = Long.valueOf(((JsonNumber) apiship.get(j)).longValue());
                ShipDto ship = shipMap.get(shipid);

                if (ship != null) {
                    dockdto.addShip(ship);

                    if ((i == 0) && (j == 0)) {
                        if ((secretary == null) || (ship.getId() != secretary.getId())) {
                            addConsole(ship.getName() + "(Lv" + ship.getLv() + ")" + " が秘書艦に任命されました");
                        }
                        // 秘書艦を設定
                        secretary = ship;
                    }
                    // 艦隊IDを設定
                    ship.setFleetid(fleetid);
                }
            }
        }
    }

    /**
     * 司令部を更新する
     * 
     * @param data
     */
    private static void doBasic(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");

            // 指令部Lv
            hqLevel = apidata.getJsonNumber("api_level").intValue();
            // 最大所有艦娘数
            maxChara = apidata.getJsonNumber("api_max_chara").intValue();
            // 最大所有装備数
            maxSlotitem = apidata.getJsonNumber("api_max_slotitem").intValue();

            addConsole("司令部を更新しました");
        } catch (Exception e) {
            LOG.warn("司令部を更新するに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 遠征を更新します
     * 
     * @param data
     */
    private static void doDeckPort(Data data) {
        try {
            JsonArray apidata = data.getJsonObject().getJsonArray("api_data");

            deckMissions = new DeckMissionDto[] { DeckMissionDto.EMPTY, DeckMissionDto.EMPTY, DeckMissionDto.EMPTY };

            for (int i = 1; i < apidata.size(); i++) {
                JsonObject object = (JsonObject) apidata.get(i);
                String name = object.getString("api_name");
                JsonArray jmission = object.getJsonArray("api_mission");

                long section = ((JsonNumber) jmission.get(1)).longValue();
                String mission = Deck.get(Long.toString(section));
                long milis = ((JsonNumber) jmission.get(2)).longValue();
                long fleetid = object.getJsonNumber("api_id").longValue();

                Set<Long> ships = new LinkedHashSet<Long>();
                JsonArray shiparray = object.getJsonArray("api_ship");
                for (JsonValue jsonValue : shiparray) {
                    long shipid = ((JsonNumber) jsonValue).longValue();
                    if (shipid != -1) {
                        ships.add(shipid);
                    }
                }

                Date time = null;
                if (milis > 0) {
                    time = new Date(milis);
                }
                deckMissions[i - 1] = new DeckMissionDto(name, mission, time, fleetid, ships);
            }

            addConsole("遠征情報を更新しました");
        } catch (Exception e) {
            LOG.warn("遠征を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 遠征(帰還)を更新します
     * 
     * @param data
     */
    private static void doMissionResult(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");

            MissionResultDto result = new MissionResultDto();

            int clearResult = apidata.getJsonNumber("api_clear_result").intValue();
            result.setClearResult(clearResult);
            result.setQuestName(apidata.getString("api_quest_name"));

            if (clearResult != 0) {
                JsonArray material = apidata.getJsonArray("api_get_material");
                result.setFuel(material.getJsonNumber(0).toString());
                result.setAmmo(material.getJsonNumber(1).toString());
                result.setMetal(material.getJsonNumber(2).toString());
                result.setBauxite(material.getJsonNumber(3).toString());
            }

            missionResultList.add(result);

            addConsole("遠征(帰還)情報を更新しました");
        } catch (Exception e) {
            LOG.warn("遠征(帰還)を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 入渠を更新します
     * @param data
     */
    private static void doNdock(Data data) {
        try {
            JsonArray apidata = data.getJsonObject().getJsonArray("api_data");

            ndocks = new NdockDto[] { NdockDto.EMPTY, NdockDto.EMPTY, NdockDto.EMPTY, NdockDto.EMPTY };

            for (int i = 0; i < apidata.size(); i++) {
                JsonObject object = (JsonObject) apidata.get(i);
                long id = object.getJsonNumber("api_ship_id").longValue();
                long milis = object.getJsonNumber("api_complete_time").longValue();

                Date time = null;
                if (milis > 0) {
                    time = new Date(milis);
                }
                ndocks[i] = new NdockDto(id, time);
            }

            addConsole("入渠情報を更新しました");
        } catch (Exception e) {
            LOG.warn("入渠を更新しますに失敗しました", e);
            LOG.warn(data);
        }
    }

    /**
     * 艦娘一覧を更新します
     * 
     * @param data
     */
    private static void doShipMaster(Data data) {
        JsonArray apidata = data.getJsonObject().getJsonArray("api_data");
        for (int i = 0; i < apidata.size(); i++) {
            JsonObject object = (JsonObject) apidata.get(i);

            String id = object.getJsonNumber("api_id").toString();

            Ship.set(id, toShipInfoDto(object));
        }

        addConsole("艦娘一覧を更新しました");
    }

    /**
     * 艦娘を作成します
     * 
     * @param object
     * @return
     */
    private static ShipInfoDto toShipInfoDto(JsonObject object) {
        String name = object.getString("api_name");

        if ("なし".equals(name)) {
            return ShipInfoDto.EMPTY;
        }

        String type = ShipStyle.get(object.getJsonNumber("api_stype").toString());
        String flagship = object.getString("api_yomi");
        if ("-".equals(flagship)) {
            flagship = "";
        }
        int afterlv = object.getJsonNumber("api_afterlv").intValue();
        int maxBull = 0;
        if (object.containsKey("api_bull_max")) {
            maxBull = object.getJsonNumber("api_bull_max").intValue();
        }
        int maxFuel = 0;
        if (object.containsKey("api_fuel_max")) {
            maxFuel = object.getJsonNumber("api_fuel_max").intValue();
        }
        return new ShipInfoDto(name, type, flagship, afterlv, maxBull, maxFuel);
    }

    private static void addConsole(Object message) {
        consoleQueue.add(FORMAT.format(Calendar.getInstance().getTime()) + "  " + message.toString());
    }
}
