/*
 * @(#)LogUtil.java		       Project: crash
 * Date:2014-5-27
 *
 * Copyright (c) 2014 CFuture09, Institute of Software, 
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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
package com.githang.androidcrash.log;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Geek_Soledad <a target="_blank" href=
 *         "http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&email=XTAuOSVzPDM5LzI0OR0sLHM_MjA"
 *         style="text-decoration:none;"><img src=
 *         "http://rescdn.qqmail.com/zh_CN/htmledition/images/function/qm_open/ico_mailme_01.png"
 *         /></a>
 */
public class LogWriter {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());

    /**
     * 将日志写入文件。
     * 
     * @param tag
     * @param tr
     */
    public static synchronized void writeLog(File logFile, String tag, String deviceInfo,Throwable tr) {
        logFile.getParentFile().mkdirs();
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String time = timeFormat.format(Calendar.getInstance().getTime());
        synchronized (logFile) {
        	StringBuffer sb = null;
        	FileOutputStream fos = null;
        	Writer writer = null;
            PrintWriter printWriter = null;
            try {
            	sb = new StringBuffer();
            	fos = new FileOutputStream(logFile, true);
            	writer = new StringWriter();  
            	printWriter = new PrintWriter(writer);
                tr.printStackTrace(printWriter);
                Throwable cause = tr.getCause();  
                while (cause != null) {  
                    cause.printStackTrace(printWriter);  
                    cause = cause.getCause();  
                }
                
                if (logFile.length() == 0) {
                	sb.append(deviceInfo).append('\n');
                	sb.append("ERROR INFORMATION").append('\n');
                }
				sb.append(time).append(" ").append("E").append('/').append(tag)
						.append(" ").append('\n');
                String result = writer.toString();  
                sb.append(result).append('\n');
                fos.write(sb.toString().replaceAll("\n", "\r\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            	closeQuietly(fos);
            	closeQuietly(writer);
                closeQuietly(printWriter);
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ioe) {
                // ignore
            }
        }
    }
}
