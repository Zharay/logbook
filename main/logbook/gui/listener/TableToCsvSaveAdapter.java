/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui.listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logbook.gui.logic.CreateReportLogic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * テーブルをCSVファイルに保存するアダプターです
 *
 */
public final class TableToCsvSaveAdapter extends SelectionAdapter {

    /** シェル */
    private final Shell shell;

    /** ファイル名 */
    private final String name;

    /** ヘッダー */
    private final String[] header;

    /** テーブル */
    private final Table table;

    /**
     * コンストラクター
     * 
     * @param shell シェル
     * @param name ファイル名
     * @param table テーブル
     */
    public TableToCsvSaveAdapter(Shell shell, String name, String[] header, Table table) {
        this.shell = shell;
        this.name = name;
        this.header = header;
        this.table = table;
    }

    @Override
    public void widgetSelected(SelectionEvent arg) {
        FileDialog dialog = new FileDialog(this.shell, SWT.SAVE);
        dialog.setFileName(this.name + ".csv");
        dialog.setFilterExtensions(new String[] { "*.csv" });
        String filename = dialog.open();
        if (filename != null) {
            File file = new File(filename);
            if (file.exists()) {
                MessageBox messageBox = new MessageBox(this.shell, SWT.YES | SWT.NO);
                messageBox.setText("logbook");
                messageBox.setMessage(filename + " already exists.\nDo you want to overwrite it?");
                if (messageBox.open() == SWT.NO) {
                    return;
                }
            }
            try {
                List<String[]> body = new ArrayList<String[]>();
                TableItem[] items = this.table.getItems();
                for (TableItem item : items) {
                    String[] colums = new String[this.header.length];
                    for (int i = 0; i < colums.length; i++) {
                        colums[i] = item.getText(i);
                    }
                    body.add(colums);
                }

                CreateReportLogic.writeCsv(file, this.header, body, false);
            } catch (IOException e) {
                MessageBox messageBox = new MessageBox(this.shell, SWT.ICON_ERROR);
                messageBox.setText("Write error");
                messageBox.setMessage(e.toString());
                messageBox.open();
            }
        }
    }
}
