package so.laji.android.logger;

public final class Settings {

    private LogTool logTool;

    /**
     * Determines how logs will printed
     */
    private LogLevel logLevel = LogLevel.FULL;

    /**
     * 配置日志级别
     *
     * @param logLevel
     * @return
     */
    public Settings logLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public Settings logTool(LogTool logTool) {
        this.logTool = logTool;
        return this;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public LogTool getLogTool() {
        if (logTool == null) {
            logTool = new AndroidLogTool();
        }
        return logTool;
    }
}