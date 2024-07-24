package cn.edu.moe.user.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

/**
 * 借助hutool工具 整合加解密工具
 *
 */
public class AesSecureUtil {

    /**
     * 返回AES算法使用的随机密钥key
     * @return Base64格式的key
     */
    public static String generateAesKey(){
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        return Base64.getEncoder().encodeToString(key);
    }

    /**
     *
     * @param key base64格式的key
     * @param content 待加密文本
     * @return 加密后文本
     */
    public static String aesEncrypt(String key,String content){
        if(StringUtils.isBlank(key) || StringUtils.isBlank(content)){
            return "";
        }
        byte[] decode = Base64.getDecoder().decode(key);
        return SecureUtil.aes(decode).encryptBase64(content);
    }

    /**
     *
     * @return 解密后文本
     */
    public static String aesDecrypt(String key,String content){
        if(StringUtils.isBlank(key) || StringUtils.isBlank(content)){
            return "";
        }
        byte[] decode = Base64.getDecoder().decode(key);
        return SecureUtil.aes(decode).decryptStr(content);
    }

}

