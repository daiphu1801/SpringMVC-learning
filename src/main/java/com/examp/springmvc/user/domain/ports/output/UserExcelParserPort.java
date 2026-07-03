package com.examp.springmvc.user.domain.ports.output;

import com.examp.springmvc.user.application.usermanagement.command.UserImportRow;
import java.io.InputStream;
import java.util.List;

public interface UserExcelParserPort {
    List<UserImportRow> parse(InputStream inputStream);
}
