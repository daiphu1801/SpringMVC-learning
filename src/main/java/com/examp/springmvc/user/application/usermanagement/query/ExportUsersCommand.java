package com.examp.springmvc.user.application.usermanagement.query;

public final class ExportUsersCommand {

    private final String taskId;

    public ExportUsersCommand(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }
}
