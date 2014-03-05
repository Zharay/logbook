/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.thread;

import java.util.ArrayList;
import java.util.List;

import logbook.thread.ThreadManager.ExceptionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * スレッドの状態を監視します
 *
 */
public final class ThreadStateObserver extends Thread {

    private final Shell shell;

    private final List<Thread> observthread;

    /**
     * コンストラクター
     */
    public ThreadStateObserver(Shell shell) {
        this.observthread = new ArrayList<Thread>(ThreadManager.getThreads());
        this.shell = shell;
        this.setName("logbook_thread_state_observer");
    }

    /* (非 Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        try {
            while (true) {
                for (int i = 0; i < this.observthread.size(); i++) {
                    Thread target = this.observthread.get(i);
                    if (!target.isAlive()) {
                        ExceptionHandler handler = ThreadManager.getUncaughtExceptionHandler(target);
                        String stackTrace = handler.toString();

                        if (!"".equals(stackTrace)) {

                            StringBuilder sb = new StringBuilder();
                            sb.append(target.getClass().getName());
                        	sb.append("[");
                        	sb.append(target.getName());
                        	sb.append("]");
                        	sb.append("Terminated unexpectedly and error logged.\n");
                        	sb.append("Exception:\n");

                            final String message = sb.toString();
                            Display.getDefault().asyncExec(new Runnable() {
                                @Override
                                public void run() {
                                    MessageBox box = new MessageBox(ThreadStateObserver.this.shell, SWT.YES
                                            | SWT.ICON_ERROR);
                                    box.setText("Ended the thread unexplectedly.");
                                    box.setMessage(message);
                                    box.open();
                                }
                            });
                        }
                        this.observthread.remove(i);
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
