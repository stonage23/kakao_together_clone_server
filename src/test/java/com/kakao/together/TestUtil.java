package com.kakao.together;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

@UtilityClass
public class TestUtil {

    private static final Gson gson = new Gson();

    public static <T> T getValueFromJson(MvcResult mvcResult, String key) throws UnsupportedEncodingException {
        return (T) gson.fromJson(mvcResult.getResponse().getContentAsString(), LinkedTreeMap.class).get(key);
    }
}
