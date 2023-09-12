package com.core.basefile.restcontrollers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public abstract class AbstractImageRestController {

    @Value("${app.image_cache_Dtime}")
    private Long IMAGE_CACHE_DTIME;

    public static String getClientIpAddr(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    public void  streamWrite(HttpServletResponse response, InputStream in ) throws IOException {
        try {
            if(in!=null)
               StreamUtils.copy(in, response.getOutputStream());
        }
        finally {
            try {
                if(in!=null)
                    in.close();
            }
            catch (IOException ex) {
            }
        }
    }
    public HttpServletResponse  setHeaderImageCache(HttpServletResponse response )  {
        return setHeaderCache(response,TimeUnit.DAYS,IMAGE_CACHE_DTIME);
    }
    public HttpServletResponse  setHeaderCache(HttpServletResponse response,TimeUnit tUnit, Long val )  {
        String cacheControle=CacheControl.maxAge(val,tUnit).cachePublic().getHeaderValue();
        response.setHeader("Cache-Control", cacheControle );
//        response.setHeader("Expires", "1296000");
        response.setHeader("Pragma",  cacheControle);
        return response;
    }

    public void writeImageAsStream(HttpServletResponse response, String imagePath, String defaultImagePath,String mediaType){
        try {

            response= setHeaderImageCache(response);
            FileSystemResource imgFile1=null ;
            if (Files.exists(Paths.get(imagePath) ))
                imgFile1 = new FileSystemResource(imagePath);
            else if (Files.exists(Paths.get(defaultImagePath) ))
                imgFile1 = new FileSystemResource(defaultImagePath);

            if (imgFile1!=null) {
                response.setContentType(mediaType);
                streamWrite(response, imgFile1.getInputStream());
            }else{
                response.setStatus(HttpStatus.NOT_FOUND.value());
            }
        } catch (IOException e) {
        }
    }


}
