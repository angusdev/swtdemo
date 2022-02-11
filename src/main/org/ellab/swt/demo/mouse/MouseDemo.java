package org.ellab.swt.demo.mouse;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ellab.swt.utils.TableUtils;

public class MouseDemo {
    private static final int COL_TIME = 0;
    private static final int COL_AREA = 1;
    private static final int COL_EVENT = 2;
    private static final int COL_BUTTON = 3;
    private static final int COL_COUNT = 4;
    private static final int COL_STATE_MASK = 5;
    private static final int COL_X = 6;
    private static final int COL_Y = 7;

    private Display display;
    private Shell shell;
    private Table table;

    public class EventListener {
        private String name;
        private long startTime = -1;

        private void addEvent(String event, MouseEvent e) {
            table.setRedraw(false);

            long time = -1;
            int repeated = 0;

            if (startTime < 0) {
                startTime = new Date().getTime();
            }
            else {
                time = new Date().getTime() - startTime;
            }

            if (table.getItemCount() >= 2 && "SWT.MouseMove".equals(event)) {
                TableItem ti1 = table.getItem(table.getItemCount() - 1);
                Pattern p = Pattern.compile("^(.+)\\ \\((\\d+)\\)$");
                Matcher m = p.matcher(ti1.getText(COL_EVENT));
                if (m.matches()) {
                    repeated = Integer.parseInt(m.group(2)) + 1;
                    table.remove(table.getItemCount() - 1);
                }
                else if (ti1.getText(COL_AREA).equals(name) && ti1.getText(COL_EVENT).equals(event)) {
                    repeated = 2;
                }
            }

            TableItem ti = new TableItem(table, SWT.NONE);
            ti.setText(COL_TIME, time >= 0 ? "" + new DecimalFormat("0.###").format(time / 1000.f) : "");
            ti.setText(COL_AREA, name);
            ti.setText(COL_EVENT, event + (repeated > 1 ? (" (" + repeated + ")") : ""));
            ti.setText(COL_BUTTON, "" + e.button);
            ti.setText(COL_COUNT, "" + e.count);
            ti.setText(COL_STATE_MASK, "" + e.stateMask);
            ti.setText(COL_X, "" + e.x);
            ti.setText(COL_Y, "" + e.y);

            table.setTopIndex(table.getItemCount() - 1);

            table.setRedraw(true);
        }

        public EventListener(String name, Control control) {
            this.name = name;

            control.addMouseListener(new MouseListener() {
                @Override
                public void mouseDoubleClick(MouseEvent e) {
                    addEvent("SWT.MouseDoubleClick", e);
                }

                @Override
                public void mouseDown(MouseEvent e) {
                    addEvent("SWT.MouseDown", e);
                }

                @Override
                public void mouseUp(MouseEvent e) {
                    addEvent("SWT.MouseUp", e);
                }
            });

            control.addMouseMoveListener(new MouseMoveListener() {
                @Override
                public void mouseMove(MouseEvent e) {
                    addEvent("SWT.MouseMove", e);
                }
            });

            control.addMouseTrackListener(new MouseTrackListener() {
                @Override
                public void mouseHover(MouseEvent e) {
                    addEvent("SWT.MouseHover", e);
                }

                @Override
                public void mouseExit(MouseEvent e) {
                    addEvent("SWT.MouseExit", e);
                }

                @Override
                public void mouseEnter(MouseEvent e) {
                    addEvent("SWT.MouseEnter", e);
                }
            });

            control.addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseScrolled(MouseEvent e) {
                    addEvent("SWT.MouseWheel", e);
                }
            });
        }

    }

    public static void main(String[] args) {
        Display display = new Display();
        new MouseDemo(display).show();
    }

    public MouseDemo(Display display) {
        this.display = display;

        init();

        TableUtils.packAllColumns(table, new String[] { "88:88:88.888", "Area 8",
                "SWT.MouseDoubleClick " + SWT.MouseDoubleClick, "888", "888", "88888888", "88888", "88888" });
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
        shell.setText("Mouse Demo");
        shell.setSize(650, 368);
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(new Image(display,
                MouseDemo.class.getResourceAsStream("/org/ellab/swt/demo/mouse/mousedemo-white.ico")));

        Composite composite = new Composite(shell, SWT.NONE);
        FillLayout fl_composite = new FillLayout(SWT.HORIZONTAL);
        fl_composite.spacing = 30;
        composite.setLayout(fl_composite);
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_composite.heightHint = 100;
        gd_composite.minimumHeight = 100;
        composite.setLayoutData(gd_composite);

        CLabel lbl1 = new CLabel(composite, SWT.BORDER);
        lbl1.setAlignment(SWT.CENTER);
        lbl1.setText("Area 1");
        new EventListener(lbl1.getText(), lbl1);

        CLabel lbl2 = new CLabel(composite, SWT.BORDER);
        lbl2.setAlignment(SWT.CENTER);
        lbl2.setText("Area 2");
        new EventListener(lbl2.getText(), lbl2);

        CLabel lbl3 = new CLabel(composite, SWT.BORDER);
        lbl3.setAlignment(SWT.CENTER);
        lbl3.setText("Area 3");
        new EventListener(lbl3.getText(), lbl3);

        table = new Table(shell, SWT.BORDER | SWT.HIDE_SELECTION);
        GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_table.heightHint = 9999;
        table.setLayoutData(gd_table);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableColumn tblclmnTime = new TableColumn(table, SWT.NONE);
        tblclmnTime.setWidth(100);
        tblclmnTime.setText("Time");

        TableColumn tblclmnArea = new TableColumn(table, SWT.NONE);
        tblclmnArea.setWidth(100);
        tblclmnArea.setText("Area");

        TableColumn tblclmnEvent = new TableColumn(table, SWT.NONE);
        tblclmnEvent.setWidth(100);
        tblclmnEvent.setText("Event");

        TableColumn tblclmnButton = new TableColumn(table, SWT.NONE);
        tblclmnButton.setWidth(100);
        tblclmnButton.setText("Button");
        tblclmnButton.setToolTipText(
                "the button that was pressed or released; 1 for the first button (usually 'left') 2 for the second button (usually 'middle') 3 for the third button (usually 'right') etc.");

        TableColumn tblclmnCount = new TableColumn(table, SWT.NONE);
        tblclmnCount.setWidth(100);
        tblclmnCount.setText("Count");
        tblclmnCount.setToolTipText(
                "the number times the mouse has been clicked, as defined by the operating system; 1 for the first click, 2 for the second click and so on.");

        TableColumn tblclmnStateMask = new TableColumn(table, SWT.NONE);
        tblclmnStateMask.setWidth(100);
        tblclmnStateMask.setText("State");
        tblclmnStateMask.setToolTipText(
                "the state of the keyboard modifier keys and mouse masks at the time the event was generated.");

        TableColumn tblclmnX = new TableColumn(table, SWT.NONE);
        tblclmnX.setWidth(100);
        tblclmnX.setText("X");
        tblclmnX.setToolTipText(
                "the widget-relative, x coordinate of the pointer at the time the mouse button was pressed or released");

        TableColumn tblclmnY = new TableColumn(table, SWT.NONE);
        tblclmnY.setWidth(100);
        tblclmnY.setText("Y");
        tblclmnY.setToolTipText(
                "the widget-relative, y coordinate of the pointer at the time the mouse button was pressed or released");
    }
}
