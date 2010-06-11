package cz.silesnet.utils;

import cz.silesnet.model.Wireless;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class NodeRegexpTest {

    protected final Log log = LogFactory.getLog(getClass());

    @Test
    public void testMacCleaning() {
        Wireless node = new Wireless();

        node.setName("node-reqexp-ap");
        node.setMac("1234567890AB");
        assertTrue("1234567890AB".equals(node.getMac()));
        log.debug(node.getMac());

        node.setMac("1234567890ab");
        assertTrue("1234567890AB".equals(node.getMac()));
        log.debug(node.getMac());

        node.setMac("12:34:56:78:90:ab");
        assertTrue("1234567890AB".equals(node.getMac()));
        log.debug(node.getMac());

        node.setMac("12 34 56 78 90 ab");
        assertTrue("1234567890AB".equals(node.getMac()));
        log.debug(node.getMac());

        node.setMac("12-34-56-78-90-ab");
        assertTrue("1234567890AB".equals(node.getMac()));
        log.debug(node.getMac());

        node.setMac("12whr_%34567890ABjkl");
        assertTrue("1234567890AB".equals(node.getMac()));
        log.debug(node.getMac());

        log.debug("Formatted MAC: " + node.getMacFormatted());
        assertTrue("12:34:56:78:90:AB".equals(node.getMacFormatted()));
    }

    @Test
    public void testWepShort() {
        Wireless node = new Wireless();

        node.setName("node-reqexp-ap");
        node
                .setWep("12345678901234567890123456 *21234567890123456789012345 31234567890123456789012345 41234567890123456789012345");
        assertTrue("21234567890123456789012345".equals(node.getShortWep()));
        log.debug("Short wep of" + node + "\n" + node.getShortWep());

        node
                .setWep("12345678901234567890123456 21234567890123456789012345 31234567890123456789012345 *41234567890123456789012345");
        assertTrue("41234567890123456789012345".equals(node.getShortWep()));
        log.debug("Short wep of" + node + "\n" + node.getShortWep());

        node
                .setWep("*12345678901234567890123456 21234567890123456789012345 31234567890123456789012345 41234567890123456789012345");
        assertTrue("12345678901234567890123456".equals(node.getShortWep()));
        log.debug("Short wep of" + node + "\n" + node.getShortWep());
    }
}