package com.examp.springmvc.shared.presentation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates uploaded image files before processing.
 *
 * <p>Checks are applied in the following order:
 * <ol>
 *   <li>File extension (allowlist – only known image formats).</li>
 *   <li>MIME type reported by the browser/servlet container (allowlist).</li>
 *   <li>Maximum file size (default 5 MB).</li>
 * </ol>
 *
 * <p>Why both extension AND MIME type?
 * Extension alone can be spoofed (rename "virus.exe" to "virus.jpg").
 * MIME type alone can be spoofed via the Content-Type header.
 * Checking both raises the bar for attackers.
 * For a higher-assurance system, also read the first bytes (magic bytes) of the
 * stream to verify the actual file format.
 */
public final class ImageFileValidator {

    /** Maximum allowed file size in bytes (5 MB). */
    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;

    /** Allowed file extensions (lower-cased, no dot). */
    private static final Set<String> ALLOWED_EXTENSIONS =
            new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "avif"));

    /** Allowed MIME types. Must correspond 1-to-1 with ALLOWED_EXTENSIONS. */
    private static final Set<String> ALLOWED_MIME_TYPES =
            new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp", "image/avif"));

    private ImageFileValidator() {}

    /**
     * Validates the uploaded file.
     *
     * @param file the multipart file received from the form
     * @throws IllegalArgumentException if the file fails any validation check
     */
    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn file ảnh.");
        }

        // 1. Check file size
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File vượt quá kích thước cho phép (tối đa 5 MB). File của bạn: "
                    + (file.getSize() / (1024 * 1024)) + " MB.");
        }

        // 2. Check file extension
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("Tên file không hợp lệ.");
        }
        String ext = extractExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Định dạng file \"." + ext + "\" không được phép. " + "Chỉ chấp nhận: "
                    + String.join(", ", ALLOWED_EXTENSIONS) + ".");
        }

        // 3. Check MIME type (reported by the container)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Loại file \"" + contentType + "\" không được phép. "
                    + "Chỉ chấp nhận ảnh (JPEG, PNG, GIF, WebP, AVIF).");
        }
    }

    /** Extracts and lower-cases the file extension, or returns empty string if absent. */
    private static String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
