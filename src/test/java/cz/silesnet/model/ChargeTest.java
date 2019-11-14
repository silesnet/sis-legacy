package cz.silesnet.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChargeTest {

    private static final Charge FIBER = Charge.of(260, 21);
    private static final Charge WIRELESS20 = Charge.of(360, 21);
    private static final Charge WIRELESS30 = Charge.of(410, 21);
    private static final Charge FIBER_NOT_VAT = Charge.of(260, 0);

    @Test
    public void shouldExcludeVat() {
        assertEquals(BigDecimal.valueOf(26000, 2), FIBER_NOT_VAT.getNet());
        assertEquals(BigDecimal.valueOf(0, 2), FIBER_NOT_VAT.getVat());
        assertEquals(BigDecimal.valueOf(26000, 2), FIBER_NOT_VAT.getBrt());
    }

    @Test
    public void shouldHaveZeroCharge() {
        assertEquals(BigDecimal.valueOf(0, 2), Charge.ZERO.getBrt());
        assertEquals(BigDecimal.valueOf(0, 2), Charge.ZERO.getNet());
        assertEquals(BigDecimal.valueOf(0, 2), Charge.ZERO.getVat());
        assertEquals(FIBER, Charge.ZERO.add(FIBER));
    }

    @Test
    public void shouldAddCharges() {
        assertEquals(BigDecimal.valueOf(63000, 2), FIBER.add(FIBER).getBrt());
        assertEquals(BigDecimal.valueOf(52066, 2), FIBER.add(FIBER).getNet());
        assertEquals(BigDecimal.valueOf(10934, 2), FIBER.add(FIBER).getVat());
    }

    @Test
    public void shouldUseFractionAmount() {
        Charge halfFiber = Charge.of(0.5f, 260, 21);
        assertEquals(BigDecimal.valueOf(15700, 2), halfFiber.getBrt());
        assertEquals(BigDecimal.valueOf(12975, 2), halfFiber.getNet());
        assertEquals(BigDecimal.valueOf(2725, 2), halfFiber.getVat());

        Charge halfWireless20 = Charge.of(0.5f, 360, 21);
        assertEquals(BigDecimal.valueOf(21800, 2), halfWireless20.getBrt());
        assertEquals(BigDecimal.valueOf(18017, 2), halfWireless20.getNet());
        assertEquals(BigDecimal.valueOf(3783, 2), halfWireless20.getVat());

        Charge halfWireless30 = Charge.of(0.5f, 410, 21);
        assertEquals(BigDecimal.valueOf(24800, 2), halfWireless30.getBrt());
        assertEquals(BigDecimal.valueOf(20496, 2), halfWireless30.getNet());
        assertEquals(BigDecimal.valueOf(4304, 2), halfWireless30.getVat());
    }

    @Test
    public void shouldUseWholeAmount() {
        assertEquals(FIBER, Charge.of(1.0f, 260, 21));
        assertEquals(WIRELESS20, Charge.of(1.0f, 360, 21));
        assertEquals(WIRELESS30, Charge.of(1.0f, 410, 21));
    }
    @Test
    public void shouldCalculateNet() {
        assertEquals(BigDecimal.valueOf(26033, 2), FIBER.getNet());
        assertEquals(BigDecimal.valueOf(36033, 2), WIRELESS20.getNet());
        assertEquals(BigDecimal.valueOf(40992, 2), WIRELESS30.getNet());
    }

    @Test
    public void shouldCalculateBrt() {
        assertEquals(BigDecimal.valueOf(31500, 2), FIBER.getBrt());
        assertEquals(BigDecimal.valueOf(43600, 2), WIRELESS20.getBrt());
        assertEquals(BigDecimal.valueOf(49600, 2), WIRELESS30.getBrt());
    }

    @Test
    public void shouldCalculateVat() {
        assertEquals(BigDecimal.valueOf(5467, 2), FIBER.getVat());
        assertEquals(BigDecimal.valueOf(7567, 2), WIRELESS20.getVat());
        assertEquals(BigDecimal.valueOf(8608, 2), WIRELESS30.getVat());
    }

    @Test
    public void shouldCreateFromNetAndVatRate() {
        assertNotNull(Charge.of(260, 21));
    }

    @Test
    public void shouldCreateFromAmountNetAndVatRate() {
        assertNotNull(Charge.of(0.5f, 260, 21));
    }
}
