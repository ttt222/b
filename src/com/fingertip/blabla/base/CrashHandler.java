package com.fingertip.blabla.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.fingertip.blabla.guide.GuideActivity;
import com.fingertip.blabla.util.FileUtil;

public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";  
	private UncaughtExceptionHandler default_handler;    
    BlablaApplication application;  
      
    public CrashHandler(BlablaApplication application){  
         this.default_handler = Thread.getDefaultUncaughtExceptionHandler();  
         this.application = application;  
    }  
      
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {      
		if (!handleException(ex) && default_handler != null)
			default_handler.uncaughtException(thread, ex);
		else {       
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}    
            Intent intent = new Intent(application.getApplicationContext(), GuideActivity.class);  
            PendingIntent restartIntent = PendingIntent.getActivity(    
                    application.getApplicationContext(), 0, intent,    
                    Intent.FLAG_ACTIVITY_NEW_TASK);                                                 
            AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);    
            // 1秒钟后重启应用   
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
            application.finishActivity();  
        }    
    }  

    private boolean handleException(Throwable ex) {    
        if (ex == null)
            return false;    
        new Thread(){    
            @Override    
            public void run() {    
                Looper.prepare();    
                Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();    
                Looper.loop();    
            }   
        }.start();  
        writeLog(ex);
        return true;    
    }
    
    private void writeLog(Throwable ex) {
    	try {
    		Writer writer = new StringWriter();
    		PrintWriter printWriter = new PrintWriter(writer);
    		ex.printStackTrace(printWriter);
    		Throwable cause = ex.getCause();
    		while (cause != null) {
    			cause.printStackTrace(printWriter);
    			cause = cause.getCause();
    		}
    		printWriter.close();
    		FileUtil.writeLog(writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}