package com.rozvi14.facialrecognition.models;

import java.io.Serializable;
import java.util.Map;

public class GenericResult
        implements Serializable {

    protected boolean success;
    protected int status;
    protected String message;
    protected Map resultMapping;

    private static final long serialVersionUID = 6529685098267757700L;
    //private static final String patternGeneric = "{\"success\":%s,\"status\":%d,\"message\":\"%s\"}";

    @Override
    public String toString() {

        return String.format("className: \"%12s\" success: %b status: %s message:\"%s\"",
                "GenericResult", success, status, message);
    }

    public String toJson() {
        Object[] arrobject = new Object[4];
        arrobject[0] = this.success;
        arrobject[1] = this.status;
        arrobject[2] = this.message;
        return String.format("{\"success\":%s,\"status\":%s,\"message\":\"%s\" }", arrobject);
    }

    public GenericResult(boolean success, int status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }

    public GenericResult(boolean success, int status, String message, Map resultMapping) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.resultMapping = resultMapping;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map getResultMapping() {
        return this.resultMapping;
    }

    public void setResultMapping(Map resultMapping) {
        this.resultMapping = resultMapping;
    }
}

