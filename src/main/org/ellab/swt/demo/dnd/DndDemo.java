package org.ellab.swt.demo.dnd;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class DndDemo {
    private static final int COL_SEQ = 0;
    private static final int COL_TIME = 1;
    private static final int COL_DRAG_DROP = 2;
    private static final int COL_SOURCE = 3;
    private static final int COL_EVENT = 4;
    private static final int COL_DETAIL = 5;
    private static final int COL_TYPE = 6;
    private static final int COL_DATA = 7;

    private Button[] transferButtons;
    private Button[] operationButtons;
    private Button[] feedbackButtons;
    private long dragStartTime;
    private long dropStartTime;
    private boolean transferFileDefaultCopy;
    private int dropFeedback;
    private Map<String, Object> dragData = new HashMap<>();

    private Shell shell;
    private Display display;

    private DropTarget dropTargetLabel;
    private DropTarget dropTargetTable;

    private Composite compositeDropArea;
    private Composite compositeDropLabel;
    private Label lblDropArea;
    private Tree treeDrop;
    private TreeColumn tblclmnDrop1;
    private TreeColumn tblclmnDrop2;

    private Button btnTransferFile;
    private Button btnTransferHTML;
    private Button btnTransferImage;
    private Button btnTransferRTF;
    private Button btnTransferText;
    private Button btnTransferURL;
    private Button btnTransferFileDefaultCopy;
    private Button btnOperationDefault;
    private Button btnOperationCopy;
    private Button btnOperationMove;
    private Button btnOperationLink;
    private Button btnFeedbackSelect;
    private Button btnFeedbackInsertBefore;
    private Button btnFeedbackInsertAfter;
    private Button btnFeedbackScroll;
    private Button btnFeedbackExpand;

    private Label lblEventTransferURL;
    private Label lblEventTransferText;
    private Label lblEventTransferRTF;
    private Label lblEventTransferImage;
    private Label lblEventTransferHTML;
    private Label lblEventTransferFile;
    private Label lblEventDetailMove;
    private Label lblEventDetailLink;
    private Label lblEventDetailCopy;
    private Label lblEventDetailDefault;
    private Table table;
    private TableColumn tblclmnNewColumn_1;
    private TableColumn tblclmnNewColumn_2;
    private TableColumn tblclmnNewColumn_3;
    private TableColumn tblclmnNewColumn_4;
    private TableColumn tblclmnNewColumn_5;
    private TableColumn tblclmnNewColumn_6;
    private TableColumn tblclmnNewColumn_7;
    private TableColumn tblclmnNewColumn_8;
    private Text textEventData;
    private Combo comboEventData;
    private Label lblVersion;
    private Button btnSetupDrop;
    private Button btnSetupDrag;
    private Group grpEventDetail;
    private Group grpDragDropSettings;
    private Group grpEventData;
    private DragSource dragSource;
    private Composite compositeDragLabel;
    private Button btnDragData;
    private Button btnTransferAll;

    public static void main(String[] args) {
        Display display = new Display();
        new DndDemo(display).show();
    }

    public DndDemo(Display display) {
        this.display = display;

        init();
        postInit();

        String[] files = { "c:/", "", "" };
        dragData.put("Text", "Drag Me");
        dragData.put("HTML", "<b>Drag Me</b>");
        dragData.put("URL", "https://www.github.com/");
        dragData.put("File", files);
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
        shell.setSize(850, 622);
        shell.setText("Dnd Demo");
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(new Image(display, DndDemo.class.getResourceAsStream("/org/ellab/swt/demo/dnd/dnddemo-white.ico")));

        Composite compositeMain = new Composite(shell, SWT.NONE);
        GridLayout gl_compositeMain = new GridLayout(2, false);
        gl_compositeMain.marginWidth = 0;
        gl_compositeMain.marginHeight = 0;
        compositeMain.setLayout(gl_compositeMain);
        compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        compositeDropArea = new Composite(compositeMain, SWT.NONE);
        GridData gd_compositeDropArea = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd_compositeDropArea.heightHint = 150;
        compositeDropArea.setLayoutData(gd_compositeDropArea);
        compositeDropArea.setLayout(new GridLayout(3, false));

        compositeDragLabel = new Composite(compositeDropArea, SWT.NONE);
        GridData gd_compositeDragLabel = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        gd_compositeDragLabel.widthHint = 100;
        compositeDragLabel.setLayoutData(gd_compositeDragLabel);
        compositeDragLabel.setLayout(new GridLayout(1, false));

        Label lblDragSource = new Label(compositeDragLabel, SWT.CENTER);
        lblDragSource.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
        lblDragSource.setText("Drag Me");

        compositeDropLabel = new Composite(compositeDropArea, SWT.BORDER);
        compositeDropLabel.setLayout(new GridLayout(1, false));
        GridData gd_compositeDropLabel = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        gd_compositeDropLabel.widthHint = 100;
        compositeDropLabel.setLayoutData(gd_compositeDropLabel);

        lblDropArea = new Label(compositeDropLabel, SWT.NONE);
        lblDropArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
        lblDropArea.setText("Drop Area");

        treeDrop = new Tree(compositeDropArea, SWT.BORDER | SWT.FULL_SELECTION);
        GridData gd_treeDrop = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_treeDrop.widthHint = 2;
        treeDrop.setLayoutData(gd_treeDrop);
        treeDrop.setHeaderVisible(true);
        treeDrop.setLinesVisible(true);

        tblclmnDrop1 = new TreeColumn(treeDrop, SWT.NONE);
        tblclmnDrop1.setWidth(100);
        tblclmnDrop1.setText("Foo");

        tblclmnDrop2 = new TreeColumn(treeDrop, SWT.NONE);
        tblclmnDrop2.setWidth(100);
        tblclmnDrop2.setText("Bar");

        Composite compositeDropControl = new Composite(compositeMain, SWT.NONE);
        GridLayout gl_compositeDropControl = new GridLayout(1, false);
        gl_compositeDropControl.marginBottom = -5;
        gl_compositeDropControl.marginTop = -5;
        gl_compositeDropControl.marginRight = -5;
        compositeDropControl.setLayout(gl_compositeDropControl);
        compositeDropControl.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 2));

        grpDragDropSettings = new Group(compositeDropControl, SWT.NONE);
        grpDragDropSettings.setLayout(new GridLayout(2, true));
        grpDragDropSettings.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        grpDragDropSettings.setText("Drag/Drop Settings");

        btnTransferAll = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferAll.setSelection(true);
        btnTransferAll.setText("All");
        new Label(grpDragDropSettings, SWT.NONE);

        btnTransferText = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferText.setSelection(true);
        btnTransferText.setText("TextTransfer");

        btnTransferHTML = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferHTML.setSelection(true);
        btnTransferHTML.setText("HTMLTransfer");

        btnTransferFile = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferFile.setSelection(true);
        btnTransferFile.setText("FileTransfer");

        btnTransferURL = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferURL.setSelection(true);
        btnTransferURL.setText("URLTransfer");

        Label label = new Label(grpDragDropSettings, SWT.NONE);
        label.setText("(Drop Only)");
        new Label(grpDragDropSettings, SWT.NONE);

        btnTransferImage = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferImage.setSelection(true);
        btnTransferImage.setText("ImageTransfer");

        btnTransferRTF = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferRTF.setSelection(true);
        btnTransferRTF.setText("RTFTransfer");

        btnDragData = new Button(grpDragDropSettings, SWT.NONE);
        btnDragData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        btnDragData.setText("Drag Data");
        new Label(grpDragDropSettings, SWT.NONE);

        label = new Label(grpDragDropSettings, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        btnOperationDefault = new Button(grpDragDropSettings, SWT.CHECK);
        btnOperationDefault.setSelection(true);
        btnOperationDefault.setText("DROP_DEFAULT");
        btnOperationDefault.setData(DND.DROP_DEFAULT);

        btnOperationCopy = new Button(grpDragDropSettings, SWT.CHECK);
        btnOperationCopy.setSelection(true);
        btnOperationCopy.setText("DROP_COPY");
        btnOperationCopy.setData(DND.DROP_COPY);

        btnOperationMove = new Button(grpDragDropSettings, SWT.CHECK);
        btnOperationMove.setSelection(true);
        btnOperationMove.setText("DROP_MOVE");
        btnOperationMove.setData(DND.DROP_MOVE);

        btnOperationLink = new Button(grpDragDropSettings, SWT.CHECK);
        btnOperationLink.setSelection(true);
        btnOperationLink.setText("DROP_LINK");
        btnOperationLink.setData(DND.DROP_LINK);

        label = new Label(grpDragDropSettings, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        btnFeedbackSelect = new Button(grpDragDropSettings, SWT.CHECK);
        btnFeedbackSelect.setSelection(true);
        btnFeedbackSelect.setText("FEEDBACK_SELECT");
        btnFeedbackSelect.setData(DND.FEEDBACK_SELECT);

        btnFeedbackScroll = new Button(grpDragDropSettings, SWT.CHECK);
        btnFeedbackScroll.setSelection(true);
        btnFeedbackScroll.setText("FEEDBACK_SCROLL");
        btnFeedbackScroll.setData(DND.FEEDBACK_SCROLL);

        btnFeedbackInsertBefore = new Button(grpDragDropSettings, SWT.CHECK);
        btnFeedbackInsertBefore.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnFeedbackInsertBefore.setText("FEEDBACK_INSERT_BEFORE");
        btnFeedbackInsertBefore.setData(DND.FEEDBACK_INSERT_BEFORE);

        btnFeedbackInsertAfter = new Button(grpDragDropSettings, SWT.CHECK);
        btnFeedbackInsertAfter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnFeedbackInsertAfter.setText("FEEDBACK_INSERT_AFTER");
        btnFeedbackInsertAfter.setData(DND.FEEDBACK_INSERT_AFTER);

        btnFeedbackExpand = new Button(grpDragDropSettings, SWT.CHECK);
        btnFeedbackExpand.setText("FEEDBACK_EXPAND");
        btnFeedbackExpand.setData(DND.FEEDBACK_EXPAND);
        new Label(grpDragDropSettings, SWT.NONE);

        label = new Label(grpDragDropSettings, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

        btnTransferFileDefaultCopy = new Button(grpDragDropSettings, SWT.CHECK);
        btnTransferFileDefaultCopy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        btnTransferFileDefaultCopy.setSelection(true);
        btnTransferFileDefaultCopy.setText("Default Copy for Files");

        btnSetupDrop = new Button(grpDragDropSettings, SWT.NONE);
        btnSetupDrop.setEnabled(false);
        btnSetupDrop.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnSetupDrop.setText("Setup Drop");

        btnSetupDrag = new Button(grpDragDropSettings, SWT.NONE);
        btnSetupDrag.setEnabled(false);
        btnSetupDrag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnSetupDrag.setText("Setup Drag");

        grpEventDetail = new Group(compositeDropControl, SWT.NONE);
        grpEventDetail.setLayout(new GridLayout(3, false));
        grpEventDetail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        grpEventDetail.setText("event.detail");

        lblEventDetailDefault = new Label(grpEventDetail, SWT.NONE);
        lblEventDetailDefault.setEnabled(false);
        lblEventDetailDefault.setText("DROP_DEFAULT");

        lblEventDetailCopy = new Label(grpEventDetail, SWT.NONE);
        lblEventDetailCopy.setEnabled(false);
        lblEventDetailCopy.setText("DROP_COPY");
        new Label(grpEventDetail, SWT.NONE);

        lblEventDetailMove = new Label(grpEventDetail, SWT.NONE);
        lblEventDetailMove.setEnabled(false);
        lblEventDetailMove.setText("DROP_MOVE");

        lblEventDetailLink = new Label(grpEventDetail, SWT.NONE);
        lblEventDetailLink.setEnabled(false);
        lblEventDetailLink.setText("DROP_LINK");
        new Label(grpEventDetail, SWT.NONE);

        label = new Label(grpEventDetail, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));

        lblEventTransferText = new Label(grpEventDetail, SWT.NONE);
        lblEventTransferText.setEnabled(false);
        lblEventTransferText.setText("TextTransfer");

        lblEventTransferHTML = new Label(grpEventDetail, SWT.NONE);
        lblEventTransferHTML.setEnabled(false);
        lblEventTransferHTML.setText("HTMLTransfer");

        lblEventTransferFile = new Label(grpEventDetail, SWT.NONE);
        lblEventTransferFile.setEnabled(false);
        lblEventTransferFile.setText("FileTransfer");

        lblEventTransferURL = new Label(grpEventDetail, SWT.NONE);
        lblEventTransferURL.setEnabled(false);
        lblEventTransferURL.setText("URLTransfer");

        lblEventTransferRTF = new Label(grpEventDetail, SWT.NONE);
        lblEventTransferRTF.setEnabled(false);
        lblEventTransferRTF.setText("RTFTransfer");

        lblEventTransferImage = new Label(grpEventDetail, SWT.NONE);
        lblEventTransferImage.setEnabled(false);
        lblEventTransferImage.setText("ImageTransfer");

        grpEventData = new Group(compositeDropControl, SWT.NONE);
        grpEventData.setLayout(new GridLayout(1, false));
        grpEventData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        grpEventData.setText("event.data");

        comboEventData = new Combo(grpEventData, SWT.READ_ONLY);
        comboEventData.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        textEventData = new Text(grpEventData, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
        textEventData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        table = new Table(compositeMain, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_1.setWidth(100);
        tblclmnNewColumn_1.setText("#");

        tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_2.setWidth(100);
        tblclmnNewColumn_2.setText("Time");

        tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_3.setWidth(100);
        tblclmnNewColumn_3.setText("Ops");

        tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_4.setWidth(100);
        tblclmnNewColumn_4.setText("Area");

        tblclmnNewColumn_5 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_5.setWidth(100);
        tblclmnNewColumn_5.setText("Event");

        tblclmnNewColumn_6 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_6.setWidth(100);
        tblclmnNewColumn_6.setText("Details");

        tblclmnNewColumn_7 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_7.setWidth(100);
        tblclmnNewColumn_7.setText("Type");

        tblclmnNewColumn_8 = new TableColumn(table, SWT.NONE);
        tblclmnNewColumn_8.setWidth(100);
        tblclmnNewColumn_8.setText("Data");

        Composite compositeStatusbar = new Composite(shell, SWT.NONE);
        GridLayout gl_compositeStatusbar = new GridLayout(2, false);
        gl_compositeStatusbar.marginHeight = 0;
        gl_compositeStatusbar.marginWidth = 0;
        compositeStatusbar.setLayout(gl_compositeStatusbar);
        compositeStatusbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblVersion = new Label(compositeStatusbar, SWT.NONE);
        lblVersion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblVersion.setText("Java " + System.getProperty("java.version") + ", " + System.getProperty("os.name")
                + ", SWT " + SWT.getVersion());
        new Label(compositeStatusbar, SWT.NONE);
    }

    private void postInit() {
        shell.setDefaultButton(btnSetupDrop);

        Button[] buttons1 = { btnTransferFile, btnTransferHTML, btnTransferImage, btnTransferRTF, btnTransferText,
                btnTransferURL };
        transferButtons = buttons1;
        Button[] buttons2 = { btnOperationDefault, btnOperationCopy, btnOperationMove, btnOperationLink };
        operationButtons = buttons2;
        Button[] buttons3 = { btnFeedbackSelect, btnFeedbackInsertBefore, btnFeedbackInsertAfter, btnFeedbackScroll,
                btnFeedbackExpand };
        feedbackButtons = buttons3;

        setupDropTarget();
        setupDragSource();
        addEventListener();

        treeDrop.setRedraw(false);
        for (int i = 0; i < 3; i++) {
            TreeItem parent = new TreeItem(treeDrop, SWT.NONE);
            parent.setText(0, "foo " + String.valueOf(i + 1));
            parent.setText(1, String.valueOf(i + 1));
            for (int j = 0; j < 3; j++) {
                TreeItem child = new TreeItem(parent, SWT.NONE);
                child.setText(0, "foo " + String.valueOf(i + 1) + "-" + String.valueOf(j + 1));
                child.setText(1, String.valueOf(j + 1));
            }
            if (i == 0) {
                parent.setExpanded(true);
            }
        }
        treeDrop.setRedraw(true);

        table.setRedraw(false);
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(COL_SEQ, "9999");
        item.setText(COL_TIME, "99.999");
        item.setText(COL_DRAG_DROP, "Drag");
        item.setText(COL_SOURCE, "Table");
        item.setText(COL_EVENT, "dragOperationChanged");
        item.setText(COL_TYPE, "Image");
        item = new TableItem(table, SWT.NONE);
        item.setText(COL_DRAG_DROP, "Drop");
        item.setText(COL_SOURCE, "Label");
        item.setText(COL_TYPE, "HTML");
        table.getColumn(COL_SEQ).pack();
        table.getColumn(COL_TIME).pack();
        table.getColumn(COL_DRAG_DROP).pack();
        table.getColumn(COL_SOURCE).pack();
        table.getColumn(COL_EVENT).pack();
        table.getColumn(COL_TYPE).pack();
        table.removeAll();
        table.setRedraw(true);
        table.setFocus();
    }

    private void addEventListener() {
        comboEventData.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                if (comboEventData.getSelectionIndex() >= 0) {
                    final Object[] dataArray = (Object[]) comboEventData.getData();
                    textEventData.setText(dataArray[comboEventData.getSelectionIndex()].toString());
                }
                else {
                    textEventData.setText("");
                }
            }
        });

        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final TableItem[] sel = table.getSelection();
                if (sel != null && sel.length > 0) {
                    final int eventDetail = (int) ((Object[]) sel[0].getData())[1];
                    lblEventDetailDefault.setEnabled((eventDetail & DND.DROP_DEFAULT) > 0);
                    lblEventDetailCopy.setEnabled((eventDetail & DND.DROP_COPY) > 0);
                    lblEventDetailLink.setEnabled((eventDetail & DND.DROP_LINK) > 0);
                    lblEventDetailMove.setEnabled((eventDetail & DND.DROP_MOVE) > 0);

                    comboEventData.removeAll();
                    Object dataObj = ((Object[]) sel[0].getData())[2];
                    if (dataObj != null) {
                        Object[] dataArray = null;
                        if (dataObj.getClass().isArray()) {
                            dataArray = (Object[]) dataObj;
                        }
                        else {
                            dataArray = new Object[] { dataObj };
                        }
                        final int totalDataCount = dataArray.length;
                        comboEventData.setData(dataArray);
                        Arrays.stream(dataArray).forEach(obj -> {
                            comboEventData.add("#" + (comboEventData.getItemCount() + 1) + "/" + totalDataCount + " "
                                    + obj.getClass().getName());
                        });
                        if (comboEventData.getItemCount() > 0) {
                            comboEventData.select(0);
                        }
                    }

                    @SuppressWarnings("unchecked")
                    final Set<String> types = (Set<String>) ((Object[]) sel[0].getData())[3];
                    lblEventTransferFile.setEnabled(types.contains("File"));
                    lblEventTransferHTML.setEnabled(types.contains("HTML"));
                    lblEventTransferImage.setEnabled(types.contains("Image"));
                    lblEventTransferRTF.setEnabled(types.contains("RTF"));
                    lblEventTransferText.setEnabled(types.contains("Text"));
                    lblEventTransferURL.setEnabled(types.contains("URL"));
                }
            }
        });

        btnTransferAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnTransferAll.setGrayed(false);
                Arrays.stream(transferButtons).forEach(b -> b.setSelection(((Button) e.getSource()).getSelection()));
            }
        });

        Arrays.stream(transferButtons).forEach(c -> ((Button) c).addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectedCount = Arrays.stream(transferButtons).mapToInt(b -> b.getSelection() ? 1 : 0).sum();
                if (selectedCount == transferButtons.length) {
                    btnTransferAll.setSelection(true);
                    btnTransferAll.setGrayed(false);
                }
                else if (selectedCount == 0) {
                    btnTransferAll.setSelection(false);
                    btnTransferAll.setGrayed(false);
                }
                else {
                    btnTransferAll.setSelection(true);
                    btnTransferAll.setGrayed(true);
                }
            }
        }));

        Arrays.stream(grpDragDropSettings.getChildren())
                .filter(c -> c instanceof Button && (c.getStyle() & SWT.CHECK) > 0)
                .forEach(c -> ((Button) c).addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        btnSetupDrop.setEnabled(true);
                        btnSetupDrag.setEnabled(true);
                    }
                }));

        btnDragData.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new DragDataDialog(display, dragData).open();
            }
        });

        btnSetupDrag.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setupDragSource();
                btnSetupDrag.setEnabled(false);
            }
        });

    }

    private Transfer[] getTransferTypes(boolean drop) {
        List<Transfer> list = new ArrayList<>();
        if (btnTransferFile.getSelection()) {
            list.add(FileTransfer.getInstance());
        }
        if (btnTransferHTML.getSelection()) {
            list.add(HTMLTransfer.getInstance());
        }
        if (btnTransferText.getSelection()) {
            list.add(TextTransfer.getInstance());
        }
        if (btnTransferURL.getSelection()) {
            list.add(URLTransfer.getInstance());
        }
        if (drop) {
            if (btnTransferImage.getSelection()) {
                list.add(ImageTransfer.getInstance());
            }
            if (btnTransferRTF.getSelection()) {
                list.add(RTFTransfer.getInstance());
            }
        }

        return list.toArray(new Transfer[0]);
    }

    private String transferTypesToString(Transfer[] transfers) {
        final LinkedHashSet<String> types = new LinkedHashSet<>();

        Arrays.stream(transfers).forEach(t -> {
            String[] splitted = t.getClass().getName().split("\\.");
            types.add(splitted[splitted.length - 1].replaceAll("Transfer$", ""));
        });

        if (types.size() > 0) {
            return String.join(", ", types.toArray(new String[0]));
        }
        else {
            return "";
        }
    }

    private String transferTypesToString(final Transfer[] transfers, final TransferData[] dataTypes) {
        final LinkedHashSet<String> types = new LinkedHashSet<>();

        Arrays.stream(transfers).forEach(t -> {
            Arrays.stream(dataTypes).forEach(dt -> {
                if (t.isSupportedType(dt)) {
                    String[] splitted = t.getClass().getName().split("\\.");
                    types.add(splitted[splitted.length - 1].replaceAll("Transfer$", ""));
                }
            });
        });

        if (types.size() > 0) {
            return String.join(", ", types.toArray(new String[0]));
        }
        else {
            return "";
        }
    }

    private int getDragDropOperations() {
        return Arrays.stream(operationButtons).mapToInt(b -> b.getSelection() ? (int) b.getData() : 0).reduce(0,
                (a, b) -> a | b);
    }

    private int getDropFeedback() {
        return Arrays.stream(feedbackButtons).mapToInt(b -> b.getSelection() ? (int) b.getData() : 0).reduce(0,
                (a, b) -> a | b);
    }

    private void setupDropTarget() {
        transferFileDefaultCopy = btnTransferFileDefaultCopy.getSelection();
        dropFeedback = getDropFeedback();

        if (dropTargetLabel != null) {
            dropTargetLabel.dispose();
            dropTargetLabel = null;
        }
        if (dropTargetTable != null) {
            dropTargetTable.dispose();
            dropTargetTable = null;
        }

        dropTargetLabel = new DropTarget(compositeDropLabel, getDragDropOperations());
        dropTargetLabel.setTransfer(getTransferTypes(true));

        dropTargetTable = new DropTarget(treeDrop, getDragDropOperations());
        dropTargetTable.setTransfer(getTransferTypes(true));

        DropTarget[] targets = { dropTargetLabel, dropTargetTable };

        Arrays.stream(targets).forEach(t -> {
            setupDefaultDropListener(t);
            setupCustomDropListener(t);
        });
    }

    private void setupDefaultDropListener(DropTarget t) {
        t.addDropListener(new DropTargetListener() {
            private void addHistory(DropTargetEvent event, String eventType, String detail) {
                String time = "";
                final long now = System.currentTimeMillis();
                if (dropStartTime == 0) {
                    dropStartTime = now;
                }
                else {
                    final NumberFormat fmt = DecimalFormat.getInstance();
                    fmt.setRoundingMode(RoundingMode.HALF_UP);
                    fmt.setMinimumFractionDigits(3);
                    fmt.setMaximumFractionDigits(3);
                    fmt.setGroupingUsed(true);
                    time = fmt.format((now - dropStartTime) / 1000f);
                }

                final LinkedHashSet<String> types = new LinkedHashSet<>();

                final TableItem item = new TableItem(table, SWT.NONE);
                item.setText(COL_SEQ, "" + (table.getItemCount()));
                item.setText(COL_TIME, time);
                item.setText(COL_DRAG_DROP, "Drop");
                item.setText(COL_SOURCE, event.getSource() == dropTargetLabel ? "Label"
                        : (event.getSource() == dropTargetTable ? "Table" : ""));
                item.setText(COL_EVENT, eventType);
                if (detail != null) {
                    item.setText(COL_DETAIL, detail);
                }
                if (event.dataTypes != null && event.dataTypes.length > 0) {
                    item.setText(COL_TYPE,
                            transferTypesToString(((DropTarget) event.getSource()).getTransfer(), event.dataTypes));
                }
                if (event.data != null) {
                    if (event.data.getClass().isArray()) {
                        if (((Object[]) event.data).length > 0) {
                            item.setText(COL_DATA, ((Object[]) event.data)[0].toString());
                        }
                    }
                    else {
                        item.setText(COL_DATA, event.data.toString());
                    }
                }
                final Object[] clone = new Object[] { event.dataTypes, event.detail, event.data, types };
                item.setData(clone);

                table.setTopIndex(table.getItemCount() - 1);
            }

            public void dragEnter(DropTargetEvent event) {
                dropStartTime = 0;

                final String s = DndDemoUtils.getMatchedFields(event.detail, DND.class, ",", "DROP_", DND.DROP_DEFAULT,
                        DND.DROP_COPY, DND.DROP_LINK, DND.DROP_TARGET_MOVE);

                addHistory(event, "dragEnter", s);
            }

            public void dragOver(DropTargetEvent event) {
                boolean toAdd = true;
                if (table.getItemCount() > 0) {
                    TableItem ti = table.getItem(table.getItemCount() - 1);
                    if (ti != null && "dragOver".equals(ti.getText(COL_EVENT))) {
                        toAdd = false;
                    }
                }
                if (toAdd) {
                    addHistory(event, "dragOver", null);
                }
                event.feedback = dropFeedback;
            }

            public void dragOperationChanged(DropTargetEvent event) {
                String s = DndDemoUtils.getMatchedFields(event.detail, DND.class, ", ", "DROP_", DND.DROP_DEFAULT,
                        DND.DROP_MOVE, DND.DROP_LINK, DND.DROP_TARGET_MOVE);
                addHistory(event, "dragOperationChanged", s);
            }

            public void dragLeave(DropTargetEvent event) {
                addHistory(event, "dragLeave", null);
            }

            public void dropAccept(DropTargetEvent event) {
                addHistory(event, "dropAccept", null);
            }

            public void drop(DropTargetEvent event) {
                addHistory(event, "drop", null);
            }
        });
    }

    private void setupCustomDropListener(DropTarget t) {
        t.addDropListener(new DropTargetListener() {
            private void handleFileType(DropTargetEvent event) {
                if (transferFileDefaultCopy) {
                    boolean isFileTransfer = false;
                    FileTransfer ft = FileTransfer.getInstance();
                    for (int i = 0; i < event.dataTypes.length; i++) {
                        if (ft.isSupportedType(event.dataTypes[i])) {
                            isFileTransfer = true;
                            break;
                        }
                    }
                    if (isFileTransfer && event.detail == DND.DROP_DEFAULT) {
                        event.detail = DND.DROP_COPY;
                    }
                }
            }

            public void dragEnter(DropTargetEvent event) {
                handleFileType(event);
            }

            public void dragOver(DropTargetEvent event) {
            }

            public void dragOperationChanged(DropTargetEvent event) {
                handleFileType(event);
            }

            public void dragLeave(DropTargetEvent event) {
            }

            public void dropAccept(DropTargetEvent event) {
            }

            public void drop(DropTargetEvent event) {
            }
        });
    }

    private void setupDragSource() {
        if (dragSource != null) {
            dragSource.dispose();
            dragSource = null;
        }

        dragSource = new DragSource(compositeDragLabel, getDragDropOperations());
        dragSource.setTransfer(getTransferTypes(false));

        dragSource.addDragListener(new DragSourceListener() {
            private void addHistory(DragSourceEvent event, String eventType, String detail) {
                String time = "";
                final long now = System.currentTimeMillis();
                if (dragStartTime == 0) {
                    dragStartTime = now;
                }
                else {
                    final NumberFormat fmt = DecimalFormat.getInstance();
                    fmt.setRoundingMode(RoundingMode.HALF_UP);
                    fmt.setMinimumFractionDigits(3);
                    fmt.setMaximumFractionDigits(3);
                    fmt.setGroupingUsed(true);
                    time = fmt.format((now - dragStartTime) / 1000f);
                }

                final LinkedHashSet<String> types = new LinkedHashSet<>();

                final TableItem item = new TableItem(table, SWT.NONE);
                item.setBackground(new Color(display, new RGB(192, 192, 192)));
                item.setText(COL_SEQ, "" + (table.getItemCount()));
                item.setText(COL_TIME, time);
                item.setText(COL_DRAG_DROP, "Drag");
                item.setText(COL_SOURCE, event.getSource() == dropTargetLabel ? "Label"
                        : (event.getSource() == dropTargetTable ? "Table" : ""));
                item.setText(COL_EVENT, eventType);
                if (detail != null) {
                    item.setText(COL_DETAIL, detail);
                }
                item.setText(COL_TYPE, transferTypesToString(((DragSource) event.getSource()).getTransfer()));
                if (event.data != null) {
                    if (event.data.getClass().isArray()) {
                        if (((Object[]) event.data).length > 0) {
                            item.setText(COL_DATA, ((Object[]) event.data)[0].toString());
                        }
                    }
                    else {
                        item.setText(COL_DATA, event.data.toString());
                    }
                }
                final Object[] clone = new Object[] { null, event.detail, event.data, types };
                item.setData(clone);

                table.setTopIndex(table.getItemCount() - 1);
            }

            @Override
            public void dragStart(DragSourceEvent event) {
                addHistory(event, "dragStart", null);
                event.doit = true;
            }

            @Override
            public void dragSetData(DragSourceEvent event) {
                addHistory(event, "dragSetData", null);
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = dragData.get("Text");
                }
                else if (FileTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = Arrays.stream((String[]) dragData.get("File")).filter(s -> s != null && s.length() > 0)
                            .toArray(size -> new String[size]);
                }
                else {
                    event.data = "";
                }
            }

            @Override
            public void dragFinished(DragSourceEvent event) {
                addHistory(event, "dragFinished", null);
            }
        });
    }
}
