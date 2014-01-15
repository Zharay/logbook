/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui;

import logbook.config.ShipGroupConfig;
import logbook.config.bean.ShipGroupBean;
import logbook.dto.ShipFilterDto;
import logbook.gui.logic.CreateReportLogic;
import logbook.gui.logic.TableItemCreator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

/**
 * 所有艦娘一覧テーブル
 *
 */
public final class ShipTable extends AbstractTableDialog {

    /** 成長余地 */
    private static boolean specdiff = false;

    /** フィルター */
    private ShipFilterDto filter = new ShipFilterDto();

    /**
     * @param parent
     */
    public ShipTable(Shell parent) {
        super(parent);
    }

    /**
     * フィルターを設定する
     * @param filter フィルター
     */
    public void updateFilter(ShipFilterDto filter) {
        this.filter = filter;
        this.reloadTable();
    }

    @Override
    protected void createContents() {
        // メニューバーに追加する
        // フィルターメニュー
        SelectionListener groupListener = new GroupFilterSelectionAdapter(this);
        MenuItem groupCascade = new MenuItem(this.opemenu, SWT.CASCADE);
        groupCascade.setText("&Group Filter");
        Menu groupMenu = new Menu(groupCascade);
        groupCascade.setMenu(groupMenu);
        MenuItem nullGroupItem = new MenuItem(groupMenu, SWT.RADIO);
        nullGroupItem.setText("Select None\tF6");
        nullGroupItem.setAccelerator(SWT.F6);
        nullGroupItem.setSelection(true);
        nullGroupItem.addSelectionListener(groupListener);
        for (int i = 0; i < ShipGroupConfig.get().getGroup().size(); i++) {
            ShipGroupBean group = ShipGroupConfig.get().getGroup().get(i);
            MenuItem groupItem = new MenuItem(groupMenu, SWT.RADIO);
            if ((SWT.KEYCODE_BIT + 16 + i) <= SWT.F20) {
                groupItem.setText(group.getName() + "\t" + "F" + (i + 7));
                groupItem.setAccelerator(SWT.KEYCODE_BIT + 16 + i);
            } else {
                groupItem.setText(group.getName());
            }
            groupItem.setData(group);
            groupItem.addSelectionListener(groupListener);
        }
        final MenuItem filter = new MenuItem(this.opemenu, SWT.PUSH);
        filter.setText("&Filter\tCtrl+F");
        filter.setAccelerator(SWT.CTRL + 'F');
        filter.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new ShipFilterDialog(ShipTable.this.shell, ShipTable.this, ShipTable.this.filter).open();
            }
        });
        // セパレータ
        new MenuItem(this.opemenu, SWT.SEPARATOR);
        // 成長の余地を表示メニュー
        final MenuItem switchdiff = new MenuItem(this.opemenu, SWT.CHECK);
        switchdiff.setText("Show stats potential");
        switchdiff.setSelection(specdiff);
        switchdiff.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                specdiff = switchdiff.getSelection();
                ShipTable.this.reloadTable();
            }
        });
        // 右クリックメニューに追加する
        final MenuItem filtertable = new MenuItem(this.tablemenu, SWT.NONE);
        filtertable.setText("&Filter");
        filtertable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new ShipFilterDialog(ShipTable.this.shell, ShipTable.this, ShipTable.this.filter).open();
            }
        });

    }

    @Override
    protected String getTitle() {
        return "Ship List";
    }

    @Override
    protected Point getSize() {
        return new Point(600, 350);
    }

    @Override
    protected String[] getTableHeader() {
        return CreateReportLogic.getShipListHeader();
    }

    @Override
    protected void updateTableBody() {
        this.body = CreateReportLogic.getShipListBody(ShipTable.specdiff, this.filter);
    }

    @Override
    protected TableItemCreator getTableItemCreator() {
        return CreateReportLogic.SHIP_LIST_TABLE_ITEM_CREATOR;
    }

    @Override
    protected SelectionListener getHeaderSelectionListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.getSource() instanceof TableColumn) {
                    ShipTable.this.sortTableItems((TableColumn) e.getSource());
                }
            }
        };
    }

    /**
     * フィルターを取得します。
     * @return フィルター
     */
    public ShipFilterDto getFilter() {
        return this.filter;
    }

    /**
     * グループフィルターを選択した時に呼び出されるアダプター
     *
     */
    private static final class GroupFilterSelectionAdapter extends SelectionAdapter {

        /** ダイアログ */
        private final ShipTable dialog;

        public GroupFilterSelectionAdapter(ShipTable dialog) {
            this.dialog = dialog;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (e.widget instanceof MenuItem) {
                MenuItem item = (MenuItem) e.widget;
                if (item.getSelection()) {
                    ShipGroupBean group = (ShipGroupBean) item.getData();
                    this.dialog.getFilter().group = group;
                    this.dialog.reloadTable();
                }
            }
        }
    }
}
