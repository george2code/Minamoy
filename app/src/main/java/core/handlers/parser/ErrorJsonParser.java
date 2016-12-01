package core.handlers.parser;

import org.json.JSONObject;
import core.model.ServerError;

public class ErrorJsonParser {
    public static ServerError parseError(String content) {
        try {
            JSONObject obj = new JSONObject(content);
            ServerError serverError = new ServerError();

            if (!obj.isNull("type"))
                serverError.setType(obj.getString("type"));
            if (!obj.isNull("description"))
                serverError.setDescription(obj.getString("description"));

            return serverError;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}