package crazypants.enderzoo;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log {

  public static final Logger LOG = LogManager.getLogger(EnderZoo.MODID);

  public static void warn(String msg) {
	  LOG.log(Level.WARN, msg);
  }

  public static void error(String msg) {
	  LOG.log(Level.ERROR, msg);
  }

  public static void info(String msg) {
	  LOG.log(Level.INFO, msg);
  }

  public static void debug(String msg) {
	  LOG.log(Level.DEBUG, msg);
  }

  private Log() {
  }

}
