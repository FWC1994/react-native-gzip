package com.reactlibrary;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class GzipModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public GzipModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "Gzip";
    }


    /**
     * 解压缩gz文件
     * @param file 压缩包文件
     * @param targetPath 目标文件夹
     */
    @ReactMethod
    private static void gunzip(String sourcePath, String targetPath, Promise promise){
        FileInputStream  fileInputStream = null;
        GZIPInputStream gzipIn = null;
        OutputStream out = null;
        String suffix = ".gz";
        try {
            File sourceFile = new File(sourcePath);
            fileInputStream = new FileInputStream(sourceFile);
            gzipIn = new GZIPInputStream(fileInputStream);

            File destDir = new File(targetPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

//            // 创建输出目录
//            createDirectory(targetPath, null);

            File tempFile = new File(targetPath + sourceFile.separator + sourceFile.getName().replace(suffix, ""));
            out = new FileOutputStream(tempFile);
            int count;
            byte data[] = new byte[2048];
            while ((count = gzipIn.read(data)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
            promise.resolve("{\"path\":" + targetPath + "}");
        } catch (IOException e) {
            e.printStackTrace();
            promise.reject(e);
        }finally {
            try {
                if(out != null){
                    out.close();
                }
                if(gzipIn != null){
                    gzipIn.close();
                }
                if(fileInputStream != null){
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                promise.reject(e);
            }
        }
    }
}
