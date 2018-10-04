package com.aws.photosharing.rest;

import com.aws.photosharing.entity.UserPhoto;
import com.aws.photosharing.model.ImageUrls;
import com.aws.photosharing.security.JwtHelper;
import com.aws.photosharing.service.PhotoShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@RestController
public class PhotoShareController {

    private final PhotoShareService photoShareService;
    private final JwtHelper jwtHelper;

    public PhotoShareController(@Autowired PhotoShareService photoShareService, @Autowired JwtHelper jwtHelper) {
        this.photoShareService = photoShareService;
        this.jwtHelper = jwtHelper;
    }

    @RequestMapping(value = "/v1/authenticate", method = RequestMethod.POST)
    public ResponseEntity<Void> authenticate(@RequestParam(name = "userName") String userName, @RequestParam(name = "password") String password) {
        String jwt = jwtHelper.create(userName);
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, jwt);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/photo", method = RequestMethod.POST)
    public ResponseEntity<Void> upload(@RequestAttribute(name = "userName") String userName,
                                       @RequestPart(value = "file") MultipartFile file,
                                       final UriComponentsBuilder ucBuilder) {
        String id = null;
        try {
            id = photoShareService.upload(userName, file.getBytes());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/v1/photo/{id}").buildAndExpand(id).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/v1/photo", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@RequestAttribute(name = "userName") String userName,
                                         @RequestParam final String id,
                                         @RequestParam final String tags) {
        boolean success = photoShareService.update(id, tags);
        if (!success) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

        @RequestMapping(value = "/v1/photo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageUrls> get(@RequestAttribute(name = "userName") String userName,
                                         @RequestParam final String tags) {
        // prepare the response headers
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(photoShareService.getByTags(userName, tags), responseHeaders, HttpStatus.OK);
    }
}