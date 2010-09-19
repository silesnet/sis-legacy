package cz.silesnet.model;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConnectivityTest {

  @Test
  public void testUnit() throws Exception {
    Connectivity connectivity = new Connectivity();
    connectivity.setBps("k");
    assertEquals("kbps", connectivity.getUnit());
  }

  @Test
  public void testLinkSpeedText() throws Exception {
    Connectivity connectivity = new Connectivity();
    connectivity.setDownload(4);
    connectivity.setUpload(2);
    assertEquals("4/2 Mbps", connectivity.getLinkSpeedText());
  }

  @Test
  public void testLinkSpeedTextWhenNotSet() throws Exception {
    Connectivity connectivity = new Connectivity();
    assertEquals("", connectivity.getLinkSpeedText());
  }

  @Test
  public void testPartialSpeed() throws Exception {
    Connectivity connectivity = new Connectivity();
    connectivity.setDownload(4);
    assertEquals("4 Mbps", connectivity.getLinkSpeedText());
  }

}
