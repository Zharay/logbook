/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui.widgets;

import java.text.MessageFormat;
import java.util.List;

import logbook.config.AppConfig;
import logbook.constants.AppConstants;
import logbook.data.context.GlobalContext;
import logbook.dto.DockDto;
import logbook.dto.ShipDto;
import logbook.gui.ApplicationMain;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * 艦隊タブのウィジェットです
 *
 */
public class FleetComposite extends Composite {

    /** 警告 */
    private static final int WARN = 1;
    /** 致命的 */
    private static final int FATAL = 2;
    /** 1艦隊に編成できる艦娘の数 */
    private static final int MAXCHARA = 6;

    /** タブ */
    private final CTabItem tab;
    /** メイン画面 */
    private final ApplicationMain main;
    /** フォント大きい */
    private final Font large;
    /** フォント小さい */
    private final Font small;
    /** 艦隊 */
    private DockDto dock;

    private final Composite fleetGroup;

    /** タブアイコン表示 */
    private int state;
    /** コンディション最小値(メッセージ表示用) */
    private long cond;
    /** 疲労回復時間(メッセージ表示用) */
    private String clearDate;
    /** 大破している */
    private boolean badlyDamage;

    /** アイコンラベル */
    private final Label[] iconLabels = new Label[MAXCHARA];
    /** 名前ラベル */
    private final Label[] nameLabels = new Label[MAXCHARA];
    /** HP */
    private final Label[] hpLabels = new Label[MAXCHARA];
    /** HPゲージ */
    private final Label[] hpgaugeLabels = new Label[MAXCHARA];
    /** HPメッセージ */
    private final Label[] hpmsgLabels = new Label[MAXCHARA];
    /** コンディション */
    private final Label[] condLabels = new Label[MAXCHARA];
    /** コンディションステータス */
    private final Label[] condstLabels = new Label[MAXCHARA];
    /** 弾ステータス */
    private final Label[] bullstLabels = new Label[MAXCHARA];
    /** 燃料ステータス */
    private final Label[] fuelstLabels = new Label[MAXCHARA];
    /** メッセージ */
    private final StyledText message;

    /**
     * @param parent 艦隊タブの親
     * @param tabItem 艦隊タブ
     */
    public FleetComposite(CTabFolder parent, CTabItem tabItem, ApplicationMain main) {
        super(parent, SWT.NONE);
        this.tab = tabItem;
        this.main = main;
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glParent = new GridLayout(1, false);
        glParent.horizontalSpacing = 0;
        glParent.marginTop = 0;
        glParent.marginWidth = 0;
        glParent.marginHeight = 0;
        glParent.marginBottom = 0;
        glParent.verticalSpacing = 0;
        this.setLayout(glParent);

        FontData normalfd = parent.getShell().getFont().getFontData()[0];
        FontData largefd = new FontData(normalfd.getName(), normalfd.getHeight() + 2, normalfd.getStyle());
        FontData smallfd = new FontData(normalfd.getName(), normalfd.getHeight() - 1, normalfd.getStyle());

        this.large = new Font(Display.getCurrent(), largefd);
        this.small = new Font(Display.getCurrent(), smallfd);

        this.fleetGroup = new Composite(this, SWT.NONE);
        this.fleetGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout glShipGroup = new GridLayout(3, false);
        glShipGroup.horizontalSpacing = 0;
        glShipGroup.marginTop = 0;
        glShipGroup.marginWidth = 1;
        glShipGroup.marginHeight = 0;
        glShipGroup.marginBottom = 0;
        glShipGroup.verticalSpacing = 0;
        this.fleetGroup.setLayout(glShipGroup);
        this.init();

        // セパレーター
        Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // メッセージ
        this.message = new StyledText(this, SWT.READ_ONLY | SWT.WRAP);
        this.message.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        this.message.setWordWrap(true);
        this.message.setBackground(this.getBackground());

        this.fleetGroup.layout();
    }

