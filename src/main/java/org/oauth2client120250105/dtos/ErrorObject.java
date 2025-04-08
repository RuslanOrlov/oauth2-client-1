package org.oauth2client120250105.dtos;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
public class ErrorObject {

    private String status;
    private String error;
    private String exception;
    private String message;
    private String errors;
    private String trace;
    private String path;
    private String requestMethod;
    private String timestamp;

    public ErrorObject(int status, Map<String, Object> errorAttributes) {
        this.status = errorAttributes.get("status") != null ?
                errorAttributes.get("status").toString() : null;
        this.error = errorAttributes.get("error") != null ?
                errorAttributes.get("error").toString() : null;
        this.exception = errorAttributes.get("exception") != null ?
                errorAttributes.get("exception").toString() : null;
        this.message = errorAttributes.get("message") != null ?
                errorAttributes.get("message").toString() : null;
        this.errors = errorAttributes.get("errors") != null ?
                errorAttributes.get("errors").toString() : null;
        this.trace = errorAttributes.get("trace") != null ?
                (errorAttributes.get("trace").toString().length() > 1000 ?
                        errorAttributes.get("trace").toString().substring(0, 1000) + "..." :
                        errorAttributes.get("trace").toString()
                ) : null;
        this.path = errorAttributes.get("path") != null ?
                errorAttributes.get("path").toString() : null;
        this.requestMethod = errorAttributes.get("requestMethod") != null ?
                errorAttributes.get("requestMethod").toString() : null;

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.timestamp = dateTime.format(formatter);
    }

}
