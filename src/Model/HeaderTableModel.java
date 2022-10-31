/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
import View.Invoice;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Twins
 */
public class HeaderTableModel extends AbstractTableModel {

    private ArrayList<InvoiceHeader> invoices;
    private String[] columns = {"Num", "Customer", "Date", "Total"};

    public HeaderTableModel(ArrayList<InvoiceHeader> invoices) {
        this.invoices = invoices;
    }

    @Override
    public int getRowCount() {
        return invoices.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        InvoiceHeader inv = invoices.get(rowIndex);
        switch (columnIndex) {
            case 0: return inv.getNum();
            case 1: return inv.getName();
            case 2: return Invoice.sdf.format(inv.getDate());
            case 3: return inv.getTotal();
        }
        return "";
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

}
