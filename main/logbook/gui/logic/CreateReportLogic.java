/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui.logic;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import logbook.config.GlobalConfig;
import logbook.data.context.GlobalContext;
import logbook.dto.BattleDto;
import logbook.dto.BattleResultDto;
import logbook.dto.CreateItemDto;
import logbook.dto.DeckMissionDto;
import logbook.dto.DockDto;
import logbook.dto.GetShipDto;
import logbook.dto.ItemDto;
import logbook.dto.MissionResultDto;
import logbook.dto.NdockDto;
import logbook.dto.ShipDto;
import logbook.dto.ShipFilterDto;
import logbook.dto.ShipInfoDto;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 各種報告書を作成します
 *
 */
public final class CreateReportLogic {

    /** テーブルアイテム作成(デフォルト) */
    public static final TableItemCreator DEFAULT_TABLE_ITEM_CREATOR = new TableItemCreator() {

        @Override
        public void init() {
        }

        @Override
        public TableItem create(Table table, String[] text, int count) {
            TableItem item = new TableItem(table, SWT.NONE);
            // 偶数行に背景色を付ける
            if ((count % 2) != 0) {
                item.setBackground(SWTResourceManager.getColor(GlobalConfig.ROW_BACKGROUND));
            }
            item.setText(text);
            return item;
        }
    };

    /** テーブルアイテム作成(所有艦娘一覧) */
    public static final TableItemCreator SHIP_LIST_TABLE_ITEM_CREATOR = new TableItemCreator() {

        private Set<Long> deckmissions;

        private Set<Long> docks;

        @Override
        public void init() {
            // 遠征
            this.deckmissions = new HashSet<Long>();
            for (DeckMissionDto deckMission : GlobalContext.getDeckMissions()) {
                if ((deckMission.getMission() != null) && (deckMission.getShips() != null)) {
                    this.deckmissions.addAll(deckMission.getShips());
                }
            }
            // 入渠
            this.docks = new HashSet<Long>();
            for (NdockDto ndock : GlobalContext.getNdocks()) {
                if (ndock.getNdockid() != 0) {
                    this.docks.add(ndock.getNdockid());
                }
            }
        }

        @Override
        public TableItem create(Table table, String[] text, int count) {
            // 艦娘
            Long ship = Long.valueOf(text[1]);

            TableItem item = new TableItem(table, SWT.NONE);
            // 偶数行に背景色を付ける
            if ((count % 2) != 0) {
                item.setBackground(SWTResourceManager.getColor(GlobalConfig.ROW_BACKGROUND));
            }

            // 疲労
            int cond = Integer.parseInt(text[5]);
            if (cond <= GlobalConfig.COND_RED) {
                item.setForeground(SWTResourceManager.getColor(GlobalConfig.COND_RED_COLOR));
            } else if (cond <= GlobalConfig.COND_ORANGE) {
                item.setForeground(SWTResourceManager.getColor(GlobalConfig.COND_ORANGE_COLOR));
            }

            // 遠征
            if (this.deckmissions.contains(ship)) {
                item.setForeground(SWTResourceManager.getColor(GlobalConfig.MISSION_COLOR));
            }
            // 入渠
            if (this.docks.contains(ship)) {
                item.setForeground(SWTResourceManager.getColor(GlobalConfig.NDOCK_COLOR));
            }

            item.setText(text);
            return item;
        }
    };

    /** ロガー */
    private static final Logger LOG = LogManager.getLogger(CreateReportLogic.class);

    /** 日付フォーマット */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(GlobalConfig.DATE_FORMAT);

    /**
     * ドロップ報告書のヘッダー
     * 
     * @return ヘッダー
     */
    public static String[] getBattleResultHeader() {
        return new String[] { "", "Time", "Map", "Rank", "Enemy Fleet", "Drop Type", "Ship Name" };
    }

    /**
     * ドロップ報告書の内容
     * @return 内容
     */
    public static List<String[]> getBattleResultBody() {
        List<BattleResultDto> results = GlobalContext.getBattleResultList();
        List<Object[]> body = new ArrayList<Object[]>();

        for (int i = 0; i < results.size(); i++) {
            BattleResultDto item = results.get(i);
            body.add(new Object[] { Integer.toString(i + 1), FORMAT.format(item.getBattleDate()), item.getQuestName(),
                    item.getRank(), item.getEnemyName(), item.getDropType(), item.getDropName() });
        }
        return toListStringArray(body);
    }

