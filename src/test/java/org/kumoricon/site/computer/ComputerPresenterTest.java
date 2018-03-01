package org.kumoricon.site.computer;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class ComputerPresenterTest {
    @Test
    public void getNetworkFromIpAddress() {
        assertEquals("IP Address", "192.168.1.", ComputerPresenter.getNetworkFromIpAddress("192.168.1.23"));
        assertEquals("IP address", "123.123.123.", ComputerPresenter.getNetworkFromIpAddress("123.123.123.123"));
        assertEquals("IP address", "1.1.1.", ComputerPresenter.getNetworkFromIpAddress("1.1.1.1"));
    }

    @Test
    public void getNetworkFromIpAddressWithSpaces() {
        assertEquals("IP Address", "192.168.1.", ComputerPresenter.getNetworkFromIpAddress("  192.168.1.23"));
        assertEquals("IP Address", "192.168.1.", ComputerPresenter.getNetworkFromIpAddress("192.168.1.23"  ));
        assertEquals("IP Address", "192.168.1.", ComputerPresenter.getNetworkFromIpAddress("  192.168.1.23"  ));
    }

    @Test
    public void getNetworkFromIpAddressNull() {
        assertEquals("Null IP address", null, ComputerPresenter.getNetworkFromIpAddress(null));
    }

    @Test
    public void getNetworkFromIpAddressNonAddress() {
        assertEquals("Bad IP address returns original string", "abcd", ComputerPresenter.getNetworkFromIpAddress("abcd"));
    }


}