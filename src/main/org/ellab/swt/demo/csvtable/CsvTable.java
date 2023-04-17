package org.ellab.swt.demo.csvtable;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ellab.swt.utils.TableUtils;

public class CsvTable {
    private Display display;
    private Shell shell;
    private Table table;

    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF-8");

        Display display = new Display();
        new CsvTable(display).show();
    }

    public CsvTable(Display display) throws IOException {
        this.display = display;

        init();

        final FileTransfer fileTransfer = FileTransfer.getInstance();
        Transfer[] types = new Transfer[] { fileTransfer };
        DropTarget dropTarget = new DropTarget(table, DND.DROP_MOVE);
        dropTarget.setTransfer(types);
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] files = (String[]) event.data;
                    if (files.length > 0) {
                        try {
                            fileToTable(table, files[0]);
                            TableUtils.packAllColumns(table);
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void show() {
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void init() {
        shell = new Shell(display, SWT.TITLE | SWT.RESIZE | SWT.CLOSE | SWT.MAX | SWT.MIN);
        shell.setText("CsvTable");
        shell.setSize(650, 368);
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(new Image(display,
                CsvTable.class.getResourceAsStream("/org/ellab/swt/demo/csvtable/csvtable-white.ico")));

        Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setText("Drop the CSV file to the below table");

        table = new Table(shell, SWT.BORDER | SWT.HIDE_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
    }

    public static void fileToTable(final Table table, final String file) throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.builder().setTrim(true).setIgnoreSurroundingSpaces(true).build();
        final Reader reader = Files.newBufferedReader(Paths.get(file));
        try (final CSVParser csv = new CSVParser(reader, format)) {
            table.setRedraw(false);

            table.removeAll();
            while (table.getColumnCount() > 0) {
                table.getColumns()[0].dispose();
            }

            final boolean[] init = new boolean[] { false };
            csv.getRecords().stream().forEach(r -> {
                if (init[0]) {
                    TableItem ti = new TableItem(table, SWT.NONE);
                    for (int i = 0; i < r.size(); i++) {
                        ti.setText(i, r.get(i));
                    }
                }
                else {
                    init[0] = true;
                    for (int i = 0; i < r.size(); i++) {
                        new TableColumn(table, SWT.NONE).setText(r.get(i));
                    }
                }
            });
            table.setRedraw(true);
        }
    }
}
