package so.laji.android.logger;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Logger is a wrapper for logging utils
 * But more pretty, simple and powerful
 */
final class LoggerPrinter implements Printer {

    private static final String tag = "DEFAULT_TAG";
    private static final int offset = 5;

    private static final int DEBUG = 3;
    private static final int ERROR = 6;
    private static final int ASSERT = 7;
    private static final int INFO = 4;
    private static final int VERBOSE = 2;
    private static final int WARN = 5;

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;

    /**
     * It is used to determine log settings such as method count, thread info visibility
     */
    private Settings settings;

    /**
     * It is used to change the tag
     */
    @Override
    public Settings init() {
        this.settings = new Settings();
        return settings;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public void d(String message, Object... args) {
        logFormat(DEBUG, message, args);
    }

    @Override
    public void e(String message, Object... args) {
        e(null, message, args);
    }

    @Override
    public void e(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + Log.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        logFormat(ERROR, message, args);
    }

    @Override
    public void w(String message, Object... args) {
        logFormat(WARN, message, args);
    }

    @Override
    public void i(String message, Object... args) {
        logFormat(INFO, message, args);
    }

    @Override
    public void v(String message, Object... args) {
        logFormat(VERBOSE, message, args);
    }

    @Override
    public void wtf(String message, Object... args) {
        logFormat(ASSERT, message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    @Override
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
            }
        } catch (JSONException e) {
            e(e.getCause().getMessage() + "\n" + json);
        }
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    @Override
    public void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            d("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(e.getCause().getMessage() + "\n" + xml);
        }
    }

    @Override
    public void clear() {
        settings = null;
    }

    /**
     * 日志格式化输出
     *
     * @param logType
     * @param msg
     * @param args
     */
    private synchronized void logFormat(int logType, String msg, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }

        String message = createMessage(msg, args);

        if (TextUtils.isEmpty(message)) {
            message = "Empty/NULL log message";
        }
        StringBuilder builder = new StringBuilder();

        // 线程名
        builder.append("[Thread:").append(Thread.currentThread().getName()).append("]");
        // 类 + 行号
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        builder.append(" (").append(trace[offset].getFileName()).append(":").append(trace[offset].getLineNumber()).append(") ");
        builder.append(message);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = builder.toString().getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logContent(logType, tag, builder.toString());
            return;
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(logType, tag, new String(bytes, i, count));
        }
    }

    private void logContent(int logType, String tag, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, line);
        }
    }

    private void logChunk(int logType, String tag, String chunk) {
        switch (logType) {
            case ERROR:
                settings.getLogTool().e(tag, chunk);
                break;
            case INFO:
                settings.getLogTool().i(tag, chunk);
                break;
            case VERBOSE:
                settings.getLogTool().v(tag, chunk);
                break;
            case WARN:
                settings.getLogTool().w(tag, chunk);
                break;
            case ASSERT:
                settings.getLogTool().wtf(tag, chunk);
                break;
            case DEBUG:
                // Fall through, log debug by default
            default:
                settings.getLogTool().d(tag, chunk);
                break;
        }
    }


    private String createMessage(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

}