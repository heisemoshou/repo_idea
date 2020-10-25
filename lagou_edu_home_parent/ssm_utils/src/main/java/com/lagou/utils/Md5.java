package com.lagou.utils;


import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class Md5 {

    // 声明了一个变量 相当于为MD5加密过程中所需要的key起了个默认值
    public final static  String md5key = "lagou";
    /**
     * MD5方法
     * @param text 明文
     * @param key 密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) throws Exception {
        //加密后的字符串
        String encodeStr= DigestUtils.md5Hex(text+key);
        System.out.println("MD5加密后的字符串为:encodeStr="+encodeStr);
        return encodeStr;
    }

    /**
     * MD5验证方法
     * @param text 明文
     * @param key 密钥
     * @param md5 密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if(md5Text.equalsIgnoreCase(md5))
        {
            System.out.println("MD5验证通过");
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

        // 注册  用户名：tom 密码：123456
        // 添加用户时：要将明文密码转换为密文密码
        String lagou = Md5.md5("123456", "lagou");
        System.out.println("加密后的字符串为:"+lagou);

        // 登录 用户名：tom  密码：123456
        // 1.根据前台传递过来的用户名tom先在user表中查询出对应的密文密码
        // select * from user where username = tom

        // 2.调用verify 方法 进行密码校验
        // Md5.verify("123456", "lagou", "获取到的md5值");

    }

}
