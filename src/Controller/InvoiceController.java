/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;


import Model.HeaderTableModel;
import Model.InvoiceHeader;
import Model.InvoiceLine;
import Model.LineTableModel;
import View.Invoice;
import View.InvoiceHeaderDialog;
import View.InvoiceLineDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Twins
 */
public class InvoiceController implements ActionListener, ListSelectionListener {

    private Invoice frame;
    private InvoiceHeaderDialog headerDialog;
    private InvoiceLineDialog lineDialog;

    public InvoiceController(Invoice frame) {
        this.frame = frame;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        System.out.println("Row Selected");
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow == -1) {
            frame.getCustNameLbl().setText("");
            frame.getInvNumLbl().setText("");
            frame.getInvTotalLbl().setText("");
            frame.getInvDateLbl().setText("");
            frame.setLineTableModel(new LineTableModel());
        } else {
            InvoiceHeader selectedInvoice = frame.getInvoices().get(selectedRow);
            frame.getCustNameLbl().setText(selectedInvoice.getName());
            frame.getInvNumLbl().setText("" + selectedInvoice.getNum());
            frame.getInvTotalLbl().setText("" + selectedInvoice.getTotal());
            frame.getInvDateLbl().setText(frame.sdf.format(selectedInvoice.getDate()));
            frame.setLineTableModel(new LineTableModel(selectedInvoice.getLines()));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        //System.out.println("Hello from action! " + ac);
        switch (ac) {
            case "New Invoice":
                newInvoice();
                break;

            case "Delete Invoice":
                deleteInvoice();
                break;

            case "New Item":
                newItem();
                break;

            case "Delete Item":
                deleteItem();
                break;

            case "Load Files":
                loadFiles(null, null);
                break;

            case "Save Data":
                saveData();
                break;

            case "newInvoiceOK":
                newInvoiceOK();
                break;

            case "newInvoiceCancel":
                newInvoiceCancel();
                break;

            case "newLineOK":
                newLineOK();
                break;

            case "newLineCancel":
                newLineCancel();
                break;
        }
    }

    private void newInvoice() {
        headerDialog = new InvoiceHeaderDialog(frame);
        headerDialog.setVisible(true);
    }

    private void deleteInvoice() {
        int selectedRow = frame.getInvoicesTable().getSelectedRow();
        if (selectedRow != -1) {
            frame.getInvoices().remove(selectedRow);
            frame.getHeaderTableModel().fireTableDataChanged();
        }
    }

    private void newItem() {
        int selectedInvoice = frame.getInvoicesTable().getSelectedRow();
        if (selectedInvoice == -1) {
            JOptionPane.showMessageDialog(frame, "First, select Invoice to add item to it", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            lineDialog = new InvoiceLineDialog(frame);
            lineDialog.setVisible(true);
        }
    }

    private void deleteItem() {
        int selectedInvoice = frame.getInvoicesTable().getSelectedRow();
        int selectedItem = frame.getItemsTable().getSelectedRow();
        if (selectedInvoice != -1 && selectedItem != -1) {
            InvoiceHeader invoice = frame.getInvoices().get(selectedInvoice);
            invoice.getLines().remove(selectedItem);
            frame.getLineTableModel().fireTableDataChanged();
            frame.getHeaderTableModel().fireTableDataChanged();
            frame.getInvoicesTable().setRowSelectionInterval(selectedInvoice, selectedInvoice);
        }
    }

    public void loadFiles(String hPath, String lPath) {
        System.out.println("Files will be loaded!");
        File hFile = null;
        File lFile = null;
        if (hPath == null && lPath == null) {
            JFileChooser fc = new JFileChooser();
            JOptionPane.showMessageDialog(frame, "Please, select Header file!", "Header", JOptionPane.WARNING_MESSAGE);
            int result = fc.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                hFile = fc.getSelectedFile();
                JOptionPane.showMessageDialog(frame, "Please, select Line file!", "Line", JOptionPane.WARNING_MESSAGE);
                result = fc.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    lFile = fc.getSelectedFile();
                }
            }
        } else {
            hFile = new File(hPath);
            lFile = new File(lPath);
        }