    /**
     * ドロップ報告書のヘッダー(保存用)
     * 
     * @return ヘッダー
     */
    public static String[] getBattleResultStoreHeader() {
        return new String[] { "", "日付", "海域", "ランク", "敵艦隊", "ドロップ艦種", "ドロップ艦娘",
                "味方艦1", "味方艦1HP",
                "味方艦2", "味方艦2HP",
                "味方艦3", "味方艦3HP",
                "味方艦4", "味方艦4HP",
                "味方艦5", "味方艦5HP",
                "味方艦6", "味方艦6HP",
                "敵艦1", "敵艦1HP",
                "敵艦2", "敵艦2HP",
                "敵艦3", "敵艦3HP",
                "敵艦4", "敵艦4HP",
                "敵艦5", "敵艦5HP",
                "敵艦6", "敵艦6HP" };
    }

    /**
     * ドロップ報告書の内容(保存用)
     * @param results ドロップ報告書
     * 
     * @return 内容
     */
    public static List<String[]> getBattleResultStoreBody(List<BattleResultDto> results) {
        List<Object[]> body = new ArrayList<Object[]>();

        for (int i = 0; i < results.size(); i++) {
            BattleResultDto item = results.get(i);
            BattleDto battle = item.getBattleDto();
            String[] friend = new String[6];
            String[] friendHp = new String[6];
            String[] enemy = new String[6];
            String[] enemyHp = new String[6];

            Arrays.fill(friend, "");
            Arrays.fill(friendHp, "");
            Arrays.fill(enemy, "");
            Arrays.fill(enemyHp, "");

            if (battle != null) {
                DockDto dock = battle.getDock();
                if (dock != null) {
                    List<ShipDto> friendships = dock.getShips();
                    int[] fnowhps = battle.getNowFriendHp();
                    int[] fmaxhps = battle.getMaxFriendHp();
                    for (int j = 0; j < friendships.size(); j++) {
                        ShipDto ship = friendships.get(j);
                        friend[j] = ship.getName() + "(Lv" + ship.getLv() + ")";
                        friendHp[j] = fnowhps[j] + "/" + fmaxhps[j];
                    }
                    List<ShipInfoDto> enemyships = battle.getEnemy();
                    int[] enowhps = battle.getNowEnemyHp();
                    int[] emaxhps = battle.getMaxEnemyHp();
                    for (int j = 0; j < enemyships.size(); j++) {
                        ShipInfoDto ship = enemyships.get(j);
                        if (!StringUtils.isEmpty(ship.getFlagship())) {
                            enemy[j] = ship.getName() + "(" + ship.getFlagship() + ")";
                        } else {
                            enemy[j] = ship.getName();
                        }
                        enemyHp[j] = enowhps[j] + "/" + emaxhps[j];
                    }
                }
            }

            body.add(new Object[] { Integer.toString(i + 1), FORMAT.format(item.getBattleDate()), item.getQuestName(),
                    item.getRank(), item.getEnemyName(), item.getDropType(), item.getDropName(), friend[0],
                    friendHp[0], friend[1], friendHp[1], friend[2], friendHp[2], friend[3], friendHp[3], friend[4],
                    friendHp[4], friend[5], friendHp[5], enemy[0], enemyHp[0], enemy[1], enemyHp[1], enemy[2],
                    enemyHp[2], enemy[3], enemyHp[3], enemy[4], enemyHp[4], enemy[5], enemyHp[5] });
        }
        return toListStringArray(body);
    }

    /**
     * 建造報告書のヘッダー
     * 
     * @return ヘッダー
     */
    public static String[] getCreateShipHeader() {
        return new String[] { "", "Time", "Craft Type", "Name", "Type", "Fuel", "Ammo", "Steel", "Bauxite", "Dev Material", "空きドック", "Secretary", "HQ Lv" };
    }

