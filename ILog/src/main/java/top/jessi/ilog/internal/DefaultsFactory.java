/*
 * Copyright 2016 Elvis Hew
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

package top.jessi.ilog.internal;

import java.util.Map;

import top.jessi.ilog.flattener.DefaultFlattener;
import top.jessi.ilog.flattener.Flattener2;
import top.jessi.ilog.formatter.border.BorderFormatter;
import top.jessi.ilog.formatter.border.DefaultBorderFormatter;
import top.jessi.ilog.formatter.message.json.DefaultJsonFormatter;
import top.jessi.ilog.formatter.message.json.JsonFormatter;
import top.jessi.ilog.formatter.message.object.ObjectFormatter;
import top.jessi.ilog.formatter.message.throwable.DefaultThrowableFormatter;
import top.jessi.ilog.formatter.message.throwable.ThrowableFormatter;
import top.jessi.ilog.formatter.message.xml.DefaultXmlFormatter;
import top.jessi.ilog.formatter.message.xml.XmlFormatter;
import top.jessi.ilog.formatter.stacktrace.DefaultStackTraceFormatter;
import top.jessi.ilog.formatter.stacktrace.StackTraceFormatter;
import top.jessi.ilog.formatter.thread.DefaultThreadFormatter;
import top.jessi.ilog.formatter.thread.ThreadFormatter;
import top.jessi.ilog.internal.printer.file.backup.BackupStrategyWrapper;
import top.jessi.ilog.printer.Printer;
import top.jessi.ilog.printer.file.FilePrinter;
import top.jessi.ilog.printer.file.backup.BackupStrategy2;
import top.jessi.ilog.printer.file.backup.FileSizeBackupStrategy2;
import top.jessi.ilog.printer.file.clean.CleanStrategy;
import top.jessi.ilog.printer.file.clean.NeverCleanStrategy;
import top.jessi.ilog.printer.file.naming.ChangelessFileNameGenerator;
import top.jessi.ilog.printer.file.naming.FileNameGenerator;
import top.jessi.ilog.printer.file.writer.SimpleWriter;
import top.jessi.ilog.printer.file.writer.Writer;

/**
 * Factory for providing default implementation.
 */
public class DefaultsFactory {

    private static final String DEFAULT_LOG_FILE_NAME = "log";

    private static final long DEFAULT_LOG_FILE_MAX_SIZE = 1024 * 1024; // 1M bytes;

    /**
     * Create the default JSON formatter.
     */
    public static JsonFormatter createJsonFormatter() {
        return new DefaultJsonFormatter();
    }

    /**
     * Create the default XML formatter.
     */
    public static XmlFormatter createXmlFormatter() {
        return new DefaultXmlFormatter();
    }

    /**
     * Create the default throwable formatter.
     */
    public static ThrowableFormatter createThrowableFormatter() {
        return new DefaultThrowableFormatter();
    }

    /**
     * Create the default thread formatter.
     */
    public static ThreadFormatter createThreadFormatter() {
        return new DefaultThreadFormatter();
    }

    /**
     * Create the default stack trace formatter.
     */
    public static StackTraceFormatter createStackTraceFormatter() {
        return new DefaultStackTraceFormatter();
    }

    /**
     * Create the default border formatter.
     */
    public static BorderFormatter createBorderFormatter() {
        return new DefaultBorderFormatter();
    }

    /**
     * Create the default {@link Flattener2}.
     */
    public static Flattener2 createFlattener2() {
        return new DefaultFlattener();
    }

    /**
     * Create the default printer.
     */
    public static Printer createPrinter() {
        return Platform.get().defaultPrinter();
    }

    /**
     * Create the default file name generator for {@link FilePrinter}.
     */
    public static FileNameGenerator createFileNameGenerator() {
        return new ChangelessFileNameGenerator(DEFAULT_LOG_FILE_NAME);
    }

    /**
     * Create the default backup strategy for {@link FilePrinter}.
     */
    public static BackupStrategy2 createBackupStrategy() {
        return new BackupStrategyWrapper(new FileSizeBackupStrategy2(DEFAULT_LOG_FILE_MAX_SIZE, 10));
    }

    /**
     * Create the default clean strategy for {@link FilePrinter}.
     */
    public static CleanStrategy createCleanStrategy() {
        return new NeverCleanStrategy();
    }

    /**
     * Create the default writer for {@link FilePrinter}.
     */
    public static Writer createWriter() {
        return new SimpleWriter();
    }

    /**
     * Get the builtin object formatters.
     *
     * @return the builtin object formatters
     */
    public static Map<Class<?>, ObjectFormatter<?>> builtinObjectFormatters() {
        return Platform.get().builtinObjectFormatters();
    }
}
