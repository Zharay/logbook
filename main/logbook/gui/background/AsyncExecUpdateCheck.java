/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui.background;

import java.awt.Desktop;

import logbook.constants.AppConstants;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * アップデートチェックを行います
 *
 */
public final class AsyncExecUpdateCheck extends Thread {

    private static final Logger LOG = LogManager.getLogger(AsyncExecUpdateCheck.class);

    private final Shell shell;

    /**
     * コンストラクター
     * 
     * @param shell
     */
    public AsyncExecUpdateCheck(Shell shell) {
        this.shell = shell;
        this.setName("logbook_async_exec_update_check");
    }

    @Override
    public void run() {
        try {
            final String newversion = IOUtils.toString(AppConstants.UPDATE_CHECK_URI);

            if (!AppConstants.VERSION.equals(newversion)) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {

                        MessageBox box = new MessageBox(AsyncExecUpdateCheck.this.shell, SWT.YES | SWT.NO
                                | SWT.ICON_QUESTION);
                        box.setText("Update");
                        box.setMessage("There is a new version available. Update?\r\n"
                                + "Current: " + AppConstants.VERSION + "\r\n"
                                + "Latest: " + newversion + "\r\n"
                                + "※You can turn off this notification in the Settings menu");

                        // OKを押されたらホームページへ移動する
                        if (box.open() == SWT.YES) {
                            try {
                                Desktop.getDesktop().browse(AppConstants.HOME_PAGE_URI);
                            } catch (Exception e) {
                                LOG.warn("Failed to open the website", e);
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            // アップデートチェック失敗はクラス名のみ
            LOG.info(e.getClass().getName() + " failed to do an update check");
        }
    }
}
