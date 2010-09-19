package cz.silesnet.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class NavigationUtilsTest {

  @Test
  public void testRemoveSystemParameters() {
    String query = null;
    assertTrue(NavigationUtils.removeSystemParameters(query) == null);

    // this should not change
    query = "";
    assertTrue(query.equals(NavigationUtils.removeSystemParameters(query)));

    query = "param=value";
    assertTrue(query.equals(NavigationUtils.removeSystemParameters(query)));

    query = "#fido";
    assertTrue(query.equals(NavigationUtils.removeSystemParameters(query)));

    query = "param=value+value1&param2=value2+value3";
    assertTrue(query.equals(NavigationUtils.removeSystemParameters(query)));

    query = "param=value+value1&param2=value2+value3#fido";
    assertTrue(query.equals(NavigationUtils.removeSystemParameters(query)));

    query = "param=value+value1&param2=value2+value3&param3=value4#fido";
    assertTrue(query.equals(NavigationUtils.removeSystemParameters(query)));

    query = "_param=value";
    assertTrue("".equals(NavigationUtils.removeSystemParameters(query)));

    query = "_param=value&_param2=value2";
    assertTrue("".equals(NavigationUtils.removeSystemParameters(query)));

    query = "_param=value#fido";
    assertTrue("#fido"
        .equals(NavigationUtils.removeSystemParameters(query)));

    query = "param=value+value1&_param=value2";
    assertTrue("param=value+value1".equals(NavigationUtils
        .removeSystemParameters(query)));

    query = "param=value+value1&_param=value2#fido";
    assertTrue("param=value+value1#fido".equals(NavigationUtils
        .removeSystemParameters(query)));

    query = "param=value+value1&_param=value2&param2=value3";
    assertTrue("param=value+value1&param2=value3".equals(NavigationUtils
        .removeSystemParameters(query)));

    query = "param=value+value1&_param=value2&param2=value3#fido";
    assertTrue("param=value+value1&param2=value3#fido"
        .equals(NavigationUtils.removeSystemParameters(query)));

    query = "_param";
    assertTrue("".equals(NavigationUtils.removeSystemParameters(query)));

    query = "_param&_param2";
    assertTrue("".equals(NavigationUtils.removeSystemParameters(query)));

    query = "_param#fido";
    assertTrue("#fido"
        .equals(NavigationUtils.removeSystemParameters(query)));

    query = "param=value+value1&_param";
    assertTrue("param=value+value1".equals(NavigationUtils
        .removeSystemParameters(query)));

    query = "param=value+value1&_param#fido";
    assertTrue("param=value+value1#fido".equals(NavigationUtils
        .removeSystemParameters(query)));

    query = "param=value+value1&_param&param2=value3";
    assertTrue("param=value+value1&param2=value3".equals(NavigationUtils
        .removeSystemParameters(query)));

    query = "param=value+value1&_param&param2=value3#fido";
    assertTrue("param=value+value1&param2=value3#fido"
        .equals(NavigationUtils.removeSystemParameters(query)));
  }
}