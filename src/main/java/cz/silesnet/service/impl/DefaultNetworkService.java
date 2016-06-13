package cz.silesnet.service.impl;

import cz.silesnet.service.NetworkService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DefaultNetworkService implements NetworkService, InitializingBean {
  private static final Log log = LogFactory.getLog(DefaultNetworkService.class);

  private File kickPppoeUserCommand;
  private JdbcTemplate jdbcTemplate;

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public void setKickPppoeUserCommand(File kickPppoeUserCommand) {
    this.kickPppoeUserCommand = kickPppoeUserCommand;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(jdbcTemplate);
    Assert.notNull(kickPppoeUserCommand);
  }

  @Override
  public void kickUser(final long serviceId) {
    final String[] serviceMasterAndLogin = serviceMasterAndLogin(serviceId);
    if (serviceMasterAndLogin.length == 2) {
      final String master = serviceMasterAndLogin[0];
      final String login = serviceMasterAndLogin[1];
      final Runnable task = new Runnable() {
        @Override
        public void run() {
          try {
            Process process = new ProcessBuilder(kickPppoeUserCommand.getPath(), master, login)
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
  }

  private String[] serviceMasterAndLogin(final long serviceId) {
    final List<String[]> list = jdbcTemplate.query("SELECT master, login FROM pppoe WHERE service_id=?",
        new Object[]{serviceId}, new RowMapper<String[]>() {
          @Override
          public String[] mapRow(ResultSet rs, int i) throws SQLException {
            final String[] result = new String[2];
            result[0] = rs.getString("master");
            result[1] = rs.getString("login");
            return result;
          }
        });
    return list.size() == 1 ? list.get(0) : new String[0];
  }

}
