package com.kakao.together.service;

import com.kakao.together.domain.entity.Image;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    public Image createIfSrcNotExist(String src) {
        return new Image();
    }
}
