package com.core.basefile.restcontrollers;


import com.core.basefile.services.FileImageService;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@Controller
@RequestMapping(value="/img")
public class ImageRestController extends AbstractImageRestController {

    @Value("${app.file_upload_root}")
    private String uploadRoot;

    @Autowired
    private FileImageService fileImageService;


    @RequestMapping(value = "/{targetType}/{img_name}/{title}.{extension}", method = RequestMethod.GET )
    public void getContentArticleImageJpeg(HttpServletRequest request, HttpServletResponse response , @PathVariable(value = "targetType") char targetType, @PathVariable(value = "img_name") String img_name, @PathVariable(value = "title") String title, @PathVariable(value = "extension") String extension) {
        this.getContentImage(request, response, targetType, img_name, extension);
    }


    public void getContentImage(HttpServletRequest request,HttpServletResponse response ,char targetType,  /*String id,*/ String img_name, String extension) {
        String type="w";
        //type=Character.toLowerCase(type);
        img_name=img_name.replace("_"," ");
        String[] names=img_name.split("-");
        String fileDir=img_name.replace("-","/").replace("/"+names[names.length-1],"");

        UserAgent userAgent = UserAgent.parseUserAgentString(fileImageService.getUserAgent(request));
        if (userAgent.getOperatingSystem().getDeviceType()== DeviceType.MOBILE)
            type="m";


        String defaultImagePath=uploadRoot +  "/" + type +"nopic."+ extension;
        String mediaType=fileImageService.getContentType(extension);
        if (!mediaType.equalsIgnoreCase("image/jpeg"))
            type="";

        if (names.length>1) {
            String middlePath = fileImageService.getMiddlePath(targetType);
            String imageType = (middlePath.contains("/page/") || middlePath.contains("/site/") || middlePath.contains("/pwa_config/") || extension.toLowerCase() == "ico") ? "" : type + "";
            //String imagePath=uploadRoot +  middlePath+ id.trim() +"/" + imageType + img_name.toLowerCase().trim() + "."+ extension;
            String imagePath = uploadRoot + middlePath + fileDir + "/" + imageType + names[names.length - 1].toLowerCase().trim() + "." + extension.toLowerCase();
            writeImageAsStream(response, imagePath, defaultImagePath, mediaType);
        }else
            response.setStatus(HttpStatus.NOT_FOUND.value());
    }


    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.GET,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public  void getLogoImage(HttpServletResponse response, @PathVariable(value = "type") String type , @PathVariable(value = "id") Long id)  {
        try {
            if (type != null) {
                type=type.toLowerCase();
                response= setHeaderImageCache(response);
                if (type.trim().toLowerCase().equals("u")) {
                    InputStream userInputStream= fileImageService.getUserProfileImage(id);
                    response.setContentType( MediaType.IMAGE_JPEG_VALUE);
                    streamWrite(response,userInputStream);
                }
            }
        } catch (IOException e) {
            //  throw new FileStorageException("Could not initialize File storage Service", e);
        }
    }

    private static String redirect301(HttpServletResponse response, String newUrl) {

        URI uri = null;
        try {
            uri = new URI(newUrl);
        } catch (URISyntaxException e) {
        }
        String url = uri != null ? uri.toASCIIString() : newUrl;
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", url);
        response.setHeader("Connection", "close");
        return "redirect:" + url;
    }

}
