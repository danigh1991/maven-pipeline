package com.core.common.services.impl;

import com.core.common.services.ImageService;
import com.core.exception.FileStorageException;
import com.core.exception.ResourceNotFoundException;
import com.core.datamodel.model.dbmodel.User;
import com.core.common.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service("imageServiceImpl")
public class ImageServiceImpl extends AbstractService implements ImageService {
    @Value("${app.file_upload_userprofiles}")
    private String userProfileRoot;
    @Value("${app.file_upload_store_logos}")
    private String storeLogosRoot;

    @Value("${app.file_upload_brand_logos}")
    private String brandLogosRoot;

    @Autowired
    private UserService userService;


    private String  tmpStoreLogoPerfix="tmpStoreLogo_" ;

    @Override
    public InputStream getUserProfileImage(Long userId)throws IOException{
        return  getUserProfileImage(userId,false);
    }
    @Override
    public InputStream getUserProfileImage(Long userId,Boolean isTemp) throws IOException {

        if (userId==0)
            throw new ResourceNotFoundException(userId.toString(), "common.image.userImage_notFound");
        String imagePath ="";
        if (isTemp)
            imagePath = userProfileRoot + "/tmp/" + userId.toString() + "/" + userId.toString() + ".jpg";
        else
            imagePath = userProfileRoot + "/" + userId.toString() + "/" + userId.toString() + ".jpg";
        InputStream in =null;
        try {
            if (!Files.exists(Paths.get(imagePath))) {
                /*if (userService.getCurrentUser().getGender() == 1)*/
                User user = userService.getUserInfo(userId);
                if(user != null) {
                    if (user.getGender() == 1)
                        imagePath = userProfileRoot + "/01.jpg";
                    else if (user.getGender() == 2)
                        imagePath = userProfileRoot + "/02.jpg";
                    else
                        imagePath = userProfileRoot + "/01.jpg";
                }
                else
                    imagePath = userProfileRoot + "/01.jpg";
            }
            if (Files.exists(Paths.get(imagePath))) {
                FileSystemResource imgFile = new FileSystemResource(imagePath);
                in = imgFile.getInputStream();
            } else {
                throw new ResourceNotFoundException(userId.toString(), "common.image.id_notFound" , userId.toString() );
            }
        } catch (Exception e) {
            throw new FileStorageException("Could not initialize File storage Service", e);
        }
        return in;
    }
    @Override
    public InputStream getStoreLogo(Long storeId) throws IOException{
        return  getStoreLogo(storeId,false);
    }
    @Override
    public InputStream getStoreLogo(Long storeId,Boolean isTemp) throws IOException{
        String imagePath = "";
        if (isTemp)
            imagePath=storeLogosRoot + "/tmp/" + storeId.toString() + "/" + storeId.toString() + ".jpg";
        else
            imagePath=storeLogosRoot + "/" + storeId.toString() + "/" + storeId.toString() + ".jpg";
        InputStream in =getLogoImage(imagePath,storeId,storeLogosRoot);
        return in;
    }

    @Override
    public InputStream getTStoreLogo(String fileName) throws IOException {
        InputStream in =getLogoImage(storeLogosRoot + "/tmp/" +tmpStoreLogoPerfix +userService.getCurrentUser().getId() + ".jpg" ,0l,storeLogosRoot);
        return in;
    }

    @Override
    public InputStream getBrandLogo(Long brandId) throws IOException {
        return getBrandLogo(brandId,false);
    }
    @Override
    public InputStream getBrandLogo(Long brandId,Boolean isTemp) throws IOException {
        String imagePath="";
        if (isTemp)
            imagePath = brandLogosRoot + "/tmp/" + brandId.toString() + "/" + brandId.toString() + ".jpg";
        else
            imagePath = brandLogosRoot + "/" + brandId.toString() + "/" + brandId.toString() + ".jpg";

        InputStream in =getLogoImage(imagePath,brandId,brandLogosRoot);
        return in;
    }

    private InputStream getLogoImage(String imagePath,Long id ,String logoRoot){
        InputStream in =null;
        try {
            if (!Files.exists(Paths.get(imagePath))) {
                if (!Files.exists(Paths.get(imagePath.replace(".jpg", ".png")))) {
                    imagePath = logoRoot + "/00.jpg";
                } else {
                    imagePath = imagePath.replace(".jpg", ".png");
                }
            }

            if (Files.exists(Paths.get(imagePath))) {
                FileSystemResource imgFile = new FileSystemResource(imagePath);
                in = imgFile.getInputStream();
            } else {
                throw new ResourceNotFoundException(id.toString(), "common.image.id_notFound" , id.toString());
            }
        } catch (Exception e) {
            throw new FileStorageException("Could not initialize File storage Service", e);
        }
        return in;
    }


}
