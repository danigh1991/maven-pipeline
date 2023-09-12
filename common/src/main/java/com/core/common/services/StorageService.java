package com.core.common.services;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;


public interface StorageService {
    void init();

    String store(String targetPath,String id, MultipartFile file,int w ,int h);
    String store(String targetPath,String id,Boolean createDirByFileName, MultipartFile file,int w ,int h);
    Boolean checkMetaData(MultipartFile f, String contentType);
    Boolean checkImageRatio(MultipartFile image,Double ratio);

}