    /**
     * 初期化
     */
    private void init() {
        for (int i = 0; i < MAXCHARA; i++) {
            // アイコン
            Label iconlabel = new Label(this.fleetGroup, SWT.NONE);
            GridData gdIconlabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
            gdIconlabel.widthHint = 16;
            iconlabel.setLayoutData(gdIconlabel);
            // 名前
            Label namelabel = new Label(this.fleetGroup, SWT.NONE);
            namelabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            namelabel.setFont(this.large);
            namelabel.setText("Name");
            // HP
            Composite hpComposite = new Composite(this.fleetGroup, SWT.NONE);
            GridLayout glHp = new GridLayout(3, false);
            glHp.horizontalSpacing = 0;
            glHp.marginTop = 0;
            glHp.marginWidth = 2;
            glHp.marginHeight = 0;
            glHp.marginBottom = 0;
            glHp.verticalSpacing = 0;
            hpComposite.setLayout(glHp);
            hpComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

            Label hp = new Label(hpComposite, SWT.NONE);
            hp.setFont(this.small);
            Label hpgauge = new Label(hpComposite, SWT.NONE);
            Label hpmsg = new Label(hpComposite, SWT.NONE);
            hpmsg.setText("Good");

            // ステータス
            new Label(this.fleetGroup, SWT.NONE);
            Composite stateComposite = new Composite(this.fleetGroup, SWT.NONE);
            GridLayout glState = new GridLayout(3, false);
            glState.horizontalSpacing = 0;
            glState.marginTop = 0;
            glState.marginHeight = 0;
            glState.marginBottom = 0;
            glState.verticalSpacing = 0;
            stateComposite.setLayout(glState);
            stateComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            Label condst = new Label(stateComposite, SWT.NONE);
            condst.setText("Morale ");
            Label fuelst = new Label(stateComposite, SWT.NONE);
            fuelst.setText("Fuel ");
            Label bullst = new Label(stateComposite, SWT.NONE);
            bullst.setText("Ammo");

            // 疲労
            Label cond = new Label(this.fleetGroup, SWT.NONE);
            cond.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
            cond.setText("49 cond.");

            this.iconLabels[i] = iconlabel;
            this.nameLabels[i] = namelabel;
            this.hpLabels[i] = hp;
            this.hpgaugeLabels[i] = hpgauge;
            this.hpmsgLabels[i] = hpmsg;
            this.condLabels[i] = cond;
            this.condstLabels[i] = condst;
            this.bullstLabels[i] = bullst;
            this.fuelstLabels[i] = fuelst;
        }
    }

