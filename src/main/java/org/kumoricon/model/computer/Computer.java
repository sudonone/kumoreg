package org.kumoricon.model.computer;

import com.vaadin.annotations.AutoGenerated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.net.InetAddress;

@Entity
public class Computer {
    @Id
    @AutoGenerated
    private Integer id;
    private java.net.InetAddress address;
    @OneToOne
    private Printer printer;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public InetAddress getAddress() { return address; }
    public void setAddress(InetAddress address) { this.address = address; }
    public void setAddress(byte[] address) throws Exception { this.address = InetAddress.getByAddress(address); }

    public Printer getPrinter() { return printer; }
    public void setPrinter(Printer printer) { this.printer = printer; }

    public String toString() {
        return String.format("Computer %s with printer %s", address.toString(), getPrinter().getAddress().toString());
    }
}
