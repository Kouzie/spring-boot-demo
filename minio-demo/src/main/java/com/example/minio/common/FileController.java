package com.example.minio.common;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileAdaptor fileAdaptor;
    @Value("${object.storage.path}")
    private String storagePath;

    @PostMapping
    public FileInfoDto uploadFile(@RequestParam(value = "file") MultipartFile file) throws IOException {
        String savedPath = fileAdaptor.putFile("kouzie/", file.getInputStream(), file.getContentType());
        return new FileInfoDto(storagePath + savedPath);
    }

    @DeleteMapping
    public void removeFile(@RequestParam("path") String path) {
        fileAdaptor.removeFile(path);
        return;
    }
}
