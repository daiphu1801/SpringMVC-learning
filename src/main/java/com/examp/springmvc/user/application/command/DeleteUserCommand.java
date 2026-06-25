package com.examp.springmvc.user.application.command;

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
