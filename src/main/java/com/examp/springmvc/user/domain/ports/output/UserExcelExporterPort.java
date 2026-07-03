package com.examp.springmvc.user.domain.ports.output;

import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import java.io.OutputStream;
import java.util.List;

public interface UserExcelExporterPort {
    void export(List<UserDTO> users, OutputStream outputStream, ProgressListener progressListener);

    interface ProgressListener {
        void onProgress(int successCount);
    }
}
