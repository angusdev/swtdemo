package org.ellab.swt.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class TableUtils {
    public static void packAllColumns(Table table) {
        table.setRedraw(false);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumn(i).pack();
        }

        table.setRedraw(true);
    }

    public static void packAllColumns(Table table, String[] defaultData) {
        table.setRedraw(false);

        TableItem ti = new TableItem(table, SWT.NONE);
        for (int i = 0; i < defaultData.length; i++) {
            ti.setText(i, defaultData[i]);
        }
        packAllColumns(table);
        table.removeAll();

        table.setRedraw(true);
    }
}
