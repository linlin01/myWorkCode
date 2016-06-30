package com.ragentek.copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.os.storage.StorageVolume;
import android.os.storage.StorageManager;
import android.os.RemoteException;
import android.os.Environment;
import android.os.SystemProperties;
import android.net.Uri;

public class CopyService extends BroadcastReceiver {

    private static final String TAG = "AndroidCopyService";
    private static final String IS_FIRST_BOOT = "is_first_boot";
    private static final String IS_FIRST_OTA_BOOT = "android.intent.action.THE_FIRST_OTA_BOOT";
    private boolean is2SdcardSwap = SystemProperties.get("ro.mtk_2sdcard_swap").equals("1");
    private boolean is_first_ota_boot;
    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        is_first_ota_boot=intent.getAction().equals(IS_FIRST_OTA_BOOT);
        Log.e(TAG, "action=" + intent.getAction());
        if ("com.android.init.COPY_ACTION".equals(intent.getAction())||is_first_ota_boot) {
            ContentResolver cs = context.getContentResolver();
            if ((Settings.System.getInt(cs, IS_FIRST_BOOT, 1) == 1)||is_first_ota_boot) {
                new Thread(){
                    public void run() {
                        initCopierConfig();
                        Settings.System.putInt(mContext.getContentResolver(), IS_FIRST_BOOT, 0);
                    };
                }.start();
            }
        }
    }

    /**
     * init coperis config
     */
    private void initCopierConfig() {
        // Get Test Item
        try {
            XmlPullParser mXmlPullParser = null;
            mXmlPullParser = mContext.getResources().getXml(R.xml.copier_config);

            int mEventType = mXmlPullParser.getEventType();
            while (mEventType != XmlPullParser.END_DOCUMENT) {
                if (mEventType == XmlPullParser.START_DOCUMENT) {

                } else if (mEventType == XmlPullParser.END_DOCUMENT) {

                } else if (mEventType == XmlPullParser.START_TAG) {
                    String name = mXmlPullParser.getName();
                    if (name.equals("Copier")) {
                        String srcPath = mXmlPullParser.getAttributeValue(null, "srcPath");
                        String dstPath = mXmlPullParser.getAttributeValue(null, "dstPath");
                        Log.e(TAG, "item => srcPath=" + srcPath + ";dstPath=" + dstPath);
                        if(doCopy(srcPath, dstPath)) {
                            Log.e(TAG, "srcPath=" + srcPath + ";dstPath=" + dstPath + "; do OK!");
                        }
                        try {
                            Thread.sleep(1000*20);
                        }catch(Exception e) {

                        }
                    }
                } else if (mEventType == XmlPullParser.END_TAG) {
                } else if (mEventType == XmlPullParser.TEXT) {
                }
                mEventType = mXmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean haveExternalSDCard() {
        StorageManager storageManager = null;
         try {
            //modify by zhanwenyu 20140519 (start)
            //storageManager = new StorageManager(null);
            storageManager = (StorageManager)(mContext.getSystemService(Context.STORAGE_SERVICE));
            //storageManager = new StorageManager(mContext.getContentResolver(), null);
            //modify by zhanwenyu 20140519 (end)
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(storageManager != null) {
            StorageVolume[] volumes = storageManager.getVolumeList();
            for (int i = 0; i < volumes.length; i++) {
                if (volumes[i].isRemovable() && Environment.MEDIA_MOUNTED.equals(
                        storageManager.getVolumeState(volumes[i].getPath()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean doCopy(String srcPath,String dstPath){
        File srcDir = new File(srcPath);
        File dstDir = new File(dstPath);
        File[] files = srcDir.listFiles();
        boolean allFileCopyOK = true;
        Log.e(TAG, "srcPath=" + srcPath + ";dstPath=" + dstPath);
        Log.e(TAG, "dstDir.exists()=" + dstDir.exists());
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }
        if (files != null && files.length > 0) {
            for (final File file : files) {
                if(file.isDirectory()) { // is directory
                    doCopy(file.getAbsolutePath(),dstPath + File.separator + file.getName());
                } else { // is file
                    final String dstFile = dstPath + File.separator + file.getName();
                    
                    new Thread(){
                        public void run() {
                            if(copyFile(file.getPath(), dstFile)) {
                                Log.v(TAG, "copy ok!");
                            }
                            if(!new File(dstFile).exists()) {
                                if(copyFile(file.getPath(), dstFile)) {
                                    Log.v(TAG, "copy ok!");
                                }
                            }
                            if(!new File(dstFile).exists()) {
                                if(copyFile(file.getPath(), dstFile)) {
                                    Log.v(TAG, "copy ok!");
                                }
                            }
                            if(!new File(dstFile).exists()) {
                                if(copyFile(file.getPath(), dstFile)) {
                                    Log.v(TAG, "copy ok!");
                                }
                            }
                        };
                    }.start();
                    try {
                        Thread.sleep(100);
                    }catch(Exception e) {

                    }
                }
            }
        } else {
            Log.v(TAG, "There is not fils in :" + srcPath);
        }
        return allFileCopyOK;
    }

    private boolean copyFile(String srcFile, String dstFile){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        int fileSize = 0;
        try {
            fis = new FileInputStream(new File(srcFile));
            fos = new FileOutputStream(new File(dstFile));
            int readLength = 0;
            byte[] buffer = new byte[1024];
            while ((readLength = fis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, readLength);
                fileSize +=readLength;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileSize == 0){
                return false;
            }
        }
        return true;
    }
}
