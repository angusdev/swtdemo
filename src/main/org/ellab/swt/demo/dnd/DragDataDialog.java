package org.ellab.swt.demo.dnd;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DragDataDialog {
    private Display display;
    private Shell shlSetupDragData;
    private Composite compositeMain;
    private Label label;
    private Text txtText;
    private Text txtHTML;
    private Text txtFile1;
    private Text txtFile2;
    private Text txtFile3;
    private Text txtURL;
    private Button btnFileBrowse1;
    private Button btnFileBrowse2;
    private Button btnFileBrowse3;
    private Button btnClose;
    private DropTarget dropTarget;
    private Composite composite;

    public DragDataDialog(Display display, Map<String, Object> dragData) {
        this.display = display;
        init();

        txtText.setText((String) dragData.get("Text"));
        txtHTML.setText((String) dragData.get("HTML"));
        txtURL.setText((String) dragData.get("URL"));
        txtFile1.setText(((String[]) dragData.get("File"))[0]);
        txtFile2.setText(((String[]) dragData.get("File"))[1]);
        txtFile3.setText(((String[]) dragData.get("File"))[2]);

        Arrays.stream(compositeMain.getChildren()).filter(c -> c instanceof Text).map(c -> (Text) c).forEach(c -> {
            DropTarget target = new DropTarget(c, DND.DROP_DEFAULT | DND.DROP_COPY);
            Transfer[] transfers = { TextTransfer.getInstance() };
            target.setTransfer(transfers);
            target.addDropListener(new DropTargetAdapter() {
                @Override
                public void dragEnter(DropTargetEvent event) {
                    if (event.detail == DND.DROP_DEFAULT) {
                        event.detail = DND.DROP_COPY;
                    }
                }

                @Override
                public void drop(DropTargetEvent event) {
                    c.setText(event.data.toString());
                }
            });
        });

        btnClose.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                dragData.put("Text", txtText.getText());
                dragData.put("HTML", txtHTML.getText());
                dragData.put("URL", txtURL.getText());
                ((String[]) dragData.get("File"))[0] = txtFile1.getText();
                ((String[]) dragData.get("File"))[1] = txtFile2.getText();
                ((String[]) dragData.get("File"))[2] = txtFile2.getText();

                shlSetupDragData.dispose();
            }

        });
        setupDropTarget();
    }

    private void init() {
        shlSetupDragData = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
        shlSetupDragData.setMinimumSize(new Point(500, 25));
        shlSetupDragData.setSize(500, 500);
        shlSetupDragData.setText("Setup Drag Data");
        shlSetupDragData.setLayout(new GridLayout(1, false));
        shlSetupDragData.setImage(
                new Image(display, DragDataDialog.class.getResourceAsStream("/org/ellab/swt/demo/dnd/dnddemo.ico")));

        compositeMain = new Composite(shlSetupDragData, SWT.NONE);
        compositeMain.setLayout(new GridLayout(2, false));
        GridData gd_compositeMain = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_compositeMain.heightHint = 50;
        compositeMain.setLayoutData(gd_compositeMain);

        composite = new Composite(compositeMain, SWT.NONE);
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
        gd_composite.heightHint = 50;
        composite.setLayoutData(gd_composite);
        composite.setLayout(new GridLayout(1, false));

        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
        label.setSize(49, 13);
        label.setText("Drop Here");

        label = new Label(compositeMain, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        label.setText("&TextTransfer");

        txtText = new Text(compositeMain, SWT.BORDER | SWT.MULTI);
        GridData gd_txtText = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gd_txtText.heightHint = 50;
        txtText.setLayoutData(gd_txtText);

        label = new Label(compositeMain, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        label.setText("&HTMLTransfer");

        txtHTML = new Text(compositeMain, SWT.BORDER | SWT.MULTI);
        GridData gd_txtHTML = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gd_txtHTML.heightHint = 50;
        txtHTML.setLayoutData(gd_txtHTML);

        label = new Label(compositeMain, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1));
        label.setText("&FileTransfer");

        txtFile1 = new Text(compositeMain, SWT.BORDER);
        txtFile1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnFileBrowse1 = new Button(compositeMain, SWT.NONE);
        btnFileBrowse1.setText("...");

        txtFile2 = new Text(compositeMain, SWT.BORDER);
        txtFile2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnFileBrowse2 = new Button(compositeMain, SWT.NONE);
        btnFileBrowse2.setText("...");

        txtFile3 = new Text(compositeMain, SWT.BORDER);
        txtFile3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnFileBrowse3 = new Button(compositeMain, SWT.NONE);
        btnFileBrowse3.setText("...");

        label = new Label(compositeMain, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        label.setText("&URLTransfer");

        txtURL = new Text(compositeMain, SWT.BORDER);
        GridData gd_txtURL = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gd_txtURL.heightHint = 50;
        txtURL.setLayoutData(gd_txtURL);

        btnClose = new Button(compositeMain, SWT.NONE);
        btnClose.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
        btnClose.setText("Close");

        dropTarget = new DropTarget(composite, DND.DROP_DEFAULT | DND.DROP_COPY | DND.DROP_MOVE);
    }

    public void open() {
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shlSetupDragData.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shlSetupDragData.setLocation(x, y);

        shlSetupDragData.open();
        // shlSetupDragData.pack();

        while (!shlSetupDragData.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void setupDropTarget() {
        Transfer[] transfers = { FileTransfer.getInstance(), TextTransfer.getInstance(), RTFTransfer.getInstance(),
                HTMLTransfer.getInstance(), URLTransfer.getInstance() };
        dropTarget.setTransfer(transfers);

        dropTarget.addDropListener(new DropTargetListener() {
            @Override
            public void dropAccept(DropTargetEvent event) {
            }

            @Override
            public void drop(DropTargetEvent event) {
                Arrays.stream(event.dataTypes).forEach(dt -> {
                    if (TextTransfer.getInstance().isSupportedType(dt)) {
                        txtText.setText(event.data.toString());
                    }
                    else if (HTMLTransfer.getInstance().isSupportedType(dt)) {
                        txtHTML.setText(event.data.toString());
                    }
                    else if (URLTransfer.getInstance().isSupportedType(dt)) {
                        txtURL.setText(event.data.toString());
                    }
                    else if (FileTransfer.getInstance().isSupportedType(dt)) {
                        String[] data = (String[]) event.data;
                        txtFile1.setText(data.length > 0 ? data[0] : "");
                        txtFile2.setText(data.length > 1 ? data[1] : "");
                        txtFile3.setText(data.length > 2 ? data[2] : "");
                    }
                });
            }

            @Override
            public void dragOver(DropTargetEvent event) {
            }

            @Override
            public void dragOperationChanged(DropTargetEvent event) {
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragEnter(DropTargetEvent event) {
            }
        });
    }

}
