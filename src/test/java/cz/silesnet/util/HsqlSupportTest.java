package cz.silesnet.util;

import org.testng.annotations.Test;

/**
 * User: der3k
 * Date: 28.9.2010
 * Time: 19:06:28
 */
public class HsqlSupportTest {
  @Test
  public void ilike() throws Exception {
    assert HsqlSupport.ilike("aaabbCC", "AAA%cc");
  }
}
