package com.core.common.security;


import com.core.common.util.Utils;
import com.core.exception.InvalidDataException;
import com.core.exception.MyException;
import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadConfig;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

@Component("cipherHelper")
public class CipherHelper {
    public static enum KeyType {
        AES128_GCM,
        AES256_SIV,
        HMAC_SHA256_128BITTAG,
        ECDSA_P256,
    }

    public CipherHelper() throws Exception {
        AeadConfig.register();
    }

    public String GenerateKey(KeyType keyType) throws Exception {
        if (keyType==null)
            keyType=KeyType.AES128_GCM;
        OutputStream outputStream = new ByteArrayOutputStream();
        CleartextKeysetHandle.write(KeysetHandle.generateNew(KeyTemplates.get(keyType.toString())), JsonKeysetWriter.withOutputStream(outputStream));
        return outputStream.toString();
    }

    public String cipherString(String input, String key)  {
        //Verify parameters
        if (Utils.isStringSafeEmpty(input) || Utils.isStringSafeEmpty(key))
            throw new InvalidDataException("Invalid Date","common.cipherHelper.invalid_params");

        try {
            KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withBytes(key.getBytes()));
            if (keysetHandle == null)
                throw new InvalidDataException("Invalid Date","common.cipherHelper.invalid_key");

            //Get the primitive
            Aead aead = keysetHandle.getPrimitive(Aead.class);

            //Cipher the token
            byte[] cipheredToken = aead.encrypt(input.getBytes(), null);

            return DatatypeConverter.printHexBinary(cipheredToken);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new MyException("CipherHelper.cipherString",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException("CipherHelper.cipherString",e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("CipherHelper.cipherString",e.getMessage());
        }
    }

    public String decipherString (String inputInHex, String key) {
        //Verify parameters
        if (Utils.isStringSafeEmpty(inputInHex) || Utils.isStringSafeEmpty(key)) {
            throw new IllegalArgumentException("common.cipherHelper.invalid_params");
        }
        try {
            KeysetHandle keysetHandle=CleartextKeysetHandle.read(JsonKeysetReader.withString(key));
            if (keysetHandle == null)
                throw new IllegalArgumentException("common.cipherHelper.invalid_key");
            //Get the primitive
            Aead aead = keysetHandle.getPrimitive(Aead.class);
            //Decode the ciphered token
            byte[] cipheredToken = DatatypeConverter.parseHexBinary(inputInHex);
            //Decipher the token
            byte[] decipheredToken = aead.decrypt(cipheredToken, null);
           return new String(decipheredToken);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new MyException("CipherHelper.decipherString",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException("CipherHelper.decipherString",e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException("CipherHelper.decipherString",e.getMessage());
        }
    }
}


