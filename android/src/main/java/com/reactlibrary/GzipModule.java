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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

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

    @ReactMethod
    public void unTar(String source, String target, Boolean force, Promise promise) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if(!checkDir(sourceFile, targetFile, force)){
            promise.reject("-2", "error");
            return;
        }

        ArchiveInputStream inputStream = null;
        FileInputStream fileInputStream;

        try{
            fileInputStream = FileUtils.openInputStream(sourceFile);
            inputStream = new ArchiveStreamFactory()
                    .createArchiveInputStream(ArchiveStreamFactory.TAR, fileInputStream);

            ArchiveEntry archiveEntry = inputStream.getNextEntry();

            while (archiveEntry != null) {
                File destFile = new File(targetFile, archiveEntry.getName());
                if (archiveEntry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    final FileOutputStream outputStream = FileUtils.openOutputStream(destFile);
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.close();
                }
                archiveEntry = inputStream.getNextEntry();
            }

            WritableMap map = Arguments.createMap();
            map.putString("path", targetFile.getAbsolutePath());
            promise.resolve(map);
        } catch (ArchiveException | IOException  e) {
            e.printStackTrace();
            promise.reject("-2", "untar error");
        }
    }

    @ReactMethod
    public void unGzip(String source, String target, Boolean force, Promise promise) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if(!checkDir(sourceFile, targetFile, force)){
            promise.reject("-2", "error");
            return;
        }

        FileInputStream fileInputStream;

        try{
            fileInputStream = FileUtils.openInputStream(sourceFile);
            final CompressorInputStream compressorInputStream = new CompressorStreamFactory()
                    .createCompressorInputStream(CompressorStreamFactory.GZIP, fileInputStream);

            final FileOutputStream outputStream = FileUtils.openOutputStream(targetFile);
            IOUtils.copy(compressorInputStream, outputStream);
            outputStream.close();

            WritableMap map = Arguments.createMap();
            map.putString("path", targetFile.getAbsolutePath());
            promise.resolve(map);
        } catch (IOException | CompressorException e) {
            e.printStackTrace();
            promise.reject("-2", "ungzip error");
        }
    }

    @ReactMethod
    public void unGzipTar(String source, String target, Boolean force, Promise promise) {
        File sourceFile = new File(source);
        File targetFile = new File(target);
        if(!checkDir(sourceFile, targetFile, force)){
            promise.reject("-2", "error");
            return;
        }

        ArchiveInputStream inputStream = null;
        FileInputStream fileInputStream;

        try{
            fileInputStream = FileUtils.openInputStream(sourceFile);
            final CompressorInputStream compressorInputStream = new CompressorStreamFactory()
                    .createCompressorInputStream(CompressorStreamFactory.GZIP, fileInputStream);
            inputStream = new ArchiveStreamFactory()
                    .createArchiveInputStream(ArchiveStreamFactory.TAR, compressorInputStream);
            ArchiveEntry archiveEntry = inputStream.getNextEntry();

            while (archiveEntry != null) {
                File destFile = new File(targetFile, archiveEntry.getName());
                if (archiveEntry.isDirectory()) {
                    destFile.mkdirs();
                } else {
                    final FileOutputStream outputStream = FileUtils.openOutputStream(destFile);
                    IOUtils.copy(inputStream, outputStream);
                    outputStream.close();
                }
                archiveEntry = inputStream.getNextEntry();
            }

            WritableMap map = Arguments.createMap();
            map.putString("path", targetFile.getAbsolutePath());
            promise.resolve(map);
        } catch (IOException | CompressorException | ArchiveException e) {
            e.printStackTrace();
            promise.reject("-2", "ungzip error");
        }
    }

    private Boolean checkDir(File sourceFile, File targetFile, Boolean force) {
        if (!sourceFile.exists()) {
            return false;
        }

        if (targetFile.exists()) {
            if (!force) {
                return false;
            }

            try {
                if (targetFile.isDirectory()) {
                    FileUtils.deleteDirectory(targetFile);
                } else {
                    targetFile.delete();
                }
                targetFile.mkdirs();
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }
}

