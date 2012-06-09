package utils;

import java.io.File;
import java.math.BigInteger;

/**
 * User: divxer
 * Date: 12-6-7
 * Time: 下午9:15
 */
public class UpYunUtils {
    public static String picBedDomain = "http://wadianying.b0.upaiyun.com";

    public static String getTinyUrl(String pictureId) {
        BaseX bx = new BaseX(BaseX.DICTIONARY_32_SMALL);
        String encoded = bx.encode(new BigInteger(pictureId));

        return encoded;
    }

    public static boolean saveImg2Upyun(String upYunDirName, String pictureLocation) {
        System.out.println("upYunDirName=" + upYunDirName + "; pictureLocation=" + pictureLocation);

        /// 初始化空间
        UpYun upyun = new UpYun("username", "admin", "password");
        System.out.println("SDK 版本 "+upyun.version());
        /// 设置是否打印调试信息
        upyun.debug = true;

        /// 获取空间占用大小
        long x = 0;
        try {
            x = upyun.getBucketUsage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /// 上传文件
        File file = new File(pictureLocation);

        // 设置待上传文件的 Content-MD5 值（如又拍云服务端收到的文件MD5值与用户设置的不一致，将回报 406 Not Acceptable 错误）
        try {
            upyun.setContentMD5(UpYun.md5(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            upyun.writeFile("/" + upYunDirName + "/" + file.getName(), file, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}
