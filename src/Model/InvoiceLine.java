/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Twins
 */
public class InvoiceLine {
    private String name;
    private double price;
    private int count;
    private InvoiceHeader invoice;

    public InvoiceLine(String name, double price, int count, InvoiceHeader invoice) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.invoice = invoice;
        this.invoice.getLines().add(this);
    }

    public double getTotal() {
        return price * count;
    }
    
    public InvoiceHeader getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceHeader invoice) {
        this.invoice = invoice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "InvoiceLine{" + "name=" + name + ", price=" + price + ", count=" + count + '}';
    }
    
     public String getAsCSV() {
        return invoice.getNum() + "," + name + "," + price + "," + count;
    }
    
}
