package com.aws.photosharing.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImageUrls {
    List<String> urls = new ArrayList<>(10);

    public void add(String url) {
        this.urls.add(url);
    }
}
