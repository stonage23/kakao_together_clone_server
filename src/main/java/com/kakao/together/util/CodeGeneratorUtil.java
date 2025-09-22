package com.kakao.together.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class CodeGeneratorUtil {

    public static String createRandomCode(int length) {
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(3);

            switch (index) {
                //  a~z
                case 0 -> key.append((char) (random.nextInt(26) + 97));
                //  A~Z
                case 1 -> key.append((char) (random.nextInt(26) + 65));
                // 0~9
                case 2 -> key.append((random.nextInt(10)));
            }
        }
        return String.valueOf(key);
    }
}
