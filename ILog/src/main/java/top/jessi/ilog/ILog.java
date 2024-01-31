/*
 * Copyright 2015 Elvis Hew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.jessi.ilog;

import android.app.Application;
import android.util.Log;

import top.jessi.ilog.formatter.border.BorderFormatter;
import top.jessi.ilog.formatter.message.json.JsonFormatter;
import top.jessi.ilog.formatter.message.object.ObjectFormatter;
import top.jessi.ilog.formatter.message.throwable.ThrowableFormatter;
import top.jessi.ilog.formatter.message.xml.XmlFormatter;
import top.jessi.ilog.formatter.stacktrace.StackTraceFormatter;
import top.jessi.ilog.formatter.thread.ThreadFormatter;
import top.jessi.ilog.interceptor.Interceptor;
import top.jessi.ilog.internal.DefaultsFactory;
import top.jessi.ilog.internal.Platform;
import top.jessi.ilog.internal.util.StackTraceUtil;
import top.jessi.ilog.printer.Printer;
import top.jessi.ilog.printer.PrinterSet;

/**
 * A log tool which can be used in android or java, the most important feature is it can print the
 * logs to multiple place in the same time, such as android shell, console and file, you can
 * even print the log to the remote server if you want, all of these can be done just within one
 * calling.
 * <br>Also, XLog is very flexible, almost every component is replaceable.
 * <p>
 * <b>How to use in a general way:</b>
 * <p>
 * <b>1. Initial the log system.</b>
 * <br>Using one of
 * <br>{@link ILog#init()}
 * <br>{@link ILog#init(int)},
 * <br>{@link ILog#init(LogConfiguration)}
 * <br>{@link ILog#init(Printer...)},
 * <br>{@link ILog#init(int, Printer...)},
 * <br>{@link ILog#init(LogConfiguration, Printer...)},
 * <br>that will setup a {@link Logger} for a global usage.
 * If you want to use a customized configuration instead of the global one to log something, you can
 * start a customization logging.
 * <p>
 * For android, a best place to do the initialization is {@link Application#onCreate()}.
 * <p>
 * <b>2. Start to log.</b>
 * <br>{@link #v(String, Object...)}, {@link #v(String)} and {@link #v(String, Throwable)} are for
 * logging a {@link LogLevel#VERBOSE} message.
 * <br>{@link #d(String, Object...)}, {@link #d(String)} and {@link #d(String, Throwable)} are for
 * logging a {@link LogLevel#DEBUG} message.
 * <br>{@link #i(String, Object...)}, {@link #i(String)} and {@link #i(String, Throwable)} are for
 * logging a {@link LogLevel#INFO} message.
 * <br>{@link #w(String, Object...)}, {@link #w(String)} and {@link #w(String, Throwable)} are for
 * logging a {@link LogLevel#WARN} message.
 * <br>{@link #e(String, Object...)}, {@link #e(String)} and {@link #e(String, Throwable)} are for
 * logging a {@link LogLevel#ERROR} message.
 * <br>{@link #log(int, String, Object...)}, {@link #log(int, String)} and
 * {@link #log(int, String, Throwable)} are for logging a specific level message.
 * <br>{@link #json(String)} is for logging a {@link LogLevel#DEBUG} JSON string.
 * <br>{@link #xml(String)} is for logging a {@link LogLevel#DEBUG} XML string.
 * <br> Also, you can directly log any object with specific log level, like {@link #v(Object)},
 * and any object array with specific log level, like {@link #v(Object[])}.
 * <p>
 * <b>How to use in a dynamically customizing way after initializing the log system:</b>
 * <p>
 * <b>1. Start a customization.</b>
 * <br>Call any of
 * <br>{@link #logLevel(int)}
 * <br>{@link #tag(String)},
 * <br>{@link #enableThreadInfo()},
 * <br>{@link #disableThreadInfo()},
 * <br>{@link #enableStackTrace(int)},
 * <br>{@link #enableStackTrace(String, int)},
 * <br>{@link #disableStackTrace()},
 * <br>{@link #enableBorder()},
 * <br>{@link #disableBorder()},
 * <br>{@link #jsonFormatter(JsonFormatter)},
 * <br>{@link #xmlFormatter(XmlFormatter)},
 * <br>{@link #threadFormatter(ThreadFormatter)},
 * <br>{@link #stackTraceFormatter(StackTraceFormatter)},
 * <br>{@link #throwableFormatter(ThrowableFormatter)}
 * <br>{@link #borderFormatter(BorderFormatter)}
 * <br>{@link #addObjectFormatter(Class, ObjectFormatter)}
 * <br>{@link #addInterceptor(Interceptor)}
 * <br>{@link #printers(Printer...)},
 * <br>it will return a {@link Logger.Builder} object.
 * <p>
 * <b>2. Finish the customization.</b>
 * <br>Continue to setup other fields of the returned {@link Logger.Builder}.
 * <p>
 * <b>3. Build a dynamically generated {@link Logger}.</b>
 * <br>Call the {@link Logger.Builder#build()} of the returned {@link Logger.Builder}.
 * <p>
 * <b>4. Start to log.</b>
 * <br>The logging methods of a {@link Logger} is completely same as that ones in {@link ILog}.
 * <br>As a convenience, you can ignore the step 3, just call the logging methods of
 * {@link Logger.Builder}, it will automatically build a {@link Logger} and call the target
 * logging method.
 */
