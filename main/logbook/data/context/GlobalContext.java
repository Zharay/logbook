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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.annotation.CheckForNull;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import logbook.config.AppConfig;
import logbook.config.KdockConfig;
import logbook.constants.AppConstants;
import logbook.data.Data;
import logbook.data.DataQueue;
import logbook.dto.BattleDto;
import logbook.dto.BattleResultDto;
import logbook.dto.CreateItemDto;
import logbook.dto.DeckMissionDto;
import logbook.dto.DockDto;
import logbook.dto.GetShipDto;
import logbook.dto.ItemDto;
import logbook.dto.MaterialDto;
import logbook.dto.MissionResultDto;
import logbook.dto.NdockDto;
import logbook.dto.QuestDto;
import logbook.dto.ResourceDto;
import logbook.dto.ShipDto;
import logbook.dto.ShipInfoDto;
import logbook.gui.logic.CreateReportLogic;
import logbook.internal.Deck;
import logbook.internal.Item;
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

    /** 装備Map */
    private static Map<Long, ItemDto> itemMap = new ConcurrentSkipListMap<Long, ItemDto>();

    /** 艦娘Map */
    private static Map<Long, ShipDto> shipMap = new ConcurrentSkipListMap<Long, ShipDto>();

    /** 秘書艦 */
    private static ShipDto secretary;

    /** 建造 */
    private static List<GetShipDto> getShipList = new ArrayList<GetShipDto>();

    /** 建造(投入資源) */
    private static Map<String, ResourceDto> getShipResource = new HashMap<String, ResourceDto>();

    /** 開発 */
    private static List<CreateItemDto> createItemList = new ArrayList<CreateItemDto>();

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

    /** 任務Map */
    private static SortedMap<Integer, QuestDto> questMap = new ConcurrentSkipListMap<Integer, QuestDto>();

    /** 出撃中か */
    private static boolean[] isSortie = new boolean[4];

    /** ログキュー */
    private static Queue<String> consoleQueue = new ArrayBlockingQueue<String>(10);

    /** 保有資源・資材 */
    private static MaterialDto material = null;

    /** 最後に資源ログに追加した時間 */
    private static Date materialLogLastUpdate = null;

    /**
     * @return 装備Map
     */
    public static Map<Long, ItemDto> getItemMap() {
        return Collections.unmodifiableMap(itemMap);
    }

    /**
     * 装備を復元する
     * @param map
     */
    public static void setItemMap(Map<Long, String> map) {
        for (Entry<Long, String> entry : map.entrySet()) {
            String id = entry.getValue();
            ItemDto item = Item.get(id);
            if (item != null) {
                itemMap.put(entry.getKey(), item);
            }
        }
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
     * 艦娘が入渠しているかを調べます
     * 
     * @param ship 艦娘
     * @return 入渠している場合true
     */
    public static boolean isNdock(ShipDto ship) {
        return isNdock(ship.getId());
    }

    /**
     * 艦娘が入渠しているかを調べます
     * @param ship 艦娘ID
     * @return 入渠している場合true
     */
    public static boolean isNdock(long ship) {
        for (NdockDto ndock : ndocks) {
            if (ship == ndock.getNdockid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 艦隊が遠征中かを調べます
     * 
     * @param 
     */
    public static boolean isMission(String idstr) {
        int id = Integer.parseInt(idstr);
        for (int i = 0; i < deckMissions.length; i++) {
            if ((deckMissions[i].getMission() != null) && (deckMissions[i].getFleetid() == id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return ドック
     */
    public static DockDto getDock(String id) {
        return dock.get(id);
    }

    /**
     * 任務を取得します
     * @return 任務
     */
    public static Map<Integer, QuestDto> getQuest() {
        return Collections.unmodifiableMap(questMap);
    }

    /**
     * 出撃中かを調べます
     * @return 出撃中
     */
    public static boolean isSortie(String idstr) {
        int id = Integer.parseInt(idstr);
        return isSortie[id - 1];
    }

    /**
     * 保有資材を取得します
     * @return 保有資材
     */
    @CheckForNull
    public static MaterialDto getMaterial() {
        return material;
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
            if (AppConfig.get().isStoreJson()) {
                doStoreJson(data);
            }
            switch (data.getDataType()) {
            // 補給
            case CHARGE:
                doCharge(data);
                break;
            // 編成
            case CHANGE:
                doChange(data);
                break;
            // 母港
            case PORT:
                doPort(data);
                break;
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
            // 資材
            case MATERIAL:
                doMaterial(data);
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
            case CREATE_SHIP:
                doCreateship(data);
                break;
            // 建造ドック
            case KDOCK:
                doKdock(data);
                break;
            // 建造(入手)
            case GET_SHIP:
                doGetship(data);
                break;
            // 装備開発
            case CREATE_ITEM:
                doCreateitem(data);
                break;
            // 解体
            case DESTROY_SHIP:
                doDestroyShip(data);
                break;
            // 廃棄
            case DESTROY_ITEM2:
                doDestroyItem2(data);
                break;
            // 近代化改修
            case POWERUP:
                doPowerup(data);
                break;
            // 海戦
            case BATTLE:
                doBattle(data);
                break;
            // 海戦
            case BATTLE_MIDNIGHT:
                doBattle(data);
                break;
            // 海戦
            case BATTLE_SP_MIDNIGHT:
                doBattle(data);
                break;
            // 海戦
            case BATTLE_NIGHT_TO_DAY:
                doBattle(data);
                break;
            // 海戦結果
            case BATTLE_RESULT:
                doBattleresult(data);
                break;
            // 艦隊
            case DECK:
                doDeck(data);
                break;
            // 出撃
            case START:
                doStart(data);
                break;
            // 任務
            case QUEST_LIST:
                doQuest(data);
                break;
            // 任務消化
            case QUEST_CLEAR:
                doQuestClear(data);
                break;
            // 設定
            case START2:
                doStart2(data);
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
            File file = new File(FilenameUtils.concat(AppConfig.get().getStoreJsonPath(), fname));

            FileUtils.write(file, data.getJsonObject().toString());
        } catch (IOException e) {
            LOG.warn("Failed to save the JSON object", e);
            LOG.warn(data);
        }

    }

    /**
     * 補給を更新します
     * @param data
     */
    private static void doCharge(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");
            if (apidata != null) {
                JsonArray ships = apidata.getJsonArray("api_ship");
                for (JsonValue shipval : ships) {
                    JsonObject shipobj = (JsonObject) shipval;

                    Long shipid = shipobj.getJsonNumber("api_id").longValue();
                    int fuel = shipobj.getJsonNumber("api_fuel").intValue();
                    int bull = shipobj.getJsonNumber("api_bull").intValue();

                    ShipDto ship = shipMap.get(shipid);
                    if (ship != null) {
                        ship.setFuel(fuel);
                        ship.setBull(bull);

                        String fleetid = ship.getFleetid();
                        if (fleetid != null) {
                            DockDto dockdto = dock.get(fleetid);
                            if (dockdto != null) {
                                dockdto.setUpdate(true);
                            }
                        }
                    }
                }
                addConsole("Supplies has been updated");
            }
        } catch (Exception e) {
            LOG.warn("Failed to update supplies.", e);
            LOG.warn(data);
        }
    }

    /**
     * 編成を更新します
     * @param data
     */
    private static void doChange(Data data) {
        try {
            String fleetid = data.getField("api_id");
            long shipid = Long.parseLong(data.getField("api_ship_id"));
            int shipidx = Integer.parseInt(data.getField("api_ship_idx"));

            DockDto dockdto = dock.get(fleetid);

            if (dockdto != null) {
                List<ShipDto> ships = dockdto.getShips();

                DockDto newdock = new DockDto(dockdto.getId(), dockdto.getName());
                if (shipidx == -1) {
                    // 旗艦以外解除
                    newdock.addShip(ships.get(0));
                } else {
                    // 入れ替えまたは外す
                    // 入れ替え後の艦娘(外す場合はnull)
                    ShipDto rship = shipMap.get(shipid);
                    ShipDto[] shiparray = new ShipDto[7];

                    for (int i = 0; i < ships.size(); i++) {
                        shiparray[i] = ships.get(i);
                    }
                    shiparray[shipidx] = rship;

                    for (ShipDto shipdto : shiparray) {
                        if (shipdto != null) {
                            newdock.addShip(shipdto);
                        }
                    }
                }
                dock.put(fleetid, newdock);
            }
        } catch (Exception e) {
            LOG.warn("Failed to update organization.", e);
            LOG.warn(data);
        }
    }

    /**
     * 母港を更新します
     * @param data
     */
    private static void doPort(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");
            if (apidata != null) {

                // 基本情報を更新する
                JsonObject apiBasic = apidata.getJsonObject("api_basic");
                doBasicSub(apiBasic);
                addConsole("Updated HQ");

                // 保有資材を更新する
                JsonArray apiMaterial = apidata.getJsonArray("api_material");
                doMaterialSub(apiMaterial);
                addConsole("Updated materials");

                // 保有艦娘を更新する
                JsonArray apiShip = apidata.getJsonArray("api_ship");
                for (int i = 0; i < apiShip.size(); i++) {
                    ShipDto ship = new ShipDto((JsonObject) apiShip.get(i));
                    shipMap.put(Long.valueOf(ship.getId()), ship);
                }
                JsonArray apiDeckPort = apidata.getJsonArray("api_deck_port");
                doDeck(apiDeckPort);
                addConsole("Ship list updated");

                // 入渠の状態を更新する
                JsonArray apiNdock = apidata.getJsonArray("api_ndock");
                doNdockSub(apiNdock);
                addConsole("Updated dock information");

                // 遠征の状態を更新する
                deckMissions = new DeckMissionDto[] { DeckMissionDto.EMPTY, DeckMissionDto.EMPTY, DeckMissionDto.EMPTY };
                for (int i = 1; i < apiDeckPort.size(); i++) {
                    JsonObject object = (JsonObject) apiDeckPort.get(i);
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
                addConsole("Expeditions updated");
            }
        } catch (Exception e) {
            LOG.warn("Failed to update homeport", e);
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

                addConsole("Battle information updated");
            }
        } catch (Exception e) {
            LOG.warn("Battle information update failed", e);
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

            addConsole("Battle information updated");
        } catch (Exception e) {
            LOG.warn("Battle information update failed", e);
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
            KdockConfig.store(kdockid, resource);

            addConsole("Construction information updated");
        } catch (Exception e) {
            LOG.warn("Construction information update failed", e);
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
                    KdockConfig.store(lastBuildKdock, resource);
                }
            }
            addConsole("Construction information updated");
        } catch (Exception e) {
            LOG.warn("Construction information update failed", e);
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
            String dock = data.getField("api_kdock_id");

            // 艦娘の装備を追加します
            JsonArray slotitem = apidata.getJsonArray("api_slotitem");
            for (int i = 0; i < slotitem.size(); i++) {
                JsonObject object = (JsonObject) slotitem.get(i);
                String typeid = object.getJsonNumber("api_slotitem_id").toString();
                Long id = object.getJsonNumber("api_id").longValue();
                ItemDto item = Item.get(typeid);
                if (item != null) {
                    itemMap.put(id, item);
                }
            }
            // 艦娘を追加します
            JsonObject apiShip = apidata.getJsonObject("api_ship");
            ShipDto ship = new ShipDto(apiShip);
            shipMap.put(Long.valueOf(ship.getId()), ship);
            // 投入資源を取得する
            ResourceDto resource = getShipResource.get(dock);
            if (resource == null) {
                resource = KdockConfig.load(dock);
            }
            GetShipDto dto = new GetShipDto(ship, resource);
            getShipList.add(dto);
            CreateReportLogic.storeCreateShipReport(dto);
            // 投入資源を除去する
            getShipResource.remove(dock);
            KdockConfig.remove(dock);

            addConsole("Construction information updated");
        } catch (Exception e) {
            LOG.warn("Construction information update failed", e);
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

            CreateItemDto createitem = new CreateItemDto(apidata, resources);
            if (createitem.isCreateFlag()) {
                JsonObject object = apidata.getJsonObject("api_slot_item");
                String typeid = object.getJsonNumber("api_slotitem_id").toString();
                Long id = object.getJsonNumber("api_id").longValue();
                ItemDto item = Item.get(typeid);
                if (item != null) {
                    itemMap.put(id, item);

                    createitem.setName(item.getName());
                    createitem.setType(item.getType());
                    createItemList.add(createitem);
                }
            } else {
                createItemList.add(createitem);
            }
            CreateReportLogic.storeCreateItemReport(createitem);

            addConsole("Craft information updated");
        } catch (Exception e) {
            LOG.warn("Craft information update failed", e);
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
                String typeid = object.getJsonNumber("api_slotitem_id").toString();
                Long id = object.getJsonNumber("api_id").longValue();
                ItemDto item = Item.get(typeid);
                if (item != null) {
                    itemMap.put(id, item);
                }
            }

            addConsole("Equipment list updated");
        } catch (Exception e) {
            LOG.warn("Equipment list update failed", e);
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
            // 出撃中ではない
            Arrays.fill(isSortie, false);
            // 情報を破棄
            shipMap.clear();
            for (int i = 0; i < shipdata.size(); i++) {
                ShipDto ship = new ShipDto((JsonObject) shipdata.get(i));
                shipMap.put(Long.valueOf(ship.getId()), ship);
            }
            // 艦隊を設定
            doDeck(apidata.getJsonArray("api_deck_data"));

            addConsole("Ship information updated");
        } catch (Exception e) {
            LOG.warn("Ship information update failed", e);
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

            addConsole("Ship information updated");
        } catch (Exception e) {
            LOG.warn("Ship information update failed", e);
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
            addConsole("Fleet info updated");
        } catch (Exception e) {
            LOG.warn("Fleet info update failed", e);
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
                            addConsole(ship.getName() + "(Lv" + ship.getLv() + ")" + " has been appointed as the secretary");
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
     * 艦娘を解体します
     * @param data
     */
    private static void doDestroyShip(Data data) {
        try {
            Long shipid = Long.parseLong(data.getField("api_ship_id"));
            ShipDto ship = shipMap.get(shipid);
            if (ship != null) {
                // 持っている装備を廃棄する
                List<Long> items = ship.getItemId();
                for (Long item : items) {
                    itemMap.remove(item);
                }
                // 艦娘を外す
                shipMap.remove(ship.getId());
            }

            addConsole("Ship dismantle updated");
        } catch (Exception e) {
            LOG.warn("Failed to update ship dismantle", e);
            LOG.warn(data);
        }
    }

    /**
     * 装備を廃棄します
     * @param data
     */
    private static void doDestroyItem2(Data data) {
        try {
            String itemids = data.getField("api_slotitem_ids");
            for (String itemid : itemids.split(",")) {
                Long item = Long.parseLong(itemid);
                itemMap.remove(item);
            }
            addConsole("Destroyed equipment updated");
        } catch (Exception e) {
            LOG.warn("Failed to update destroyed equipment", e);
            LOG.warn(data);
        }
    }

    /**
     * 近代化改修します
     * @param data
     */
    private static void doPowerup(Data data) {
        try {
            String shipids = data.getField("api_id_items");
            for (String shipid : shipids.split(",")) {
                ShipDto ship = shipMap.get(Long.parseLong(shipid));
                if (ship != null) {
                    // 持っている装備を廃棄する
                    List<Long> items = ship.getItemId();
                    for (Long item : items) {
                        itemMap.remove(item);
                    }
                    // 艦娘を外す
                    shipMap.remove(ship.getId());
                }
            }
            addConsole("Equipment updated");
        } catch (Exception e) {
            LOG.warn("Failed to update equipment", e);
            LOG.warn(data);
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
            doBasicSub(apidata);

            addConsole("Updated HQ");
        } catch (Exception e) {
            LOG.warn("Failed to update HQ", e);
            LOG.warn(data);
        }
    }

    /**
     * 司令部を更新する
     * 
     * @param apidata
     */
    private static void doBasicSub(JsonObject apidata) {
        // 指令部Lv
        hqLevel = apidata.getJsonNumber("api_level").intValue();
        // 最大所有艦娘数
        maxChara = apidata.getJsonNumber("api_max_chara").intValue();
        // 最大所有装備数
        maxSlotitem = apidata.getJsonNumber("api_max_slotitem").intValue();
    }

    /**
     * 保有資材を更新する
     * 
     * @param data
     */
    private static void doMaterial(Data data) {
        try {
            JsonArray apidata = data.getJsonObject().getJsonArray("api_data");

            doMaterialSub(apidata);

            addConsole("Materials updated");
        } catch (Exception e) {
            LOG.warn("Failed to update materials", e);
            LOG.warn(data);
        }
    }

    /**
     * 保有資材を更新する
     * 
     * @param apidata
     */
    private static void doMaterialSub(JsonArray apidata) {
        Date time = Calendar.getInstance().getTime();
        MaterialDto dto = new MaterialDto();
        dto.setTime(time);

        for (JsonValue value : apidata) {
            JsonObject entry = (JsonObject) value;

            switch (entry.getInt("api_id")) {
            case AppConstants.MATERIAL_FUEL:
                dto.setFuel(entry.getInt("api_value"));
                break;
            case AppConstants.MATERIAL_AMMO:
                dto.setAmmo(entry.getInt("api_value"));
                break;
            case AppConstants.MATERIAL_METAL:
                dto.setMetal(entry.getInt("api_value"));
                break;
            case AppConstants.MATERIAL_BAUXITE:
                dto.setBauxite(entry.getInt("api_value"));
                break;
            case AppConstants.MATERIAL_BURNER:
                dto.setBurner(entry.getInt("api_value"));
                break;
            case AppConstants.MATERIAL_BUCKET:
                dto.setBucket(entry.getInt("api_value"));
                break;
            case AppConstants.MATERIAL_RESEARCH:
                dto.setResearch(entry.getInt("api_value"));
                break;
            default:
                break;
            }
        }
        material = dto;

        // 資材ログに書き込む
        if ((materialLogLastUpdate == null)
                || (((time.getTime() - materialLogLastUpdate.getTime()) / 1000) >
                AppConfig.get().getMaterialLogInterval())) {
            CreateReportLogic.storeMaterialReport(material);

            materialLogLastUpdate = time;
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

            CreateReportLogic.storeCreateMissionReport(result);
            missionResultList.add(result);

            addConsole("Expedition result info updated");
        } catch (Exception e) {
            LOG.warn("Expedition result info update failed", e);
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

            doNdockSub(apidata);

            addConsole("Dock information updated");
        } catch (Exception e) {
            LOG.warn("Failed to update dock information", e);
            LOG.warn(data);
        }
    }

    /**
     * 入渠を更新します
     * @param apidata
     */
    private static void doNdockSub(JsonArray apidata) {
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
    }

    /**
     * 任務を更新します
     * 
     * @param data
     */
    private static void doQuest(Data data) {
        try {
            JsonObject apidata = data.getJsonObject().getJsonObject("api_data");
            JsonArray apilist = apidata.getJsonArray("api_list");
            for (JsonValue value : apilist) {
                if (value instanceof JsonObject) {
                    JsonObject questobject = (JsonObject) value;
                    // 任務を作成
                    QuestDto quest = new QuestDto();
                    quest.setNo(questobject.getInt("api_no"));
                    quest.setCategory(questobject.getInt("api_category"));
                    quest.setType(questobject.getInt("api_type"));
                    quest.setState(questobject.getInt("api_state"));
                    quest.setTitle(questobject.getString("api_title"));
                    quest.setDetail(questobject.getString("api_detail"));
                    JsonArray material = questobject.getJsonArray("api_get_material");
                    quest.setFuel(material.getJsonNumber(0).toString());
                    quest.setAmmo(material.getJsonNumber(1).toString());
                    quest.setMetal(material.getJsonNumber(2).toString());
                    quest.setBauxite(material.getJsonNumber(3).toString());
                    quest.setBonusFlag(questobject.getInt("api_bonus_flag"));
                    quest.setProgressFlag(questobject.getInt("api_progress_flag"));

                    questMap.put(quest.getNo(), quest);
                }
            }
            addConsole("Missions updated");
        } catch (Exception e) {
            LOG.warn("Failed to update missions!", e);
            LOG.warn(data);
        }
    }

    /**
     * 消化した任務を除去します
     * 
     * @param data
     */
    private static void doQuestClear(Data data) {
        try {
            String idstr = data.getField("api_quest_id");
            if (idstr != null) {
                Integer id = Integer.valueOf(idstr);
                questMap.remove(id);
            }
        } catch (Exception e) {
            LOG.warn("Failed to clear quests", e);
            LOG.warn(data);
        }
    }

    /**
     * 出撃を更新します
     * 
     * @param data
     */
    private static void doStart(Data data) {
        String idstr = data.getField("api_deck_id");
        if (idstr != null) {
            int id = Integer.parseInt(idstr);
            isSortie[id - 1] = true;
        }

        addConsole("Sortie updated");
    }

    /**
     * 設定を更新します
     * 
     * @param data
     */
    private static void doStart2(Data data) {
        try {
            JsonObject obj = data.getJsonObject().getJsonObject("api_data");
            if (obj != null) {
                // 艦娘一覧
                JsonArray apiMstShip = obj.getJsonArray("api_mst_ship");
                for (int i = 0; i < apiMstShip.size(); i++) {
                    JsonObject object = (JsonObject) apiMstShip.get(i);
                    String id = object.getJsonNumber("api_id").toString();
                    Ship.set(id, toShipInfoDto(object, id));
                }
                addConsole("Ship list updated");

                // 装備一覧
                JsonArray apiMstSlotitem = obj.getJsonArray("api_mst_slotitem");
                for (int i = 0; i < apiMstSlotitem.size(); i++) {
                    JsonObject object = (JsonObject) apiMstSlotitem.get(i);
                    ItemDto item = new ItemDto(object);
                    String id = object.getJsonNumber("api_id").toString();
                    Item.set(id, item);
                }
                addConsole("Equipment updated");
            }

            addConsole("Settings updated");
        } catch (Exception e) {
            LOG.warn("Failed to update configuration", e);
            LOG.warn(data);
        }
    }

    /**
     * 艦娘を作成します
     * 
     * @param object
     * @return
     */
    private static ShipInfoDto toShipInfoDto(JsonObject object, String id) {
        String name = object.getString("api_name");

        if ("なし".equals(name)) {
            return ShipInfoDto.EMPTY;
        }

        String type = ShipStyle.get(object.getJsonNumber("api_stype").toString());
        ShipInfoDto d = Ship.get(id);
        if (d != null) {
            name = d.getName() != "" ? d.getName() : name;
            type = d.getType() != "" ? d.getType() : type;
            if (d.getName() != name) {
                addConsole("Missing id[" + id + "]");
                LOG.warn("Missing id[" + id + "]");
            }
        }
        else {
            addConsole("Missing id[" + id + "]");
            LOG.warn("Missing id[" + id + "]");
        }
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
        consoleQueue.add(new SimpleDateFormat(AppConstants.DATE_SHORT_FORMAT).format(Calendar.getInstance().getTime())
                + "  " + message.toString());
    }
}
