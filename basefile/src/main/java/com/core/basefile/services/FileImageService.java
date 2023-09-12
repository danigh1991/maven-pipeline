package com.core.basefile.services;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

public interface FileImageService {
    InputStream getUserProfileImage(Long userId) throws IOException;
    InputStream getUserProfileImage(Long userId, Boolean isTemp)throws IOException;
    String getUserAgent(HttpServletRequest request);
    String  getContentType(String fileExt);
    String getMiddlePath(char targetType);
}
