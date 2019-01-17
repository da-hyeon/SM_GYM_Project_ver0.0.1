package com.example.hdh.smgproject.Class.RequestClass;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.hdh.smgproject.Adapter.SchedulePTListAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hwangdahyeon on 2018. 5. 3..
 */

public class ScheduleDeleteRequest extends StringRequest {
    final static private String URL = "http://kjg123kg.cafe24.com/ScheduleDelete_SYG.php";
    SchedulePTListAdapter schedulePTListAdapter;
    private Map<String, String> parameters;

    public ScheduleDeleteRequest(String userID, int ptID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("ptID", String.valueOf(ptID));
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}