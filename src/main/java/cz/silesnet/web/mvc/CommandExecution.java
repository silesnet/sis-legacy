package cz.silesnet.web.mvc;

import com.google.common.collect.Sets;
import cz.silesnet.service.CommandManager;

import java.util.Set;

public class CommandExecution {
  private final CommandManager commandManager;
  private final long commandId;
  private String currentStatus = "";
  private final Set<String> finalStatuses  = Sets.newHashSet("completed", "failed");

  public CommandExecution(CommandManager commandManager, long commandId) {
    this.commandManager = commandManager;
    this.commandId = commandId;
  }

  public void waitUntilFinished(long timeout) {
    final long timeoutStamp = System.currentTimeMillis() + timeout;
    while (System.currentTimeMillis() < timeoutStamp) {
      currentStatus = commandManager.status(commandId);
      if (finalStatuses.contains(currentStatus)) {
        break;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  public String currentStatus() {
    return currentStatus;
  }

  public boolean hasFinished() {
    return finalStatuses.contains(currentStatus);
  }

  public boolean hasCompleted() {
    return "completed".equals(currentStatus) && hasFinished();
  }

  public boolean hasFailed() {
    return "failed".equals(currentStatus) && hasFinished();
  }

}