    /**
     * 艦隊を更新します
     * 
     * @param dock
     */
    public void updateFleet(DockDto dock) {
        if (this.dock == dock) {
            return;
        }
        this.dock = dock;
        this.state = 0;
        this.cond = 49;
        this.clearDate = null;
        this.badlyDamage = false;
        this.message.setText("");

        List<ShipDto> ships = dock.getShips();
        for (int i = ships.size(); i < MAXCHARA; i++) {
            this.iconLabels[i].setImage(null);
            this.nameLabels[i].setText("");
            this.hpLabels[i].setText("");
            this.hpgaugeLabels[i].setImage(null);
            this.hpmsgLabels[i].setText("");
            this.condLabels[i].setText("");
            this.condstLabels[i].setText("");
            this.bullstLabels[i].setText("");
            this.fuelstLabels[i].setText("");
        }
        for (int i = 0; i < ships.size(); i++) {
            int state = 0;
            ShipDto ship = ships.get(i);
            // 名前
            String name = ship.getName();
            // HP
            long nowhp = ship.getNowhp();
            // MaxHP
            long maxhp = ship.getMaxhp();
            // HP割合
            float hpratio = (float) nowhp / (float) maxhp;
            // 疲労
            long cond = ship.getCond();
            // 弾
            int bull = ship.getBull();
            // 弾Max
            int bullmax = ship.getBullMax();
            // 残弾比
            float bullraito = bullmax != 0 ? (float) bull / (float) bullmax : 1f;
            // 燃料
            int fuel = ship.getFuel();
            // 燃料Max
            int fuelmax = ship.getFuelMax();
            // 残燃料比
            float fuelraito = fuelmax != 0 ? (float) fuel / (float) fuelmax : 1f;

            // 疲労している艦娘がいる場合メッセージを表示
            if (this.cond > cond) {
                this.cond = cond;
                this.clearDate = ship.getCondClearDate();
            }

            // 体力メッセージ
            if (hpratio <= AppConstants.BADLY_DAMAGE) {
                if (AppConfig.get().isFatalBybadlyDamage()) {
                    // 大破で致命的アイコン
                    state |= FATAL;
                }
                // 大破している艦娘がいる場合メッセージを表示
                this.badlyDamage = true;
                this.hpmsgLabels[i].setText("Heavy");
                this.hpmsgLabels[i].setBackground(SWTResourceManager.getColor(AppConstants.COND_RED_COLOR));
                this.hpmsgLabels[i].setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
            } else if (hpratio <= AppConstants.HALF_DAMAGE) {
                if (AppConfig.get().isWarnByHalfDamage()) {
                    // 中破で警告アイコン
                    state |= WARN;
                }

                this.hpmsgLabels[i].setText("Medium");
                this.hpmsgLabels[i].setBackground(SWTResourceManager.getColor(AppConstants.COND_ORANGE_COLOR));
                this.hpmsgLabels[i].setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
            } else if (hpratio <= AppConstants.SLIGHT_DAMAGE) {
                this.hpmsgLabels[i].setText("Light");
                this.hpmsgLabels[i].setBackground(null);
                this.hpmsgLabels[i].setForeground(null);
            } else {
                this.hpmsgLabels[i].setText("Good");
                this.hpmsgLabels[i].setBackground(null);
                this.hpmsgLabels[i].setForeground(null);
            }

            // ステータス
            // ステータス.疲労
            this.condstLabels[i].setText("Morale ");
            if (cond >= 49) {
                this.condstLabels[i].setEnabled(false);
            } else {
                this.condstLabels[i].setEnabled(true);
            }
            // ステータス.燃料
            this.fuelstLabels[i].setText("Fuel ");
            if (fuelraito >= 1f) {
                this.fuelstLabels[i].setEnabled(false);
                this.fuelstLabels[i].setForeground(null);
            } else {
                if (AppConfig.get().isWarnByNeedSupply()) {
                    // 補給不足で警告アイコン
                    state |= WARN;
                }
                this.fuelstLabels[i].setEnabled(true);
                if (fuelraito <= AppConstants.EMPTY_SUPPLY) {
                    // 補給赤
                    this.fuelstLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_RED_COLOR));
                } else if (fuelraito <= AppConstants.LOW_SUPPLY) {
                    // 補給橙
                    this.fuelstLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_ORANGE_COLOR));
                }
            }
            // ステータス.弾
            this.bullstLabels[i].setText("Ammo");
            if (bullraito >= 1f) {
                this.bullstLabels[i].setEnabled(false);
                this.bullstLabels[i].setBackground(null);
                this.bullstLabels[i].setForeground(null);
            } else {
                if (AppConfig.get().isWarnByNeedSupply()) {
                    // 補給不足で警告アイコン
                    state |= WARN;
                }
                this.bullstLabels[i].setEnabled(true);
                if (bullraito <= AppConstants.EMPTY_SUPPLY) {
                    this.bullstLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_RED_COLOR));
                } else if (bullraito <= AppConstants.LOW_SUPPLY) {
                    this.bullstLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_ORANGE_COLOR));
                }
            }
            // コンディション
            if (cond <= AppConstants.COND_RED) {
                // 疲労19以下
                if (AppConfig.get().isWarnByCondState()) {
                    // 疲労状態で警告アイコン
                    state |= WARN;
                }
                this.condLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_RED_COLOR));
                this.condstLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_RED_COLOR));
            } else if (cond <= AppConstants.COND_ORANGE) {
                // 疲労29以下
                if (AppConfig.get().isWarnByCondState()) {
                    // 疲労状態で警告アイコン
                    state |= WARN;
                }
                this.condLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_ORANGE_COLOR));
                this.condstLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_ORANGE_COLOR));
            } else if (cond >= AppConstants.COND_GREEN) {
                // 疲労50以上
                this.condLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_GREEN_COLOR));
                this.condstLabels[i].setForeground(SWTResourceManager.getColor(AppConstants.COND_GREEN_COLOR));
            } else {
                this.condLabels[i].setForeground(null);
                this.condstLabels[i].setForeground(null);
            }

            if ((state & FATAL) == FATAL) {
                this.iconLabels[i].setImage(SWTResourceManager.getImage(FleetComposite.class,
                        AppConstants.R_ICON_EXCLAMATION));
            } else if ((state & WARN) == WARN) {
                this.iconLabels[i].setImage(SWTResourceManager
                        .getImage(FleetComposite.class, AppConstants.R_ICON_ERROR));
            } else {
                this.iconLabels[i].setImage(null);
            }

            // ラベルを更新する
            this.nameLabels[i].setText(name);
            this.nameLabels[i].setToolTipText(MessageFormat.format(AppConstants.TOOLTIP_FLEETTAB_SHIP, nowhp, maxhp,
                    fuel, fuelmax, bull, bullmax, ship.getNext()));
            this.hpLabels[i].setText(MessageFormat.format("{0}/{1} ", nowhp, maxhp));
            this.hpgaugeLabels[i].setImage(this.getHpGaugeImage(hpratio));
            this.condLabels[i].setText(MessageFormat.format("{0} cond.", cond));
            this.bullstLabels[i].getParent().layout(true);
            this.state |= state;
        }
        // メッセージを更新する
        // 入渠中の艦娘を探す
        boolean isBathwater = false;
        for (ShipDto shipDto : ships) {
            if (GlobalContext.isNdock(shipDto)) {
                isBathwater = true;
                break;
            }
        }
        // 制空値を計算
        int seiku = 0;
        for (ShipDto shipDto : ships) {
            seiku += shipDto.getSeiku();
        }
        if (GlobalContext.isMission(this.dock.getId())) {
            // 遠征中
            StyleRange style = new StyleRange();
            style.fontStyle = SWT.BOLD;
            style.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE);
            this.addStyledText(this.message, AppConstants.MESSAGE_MISSION, style);
        } else if (isBathwater) {
            // 入渠中
            StyleRange style = new StyleRange();
            style.fontStyle = SWT.BOLD;
            style.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE);
            this.addStyledText(this.message,
                    MessageFormat.format(AppConstants.MESSAGE_BAD, AppConstants.MESSAGE_BATHWATER), style);
        } else if (this.badlyDamage) {
            // 大破
            StyleRange style = new StyleRange();
            style.fontStyle = SWT.BOLD;
            style.underline = true;
            style.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
            style.underlineColor = SWTResourceManager.getColor(SWT.COLOR_RED);
            style.foreground = SWTResourceManager.getColor(SWT.COLOR_RED);
            this.addStyledText(this.message,
                    MessageFormat.format(AppConstants.MESSAGE_BAD, AppConstants.MESSAGE_BADLY_DAMAGE), style);
        } else {
            // 出撃可能
            StyleRange style = new StyleRange();
            style.fontStyle = SWT.BOLD;
            style.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
            this.addStyledText(this.message, AppConstants.MESSAGE_GOOD, style);
        }
        if (this.clearDate != null) {
            this.addStyledText(this.message, MessageFormat.format(AppConstants.MESSAGE_COND, this.clearDate), null);
        }
        this.addStyledText(this.message, MessageFormat.format(AppConstants.MESSAGE_SEIKU, seiku), null);

        this.updateTabIcon();
        this.showTooltip();

        this.fleetGroup.layout();
    }

    /**
     * 艦隊タブのアイコンを更新します
     */
    private void updateTabIcon() {
        if (this.state == 0) {
            this.tab.setImage(null);
        } else {
            if ((this.state & FATAL) == FATAL) {
                this.tab.setImage(SWTResourceManager.getImage(FleetComposite.class, AppConstants.R_ICON_EXCLAMATION));
            } else if ((this.state & WARN) == WARN) {
                this.tab.setImage(SWTResourceManager.getImage(FleetComposite.class, AppConstants.R_ICON_ERROR));
            }
        }
    }

    /**
     * 艦隊が出撃中で大破した場合にツールチップを表示します
     */
    private void showTooltip() {
        if (((this.state & FATAL) == FATAL) && GlobalContext.isSortie(this.dock.getId())) {
            ToolTip tip = new ToolTip(this.getShell(), SWT.BALLOON
                    | SWT.ICON_ERROR);
            tip.setText("大破警告");
            tip.setMessage(AppConstants.MESSAGE_STOP_SORTIE);
            this.main.getTrayItem().setToolTip(tip);
            tip.setVisible(true);
        }
    }

    /**
     * HPゲージのイメージを取得します
     * @param hpratio HP割合
     * @return HPゲージのイメージ
     */
    private Image getHpGaugeImage(float hpratio) {
        return SWTResourceManager
                .getImage(
                        FleetComposite.class,
                        AppConstants.R_HPGAUGE_IMAGES[(int) Math.floor(hpratio
                                * (AppConstants.R_HPGAUGE_IMAGES.length - 1))]);
    }

    /**
     * スタイル付きテキストを設定します
     * 
     * @param text StyledText
     * @param str 文字
     * @param style スタイル
     */
    private void addStyledText(StyledText text, String str, StyleRange style) {
        StyleRange[] oldranges = text.getStyleRanges();
        String beforeText = text.getText();
        StyleRange addStyle = style;
        if (addStyle == null) {
            addStyle = new StyleRange();
        }
        addStyle.start = beforeText.length();
        addStyle.length = str.length();

        StyleRange[] ranges = new StyleRange[oldranges.length + 1];
        for (int i = 0; i < oldranges.length; i++) {
            ranges[i] = oldranges[i];
        }
        ranges[oldranges.length] = addStyle;

        text.setText(beforeText + str);
        text.setStyleRanges(ranges);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.large.dispose();
        this.small.dispose();
    }
}
