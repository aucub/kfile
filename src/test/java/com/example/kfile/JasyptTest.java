package com.example.kfile;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JasyptTest {

    @Autowired
    private StringEncryptor stringEncryptor;

    /**
     * 加密解密
     */
    @Test
    public void jasyptTest() {
        // 加密
        System.out.println(stringEncryptor.encrypt("Hello"));
        // 解密
        System.out.println(stringEncryptor.decrypt("m8Lc8VOKloIYzCnxdTnzf+FjpLQiHY+ilWi+BZNSjYwjaVf+f4A0I+Rf973U38vS"));
    }

}
