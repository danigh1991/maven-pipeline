package com.core.common.services.impl;

import com.core.common.services.CommonService;
import com.core.exception.FileStorageException;
import com.core.exception.ResourceNotFoundException;
import com.core.exception.StorageFileNotFoundException;
import com.core.common.services.StorageService;
import com.core.common.util.Utils;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.image.ImageParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.stream.Stream;

@Service("fileStorageService")
public class FileStorageService extends AbstractService implements StorageService {


    @Value("${app.file_upload_root}")
    private String uploadRoot;

    public FileStorageService() {
    }

    @Override
    public void init() {
        try {
            Path rootLocationPath = Paths.get(uploadRoot);
            if (!Files.exists(rootLocationPath))
               Files.createDirectories( rootLocationPath);
        }
        catch (IOException e) {
            throw new FileStorageException("Could not initialize File storage Service", e);
        }
    }

    @Override
    public String store(String targetPath, String id, MultipartFile file,int w ,int h) {
        return this.store(targetPath,id,true,file,w,h);
    }

    @Override
    public String store(String targetPath,String id,Boolean createDirByFileName, MultipartFile file,int w ,int h) {
        Path rootLocationPath = Paths.get(targetPath);

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("file.empty.invalid");
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new FileStorageException("file.path.invalid");
            }

            Path uploadPathForUser=(createDirByFileName? rootLocationPath.resolve(id) : rootLocationPath);
            try {
                if (!Files.exists(uploadPathForUser ))
                    Files.createDirectories( uploadPathForUser);
            }
            catch (IOException e) {
                throw new FileStorageException("file.save.exception", e);
            }
            String filenameSplit[]=filename.split("\\.");
            String fileExt =filenameSplit[filenameSplit.length-1].trim();
            if (!fileExt.equalsIgnoreCase("jpg") && !fileExt.equalsIgnoreCase("png") && !fileExt.equalsIgnoreCase("jpeg"))
                throw new FileStorageException("file.extension.invalid");

            String contentType=fileExt.equalsIgnoreCase("png")?"image/PNG": "image/JPEG";
            if (!this.checkMetaData(file,contentType))
                throw new FileStorageException(	"file.content.invalid");


            if (fileExt.equalsIgnoreCase("jpg"))
               Files.deleteIfExists(uploadPathForUser.resolve(id+".png"));
            else if (fileExt.equalsIgnoreCase("png"))
                Files.deleteIfExists(uploadPathForUser.resolve(id+".jpg"));

            if (fileExt.equalsIgnoreCase("jpeg"))
                fileExt="jpg";
            Path tmpTargetPath=uploadPathForUser.resolve(id+"." + fileExt);
            this.createImage(file,w,h,null,null,0,tmpTargetPath.toString(),true,0);
            return tmpTargetPath.toString();
        }
        catch (IOException e) {
            throw new FileStorageException("file.save.exception1", e);
        }
    }

    private void createImage(MultipartFile file,int w,int h,Positions wmPos,BufferedImage waterMark,float wTransparency,String path,boolean allowOverWrite, double qlty) throws IOException {
        InputStream inputStream = file.getInputStream();
        try {
            Thumbnails.Builder builder = Thumbnails.of(inputStream).size(w, h).allowOverwrite(allowOverWrite);
            if (waterMark != null)
                builder.watermark(wmPos, waterMark, wTransparency);
            if (qlty > 0)
                builder.outputQuality(qlty).toFile(path);
            else
                builder.toFile(path);
        }finally {
            closeInputStream(inputStream);
        }
    }


    private void closeInputStream(InputStream inputStream){
        try {
            inputStream.close();
        }
        catch (IOException ex) {
        }
    }

    @Override
    public Boolean checkMetaData(MultipartFile f, String contentType) {
        try (InputStream is =f.getInputStream()) {
            ContentHandler contenthandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());
            metadata.set("Content-Type",contentType);

            //Parser parser = new JpegParser() ; //AutoDetectParser()
            Parser parser = new ImageParser();
            try {
                parser.parse(is, contenthandler, metadata, new ParseContext());
            } catch (SAXException | TikaException e) {
                // Handle error
                return false;
            }

            return true;
        } catch (IOException e) {
            // Handle error
            return false;
        }
    }

    @Override
    public Boolean checkImageRatio(MultipartFile image, Double ratio) {
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(image.getInputStream());
            float height = originalImage.getHeight();
            float width = originalImage.getWidth();
            if (height<=0 || width<=0)
                throw new FileStorageException("Invalid Size","file.hw_required");
            DecimalFormat df =new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.DOWN);
            if (Double.valueOf(df.format(width/height)).compareTo(Double.valueOf(df.format(ratio))) != 0)
                return false;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileStorageException("file.hw_exception",e.getMessage());
        }
    }


}
