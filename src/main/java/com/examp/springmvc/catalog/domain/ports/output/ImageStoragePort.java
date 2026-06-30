package com.examp.springmvc.catalog.domain.ports.output;

import java.io.InputStream;

public interface ImageStoragePort {
    String upload(InputStream inputStream, String fileName);

    void delete(String imageUrl);
}
