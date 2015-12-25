package fys.core.log;

import java.io.File;

import fys.core.config.AppConfig;

public class AppLogger {
//    private static final int DEBUG_TYPE = 1;
//    private static final int INFO_TYPE = 2;
//    private static final int ERROR_TYPE = 3;
//    private static AppLogger logger = new AppLogger();
//    private String logDir = null;
//
//    private AppLogger() {
//    }
//
//    public static AppLogger getInstance() {
//        return logger;
//    }
//
//    public void init(String applicationDir) {
//        String logDirPath = applicationDir + File.separator + "log";
//        String logDirSet = AppConfig.getInstance().getParameterConfig().getParameter("log");
//        if(logDirSet != null && !logDirSet.equals("")) {
//            logDirPath = logDirSet;
//        }
//
//        File logDirectory = new File(logDirPath);
//        if(!logDirectory.exists()) {
//            logDirectory.mkdir();
//        }
//
//        this.logDir = logDirectory.getAbsolutePath();
//    }
//
//    public void infoLog(String info) {
//        info = DateTimeUtil.getCurrentDateTime() + " system info:\t" + info + "\n";
//        if(AppConfig.getInstance().getParameterConfig().isDebugModel()) {
//            System.out.print(info);
//        } else {
//            String logFilePath = this.getLogFilePath(2);
//            this.writeLog(logFilePath, info);
//        }
//    }
//
//    public void errorLog(String info) {
//        info = DateTimeUtil.getCurrentDateTime() + " error:\t" + info + "\n";
//        if(AppConfig.getInstance().getParameterConfig().isDebugModel()) {
//            System.out.print(info);
//        } else {
//            String logFilePath = this.getLogFilePath(3);
//            this.writeLog(logFilePath, info);
//        }
//    }
//
//    public void errorLog(String info, Exception exception) {
//        info = DateTimeUtil.getCurrentDateTime() + " error:\t" + info + "\n";
//        if(AppConfig.getInstance().getParameterConfig().isDebugModel()) {
//            System.out.print(info);
//            exception.printStackTrace();
//        } else {
//            String logFilePath = this.getLogFilePath(3);
//            this.writeLog(logFilePath, info, exception);
//        }
//    }
//
//    public void debugLog(String info) {
//        if(AppConfig.getInstance().getParameterConfig().isDebugModel()) {
//            info = DateTimeUtil.getCurrentDateTime() + " debug:\t" + info + "\n";
//            System.out.print(info);
//        }
//
//    }
//
//    public void debugLog(String info, Exception exception) {
//        if(AppConfig.getInstance().getParameterConfig().isDebugModel()) {
//            info = DateTimeUtil.getCurrentDateTime() + " debug:\t" + info + "\n";
//            System.out.print(info);
//            exception.printStackTrace();
//        }
//
//    }
//
//    private String getLogFilePath(int logType) {
//        String path = this.logDir;
//        String currentDate = DateTimeUtil.getCurrentDate().replace("/", "");
//        switch(logType) {
//        case 1:
//            path = path + File.separator + "debug" + currentDate;
//            break;
//        case 2:
//            path = path + File.separator + "info" + currentDate;
//            break;
//        case 3:
//            path = path + File.separator + "error" + currentDate;
//        }
//
//        return path + ".log";
//    }
//
//    private void writeLog(String logFilePath, String info) {
//        try {
//            SimpleFileWriter e = new SimpleFileWriter(logFilePath);
//            e.setEncoding("UTF-8");
//            e.setWriteWay(true);
//            e.write(info);
//        } catch (Exception var4) {
//            System.err.println("can\'t log info to path:" + logFilePath);
//            var4.printStackTrace();
//        }
//
//    }
//
//    private void writeLog(String logFilePath, String info, Exception exception) {
//        try {
//            SimpleFileWriter e = new SimpleFileWriter(logFilePath);
//            e.setEncoding("UTF-8");
//            e.setWriteWay(true);
//            e.write(info);
//            StringWriter sw = new StringWriter();
//            exception.printStackTrace(new PrintWriter(sw));
//            e.write("\r\n" + sw);
//        } catch (Exception var6) {
//            System.err.println("can\'t log info to path:" + logFilePath);
//            var6.printStackTrace();
//        }
//
//    }
}
