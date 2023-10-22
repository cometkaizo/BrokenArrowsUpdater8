package me.cometkaizo.util;

import de.ralleytn.simple.json.JSONObject;
import de.ralleytn.simple.json.JSONParseException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class JSONUtils {

    public static JSONObject readObject(File file) throws IOException, JSONParseException {
        return new JSONObject(StringUtils.readString(file));
    }

    public static JSONObject getOrCreate(JSONObject object, String key) {
        Object result = object.get(key);
        if (!(result instanceof JSONObject jsonObject)) return new JSONObject();
        return jsonObject;
    }

    public static Collection<JSONObject> getObjects(JSONObject object) {
        return object.values().stream().filter(o -> o instanceof JSONObject).map(o -> (JSONObject) o).toList();
    }

}
