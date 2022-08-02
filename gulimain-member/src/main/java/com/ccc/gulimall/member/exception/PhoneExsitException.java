package com.ccc.gulimall.member.exception;

public class PhoneExsitException extends  RuntimeException {
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public PhoneExsitException() {
        super("该号码已存在！");
    }
}
