package com.examp.springmvc.user.application.usermanagement.command;

public class DeleteUserCommand {

    private Long id;

    public DeleteUserCommand() {}

    public DeleteUserCommand(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
