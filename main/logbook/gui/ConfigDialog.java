/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import logbook.config.AppConfig;
import logbook.gui.logic.LayoutLogic;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 設定画面
 *
 */
public final class ConfigDialog extends Dialog {

    private final Map<String, Composite> compositeMap = new HashMap<String, Composite>();
    private Shell shell;
    private Composite composite;
    private ScrolledComposite scrolledComposite;

    /**
     * Create the dialog.
     * @param parent
     */
    public ConfigDialog(Shell parent) {
        super(parent, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.RESIZE);
        this.setText("Settings");
    }

    /**
     * Open the dialog.
     */
    public void open() {
        this.createContents();
        this.shell.open();
        this.shell.layout();
        Display display = this.getParent().getDisplay();
        while (!this.shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        this.shell = new Shell(this.getParent(), this.getStyle());
        this.shell.setSize(470, 320);
        this.shell.setText(this.getText());
        this.shell.setLayout(new GridLayout(1, false));

        SashForm sashForm = new SashForm(this.shell, SWT.SMOOTH);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        // ツリーメニュー
        Tree tree = new Tree(sashForm, SWT.BORDER);
        tree.addSelectionListener(new TreeSelectionAdapter(this));
        TreeItem systemroot = new TreeItem(tree, SWT.NONE);
        systemroot.setText("System");
        systemroot.setData("system");
        TreeItem fleettab = new TreeItem(systemroot, SWT.NONE);
        fleettab.setText("Fleets");
        fleettab.setData("fleettab");
        TreeItem capture = new TreeItem(systemroot, SWT.NONE);
        capture.setText("Capture");
        capture.setData("capture");
        TreeItem development = new TreeItem(tree, SWT.NONE);
        development.setText("Developer");
        development.setData("development");

        systemroot.setExpanded(true);
        development.setExpanded(true);

        this.scrolledComposite = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        this.scrolledComposite.setExpandHorizontal(true);
        this.scrolledComposite.setExpandVertical(true);

        this.composite = new Composite(this.scrolledComposite, SWT.NONE);
        this.composite.setLayout(new GridLayout(1, false));

        // システム タブ
        Composite compositeSystem = new Composite(this.composite, SWT.NONE);
        this.compositeMap.put("system", compositeSystem);
        compositeSystem.setLayout(new GridLayout(3, false));

        Label label = new Label(compositeSystem, SWT.NONE);
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label.setText("Port*");

        final Text listenport = new Text(compositeSystem, SWT.BORDER);
        GridData gdListenport = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdListenport.widthHint = 90;
        listenport.setLayoutData(gdListenport);
        listenport.setText(Integer.toString(AppConfig.get().getListenPort()));
        new Label(compositeSystem, SWT.NONE);

        Label label3 = new Label(compositeSystem, SWT.NONE);
        label3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label3.setText("Volume (%)");

        final Text soundlevel = new Text(compositeSystem, SWT.BORDER);
        GridData gdSoundlevel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdSoundlevel.widthHint = 90;
        soundlevel.setLayoutData(gdSoundlevel);
        soundlevel.setText(Integer.toString((int) (AppConfig.get().getSoundLevel() * 100)));
        new Label(compositeSystem, SWT.NONE);

        Label label7 = new Label(compositeSystem, SWT.NONE);
        label7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label7.setText("Transparency*");

        final Text alpha = new Text(compositeSystem, SWT.BORDER);
        GridData gdAlpha = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdAlpha.widthHint = 90;
        alpha.setLayoutData(gdAlpha);
        alpha.setText(Integer.toString(AppConfig.get().getAlpha()));
        new Label(compositeSystem, SWT.NONE);

        Label label8 = new Label(compositeSystem, SWT.NONE);
        label8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label8.setText("Save report to");

        final Text reportDir = new Text(compositeSystem, SWT.BORDER);
        GridData gdReportDir = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdReportDir.widthHint = 120;
        reportDir.setLayoutData(gdReportDir);
        reportDir.setText(AppConfig.get().getReportPath());

        Button reportSavedirBtn = new Button(compositeSystem, SWT.NONE);
        reportSavedirBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(ConfigDialog.this.shell);
                dialog.setMessage("Please specify a location");
                String path = dialog.open();
                if (path != null) {
                    reportDir.setText(path);
                }
            }
        });
        reportSavedirBtn.setText("Select");

        final Button hidewindow = new Button(compositeSystem, SWT.CHECK);
        hidewindow.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        hidewindow.setText("Minimize to tray");
        hidewindow.setSelection(AppConfig.get().isHideWindow());

        final Button ontop = new Button(compositeSystem, SWT.CHECK);
        ontop.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        ontop.setText("Always on top*");
        ontop.setSelection(AppConfig.get().isOnTop());

        final Button checkUpdate = new Button(compositeSystem, SWT.CHECK);
        checkUpdate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        checkUpdate.setText("Check for updates*");
        checkUpdate.setSelection(AppConfig.get().isCheckUpdate());

        final Button checkDoit = new Button(compositeSystem, SWT.CHECK);
        checkDoit.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        checkDoit.setText("終了時に確認する");
        checkDoit.setSelection(AppConfig.get().isCheckDoit());

        // 艦隊タブ タブ
        Composite compositeFleetTab = new Composite(this.composite, SWT.NONE);
        this.compositeMap.put("fleettab", compositeFleetTab);
        compositeFleetTab.setLayout(new GridLayout(1, false));

        Label Fleetdesc = new Label(compositeFleetTab, SWT.NONE);
        Fleetdesc.setText("Display warning icon if:");

        final Button warnByNeedSupply = new Button(compositeFleetTab, SWT.CHECK);
        warnByNeedSupply.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        warnByNeedSupply.setText("insufficient supplies");
        warnByNeedSupply.setSelection(AppConfig.get().isWarnByNeedSupply());

        final Button warnByCondState = new Button(compositeFleetTab, SWT.CHECK);
        warnByCondState.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        warnByCondState.setText("low morale");
        warnByCondState.setSelection(AppConfig.get().isWarnByCondState());

        final Button warnByHalfDamage = new Button(compositeFleetTab, SWT.CHECK);
        warnByHalfDamage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        warnByHalfDamage.setText("moderately damaged");
        warnByHalfDamage.setSelection(AppConfig.get().isWarnByHalfDamage());

        final Button fatalBybadlyDamage = new Button(compositeFleetTab, SWT.CHECK);
        fatalBybadlyDamage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        fatalBybadlyDamage.setText("severely damaged");
        fatalBybadlyDamage.setSelection(AppConfig.get().isFatalBybadlyDamage());

        // キャプチャ タブ
        Composite compositeCapture = new Composite(this.composite, SWT.NONE);
        this.compositeMap.put("capture", compositeCapture);
        compositeCapture.setLayout(new GridLayout(3, false));

        Label label4 = new Label(compositeCapture, SWT.NONE);
        label4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label4.setText("Destination");

        final Text captureDir = new Text(compositeCapture, SWT.BORDER);
        GridData gdCaptureDir = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdCaptureDir.widthHint = 120;
        captureDir.setLayoutData(gdCaptureDir);
        captureDir.setText(AppConfig.get().getCapturePath());

        Button savedirBtn = new Button(compositeCapture, SWT.NONE);
        savedirBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(ConfigDialog.this.shell);
                dialog.setMessage("Please specify a location");
                String path = dialog.open();
                if (path != null) {
                    captureDir.setText(path);
                }
            }
        });
        savedirBtn.setText("Select");

        Label label5 = new Label(compositeCapture, SWT.NONE);
        label5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label5.setText("Format");

        final Combo imageformatCombo = new Combo(compositeCapture, SWT.READ_ONLY);
        imageformatCombo.setItems(new String[] { "jpg", "png" });
        imageformatCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        imageformatCombo.select(0);
        for (int i = 0; i < imageformatCombo.getItems().length; i++) {
            if (AppConfig.get().getImageFormat().equals(imageformatCombo.getItem(i))) {
                imageformatCombo.select(i);
                break;
            }
        }
        new Label(compositeCapture, SWT.NONE);

        // Development タブ
        Composite compositeDevelopment = new Composite(this.composite, SWT.NONE);
        this.compositeMap.put("development", compositeDevelopment);
        compositeDevelopment.setLayout(new GridLayout(2, false));

        new Label(compositeDevelopment, SWT.NONE);
        final Button btnJson = new Button(compositeDevelopment, SWT.CHECK);
        btnJson.setText("Save JSON to file");
        btnJson.setSelection(AppConfig.get().isStoreJson());

        Label lblJson = new Label(compositeDevelopment, SWT.NONE);
        lblJson.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblJson.setText("Destination");

        final Text jsonpath = new Text(compositeDevelopment, SWT.BORDER);
        jsonpath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        jsonpath.setText(AppConfig.get().getStoreJsonPath());

        Composite command = new Composite(this.shell, SWT.NONE);
        RowLayout rlCommand = new RowLayout();
        command.setLayout(rlCommand);
        command.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

        // OK ボタン
        Button applyBtn = new Button(command, SWT.NONE);
        applyBtn.setLayoutData(new RowData(100, SWT.DEFAULT));
        applyBtn.setText("OK");
        applyBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // 設定の保存アクション

                // system
                if (StringUtils.isNumeric(listenport.getText())) {
                    AppConfig.get().setListenPort(Integer.parseInt(listenport.getText()));
                }
                AppConfig.get().setHideWindow(hidewindow.getSelection());
                AppConfig.get().setOnTop(ontop.getSelection());
                AppConfig.get().setCheckDoit(checkDoit.getSelection());
                if (StringUtils.isNumeric(soundlevel.getText())) {
                    float level = (float) Integer.parseInt(soundlevel.getText()) / 100;
                    AppConfig.get().setSoundLevel(level);
                }
                if (StringUtils.isNumeric(alpha.getText())) {
                    AppConfig.get().setAlpha(Integer.parseInt(alpha.getText()));
                }
                AppConfig.get().setReportPath(reportDir.getText());
                AppConfig.get().setCheckUpdate(checkUpdate.getSelection());
                // fleettab
                AppConfig.get().setWarnByNeedSupply(warnByNeedSupply.getSelection());
                AppConfig.get().setWarnByCondState(warnByCondState.getSelection());
                AppConfig.get().setWarnByHalfDamage(warnByHalfDamage.getSelection());
                AppConfig.get().setFatalBybadlyDamage(fatalBybadlyDamage.getSelection());
                // capture
                AppConfig.get().setCapturePath(captureDir.getText());
                AppConfig.get().setImageFormat(imageformatCombo.getItem(imageformatCombo.getSelectionIndex()));

                // development
                AppConfig.get().setStoreJson(btnJson.getSelection());
                AppConfig.get().setStoreJsonPath(jsonpath.getText());
                try {
                    AppConfig.store();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                ConfigDialog.this.shell.close();
            }
        });

        Button cancelBtn = new Button(command, SWT.NONE);
        cancelBtn.setLayoutData(new RowData(100, SWT.DEFAULT));
        cancelBtn.setText("Cancel");
        cancelBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ConfigDialog.this.shell.close();
            }
        });

        for (Entry<String, Composite> entry : this.compositeMap.entrySet()) {
            LayoutLogic.hide(entry.getValue(), true);
        }

        sashForm.setWeights(new int[] { 2, 5 });
        this.scrolledComposite.setContent(this.composite);
        this.scrolledComposite.setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * ツリーをクリックした時に呼び出されるアダプター
     *
     */
    private static final class TreeSelectionAdapter extends SelectionAdapter {

        /** ダイアログ */
        private final ConfigDialog dialog;

        /**
         * コンストラクター
         */
        public TreeSelectionAdapter(ConfigDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Object data = e.item.getData();
            if (data instanceof String) {
                String treeKey = (String) data;
                for (Entry<String, Composite> entry : this.dialog.compositeMap.entrySet()) {
                    if (entry.getKey().equals(treeKey)) {
                        LayoutLogic.hide(entry.getValue(), false);
                    } else {
                        LayoutLogic.hide(entry.getValue(), true);
                    }
                }
                this.dialog.composite.layout();
                this.dialog.scrolledComposite.setMinSize(this.dialog.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        }
    }
}
