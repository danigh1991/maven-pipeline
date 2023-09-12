package com.core.basefile.services.impl;


import com.core.basefile.services.FileImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service("fileImageServiceImpl")
public class FileImageServiceImpl implements FileImageService {
    @Value("${app.file_upload_userprofiles}")
    private String userProfileRoot;

    @Override
    public InputStream getUserProfileImage(Long userId)throws IOException{
        return  getUserProfileImage(userId,false);
    }
    @Override
    public InputStream getUserProfileImage(Long userId,Boolean isTemp) throws IOException {
        String imagePath ="";
        if (isTemp)
            imagePath = userProfileRoot + "/tmp/" + userId.toString() + "/" + userId.toString() + ".jpg";
        else
            imagePath = userProfileRoot + "/" + userId.toString() + "/" + userId.toString() + ".jpg";
        InputStream in =null;
        try {
            if (!Files.exists(Paths.get(imagePath))) {
                    imagePath = userProfileRoot + "/01.jpg";
            }
            if (Files.exists(Paths.get(imagePath))) {
                FileSystemResource imgFile = new FileSystemResource(imagePath);
                in = imgFile.getInputStream();
            }
        } catch (Exception e) {
           // throw new FileStorageException("Could not initialize File storage Service", e);
        }
        return in;
    }

    @Override
    public String getUserAgent(HttpServletRequest request) {
        String userAgent = "";
        if (request != null) {
            userAgent = request.getHeader("user-agent");
        }
        if (userAgent == null)
            userAgent = "";
        return userAgent;
    }

    @Override
    public String  getContentType(String fileExt){
        String contentType="";
        switch (fileExt.toLowerCase()) {
            case "png":
                contentType="image/png";
                break;
            case "jpg": case "jpeg":
                contentType="image/jpeg";
                break;
            case "ico":
                contentType="application/octet-stream";
                break;
            case "webp":
                contentType="image/webp";
                break;
            case "mp3":
                contentType="audio/mp3";
                break;
            case "mp4":
                contentType="video/mp4";
                break;
            case "mpg":
                contentType="video/mpg";
                break;
            case "avi":
                contentType="video/x-msvideo";
                break;
            case "flv":
                contentType="video/x-flv";
                break;
            case "pdf":
                contentType="application/pdf";
                break;
            case "txt":
                contentType="text/plain";
                break;
            case "html":
                contentType="text/html";
                break;
            case "ttf":
                contentType="application/x-font-ttf";
                break;
            case "css":
                contentType="text/css";
                break;
            case "zip":
                contentType="application/zip";
                break;
            case "rar":
                contentType="application/x-rar-compressed";
                break;
            default:
                break;
        }
        return contentType;
    }

    @Override
    public String getMiddlePath(char targetType){
        String result="";
        switch (targetType) {
            case 'r'://root
                result = "/site/";
                break;
            case 'g'://Ghaleb
                result = "/site_theme/";
                break;

        }
        return result;
    }


}
