package com.allen.tool.encrypt;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.param.LoginParam;
import com.allen.tool.result.ResultStatus;
import com.allen.tool.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

/**
 * AES加解密工具
 *
 * @author luoxuetong
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

    /**
     * 加密登录信息
     */
    private static void encryptLoginParam(){
        String appId = "75a33291284640079a462a0723b4bd11";
        String aesKey = "WdVZ8#GYhMPsmeKQ";
        String aesIv = "L@9xxxetu9REbQGq";
        String md5Key = "pUctu54GzN@H4b6H";
        Long currentStamp = System.currentTimeMillis();
        String requestSign = DigestUtil.md5Hex(appId+currentStamp+md5Key);
        System.out.println("request-time: " + currentStamp);
        System.out.println("request-sign: " + requestSign);
        LoginParam loginParam = new LoginParam();
        loginParam.setAppId(appId);
        loginParam.setUserId("498866293303152640");
        loginParam.setAccessToken("288125fbc4414d6495ad68eebde1d21d");
        loginParam.setTenantId("lczq");
        loginParam.setDeviceId("F351D598-525C-49A3-A50B-C1C0B05FD92D");
        loginParam.setDeviceType("1");
        loginParam.setIdentifyType("1");
        loginParam.setOpStation("MI;IIP=36.110.108.162;IPORT=7004;LIP=192168070114;MAC=NA;IDFA=F351D598-525C-49A3-A50B-C1C0B05FD92D;UMPN=NA;RMPN=15622244444;ICCID=NA;OSV=iOS16.0;IMSI=NA;储宝宝+测试=4.5.6.0");
        String loginParamStr = JsonUtil.object2Json(loginParam);
        String encryptContent = encrypt(loginParamStr, aesKey, aesIv);
        System.out.println("loginParam: " + encryptContent);

        encryptContent = "ccztyJe+Z5IQ5VYa01KaRk/Z5lrVswXHouhUeyFH5DfuXOLO6gCCZzIcOZM8+MfBLcdL9/IjnsShJmgwTfH+OpuavS+betFI/nES6JIBw7I3BZ7+dnZK8V07fsXr83Xw7Cg4l89XVsfoW8qvAVUltphu0IaA+TlxDKmWIHuw3cX8pg3tyHwLait+ACe41R9URQikbued1QD41XX9Obw5YhTzO52fQAVB4ADkqqcfardUXql68+z43GGFayIT5gOQuM1Hm+XnHmINFYBGQwcQF9R+GxxlL6V9H8sGZ4BJRA7gextW1mFPPtmRjZlVV0ZOB4g+0uZQs1l6zA0RvxwXIcaC0FoCGy+v5+ghrKYmEcTSu+zM9r2GY2Qeo9ZrsSJ81KMgCqnkk27DqBJ+UBvr+3fjQpXDgU2oIA2+GuMfCCGMW6QStuZ9X4gCfd9djaz+uplHk7CNDiUgf8K8ruLGAYkvJUhyo0bRP/RKijFK7tn12V9pl5cblkmTMz6w74Bez2ZEZSfSHGbzBJcmf4JhZq4QFKYtfaVhbcx+3aK35S7V8+6QH/klxyppA7djlVIWfQK0R1eqlfCoiUhRh5PtpehtFN0aSQYZU3rTAS+69yPLLrM1/ytik7gjeapMY6mM1prAR5HPOTFCkNG0vl+4EzVJaFKaKcoWu7X7/D5JxdNHpXj9cbmwA9vHUGlUa5z6g0mZnxQ/rBIKtMBrYJQXlp+IwJ0uGnslu9yaU2/LVdTcHlozMKyNrTlQ/gVwj+IbbIop7Gytc5BhAecPDa0JAIuQ9GD08CbtdzlR2bzlJemyrqOV0CKA8ERZptfIho1FiC0pEqEj3rZJC4CsinIf56Ea4h5WoGebGcIyeVoZvsE=";
        System.out.println("userInfo: " + decrypt(encryptContent, aesKey, aesIv));
    }

    /**
     * 加密应用的密钥
     */
    private static void encryptAppInfo(){
        // 测试
//        String aesKey = "uh2#BWySEEKldm6p";
//        String aesIv = "WjBPGOG@8QGoTJvl";
        // 生产
        String aesKey = "ZqSH#kDNHrr9tW#p";
        String aesIv = "ArK#uXmP23zT77C&";
        String appAesKey = "EcNTd6spto5#aQIk";
        String appAesIv = "jl5EKsvVSE!nRcmb";
        String appMd5Key = "aV^4ivv#BkgFMIw5";
        String appAesKeyEncrypt = encrypt(appAesKey, aesKey, aesIv);
        String appAesIvEncrypt = encrypt(appAesIv, aesKey, aesIv);
        String appMd5KeyEncrypt = encrypt(appMd5Key, aesKey, aesIv);
        System.out.println("**************************");
        System.out.println("appAesKey: " + appAesKeyEncrypt);
        System.out.println("appAesIv: " + appAesIvEncrypt);
        System.out.println("appMd5Key: " + appMd5KeyEncrypt);
        System.out.println("**************************");
        System.out.println("appAesKey: " + decrypt(appAesKeyEncrypt, aesKey, aesIv));
        System.out.println("appAesIv: " + decrypt(appAesIvEncrypt, aesKey, aesIv));
        System.out.println("appMd5Key: " + decrypt(appMd5KeyEncrypt, aesKey, aesIv));
        System.out.println("**************************");
    }

    public static void main(String[] args) {
//        encryptAppInfo();
        encryptLoginParam();
//        System.out.println(UUID.randomUUID().toString().replace("-", ""));
    }
}
