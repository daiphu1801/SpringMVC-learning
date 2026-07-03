package com.examp.springmvc.user.application.usermanagement.command;

import java.io.InputStream;

public final class ImportUsersCommand {

    private final String taskId;
    private final InputStream inputStream;

    public ImportUsersCommand(String taskId, InputStream inputStream) {
        this.taskId = taskId;
        this.inputStream = inputStream;
    }

    public String getTaskId() {
        return taskId;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
