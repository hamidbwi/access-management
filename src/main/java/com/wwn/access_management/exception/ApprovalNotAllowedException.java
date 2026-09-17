package com.wwn.access_management.exception;

public class ApprovalNotAllowedException
        extends RuntimeException {

    public ApprovalNotAllowedException(String message) {
        super(message);
    }
}