        if (hFile != null && lFile != null) {
            try {
                List<String> hData = readFile(hFile);
                
                List<String> lData = readFile(lFile);
             
                System.out.println("point");

                for (String header : hData) {
                    /*
                    header = "1,12-11-2020,Sameer"
                     */
                    String[] segments = header.split(",");
                    /*
                    segments = ["1", "12-11-2020", "Sammer"]
                     */
                    int num = Integer.parseInt(segments[0]);
                    Date date = new Date();
                    try {
                        date = frame.sdf.parse(segments[1]);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(frame, "Error while parsing date: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    String name = segments[2];
                    InvoiceHeader inv = new InvoiceHeader(num, date, name);
                    frame.getInvoices().add(inv);
                    System.out.println("point");
                }
                frame.setHeaderTableModel(new HeaderTableModel(frame.getInvoices()));

                for (String line : lData) {
                    String[] segments = line.split(",");
                    int num = Integer.parseInt(segments[0]);
                    String name = segments[1];
                    int price = Integer.parseInt(segments[2]);
                    int count = Integer.parseInt(segments[3]);
                    InvoiceHeader invoice = frame.getInvoiceByNum(num);
                    InvoiceLine invLine = new InvoiceLine(name, price, count, invoice);
                }
                System.out.println("point");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error while reading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveData() {
        JFileChooser fc = new JFileChooser();
        File hFile = null;
        File lFile = null;
        
        JOptionPane.showMessageDialog(frame, "Please, select new file to save Header Data", "Header", JOptionPane.QUESTION_MESSAGE);
        int result = fc.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            hFile = fc.getSelectedFile();
            JOptionPane.showMessageDialog(frame, "Please, select new file to save Item Data", "Header", JOptionPane.QUESTION_MESSAGE);
            result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                lFile = fc.getSelectedFile();
            }
        }
        
        if (hFile != null && lFile != null) {
            String hData = "";
            String lData = "";
            
            for (InvoiceHeader inv : frame.getInvoices()) {
                hData += inv.getAsCSV();
                hData += "\n";
                for (InvoiceLine line : inv.getLines()) {
                    lData += line.getAsCSV();
                    lData += "\n";
                }
            }
            System.out.println("Check");
            try {
                FileWriter hfw = new FileWriter(hFile);
                FileWriter lfw = new FileWriter(lFile);
                
                hfw.write(hData);
                lfw.write(lData);
                
                hfw.flush();
                lfw.flush();
                
                hfw.close();
                lfw.close();
                
            } catch (Exception ex) {
                
            }
        }
    }


    private List<String> readFile(File f) throws IOException {
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        List<String> lines = new ArrayList<>();
        String line = null;

        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    private void newInvoiceOK() {
        try {
            String dateStr = headerDialog.getInvDateField().getText();
            String name = headerDialog.getCustNameField().getText();

            Date date = frame.sdf.parse(dateStr);
            int num = frame.getNextInvoiceNum();
            InvoiceHeader inv = new InvoiceHeader(num, date, name);
            frame.getInvoices().add(inv);
            frame.getHeaderTableModel().fireTableDataChanged();
            newInvoiceCancel();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(frame, "Error in Date format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void newInvoiceCancel() {
        headerDialog.setVisible(false);
        headerDialog.dispose();
        headerDialog = null;
    }

    private void newLineOK() {
        String name = lineDialog.getItemNameField().getText();
        int count = Integer.parseInt(lineDialog.getItemCountField().getText());
        int price = Integer.parseInt(lineDialog.getItemPriceField().getText());
        int selectedInvIndex = frame.getInvoicesTable().getSelectedRow();
        InvoiceHeader inv = frame.getInvoices().get(selectedInvIndex);
        
        new InvoiceLine(name, price, count, inv);
        frame.getHeaderTableModel().fireTableDataChanged();
        frame.getInvoicesTable().setRowSelectionInterval(selectedInvIndex, selectedInvIndex);
        newLineCancel();
    }

    private void newLineCancel() {
        lineDialog.setVisible(false);
        lineDialog.dispose();
        lineDialog = null;
    }

}
