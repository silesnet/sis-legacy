package cz.silesnet.service.impl;

import cz.silesnet.service.NetworkService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.sql.DataSource;

public class DefaultNetworkService implements NetworkService, InitializingBean {
  private static final Log log = LogFactory.getLog(DefaultNetworkService.class);

  private DataSource dataSource;
  private String kickPppoeUserCommand;

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setKickPppoeUserCommand(String kickPppoeUserCommand) {
    this.kickPppoeUserCommand = kickPppoeUserCommand;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(dataSource);
    Assert.notNull(kickPppoeUserCommand);
  }

  @Override
  public void kickUser(final long serviceId) {
    final String master = serviceMaster(serviceId);
    final String login = serviceLogin(serviceId);
    final Runnable task = new Runnable() {
      @Override
      public void run() {
        try {
          Process process = new ProcessBuilder(kickPppoeUserCommand, master, login)
              .redirectErrorStream(true)
              .redirectOutput(ProcessBuilder.Redirect.INHERIT)
              .start();
          int error = process.waitFor();
          if (error != 0) {
            throw new RuntimeException("error code: " + error);
          }
          log.info("PPPoE '" + master + "' kicked '" + login + "'");
        } catch (Exception e) {
          log.error("failed executing '" + kickPppoeUserCommand + "'", e);
        }
      }
    };
    final Thread thread = new Thread(task);
    thread.start();
  }

  private String serviceMaster(final long serviceId) {
    return "";
  }

  private String serviceLogin(final long serviceId) {
    return "";
  }
}
