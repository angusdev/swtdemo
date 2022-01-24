package org.ellab.swt.demo.imageviewer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.ellab.swt.demo.imageviewer.ImageViewerCanvas.ImageViewerCanvasAdapter;
import org.ellab.swt.utils.SwtUtils;

public class ImageViewer {
    private static final float ZOOM_STEP = 0.2f;
    private static final float MAX_ZOOM = 4;

    private Shell shell;
    private Display display;

    private ImageViewerCanvas previewCanvas;
    private PDFPreview preview;
    private Thread previewThread;

    private Composite compositeControl;
    private Text txtPage;

    private Canvas canvasPreview;
    private CLabel lblPreview;

    public static void main(String[] args) throws IOException {
        Display display = new Display();
        new ImageViewer(display).show();
    }

    public ImageViewer(Display display) {
        this.display = display;

        init();

        final FileTransfer fileTransfer = FileTransfer.getInstance();
        Transfer[] types = new Transfer[] { fileTransfer };
        DropTarget dropTarget = new DropTarget(shell, DND.DROP_MOVE);
        dropTarget.setTransfer(types);
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (fileTransfer.isSupportedType(event.currentDataType)) {
                    String[] files = (String[]) event.data;
                    if (files.length > 0) {
                        openFile(files[0]);
                    }
                }
            }
        });

        preview = new PDFPreview();
        previewCanvas = new ImageViewerCanvas(canvasPreview).withWheelZoomStep(ZOOM_STEP).withMaxZoom(MAX_ZOOM)
                .addListener(new ImageViewerCanvasAdapter() {
                    @Override
                    public void changePage(ImageViewerCanvas c, int offset) {
                        if (offset == ImageViewerCanvas.PREV_PAGE) {
                            previewChangePage(PDFPreview.PREV_PAGE);
                        }
                        else if (offset == ImageViewerCanvas.NEXT_PAGE) {
                            previewChangePage(PDFPreview.NEXT_PAGE);
                        }
                    }
                });

    }

    public void show() throws IOException {
        Monitor primary = display.getPrimaryMonitor();
        Rectangle parentSize = primary.getClientArea();
        Rectangle shellSize = shell.getBounds();
        shellSize.width = Math.max(shellSize.width, parentSize.width - 50);
        shellSize.height = Math.max(shellSize.height, parentSize.height - 50);
        // make it square
        shellSize.height = shellSize.width = Math.min(shellSize.width, shellSize.height);
        shell.setSize(shellSize.width, shellSize.height);
        int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
        int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;
        shell.setLocation(new Point(locationX, locationY));

        shell.open();

        shell.layout();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        if (preview != null) {
            preview.close();
        }
    }

    private void init() {
        shell = new Shell(display, SWT.TITLE | SWT.RESIZE | SWT.CLOSE | SWT.MAX | SWT.MIN);
        shell.setSize(800, 600);
        shell.setText("Image Viewer");
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        shell.setImage(new Image(display,
                ImageViewer.class.getResourceAsStream("/org/ellab/swt/demo/imageviewer/imageviewer-white.ico")));

        Composite compositePreview = new Composite(shell, SWT.NONE);
        compositePreview.setLayout(new GridLayout(1, false));

        compositeControl = new Composite(compositePreview, SWT.NONE);
        compositeControl.setLayout(new GridLayout(9, false));
        compositeControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Label lblSpace1 = new Label(compositeControl, SWT.NONE);
        lblSpace1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        Button btnFirstPage = new Button(compositeControl, SWT.NONE);
        btnFirstPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                previewChangePage(PDFPreview.FIRST_PAGE);
            }
        });
        btnFirstPage.setText("<<");

        Button btnPrevPage = new Button(compositeControl, SWT.NONE);
        btnPrevPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                previewChangePage(PDFPreview.PREV_PAGE);
            }
        });
        btnPrevPage.setText("<");

        txtPage = new Text(compositeControl, SWT.BORDER);
        txtPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                Matcher m = Pattern.compile("(\\d+)").matcher(txtPage.getText());
                if (m.matches()) {
                    previewChangePage(Integer.parseInt(m.group(1)));
                }
            }
        });
        txtPage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                txtPage.selectAll();
            }
        });
        txtPage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPage.selectAll();
            }
        });

        Button btnNextPage = new Button(compositeControl, SWT.NONE);
        btnNextPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                previewChangePage(PDFPreview.NEXT_PAGE);
            }
        });
        btnNextPage.setText(">");

        Button btnLastPage = new Button(compositeControl, SWT.NONE);
        btnLastPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                previewChangePage(PDFPreview.LAST_PAGE);
            }
        });
        btnLastPage.setText(">>");

        Label lblDpi = new Label(compositeControl, SWT.NONE);
        lblDpi.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblDpi.setText("DPI:");

        Combo cboDpi = new Combo(compositeControl, SWT.READ_ONLY);
        cboDpi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                changeDpi(Integer.parseInt(cboDpi.getText()));
            }
        });
        cboDpi.setVisibleItemCount(4);
        cboDpi.setItems(new String[] { "100", "300", "600", "1200" });
        cboDpi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        cboDpi.select(1);

        Label lblSpace2 = new Label(compositeControl, SWT.NONE);
        lblSpace2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        canvasPreview = new Canvas(compositePreview, SWT.NO_BACKGROUND);
        canvasPreview.setLayout(new FillLayout(SWT.HORIZONTAL));
        canvasPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        lblPreview = new CLabel(canvasPreview, SWT.NONE);
        lblPreview.setAlignment(SWT.CENTER);
    }

    public void openFile(String file) {
        lblPreview.setText("Loading " + file + "...");

        enableNavigation(false);
        previewThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    preview.load(file);

                    shell.getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            lblPreview.setVisible(false);
                            previewChangePage(PDFPreview.FIRST_PAGE);
                            enableNavigation(true);
                        }
                    });
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        previewThread.start();
    }

    private void enableNavigation(boolean enabled) {
        Arrays.stream(compositeControl.getChildren()).forEach(i -> i.setEnabled(enabled));
    }

    private void previewChangePage(int page) {
        // lblPreview.setVisible(true);
        // lblPreview.setText("Loading ...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                shell.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Object[] result = preview.page(page);
                            txtPage.setText(result[1] + " / " + preview.getTotalPages());
                            txtPage.selectAll();
                            previewCanvas.changeImage((BufferedImage) result[0]);
                        }
                        catch (IOException ex) {
                            SwtUtils.errorBox(shell, ex);
                        }
                        finally {
                            // lblPreview.setVisible(false);
                        }
                    }
                });
            }
        }).start();
    }

    private void changeDpi(int dpi) {
        // lblPreview.setVisible(true);
        // lblPreview.setText("Loading ...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                shell.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            previewCanvas.changeImage(preview.dpi(dpi));
                        }
                        catch (IOException | NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                        finally {
                            // lblPreview.setVisible(false);
                        }
                    }
                });
            }
        }).start();
    }
}
