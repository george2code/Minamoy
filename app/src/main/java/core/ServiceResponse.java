package core;

import java.util.Objects;
import core.enums.ResponseType;

public class ServiceResponse {

    private ResponseType type;
    private String message;
    private Object response;

    public ServiceResponse(ResponseType type, String message, Objects response) {
        this.type = type;
        this.message = message;
        this.response = response;
    }

    public ResponseType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Object getResponse() {
        return response;
    }
}