    /**
     * 建造報告書の内容
     * 
     * @return 内容
     */
    public static List<String[]> getCreateShipBody(List<GetShipDto> ships) {
        List<Object[]> body = new ArrayList<Object[]>();
        for (int i = 0; i < ships.size(); i++) {
            GetShipDto ship = ships.get(i);
            body.add(new Object[] { Integer.toString(i + 1), FORMAT.format(ship.getGetDate()), ship.getBuildType(),
                    ship.getName(), ship.getType(), ship.getFuel(), ship.getAmmo(), ship.getMetal(), ship.getBauxite(),
                    ship.getResearchMaterials(), ship.getFreeDock(), ship.getSecretary(), ship.getHqLevel() });
        }
        return toListStringArray(body);
    }

    /**
     * 開発報告書のヘッダー
     * 
     * @return ヘッダー
     */
    public static String[] getCreateItemHeader() {
        return new String[] { "", "Time", "Name", "Type", "Fuel", "Ammo", "Steel", "Bauxite", "Secretary", "HQ Lv" };
    }

    /**
     * 開発報告書の内容
     * 
     * @return 内容
     */
    public static List<String[]> getCreateItemBody(List<CreateItemDto> items) {
        List<Object[]> body = new ArrayList<Object[]>();

        for (int i = 0; i < items.size(); i++) {
            CreateItemDto item = items.get(i);
            String name = "Penguin";
            String type = "";
            if (item.isCreateFlag()) {
                name = item.getName();
                type = item.getType();
            }
            body.add(new Object[] { Integer.toString(i + 1), FORMAT.format(item.getCreateDate()), name, type,
                    item.getFuel(), item.getAmmo(), item.getMetal(), item.getBauxite(), item.getSecretary(),
                    item.getHqLevel() });
        }
        return toListStringArray(body);
    }

    /**
     * 所有装備一覧のヘッダー
     * 
     * @return ヘッダー
     */
    public static String[] getItemListHeader() {
        return new String[] { "", "Name", "Type", "Qty", "Firepower", "Accuracy", "Evasion", "Range", "Luck", "Bomber", "Torpedo", "LOS", "ASW", "AA" };
    }

