/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui;

import java.util.Arrays;

import logbook.config.AppConfig;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * テーブルの列を表示・非表示選択するダイアログ
 *
 */
public final class SelectVisibleColumnDialog extends Dialog {

    /** 親ダイアログ */
    private final AbstractTableDialog dialog;

    /** シェル */
    private Shell shell;

    /**
     * Create the dialog.
     * @param parent 親シェル
     * @param dialog 親ダイアログ
     */
    public SelectVisibleColumnDialog(Shell parent, AbstractTableDialog dialog) {
        super(parent, SWT.NONE);
        this.dialog = dialog;
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
        this.shell = new Shell(this.getParent(), SWT.SHELL_TRIM | SWT.PRIMARY_MODAL);
        this.shell.setSize(300, 275);
        this.shell.setText("列の表示非表示");
        this.shell.setLayout(new FillLayout(SWT.HORIZONTAL));

        // ヘッダー
        String[] header = this.dialog.getTableHeader();
        // カラム設定を取得
        boolean[] visibles = AppConfig.get().getVisibleColumnMap().get(this.dialog.getClass().getName());
        if ((visibles == null) || (visibles.length != header.length)) {
            visibles = new boolean[header.length];
            Arrays.fill(visibles, true);
            AppConfig.get().getVisibleColumnMap().put(this.dialog.getClass().getName(), visibles);
        }

        Tree tree = new Tree(this.shell, SWT.BORDER | SWT.CHECK);

        for (int i = 1; i < header.length; i++) {
            TreeItem column = new TreeItem(tree, SWT.CHECK);
            column.setText(header[i]);
            column.setChecked(visibles[i]);
            column.setExpanded(true);
        }
        this.shell.addShellListener(new TreeShellAdapter(tree, visibles, this.dialog));
    }

    /**
     * チェックされた内容をウインドウが閉じるタイミングで保存します
     *
     */
    private static final class TreeShellAdapter extends ShellAdapter {

        private final Tree tree;
        private final boolean[] visibles;
        private final AbstractTableDialog dialog;

        public TreeShellAdapter(Tree tree, boolean[] visibles, AbstractTableDialog dialog) {
            this.tree = tree;
            this.visibles = visibles;
            this.dialog = dialog;
        }

        @Override
        public void shellClosed(ShellEvent e) {
            TreeItem[] items = this.tree.getItems();
            for (int i = 0; i < items.length; i++) {
                this.visibles[i + 1] = items[i].getChecked();
            }
            this.dialog.reloadTable();
        }
    }
}
