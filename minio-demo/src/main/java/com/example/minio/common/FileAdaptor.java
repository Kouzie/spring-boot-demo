package com.example.minio.common;

import java.io.InputStream;

public interface FileAdaptor {
    String putFile(String path, InputStream inputStream, String contentType) throws PutFileException;

    Boolean removeFile(String path) throws RemoveFileException;
}
