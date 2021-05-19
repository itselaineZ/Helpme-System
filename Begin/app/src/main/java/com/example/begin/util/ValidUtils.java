package com.example.begin.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidUtils {
    // 校验账号不能为空且必须包含@
    public static boolean isEmailValid(String account) {
        if (account == null) {
            return false;
        }
        // 正则表达式匹配邮箱
        String pattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(account);
        return m.matches();
    }

    // 校验密码不多于16位
    public static boolean isPasswordValid(String password) {
        return password.trim().length() > 0 && password.trim().length() < 16;
    }

    /**
     * MD5加密+BASE64编码
     *
     * @return 加密后字符串
     */
    public static String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // 注意这里是 Base64.NO_WRAP，不能用 Base64.DEFAULT，否则结尾会带一个 \n
        String newstr = new String(Base64.encode(md5.digest(str.getBytes("utf-8")), Base64.NO_WRAP));
        return newstr;
    }
}