    /**
     * 所有装備一覧の内容
     * 
     * @return 内容
     */
    public static List<String[]> getItemListBody() {
        Set<Entry<Long, ItemDto>> items = GlobalContext.getItemMap().entrySet();
        Map<ItemDto, Integer> itemCountMap = new HashMap<ItemDto, Integer>();

        for (Entry<Long, ItemDto> entry : items) {
            ItemDto item = entry.getValue();
            Integer count = itemCountMap.get(item);
            if (count == null) {
                count = 1;
            } else {
                count = count + 1;
            }
            itemCountMap.put(item, count);
        }

        List<Entry<ItemDto, Integer>> countitems = new ArrayList<Entry<ItemDto, Integer>>(itemCountMap.entrySet());
        Collections.sort(countitems, new Comparator<Entry<ItemDto, Integer>>() {
            @Override
            public int compare(Entry<ItemDto, Integer> o1, Entry<ItemDto, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        List<Object[]> body = new ArrayList<Object[]>();

        int count = 0;
        for (Entry<ItemDto, Integer> entry : countitems) {
            ItemDto item = entry.getKey();
            count++;
            body.add(new Object[] { count, item.getName(), item.getType(), entry.getValue(), item.getHoug(),
                    item.getHoum(), item.getKaih(), item.getLeng(), item.getLuck(), item.getBaku(), item.getRaig(),
                    item.getSaku(), item.getTais(), item.getTyku()
            });
        }
        return toListStringArray(body);
    }

    /**
     * 所有艦娘一覧のヘッダー
     * 
     * @return ヘッダー
     */
    public static String[] getShipListHeader() {
        return new String[] { "", "ID", "Fleet", "Name", "Type", "Morale", "Recovery", "Lv", "Next", "Exp", "Air Superiority", "Equipment 1", "Equipment 2",
                "Equipment 3", "Equipment 4", "HP", "Firepower", "Torpedo", "AA", "Armor", "Evasion", "ASW", "LOS", "Luck" };
    }

    /**
     * 所有艦娘一覧の内容
     * 
     * @param specdiff 成長余地
     * @param filter 鍵付きのみ
     * @return 内容
     */
    public static List<String[]> getShipListBody(boolean specdiff, ShipFilterDto filter) {
        Set<Entry<Long, ShipDto>> ships = GlobalContext.getShipMap().entrySet();
        List<Object[]> body = new ArrayList<Object[]>();
        int count = 0;
        for (Entry<Long, ShipDto> entry : ships) {
            ShipDto ship = entry.getValue();

            if ((filter != null) && !shipFilter(ship, filter)) {
                continue;
            }

            count++;

            if (!specdiff) {
                // 通常
                body.add(new Object[] {
                        count,
                        ship.getId(),
                        ship.getFleetid(),
                        ship.getName(),
                        ship.getType(),
                        ship.getCond(),
                        ship.getCondClearDate(),
                        ship.getLv(),
                        ship.getNext(),
                        ship.getExp(),
                        ship.getSeiku(),
                        ship.getSlot().get(0),
                        ship.getSlot().get(1),
                        ship.getSlot().get(2),
                        ship.getSlot().get(3),
                        ship.getMaxhp(),
                        ship.getKaryoku(),
                        ship.getRaisou(),
                        ship.getTaiku(),
                        ship.getSoukou(),
                        ship.getKaihi(),
                        ship.getTaisen(),
                        ship.getSakuteki(),
                        ship.getLucky()
                });
            } else {
                // 成長の余地
                // 火力
                long karyoku = ship.getKaryokuMax() - ship.getKaryoku();
                // 雷装
                long raisou = ship.getRaisouMax() - ship.getRaisou();
                // 対空
                long taiku = ship.getTaikuMax() - ship.getTaiku();
                // 回避
                long kaihi = ship.getKaihiMax() - ship.getKaihi();
                // 対潜
                long taisen = ship.getTaisenMax() - ship.getTaisen();
                // 索敵
                long sakuteki = ship.getSakutekiMax() - ship.getSakuteki();
                // 運
                long lucky = ship.getLuckyMax() - ship.getLucky();

                for (ItemDto item : ship.getItem()) {
                    if (item != null) {
                        karyoku += item.getHoug();
                        raisou += item.getRaig();
                        taiku += item.getTyku();
                        kaihi += item.getKaih();
                        taisen += item.getTais();
                        sakuteki += item.getSaku();
                        lucky += item.getLuck();
                    }
                }
                body.add(new Object[] {
                        count,
                        ship.getId(),
                        ship.getFleetid(),
                        ship.getName(),
                        ship.getType(),
                        ship.getCond(),
                        ship.getCondClearDate(),
                        ship.getLv(),
                        ship.getNext(),
                        ship.getExp(),
                        ship.getSeiku(),
                        ship.getSlot().get(0),
                        ship.getSlot().get(1),
                        ship.getSlot().get(2),
                        ship.getSlot().get(3),
                        ship.getMaxhp(),
                        karyoku,
                        raisou,
                        taiku,
                        ship.getSoukouMax() - ship.getSoukou(),
                        kaihi,
                        taisen,
                        sakuteki,
                        lucky
                });
            }
        }
        return toListStringArray(body);
    }

    /**
     * 遠征結果のヘッダー
     * 
     * @return ヘッダー
     */
    public static String[] getCreateMissionResultHeader() {
        return new String[] { "", "Time", "Result", "Name", "Fuel", "Ammo", "Steel", "Bauxite" };
    }

    /**
     * 遠征結果一覧の内容
     * 
     * @return 遠征結果
     */
    public static List<String[]> getMissionResultBody(List<MissionResultDto> resultlist) {
        List<Object[]> body = new ArrayList<Object[]>();

        for (int i = 0; i < resultlist.size(); i++) {
            MissionResultDto result = resultlist.get(i);

            body.add(new Object[] {
                    Integer.toString(i + 1),
                    FORMAT.format(result.getDate()),
                    result.getClearResult(),
                    result.getQuestName(),
                    result.getFuel(),
                    result.getAmmo(),
                    result.getMetal(),
                    result.getBauxite()
            });
        }
        return toListStringArray(body);
    }

    /**
     * 報告書をCSVファイルに書き込む(最初の列を取り除く)
     * 
     * @param file ファイル
     * @param header ヘッダー
     * @param body 内容
     * @param applend 追記フラグ
     * @throws IOException
     */
    public static void writeCsvStripFirstColumn(File file, String[] header, List<String[]> body, boolean applend)
            throws IOException {
        // 報告書の項番を除く
        String[] copyheader = Arrays.copyOfRange(header, 1, header.length);
        List<String[]> copybody = new ArrayList<String[]>();
        for (String[] strings : body) {
            copybody.add(Arrays.copyOfRange(strings, 1, strings.length));
        }
        writeCsv(file, copyheader, copybody, applend);
    }

    /**
     * 報告書をCSVファイルに書き込む
     * 
     * @param file ファイル
     * @param header ヘッダー
     * @param body 内容
     * @param applend 追記フラグ
     * @throws IOException
     */
    public static void writeCsv(File file, String[] header, List<String[]> body, boolean applend)
            throws IOException {
        OutputStream stream = new BufferedOutputStream(new FileOutputStream(file, applend));
        try {
            if (!file.exists() || (FileUtils.sizeOf(file) <= 0)) {
                IOUtils.write(StringUtils.join(header, ',') + "\r\n", stream, GlobalConfig.CHARSET);
            }
            for (String[] colums : body) {
                IOUtils.write(StringUtils.join(colums, ',') + "\r\n", stream, GlobalConfig.CHARSET);
            }
        } finally {
            stream.close();
        }
    }

    /**
     * オブジェクト配列をテーブルウィジェットに表示できるように文字列に変換します
     * 
     * @param from テーブルに表示する内容
     * @return テーブルに表示する内容
     */
    private static List<String[]> toListStringArray(List<Object[]> from) {
        List<String[]> body = new ArrayList<String[]>();
        for (Object[] objects : from) {
            String[] values = new String[objects.length];
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] != null) {
                    values[i] = String.valueOf(objects[i]);
                } else {
                    values[i] = "";
                }
            }
            body.add(values);
        }
        return body;
    }

