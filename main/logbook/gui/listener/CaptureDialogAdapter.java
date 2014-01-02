/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui.listener;

import logbook.gui.CaptureDialog;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * キャプチャボタンのリスナー
 * 
 */
public final class CaptureDialogAdapter extends AbstractReportAdapter {

    /**
     * @param shell シェル
     */
    public CaptureDialogAdapter(Shell shell) {
        super(shell);
    }

    @Override
    public void widgetSelected(SelectionEvent paramSelectionEvent) {
        new CaptureDialog(this.shell).open();
    }
}
