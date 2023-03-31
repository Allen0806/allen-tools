package com.allen.tool.encrypt;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.result.ResultStatus;
import com.allen.tool.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * AES加解密工具
 *
 * @author allen
 * @date 2022-08-13
 */
public class AESUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AESUtil.class);

    /**
     * AES加密，返回加密后的Base64编码字符串，加密模式：AES/CBC/PKCS5Padding
     *
     * @param content 需要加密的明文
     * @param aesKey  加密密钥
     * @param aesIv   初始向量
     * @return 加密后的Base64编码
     */
    public static String encrypt(String content, String aesKey, String aesIv) {
        if (StringUtil.isBlank(content)) {
            LOGGER.error("AES加密内容为空");
            return null;
        }
        if (StringUtil.isBlank(aesKey)) {
            LOGGER.error("AES加密密钥为空");
            throw new CustomBusinessException(ResultStatus.PARAM_ERROR.getCode(), "AES加密密钥为空");
        }
        if (StringUtil.isBlank(aesIv)) {
            LOGGER.error("AES加密初始向量为空");
            throw new CustomBusinessException(ResultStatus.PARAM_ERROR.getCode(), "AES加密初始向量为空");
        }
        try {
            AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, aesKey.getBytes(), aesIv.getBytes());
            return Base64.getEncoder().encodeToString(aes.encrypt(content));
        } catch (Exception e) {
            LOGGER.error("AES加密失败", e);
            throw new CustomBusinessException(ResultStatus.SYSTEM_ERROR.getCode(), "AES加密失败", e);
        }
    }

    /**
     * AES解密，加密模式：AES/CBC/PKCS5Padding
     *
     * @param content 需要解密的Base64编码的
     * @param aesKey  解密密钥
     * @param aesIv   初始向量
     * @return 解密后的明文
     */
    public static String decrypt(String content, String aesKey, String aesIv) {
        if (StringUtil.isBlank(content)) {
            LOGGER.error("AES解密密文为空");
            return null;
        }
        if (StringUtil.isBlank(aesKey)) {
            LOGGER.error("AES解密密钥为空");
            throw new CustomBusinessException(ResultStatus.PARAM_ERROR.getCode(), "AES解密密钥为空");
        }
        if (StringUtil.isBlank(aesIv)) {
            LOGGER.error("AES解密初始向量为空");
            throw new CustomBusinessException(ResultStatus.PARAM_ERROR.getCode(), "AES解密初始向量为空");
        }
        try {
            byte[] originalData = Base64.getDecoder().decode(content.getBytes());
            AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, aesKey.getBytes(), aesIv.getBytes());
            return aes.decryptStr(originalData);
        } catch (Exception e) {
            LOGGER.error("AES解密失败", e);
            throw new CustomBusinessException(ResultStatus.SYSTEM_ERROR.getCode(), "AES解密失败", e);
        }
    }
}
