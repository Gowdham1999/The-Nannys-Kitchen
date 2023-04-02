package com.cma.main.Utils;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CafeUtils {

    private CafeUtils() {
    }

    ;

    public static ResponseEntity<String> getResponse(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>("{\"message\":" + responseMessage + "}", httpStatus);
    }

    public static String generateBillName() {
        Date date = new Date();
        Long time = date.getTime();

        return "Bill - " + time;
    }

    public static JSONArray jsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data) {
        if (!Strings.isNullOrEmpty(data)) {
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {
            }.getType());
        }
        return new HashMap<>();
    }

    public static Boolean doesFileExist(String path) {
        File file = new File(path);
        return (file != null && file.exists()) ? Boolean.TRUE : Boolean.FALSE;
    }
}


