package cz.silesnet.model;

import junit.framework.TestCase;

public class ConnectivityTest extends TestCase {

  public void testUnit() throws Exception {
    Connectivity connectivity = new Connectivity();
    connectivity.setBps("k");
    assertEquals("kbps", connectivity.getUnit());
  }

  public void testLinkSpeedText() throws Exception {
    Connectivity connectivity = new Connectivity();
    connectivity.setDownload(4);
    connectivity.setUpload(2);
    assertEquals("4/2 Mbps", connectivity.getLinkSpeedText());
  }

  public void testLinkSpeedTextWhenNotSet() throws Exception {
    Connectivity connectivity = new Connectivity();
    assertEquals("", connectivity.getLinkSpeedText());
  }

  public void testPartialSpeed() throws Exception {
    Connectivity connectivity = new Connectivity();
    connectivity.setDownload(4);
    assertEquals("4 Mbps", connectivity.getLinkSpeedText());
  }

}
