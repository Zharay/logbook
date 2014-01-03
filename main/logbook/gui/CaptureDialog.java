/**
 * No Rights Reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Public Domain.
 */
package logbook.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import logbook.config.GlobalConfig;
import logbook.gui.logic.AwtUtils;
import logbook.gui.logic.LayoutLogic;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * キャプチャダイアログ
 *
 */
public final class CaptureDialog extends Dialog {

    private Shell shell;

    private Composite composite;
    private Text text;
    private Button capture;
    private Button interval;
    private Spinner intervalms;

    private Rectangle rectangle;
    private CaptureThread thread;

    private Font font;

    /**
     * Create the dialog.
     * @param parent
     */
    public CaptureDialog(Shell parent) {
        super(parent, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.RESIZE);
        this.setText("Capture");
    }

    /**
     * Open the dialog.
     * @return the result
     */
    public void open() {
        try {
            this.createContents();
            this.shell.open();
            this.shell.layout();
            Display display = this.getParent().getDisplay();
            while (!this.shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } finally {
            // キャプチャスレッドを停止させる
            if ((this.thread != null) && this.thread.isAlive()) {
                this.thread.shutdown();
            }
            // フォントを開放
            if (this.font != null) {
                this.font.dispose();
            }
        }
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        // シェル
        this.shell = new Shell(this.getParent(), this.getStyle());
        this.shell.setText(this.getText());
        // レイアウト
        GridLayout glShell = new GridLayout(1, false);
        glShell.horizontalSpacing = 1;
        glShell.marginHeight = 1;
        glShell.marginWidth = 1;
        glShell.verticalSpacing = 1;
        this.shell.setLayout(glShell);

        // 太字にするためのフォントデータを作成する
        FontData defaultfd = this.shell.getFont().getFontData()[0];
        FontData fd = new FontData(defaultfd.getName(), defaultfd.getHeight(), SWT.BOLD);
        this.font = new Font(Display.getDefault(), fd);

        // コンポジット
        Composite rangeComposite = new Composite(this.shell, SWT.NONE);
        rangeComposite.setLayout(new GridLayout(2, false));

        // 範囲設定
        this.text = new Text(rangeComposite, SWT.BORDER | SWT.READ_ONLY);
        GridData gdText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdText.widthHint = 120;
        this.text.setLayoutData(gdText);
        this.text.setText("Range not set");

        Button button = new Button(rangeComposite, SWT.NONE);
        button.setText("Set");
        button.addSelectionListener(new SelectRectangleAdapter());

        // コンポジット
        this.composite = new Composite(this.shell, SWT.NONE);
        GridLayout loglayout = new GridLayout(3, false);
        this.composite.setLayout(loglayout);
        this.composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        // 周期設定
        this.interval = new Button(this.composite, SWT.CHECK);
        this.interval.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CaptureDialog.this.capture.setText(getCaptureButtonText(false,
                        CaptureDialog.this.interval.getSelection()));
            }
        });
        this.interval.setText("Interval");

        this.intervalms = new Spinner(this.composite, SWT.BORDER);
        this.intervalms.setMaximum(60000);
        this.intervalms.setMinimum(100);
        this.intervalms.setSelection(1000);
        this.intervalms.setIncrement(100);

        Label label = new Label(this.composite, SWT.NONE);
        label.setText("ms");

        this.capture = new Button(this.shell, SWT.NONE);
        this.capture.setFont(this.font);
        GridData gdCapture = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gdCapture.horizontalSpan = 3;
        gdCapture.heightHint = 64;
        this.capture.setLayoutData(gdCapture);
        this.capture.setEnabled(false);
        this.capture.setText(getCaptureButtonText(false, this.interval.getSelection()));
        this.capture.addSelectionListener(new CaptureStartAdapter());

        this.shell.pack();
    }

    /**
     * キャプチャボタンの文字を取得します
     * 
     * @param isrunning
     * @param interval
     * @return
     */
    private static String getCaptureButtonText(boolean isrunning, boolean interval) {
        if (isrunning && interval) {
            return "Stop";
        } else if (interval) {
            return "Start";
        } else {
            return "Capture";
        }
    }

    /**
     * 範囲の選択を押した時
     *
     */
    public final class SelectRectangleAdapter extends SelectionAdapter {
        /** ダイアログが完全に消えるまで待つ時間 */
        private static final int WAIT = 250;

        @Override
        public void widgetSelected(SelectionEvent paramSelectionEvent) {
            try {
                Display display = Display.getDefault();
                // ダイアログを非表示にする
                CaptureDialog.this.shell.setVisible(false);
                // 消えるまで待つ
                Thread.sleep(WAIT);
                // ディスプレイに対してGraphics Contextを取得する(フルスクリーンキャプチャ)
                GC gc = new GC(display);
                Image image = new Image(display, display.getBounds());
                gc.copyArea(image, 0, 0);
                gc.dispose();

                try {
                    // 範囲を取得する
                    Rectangle rectangle = new FullScreenDialog(CaptureDialog.this.shell, image).open();

                    if ((rectangle != null) && (rectangle.width > 1) && (rectangle.height > 1)) {
                        CaptureDialog.this.rectangle = rectangle;
                        CaptureDialog.this.text.setText("(" + rectangle.x + "," + rectangle.y + ")-("
                                + (rectangle.x + rectangle.width) + "," + (rectangle.y + rectangle.height) + ")");
                        CaptureDialog.this.capture.setEnabled(true);
                    }
                } finally {
                    image.dispose();
                }
                CaptureDialog.this.shell.setVisible(true);
                CaptureDialog.this.shell.setActive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * キャプチャボタンを押した時
     *
     */
    public final class CaptureStartAdapter extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {
            CaptureThread thread = CaptureDialog.this.thread;

            Rectangle rectangle = CaptureDialog.this.rectangle;
            boolean interval = CaptureDialog.this.interval.getSelection();
            int intervalms = CaptureDialog.this.intervalms.getSelection();

            if ((thread != null) && thread.isAlive()) {
                // スレッドが生存している場合、停止させる
                thread.shutdown();

                CaptureDialog.this.capture.setText(getCaptureButtonText(false, interval));
                LayoutLogic.enable(CaptureDialog.this.composite, true);
            } else {
                // スレッドが生存していない場合キャプチャする
                thread = new CaptureThread(rectangle, interval, intervalms);
                thread.start();
                CaptureDialog.this.thread = thread;

                CaptureDialog.this.capture.setText(getCaptureButtonText(true, interval));

                if (interval) {
                    LayoutLogic.enable(CaptureDialog.this.composite, false);
                }
            }
        }
    }

    /**
     * 画面キャプチャスレッド
     *
     */
    public static final class CaptureThread extends Thread {
        /** ロガー */
        private static final Logger LOG = LogManager.getLogger(CaptureThread.class);
        /** Jpeg品質 */
        private static final float QUALITY = 0.9f;
        /** 日付フォーマット */
        private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
        /** キャプチャ範囲 */
        private final Rectangle rectangle;
        /** 周期キャプチャ */
        private boolean interval;
        /** 周期キャプチャ間隔(ms) */
        private final int ms;

        public CaptureThread(Rectangle rectangle, boolean interval, int intervalms) {
            this.rectangle = rectangle;
            this.interval = interval;
            this.ms = intervalms;
        }

        @Override
        public void run() {
            try {
                do {
                    // 時刻からファイル名を作成
                    Date now = Calendar.getInstance().getTime();
                    String fname = FilenameUtils.concat(GlobalConfig.getCapturePath(), this.format.format(now) + "."
                            + GlobalConfig.getImageFormat());
                    File file = new File(fname);

                    // 範囲をキャプチャする
                    BufferedImage image = AwtUtils.getCapture(this.rectangle);
                    if (image != null) {

                        ImageOutputStream ios = ImageIO.createImageOutputStream(file);
                        try {
                            ImageWriter writer = ImageIO.getImageWritersByFormatName(GlobalConfig.getImageFormat())
                                    .next();
                            try {
                                ImageWriteParam iwp = writer.getDefaultWriteParam();
                                if (iwp.canWriteCompressed()) {
                                    iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                                    iwp.setCompressionQuality(QUALITY);
                                }
                                writer.setOutput(ios);
                                writer.write(null, new IIOImage(AwtUtils.trim(image), null, null), iwp);
                            } finally {
                                writer.dispose();
                            }
                        } finally {
                            ios.close();
                        }
                    }
                    if (this.interval) {
                        Thread.sleep(this.ms);
                    }
                } while (this.interval);
            } catch (Exception e) {
                LOG.warn("An exception occurred during capture", e);
            }
        }

        public void shutdown() {
            this.interval = false;
        }
    }
}
