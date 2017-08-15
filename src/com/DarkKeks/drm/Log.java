package com.DarkKeks.drm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;

public class Log {
    private static Logger logger = Logger.getLogger(Log.class.getName());

    static {
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
    }

    public static void info(String line){
        logger.info(line);
    }

    public static void error(String line){
        logger.warning(line);
    }

    public static void logException(Throwable th){
        logger.log(Level.WARNING, "Exception: ", th);
    }

    private static class LogFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            String message = formatMessage(record);
            Throwable th = record.getThrown();
            if(th != null) {
                StringWriter sw = new StringWriter();
                th.printStackTrace(new PrintWriter(sw));
                message += sw.toString();
            }
            return String.format(
                    "%1$s [%2$s] %3$s\n",
                    new Date(record.getMillis()).toString(),
                    record.getLevel().getName(),
                    message);
        }
    }
}
