package cz.silesnet.model;

import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.*;

public class CommandTest {
  @Test
  public void testCommandDefaults() throws Exception {
    final Command command = new Command();
    assertNull(command.getId());
    assertNull(command.getEntity());
    assertEquals(command.getEntityId(), 0L);
    assertEquals(command.getData(), "{ }");
    assertEquals(command.getStatus(), "issued");
    final long future = new Date().getTime() + 1;
    assertTrue(command.getInsertedOn().before(new Date(future)));
    assertNull(command.getStartedOn());
    assertNull(command.getFinishedOn());
  }
}