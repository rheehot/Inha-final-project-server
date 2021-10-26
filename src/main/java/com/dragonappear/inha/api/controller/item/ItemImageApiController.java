package com.dragonappear.inha.api.controller.item;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Api(tags = {"아이템 이미지 조회 API"})
@RestController
public class ItemImageApiController {

    @ApiOperation(value = "아이템 이미지 조회 API", notes = "아이템 이미지 조회")
    @GetMapping(value = "/items/images/{fileOriginName}")
    public ResponseEntity<Resource> itemImages(@PathVariable("fileOriginName") String fileName) {
        String path = "/home/ec2-user/app/step1/Inha-final-project-server/src/main/resources/static/items/";
        FileSystemResource resource = new FileSystemResource(path+fileName);
        if (!resource.exists()) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        HttpHeaders header = new HttpHeaders();
        Path filePath = null;
        try {
            filePath = Paths.get(path+fileName);
            header.add("Content-Type", Files.probeContentType(filePath));
        } catch (IOException e) {
            return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
    }
}
