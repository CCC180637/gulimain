package com.ccc.gulimall.member.exception;

public class UsernameExsitException extends  RuntimeException{

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public UsernameExsitException() {
        super("该用户名已存在！");
    }
}
