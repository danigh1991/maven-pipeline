package com.core.common.services;

import java.io.IOException;
import java.io.InputStream;

public interface ImageService {
    InputStream getUserProfileImage(Long userId) throws IOException;
    InputStream getUserProfileImage(Long userId,Boolean isTemp)throws IOException;
    InputStream getStoreLogo(Long storeId)throws IOException;
    InputStream getStoreLogo(Long storeId,Boolean isTemp)throws IOException;
    InputStream getTStoreLogo(String fileName)throws IOException;
    InputStream getBrandLogo(Long brandId)throws IOException;
    InputStream getBrandLogo(Long brandId,Boolean isTemp)throws IOException;
    //InputStream getBusinessTypeLogo(Long storeId)throws IOException;
}
