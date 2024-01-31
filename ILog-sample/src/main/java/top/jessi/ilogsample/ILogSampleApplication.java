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

package top.jessi.ilogsample;

import android.app.Application;
import android.os.Build;

import org.json.JSONObject;

import java.io.File;

import top.jessi.ilog.ILog;
import top.jessi.ilog.LogConfiguration;
import top.jessi.ilog.LogLevel;
import top.jessi.ilog.flattener.ClassicFlattener;
import top.jessi.ilog.interceptor.BlacklistTagsFilterInterceptor;
import top.jessi.ilog.libcat.LibCat;
import top.jessi.ilog.printer.AndroidPrinter;
import top.jessi.ilog.printer.ConsolePrinter;
import top.jessi.ilog.printer.Printer;
import top.jessi.ilog.printer.file.FilePrinter;
import top.jessi.ilog.printer.file.naming.DateFileNameGenerator;
import top.jessi.ilog.printer.file.writer.SimpleWriter;

public class ILogSampleApplication extends Application {

    public static Printer globalFilePrinter;

    private static final long MAX_TIME = 1000 * 60 * 60 * 24 * 2; // two days

    @Override
    public void onCreate() {
        super.onCreate();
        initILog();
    }

    /**
     * Initialize ILog.
     */
    private void initILog() {
        LogConfiguration config = new LogConfiguration.Builder()
                // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL : LogLevel.NONE)
                // 开启日志位置打印
                .enablePrintPosition()
                // 指定 TAG，默认为 "ILog"
                .tag(getString(R.string.global_tag))
                // 允许打印线程信息，默认禁止
                // .enableThreadInfo()
                // 允许打印深度为 2 的调用栈信息，默认禁止
                // .enableStackTrace(2)
                // // 允许打印日志边框，默认禁止
                // .enableBorder()
                // 指定 JSON 格式化器，默认为 DefaultJsonFormatter
                // .jsonFormatter(new MyJsonFormatter())
                // 指定 XML 格式化器，默认为 DefaultXmlFormatter
                // .xmlFormatter(new MyXmlFormatter())
                // 指定可抛出异常格式化器，默认为 DefaultThrowableFormatter
                // .throwableFormatter(new MyThrowableFormatter())
                // 指定线程信息格式化器，默认为 DefaultThreadFormatter
                // .threadFormatter(new MyThreadFormatter())
                // 指定调用栈信息格式化器，默认为 DefaultStackTraceFormatter
                // .stackTraceFormatter(new MyStackTraceFormatter())
                // 指定边框格式化器，默认为 DefaultBorderFormatter
                // .borderFormatter(new MyBoardFormatter())
                // 为指定类型添加对象格式化器  默认使用 Object.toString()
                // .addObjectFormatter(AnyClass.class,new AnyClassObjectFormatter())
                // 添加黑名单 TAG 过滤器
                .addInterceptor(new BlacklistTagsFilterInterceptor("blacklist1", "blacklist2", "blacklist3"))
                // 添加一个日志拦截器
                // .addInterceptor(new WhitelistTagsFilterInterceptor("whitelist1", "whitelist2", "whitelist3"))
                // .addInterceptor(new MyInterceptor())
                .build();

        // 通过 System.out 打印日志到控制台的打印器
        // Printer consolePrinter = new ConsolePrinter();
        // 通过 android.util.Log 打印日志的打印器
        Printer androidPrinter = new AndroidPrinter();
        // 打印日志到文件的打印器
        Printer filePrinter = new FilePrinter
                // 指定保存日志文件的路径
                .Builder(new File(getExternalCacheDir().getAbsolutePath(), "log").getPath())
                // 指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                .fileNameGenerator(new DateFileNameGenerator())
                // 指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                // .backupStrategy(new MyBackupStrategy())
                // 指定日志文件清除策略，默认为 NeverCleanStrategy()
                // .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))
                // 指定日志平铺器，默认为 DefaultFlattener
                .flattener(new ClassicFlattener())
                // 指定日志写入器，默认为 SimpleWriter
                .writer(new SimpleWriter() {                           // Default: SimpleWriter
                    @Override
                    public void onNewFileCreated(File file) {
                        super.onNewFileCreated(file);
                        final String header = "\n>>>>>>>>>>>>>>>> File Header >>>>>>>>>>>>>>>>" +
                                "\nDevice Manufacturer: " + Build.MANUFACTURER +
                                "\nDevice Model       : " + Build.MODEL +
                                "\nAndroid Version    : " + Build.VERSION.RELEASE +
                                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                                "\nApp VersionName    : " + BuildConfig.VERSION_NAME +
                                "\nApp VersionCode    : " + BuildConfig.VERSION_CODE +
                                "\n<<<<<<<<<<<<<<<< File Header <<<<<<<<<<<<<<<<\n\n";
                        appendLog(header);
                    }
                })
                .build();

        /*
        * 初始化 XLog
        * 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
        * 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
        * */
        ILog.init(config, androidPrinter, filePrinter);

        // For future usage: partial usage in MainActivity.
        globalFilePrinter = filePrinter;

        // Intercept all logs(including logs logged by third party modules/libraries) and print them to file.
        LibCat.config(true, filePrinter);
    }
}
