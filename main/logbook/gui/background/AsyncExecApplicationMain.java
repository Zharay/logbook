/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui.background;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import logbook.config.AppConfig;
import logbook.constants.AppConstants;
import logbook.data.context.GlobalContext;
import logbook.dto.DeckMissionDto;
import logbook.dto.DockDto;
import logbook.dto.NdockDto;
import logbook.dto.ShipDto;
import logbook.gui.ApplicationMain;
import logbook.gui.logic.Sound;
import logbook.gui.logic.TimeLogic;
import logbook.gui.widgets.FleetComposite;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 非同期にメイン画面を更新します
 */
public final class AsyncExecApplicationMain extends Thread {
    private static final Logger LOG = LogManager.getLogger(AsyncExecApplicationMain.class);

    private static final int ONE_SECONDS_FORMILIS = 1000;
    private static final int ONE_MINUTES = 60;

    private final ApplicationMain main;

    /**
     * 非同期にメイン画面を更新するスレッドのコンストラクター
     * 
     * @param main メイン画面
     */
    public AsyncExecApplicationMain(ApplicationMain main) {
        this.main = main;
        this.setName("logbook_async_exec_application_main");
    }

    /**
     * 現在のメイン画面を更新します
     */
    @Override
    public void run() {

        try {
            while (true) {
                boolean update = GlobalContext.updateContext();
                if (update) {
                    // 保有アイテム数を更新する
                    Display.getDefault().asyncExec(new UpdateItemCountTask(this.main));
                    // 保有艦娘数を更新する
                    Display.getDefault().asyncExec(new UpdateShipCountTask(this.main));
                    // 艦隊タブを更新する
                    Display.getDefault().asyncExec(new UpdateFleetTabTask(this.main));
                }
                // 遠征と入渠を更新する
                Display.getDefault().asyncExec(new UpdateDeckNdockTask(this.main));

                Thread.sleep(ONE_SECONDS_FORMILIS);
            }
        } catch (Exception e) {
            LOG.fatal("Thread is aborted", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 2つの日付から残り時間を計算する
     * 
     * @param date1
     * @param date2
     * @return
     */
    private static long getRest(Date date1, Date date2) {
        return ((date2.getTime() - date1.getTime()) / ONE_SECONDS_FORMILIS);
    }

    /**
     * 保有アイテム数を更新する
     */
    private static final class UpdateItemCountTask implements Runnable {

        private final ApplicationMain main;

        /**
         * コンストラクター
         */
        public UpdateItemCountTask(ApplicationMain main) {
            this.main = main;
        }

        @Override
        public void run() {
            Button itemList = this.main.getItemList();
            String setText = "Equip(" + GlobalContext.getItemMap().size() + "/"
                    + GlobalContext.maxSlotitem() + ")";
            if (!setText.equals(itemList.getText())) {
                itemList.setText(setText);
                itemList.getParent().layout();
            }
        }
    }

    /**
     * 保有艦娘数を更新する
     */
    private static final class UpdateShipCountTask implements Runnable {

        private final ApplicationMain main;

        /**
         * コンストラクター
         */
        public UpdateShipCountTask(ApplicationMain main) {
            this.main = main;
        }

        @Override
        public void run() {
            Button shipList = this.main.getShipList();
            String setText = "Ships(" + GlobalContext.getShipMap().size() + "/" + GlobalContext.maxChara()
                    + ")";
            if (!setText.equals(shipList.getText())) {
                shipList.setText(setText);
                shipList.getParent().layout();
            }
        }
    }

    /**
     * 遠征と入渠を更新する
     */
    private static final class UpdateDeckNdockTask implements Runnable {

        private static final Logger LOG = LogManager.getLogger(UpdateDeckNdockTask.class);

        private static final boolean[] FLAG_NOTICE_DECK = { false, false, false };
        private static final boolean[] FLAG_NOTICE_NDOCK = { false, false, false, false };

        private final ApplicationMain main;

        /**
         * コンストラクター
         */
        public UpdateDeckNdockTask(ApplicationMain main) {
            this.main = main;
        }

        @Override
        public void run() {
            // 現在時刻
            Date now = Calendar.getInstance().getTime();
            List<String> notice = new ArrayList<String>();
            boolean visibleHome = false;
            // 遠征を更新する
            if (this.updateDeck(now, notice)) {
                Sound.randomExpeditionSoundPlay();
                visibleHome |= AppConfig.get().isVisibleOnReturnMission();
            }
            // 入渠を更新する
            if (this.updateNdock(now, notice)) {
                Sound.randomDockSoundPlay();
                visibleHome |= AppConfig.get().isVisibleOnReturnBathwater();
            }
            if (AppConfig.get().isUseBalloon()) {
                // バルーンツールチップを表示する
                try {
                    // 遠征・入渠のお知らせ
                    if (notice.size() > 0) {
                        ToolTip tip = new ToolTip(this.main.getShell(), SWT.BALLOON
                                | SWT.ICON_INFORMATION);
                        tip.setText("Notification");
                        tip.setMessage(StringUtils.join(notice, "\r\n"));
                        this.main.getTrayItem().setToolTip(tip);
                        if (visibleHome) {
                            this.main.getTabFolder().setSelection(0);
                        }
                        tip.setVisible(true);
                    }
                } catch (Exception e) {
                    LOG.warn("Failed to display the notification", e);
                }
            }
        }

        /**
         * 遠征を更新する
         * 
         * @param now
         * @param notice
         * @return
         */
        private boolean updateDeck(Date now, List<String> notice) {
            boolean noticeflg = false;

            Label[] deckNameLabels = { this.main.getDeck1name(), this.main.getDeck2name(), this.main.getDeck3name() };
            Text[] deckTimeTexts = { this.main.getDeck1time(), this.main.getDeck2time(), this.main.getDeck3time() };

            DeckMissionDto[] deckMissions = GlobalContext.getDeckMissions();

            for (int i = 0; i < deckMissions.length; i++) {
                String time = "";
                String dispname = "";
                if (deckMissions[i].getMission() != null) {
                    dispname = deckMissions[i].getName() + " (" + deckMissions[i].getMission() + ")";

                    if (deckMissions[i].getTime() != null) {
                        long rest = getRest(now, deckMissions[i].getTime());
                        // 20分前、10分前、5分前になったら背景色を変更する
                        if (rest <= (ONE_MINUTES * 5)) {
                            deckTimeTexts[i].setBackground(SWTResourceManager
                                    .getColor(AppConstants.TIME_IN_5_MIN));
                        } else if (rest <= (ONE_MINUTES * 10)) {
                            deckTimeTexts[i].setBackground(SWTResourceManager
                                    .getColor(AppConstants.TIME_IN_10_MIN));
                        } else if (rest <= (ONE_MINUTES * 20)) {
                            deckTimeTexts[i].setBackground(SWTResourceManager
                                    .getColor(AppConstants.TIME_IN_20_MIN));
                        } else {
                            deckTimeTexts[i].setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
                        }
                        if (this.main.getDeckNotice().getSelection()) {
                            if ((rest <= ONE_MINUTES) && !FLAG_NOTICE_DECK[i]) {
                                notice.add(deckMissions[i].getName() + " has returned from "
                                        + deckMissions[i].getMission());
                                noticeflg = true;
                                FLAG_NOTICE_DECK[i] = true;
                            } else if (AppConfig.get().isMissionRemind() && (rest < -1)
                                    && ((rest % (ONE_MINUTES * 3)) == 0)) {
                                // 3分毎にリマインドする
                                notice.add(dispname + "  will arrive soon.");
                                noticeflg = true;
                            } else if (rest > ONE_MINUTES) {
                                FLAG_NOTICE_DECK[i] = false;
                            }
                        } else {
                            FLAG_NOTICE_DECK[i] = false;
                        }
                        time = TimeLogic.toDateRestString(rest);
                        if (time == null) {
                            time = "Done";
                        }
                    }
                } else {
                    deckTimeTexts[i].setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
                }
                deckNameLabels[i].setText(dispname);
                deckTimeTexts[i].setText(time);
            }
            return noticeflg;
        }

        /**
         * 入渠を更新する
         * 
         * @param now
         * @param notice
         * @return
         */
        private boolean updateNdock(Date now, List<String> notice) {
            boolean noticeflg = false;

            Map<Long, ShipDto> shipMap = GlobalContext.getShipMap();

            Label[] ndockNameLabels = { this.main.getNdock1name(), this.main.getNdock2name(),
                    this.main.getNdock3name(), this.main.getNdock4name() };
            Text[] ndockTimeTexts = { this.main.getNdock1time(), this.main.getNdock2time(), this.main.getNdock3time(),
                    this.main.getNdock4time() };

            NdockDto[] ndocks = GlobalContext.getNdocks();

            for (int i = 0; i < ndocks.length; i++) {
                String name = "";
                String time = "";

                if (ndocks[i].getNdockid() != 0) {
                    ShipDto ship = shipMap.get(Long.valueOf(ndocks[i].getNdockid()));
                    if (ship != null) {
                        name = ship.getName() + " (Lv" + ship.getLv() + ")";
                        long rest = getRest(now, ndocks[i].getNdocktime());
                        // 20分前、10分前、5分前になったら背景色を変更する
                        if (rest <= (ONE_MINUTES * 5)) {
                            ndockTimeTexts[i].setBackground(SWTResourceManager
                                    .getColor(AppConstants.TIME_IN_5_MIN));
                        } else if (rest <= (ONE_MINUTES * 10)) {
                            ndockTimeTexts[i].setBackground(SWTResourceManager
                                    .getColor(AppConstants.TIME_IN_10_MIN));
                        } else if (rest <= (ONE_MINUTES * 20)) {
                            ndockTimeTexts[i].setBackground(SWTResourceManager
                                    .getColor(AppConstants.TIME_IN_20_MIN));
                        } else {
                            ndockTimeTexts[i].setBackground(SWTResourceManager
                                    .getColor(SWT.COLOR_WHITE));
                        }
                        if (this.main.getNdockNotice().getSelection()) {

                            if ((rest <= ONE_MINUTES) && !FLAG_NOTICE_NDOCK[i]) {
                                notice.add("Repair Dock has finished repairing " + name);
                                noticeflg = true;
                                FLAG_NOTICE_NDOCK[i] = true;
                            } else if (rest > ONE_MINUTES) {
                                FLAG_NOTICE_NDOCK[i] = false;
                            }
                        } else {
                            FLAG_NOTICE_NDOCK[i] = false;
                        }
                        time = TimeLogic.toDateRestString(rest);
                        if (time == null) {
                            time = "Done";
                        }
                    }
                } else {
                    ndockTimeTexts[i].setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
                }
                ndockNameLabels[i].setText(name);
                ndockTimeTexts[i].setText(time);
            }
            return noticeflg;
        }
    }

    /**
     * 艦隊タブを更新する
     */
    private static final class UpdateFleetTabTask implements Runnable {

        private static String[] dockname = new String[4];
        private static CTabItem[] tabItems = new CTabItem[4];
        private static FleetComposite[] dockComposites = new FleetComposite[4];

        private final ApplicationMain main;

        /**
         * コンストラクター
         */
        public UpdateFleetTabTask(ApplicationMain main) {
            this.main = main;
        }

        @Override
        public void run() {
            // タブを更新する
            CTabItem maintab = this.main.getTabFolder().getItem(0);
            maintab.setToolTipText(
                    "Equip:" + GlobalContext.getItemMap().size() + "/"
                            + GlobalContext.maxSlotitem()
                            + " Ships:" + GlobalContext.getShipMap().size() + "/"
                            + GlobalContext.maxChara());

            for (int i = 0; i < 4; i++) {
                DockDto dock = GlobalContext.getDock(Integer.toString(i + 1));
                if (dock != null) {
                    FleetComposite tabComposite = dockComposites[i];
                    CTabItem tabItem = tabItems[i];

                    if (tabItem == null) {

                        tabItem = new CTabItem(this.main.getTabFolder(), SWT.NONE);
                        tabItem.setText(dock.getName());

                        // メインコンポジット
                        tabComposite = new FleetComposite(this.main.getTabFolder(), tabItem, this.main);
                        tabItem.setControl(tabComposite);

                        tabItems[i] = tabItem;
                        dockComposites[i] = tabComposite;
                    }
                    if (!dock.getName().equals(dockname[i])) {
                        dockname[i] = dock.getName();
                    }
                    tabComposite.updateFleet(dock);
                    tabItem.setText(dock.getName());
                }
            }
        }
    }
}
