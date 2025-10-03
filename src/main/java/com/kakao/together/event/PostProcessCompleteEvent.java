package com.kakao.together.event;

import java.util.ArrayList;
import java.util.List;

public class PostProcessCompleteEvent {
    private List<Long> imageIdList = new ArrayList();

    public PostProcessCompleteEvent(List<Long> imageIdList) { this.imageIdList = imageIdList; }
    public List<Long> getImageIdList() { return imageIdList; }
}
