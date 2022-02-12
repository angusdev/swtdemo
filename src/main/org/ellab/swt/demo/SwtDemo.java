package org.ellab.swt.demo;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.ellab.swt.demo.csvtable.CsvTable;
import org.ellab.swt.demo.dnd.DndDemo;
import org.ellab.swt.demo.imageviewer.ImageViewer;
import org.ellab.swt.demo.mouse.MouseDemo;

public class SwtDemo {
    protected Shell shell;

    public static void main(String[] args) {
        try {
            SwtDemo window = new SwtDemo();
            window.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    protected void createContents() {
        shell = new Shell();
        shell.setSize(450, 300);
        shell.setText("SWT Demo");
        shell.setImage(
                new Image(shell.getDisplay(), DndDemo.class.getResourceAsStream("/org/ellab/swt/demo/swtdemo.ico")));
        RowLayout rl_shell = new RowLayout(SWT.VERTICAL);
        rl_shell.pack = false;
        shell.setLayout(rl_shell);

        Label lblVersion = new Label(shell, SWT.NONE);
        lblVersion.setText("Java " + System.getProperty("java.version") + ", " + System.getProperty("os.name")
                + ", SWT " + SWT.getPlatform() + ":" + SWT.getVersion());

        Button btnDnd = new Button(shell, SWT.NONE);
        btnDnd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new DndDemo(shell.getDisplay()).show();
            }
        });
        btnDnd.setImage(new Image(shell.getDisplay(),
                DndDemo.class.getResourceAsStream("/org/ellab/swt/demo/dnd/dnddemo.ico")));
        btnDnd.setText("Drag and Drop");

        Button btnMouse = new Button(shell, SWT.NONE);
        btnMouse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new MouseDemo(shell.getDisplay()).show();
            }
        });
        btnMouse.setImage(new Image(shell.getDisplay(),
                SwtDemo.class.getResourceAsStream("/org/ellab/swt/demo/mouse/mousedemo.ico")));
        btnMouse.setText("Mouse");

        Button btnImageViewer = new Button(shell, SWT.NONE);
        btnImageViewer.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    new ImageViewer(shell.getDisplay()).show();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        btnImageViewer.setImage(new Image(shell.getDisplay(),
                SwtDemo.class.getResourceAsStream("/org/ellab/swt/demo/imageviewer/imageviewer.ico")));
        btnImageViewer.setText("Image Viewer");
        Point bSize = btnImageViewer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        bSize.y = 64;
        btnImageViewer.setSize(bSize);

        Button btnCsvTable = new Button(shell, SWT.NONE);
        btnCsvTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    new CsvTable(shell.getDisplay()).show();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        btnCsvTable.setImage(new Image(shell.getDisplay(),
                SwtDemo.class.getResourceAsStream("/org/ellab/swt/demo/csvtable/csvtable.ico")));
        btnCsvTable.setText("CSV Table");
    }
}