public class ILog {

    /**
     * Global logger for all direct logging via {@link ILog}.
     */
    private static Logger sLogger;

    /**
     * Global log configuration.
     */
    static LogConfiguration sLogConfiguration;

    /**
     * Global log printer.
     */
    static Printer sPrinter;

    static boolean sIsInitialized;

    /**
     * Prevent instance.
     */
    private ILog() {
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @since 1.3.0
     */
    public static void init() {
        init(new LogConfiguration.Builder().build(), DefaultsFactory.createPrinter());
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     */
    public static void init(int logLevel) {
        init(new LogConfiguration.Builder().logLevel(logLevel).build(),
                DefaultsFactory.createPrinter());
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfiguration the log configuration
     * @since 1.3.0
     */
    public static void init(LogConfiguration logConfiguration) {
        init(logConfiguration, DefaultsFactory.createPrinter());
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param printers the printers, each log would be printed by all of the printers
     * @since 1.3.0
     */
    public static void init(Printer... printers) {
        init(new LogConfiguration.Builder().build(), printers);
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logLevel the log level, logs with a lower level than which would not be printed
     * @param printers the printers, each log would be printed by all of the printers
     */
    public static void init(int logLevel, Printer... printers) {
        init(new LogConfiguration.Builder().logLevel(logLevel).build(), printers);
    }

    /**
     * Initialize log system, should be called only once.
     *
     * @param logConfiguration the log configuration
     * @param printers         the printers, each log would be printed by all of the printers
     * @since 1.3.0
     */
    public static void init(LogConfiguration logConfiguration, Printer... printers) {
        if (sIsInitialized) {
            Platform.get().warn("XLog is already initialized, do not initialize again");
        }
        sIsInitialized = true;

        if (logConfiguration == null) {
            throw new IllegalArgumentException("Please specify a LogConfiguration");
        }
        sLogConfiguration = logConfiguration;

        sPrinter = new PrinterSet(printers);

        sLogger = new Logger(sLogConfiguration, sPrinter);
    }

    /**
     * Throw an IllegalStateException if not initialized.
     */
    static void assertInitialization() {
        if (!sIsInitialized) {
            throw new IllegalStateException("Do you forget to initialize XLog?");
        }
    }

    /**
     * Start to customize a {@link Logger} and set the log level.
     *
     * @param logLevel the log level to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.3.0
     */
    public static Logger.Builder logLevel(int logLevel) {
        return new Logger.Builder().logLevel(logLevel);
    }

    /**
     * Start to customize a {@link Logger} and set the tag.
     *
     * @param tag the tag to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder tag(String tag) {
        return new Logger.Builder().tag(tag);
    }


    /**
     * Start to customize a {@link Logger} and enable thread info, the thread info would be printed
     * with the log message.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @see ThreadFormatter
     * @since 1.7.0
     */
    public static Logger.Builder enableThreadInfo() {
        return new Logger.Builder().enableThreadInfo();
    }


    /**
     * Start to customize a {@link Logger} and disable thread info, the thread info won't be printed
     * with the log message.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.7.0
     */
    public static Logger.Builder disableThreadInfo() {
        return new Logger.Builder().disableThreadInfo();
    }


    /**
     * Start to customize a {@link Logger} and enable stack trace, the stack trace would be printed
     * with the log message.
     *
     * @param depth the number of stack trace elements we should log, 0 if no limitation
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @see StackTraceFormatter
     * @since 1.7.0
     */
    public static Logger.Builder enableStackTrace(int depth) {
        return new Logger.Builder().enableStackTrace(depth);
    }


    /**
     * Start to customize a {@link Logger} and enable stack trace, the stack trace would be printed
     * with the log message.
     *
     * @param stackTraceOrigin the origin of stack trace elements from which we should NOT log,
     *                         it can be a package name like "com.elvishew.xlog", a class name
     *                         like "com.yourdomain.logWrapper", or something else between
     *                         package name and class name, like "com.yourdomain.".
     *                         It is mostly used when you are using a logger wrapper
     * @param depth            the number of stack trace elements we should log, 0 if no limitation
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @see StackTraceFormatter
     * @since 1.7.0
     */
    public static Logger.Builder enableStackTrace(String stackTraceOrigin, int depth) {
        return new Logger.Builder().enableStackTrace(stackTraceOrigin, depth);
    }

    /**
     * Start to customize a {@link Logger} and disable stack trace, the stack trace won't be printed
     * with the log message.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.7.0
     */
    public static Logger.Builder disableStackTrace() {
        return new Logger.Builder().disableStackTrace();
    }

    /**
     * Start to customize a {@link Logger} and enable border, the border would surround the entire log
     * content, and separate the log message, thread info and stack trace.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @see BorderFormatter
     * @since 1.7.0
     */
    public static Logger.Builder enableBorder() {
        return new Logger.Builder().enableBorder();
    }

    /**
     * Start to customize a {@link Logger} and disable border, the log content won't be surrounded
     * by a border.
     *
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.7.0
     */
    public static Logger.Builder disableBorder() {
        return new Logger.Builder().disableBorder();
    }

    /**
     * Start to customize a {@link Logger} and set the {@link JsonFormatter}.
     *
     * @param jsonFormatter the {@link JsonFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder jsonFormatter(JsonFormatter jsonFormatter) {
        return new Logger.Builder().jsonFormatter(jsonFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link XmlFormatter}.
     *
     * @param xmlFormatter the {@link XmlFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder xmlFormatter(XmlFormatter xmlFormatter) {
        return new Logger.Builder().xmlFormatter(xmlFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link ThrowableFormatter}.
     *
     * @param throwableFormatter the {@link ThrowableFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder throwableFormatter(ThrowableFormatter throwableFormatter) {
        return new Logger.Builder().throwableFormatter(throwableFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link ThreadFormatter}.
     *
     * @param threadFormatter the {@link ThreadFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder threadFormatter(ThreadFormatter threadFormatter) {
        return new Logger.Builder().threadFormatter(threadFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link StackTraceFormatter}.
     *
     * @param stackTraceFormatter the {@link StackTraceFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder stackTraceFormatter(StackTraceFormatter stackTraceFormatter) {
        return new Logger.Builder().stackTraceFormatter(stackTraceFormatter);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link BorderFormatter}.
     *
     * @param borderFormatter the {@link BorderFormatter} to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder borderFormatter(BorderFormatter borderFormatter) {
        return new Logger.Builder().borderFormatter(borderFormatter);
    }

    /**
     * Start to customize a {@link Logger} and add an object formatter for specific class of object.
     *
     * @param objectClass     the class of object
     * @param objectFormatter the object formatter to add
     * @param <T>             the type of object
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.1.0
     */
    public static <T> Logger.Builder addObjectFormatter(Class<T> objectClass,
                                                        ObjectFormatter<? super T> objectFormatter) {
        return new Logger.Builder().addObjectFormatter(objectClass, objectFormatter);
    }

    /**
     * Start to customize a {@link Logger} and add an interceptor.
     *
     * @param interceptor the interceptor to add
     * @return the {@link Logger.Builder} to build the {@link Logger}
     * @since 1.3.0
     */
    public static Logger.Builder addInterceptor(Interceptor interceptor) {
        return new Logger.Builder().addInterceptor(interceptor);
    }

    /**
     * Start to customize a {@link Logger} and set the {@link Printer} array.
     *
     * @param printers the {@link Printer} array to customize
     * @return the {@link Logger.Builder} to build the {@link Logger}
     */
    public static Logger.Builder printers(Printer... printers) {
        return new Logger.Builder().printers(printers);
    }

    /**
     * Log an object with level {@link LogLevel#VERBOSE}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void v(Object object) {
        assertInitialization();
        sLogger.v(object);
    }

    /**
     * Log an array with level {@link LogLevel#VERBOSE}.
     *
     * @param array the array to log
     */
    public static void v(Object[] array) {
        assertInitialization();
        sLogger.v(array);
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void v(String format, Object... args) {
        assertInitialization();
        sLogger.v(format, args);
    }

    /**
     * Log a message with level {@link LogLevel#VERBOSE}.
     *
     * @param msg the message to log
     */
    public static void v(String msg) {
        assertInitialization();
        sLogger.v(msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#VERBOSE}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void v(String msg, Throwable tr) {
        assertInitialization();
        sLogger.v(msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#DEBUG}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void d(Object object) {
        assertInitialization();
        sLogger.d(object);
    }

    /**
     * Log an array with level {@link LogLevel#DEBUG}.
     *
     * @param array the array to log
     */
    public static void d(Object[] array) {
        assertInitialization();
        sLogger.d(array);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void d(String format, Object... args) {
        assertInitialization();
        sLogger.d(format, args);
    }

    /**
     * Log a message with level {@link LogLevel#DEBUG}.
     *
     * @param msg the message to log
     */
    public static void d(String msg) {
        assertInitialization();
        sLogger.d(msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#DEBUG}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void d(String msg, Throwable tr) {
        assertInitialization();
        sLogger.d(msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#INFO}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void i(Object object) {
        assertInitialization();
        sLogger.i(object);
    }

    /**
     * Log an array with level {@link LogLevel#INFO}.
     *
     * @param array the array to log
     */
    public static void i(Object[] array) {
        assertInitialization();
        sLogger.i(array);
    }

    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void i(String format, Object... args) {
        assertInitialization();
        sLogger.i(format, args);
    }

    /**
     * Log a message with level {@link LogLevel#INFO}.
     *
     * @param msg the message to log
     */
    public static void i(String msg) {
        assertInitialization();
        sLogger.i(msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#INFO}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void i(String msg, Throwable tr) {
        assertInitialization();
        sLogger.i(msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#WARN}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void w(Object object) {
        assertInitialization();
        sLogger.w(object);
    }

    /**
     * Log an array with level {@link LogLevel#WARN}.
     *
     * @param array the array to log
     */
    public static void w(Object[] array) {
        assertInitialization();
        sLogger.w(array);
    }

    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void w(String format, Object... args) {
        assertInitialization();
        sLogger.w(format, args);
    }

    /**
     * Log a message with level {@link LogLevel#WARN}.
     *
     * @param msg the message to log
     */
    public static void w(String msg) {
        assertInitialization();
        sLogger.w(msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#WARN}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void w(String msg, Throwable tr) {
        assertInitialization();
        sLogger.w(msg, tr);
    }

    /**
     * Log an object with level {@link LogLevel#ERROR}.
     *
     * @param object the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.1.0
     */
    public static void e(Object object) {
        assertInitialization();
        sLogger.e(object);
    }

    /**
     * Log an array with level {@link LogLevel#ERROR}.
     *
     * @param array the array to log
     */
    public static void e(Object[] array) {
        assertInitialization();
        sLogger.e(array);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param format the format of the message to log
     * @param args   the arguments of the message to log
     */
    public static void e(String format, Object... args) {
        assertInitialization();
        sLogger.e(format, args);
    }

    /**
     * Log a message with level {@link LogLevel#ERROR}.
     *
     * @param msg the message to log
     */
    public static void e(String msg) {
        assertInitialization();
        sLogger.e(msg);
    }

    /**
     * Log a message and a throwable with level {@link LogLevel#ERROR}.
     *
     * @param msg the message to log
     * @param tr  the throwable to be log
     */
    public static void e(String msg, Throwable tr) {
        assertInitialization();
        sLogger.e(msg, tr);
    }

    /**
     * Log an object with specific log level.
     *
     * @param logLevel the specific log level
     * @param object   the object to log
     * @see LogConfiguration.Builder#addObjectFormatter(Class, ObjectFormatter)
     * @since 1.4.0
     */
    public static void log(int logLevel, Object object) {
        assertInitialization();
        sLogger.log(logLevel, object);
    }

    /**
     * Log an array with specific log level.
     *
     * @param logLevel the specific log level
     * @param array    the array to log
     * @since 1.4.0
     */
    public static void log(int logLevel, Object[] array) {
        assertInitialization();
        sLogger.log(logLevel, array);
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param format   the format of the message to log
     * @param args     the arguments of the message to log
     * @since 1.4.0
     */
    public static void log(int logLevel, String format, Object... args) {
        assertInitialization();
        sLogger.log(logLevel, format, args);
    }

    /**
     * Log a message with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @since 1.4.0
     */
    public static void log(int logLevel, String msg) {
        assertInitialization();
        sLogger.log(logLevel, msg);
    }

    /**
     * Log a message and a throwable with specific log level.
     *
     * @param logLevel the specific log level
     * @param msg      the message to log
     * @param tr       the throwable to be log
     * @since 1.4.0
     */
    public static void log(int logLevel, String msg, Throwable tr) {
        assertInitialization();
        sLogger.log(logLevel, msg, tr);
    }

    /**
     * Log a JSON string, with level {@link LogLevel#DEBUG} by default.
     *
     * @param json the JSON string to log
     */
    public static void json(String json) {
        assertInitialization();
        sLogger.json(json);
    }

    /**
     * Log a XML string, with level {@link LogLevel#DEBUG} by default.
     *
     * @param xml the XML string to log
     */
    public static void xml(String xml) {
        assertInitialization();
        sLogger.xml(xml);
    }

    public static String getPositionInfo() {
        StackTraceElement element = new Throwable().getStackTrace()[2];
        return element.getFileName() + " -- Line：" + element.getLineNumber() + " -- Method: " + element.getMethodName();
    }
}
