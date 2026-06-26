package com.examp.springmvc.catalog.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.examp.springmvc.catalog.domain.ports.output.ImageStoragePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryImageStorageAdapter implements ImageStoragePort {
    private final Cloudinary cloudinary;

    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CloudinaryImageStorageAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String upload(InputStream inputStream, String fileName) {
        if (inputStream == null) {
            return null;
        }
        try {
            String cloudName = (String) cloudinary.config.cloudName;
            if (cloudName == null || cloudName.trim().isEmpty() || cloudName.equals("your_cloud_name")) {
                System.out.println("Warning: Cloudinary not configured. Using placeholder image.");
                return "/resources/images/placeholder-product.png";
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            byte[] bytes = buffer.toByteArray();

            Map<?, ?> uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            System.err.println("Cloudinary upload failed, falling back to placeholder: " + e.getMessage());
            return "/resources/images/placeholder-product.png";
        }
    }
}