    /**
     * 艦娘をフィルタします
     * 
     * @param ship 艦娘
     * @param filter フィルターオブジェクト
     * @return フィルタ結果
     */
    private static boolean shipFilter(ShipDto ship, ShipFilterDto filter) {
        // 名前でフィルタ
        if (!StringUtils.isEmpty(filter.nametext)) {
            // テキストが入力されている場合処理する
            if (filter.regexp) {
                // 正規表現で検索
                if (filter.namepattern == null) {
                    // 正規表現で検索する場合、パターンの作成がまだならパターンを作成する
                    try {
                        filter.namepattern = Pattern.compile(filter.nametext);
                    } catch (PatternSyntaxException e) {
                        // 無効な正規表現はfalseを返す
                        return false;
                    }
                }
                Matcher matcher = filter.namepattern.matcher(ship.getName());
                if (!matcher.find()) {
                    // マッチしない
                    return false;
                }
            } else {
                // 部分一致で検索する
                if (ship.getName().indexOf(filter.nametext) == -1) {
                    return false;
                }
            }
        }
        // 艦種でフィルタ
        if (!filter.destroyer) {
            if ("DD".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.lightCruiser) {
            if ("CL".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.torpedoCruiser) {
            if ("CLT".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.heavyCruiser) {
            if ("CA".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.flyingDeckCruiser) {
            if ("CAV".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.seaplaneTender) {
            if ("AV".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.escortCarrier) {
            if ("CVL".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.carrier) {
            if ("CV".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.battleship) {
            if ("BB".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.flyingDeckBattleship) {
            if ("BBV".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.submarine) {
            if ("SS".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.carrierSubmarine) {
            if ("SSV".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.landingship) {
            if ("LHA".equals(ship.getType())) {
                return false;
            }
        }
        if (!filter.armoredcarrier) {
            if ("CVB".equals(ship.getType())) {
                return false;
            }
        }
        // 装備でフィルタ
        if (!StringUtils.isEmpty(filter.itemname)) {
            List<ItemDto> item = ship.getItem();
            boolean hit = false;
            for (ItemDto itemDto : item) {
                if (itemDto != null) {
                    if (filter.itemname.equals(itemDto.getName())) {
                        hit = true;
                        break;
                    }
                }
            }
            if (!hit) {
                return false;
            }
        }
        // 艦隊に所属
        if (!filter.onfleet) {
            if (!StringUtils.isEmpty(ship.getFleetid())) {
                return false;
            }
        }
        // 艦隊に非所属
        if (!filter.notonfleet) {
            if (StringUtils.isEmpty(ship.getFleetid())) {
                return false;
            }
        }
        // 鍵付き
        if (!filter.locked) {
            if (ship.getLocked()) {
                return false;
            }
        }
        // 鍵付きではない
        if (!filter.notlocked) {
            if (!ship.getLocked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 海戦・ドロップ報告書を書き込む
     * 
     * @param dto 海戦・ドロップ報告
     */
    public static void storeBattleResultReport(BattleResultDto dto) {
        try {
            List<BattleResultDto> dtoList = Collections.singletonList(dto);

            File report = getStoreFile("Drop Report.csv", "Drop Report2.csv");

            CreateReportLogic.writeCsvStripFirstColumn(report,
                    CreateReportLogic.getBattleResultStoreHeader(),
                    CreateReportLogic.getBattleResultStoreBody(dtoList), true);
        } catch (IOException e) {
            LOG.warn("Write error", e);
        }
    }

    /**
     * 建造報告書を書き込む
     * 
     * @param dto 建造報告
     */
    public static void storeCreateShipReport(GetShipDto dto) {
        try {
            List<GetShipDto> dtoList = Collections.singletonList(dto);

            File report = getStoreFile("Build Report.csv", "Build Report2.csv");

            CreateReportLogic.writeCsvStripFirstColumn(report,
                    CreateReportLogic.getCreateShipHeader(),
                    CreateReportLogic.getCreateShipBody(dtoList), true);
        } catch (IOException e) {
            LOG.warn("Write error", e);
        }
    }

    /**
     * 開発報告書を書き込む
     * 
     * @param dto 開発報告
     */
    public static void storeCreateItemReport(CreateItemDto dto) {
        try {
            List<CreateItemDto> dtoList = Collections.singletonList(dto);

            File report = getStoreFile("Craft Report.csv", "Craft Report2.csv");

            CreateReportLogic.writeCsvStripFirstColumn(report,
                    CreateReportLogic.getCreateItemHeader(),
                    CreateReportLogic.getCreateItemBody(dtoList), true);
        } catch (IOException e) {
            LOG.warn("Write error", e);
        }
    }

    /**
     * 遠征報告書を書き込む
     * 
     * @param dto 遠征結果
     */
    public static void storeCreateMissionReport(MissionResultDto dto) {
        try {
            List<MissionResultDto> dtoList = Collections.singletonList(dto);

            File report = getStoreFile("Expedition Report.csv", "Expedition Report2.csv");

            CreateReportLogic.writeCsvStripFirstColumn(report,
                    CreateReportLogic.getCreateMissionResultHeader(),
                    CreateReportLogic.getMissionResultBody(dtoList), true);
        } catch (IOException e) {
            LOG.warn("Write error", e);
        }
    }

    /**
     * 書き込み先のファイルを返します
     * 
     * @param name ファイル名
     * @param altername 代替ファイル名
     * @return File
     * @throws IOException
     */
    private static File getStoreFile(String name, String altername) throws IOException {
        // 報告書の保存先にファイルを保存します
        File report = new File(FilenameUtils.concat(GlobalConfig.getReportPath(), name));
        if ((report.getParentFile() == null) && report.mkdirs()) {
            // 報告書の保存先ディレクトリが無く、ディレクトリの作成に失敗した場合はカレントフォルダにファイルを保存
            report = new File(name);
        }
        if (isLocked(report)) {
            // ロックされている場合は代替ファイルに書き込みます
            report = new File(FilenameUtils.concat(report.getParent(), altername));
        }
        return report;
    }

    /**
     * ファイルがロックされているかを確認します
     * 
     * @param file ファイル
     * @return
     * @throws IOException
     */
    private static boolean isLocked(File file) throws IOException {
        if (!file.isFile()) {
            return false;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            try {
                FileChannel channel = raf.getChannel();
                FileLock lock = channel.tryLock();
                if (lock == null) {
                    return true;
                }
                lock.release();
                return false;
            } finally {
                raf.close();
            }
        } catch (FileNotFoundException e) {
            return true;
        }
    }
}
