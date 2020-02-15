package com.cs2063.runsmart.util;

import android.content.Context;
import android.util.Log;

import com.cs2063.runsmart.model.HistoryData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class JsonUtils {

    private static final String TAG = "JsonUtils.java";

    private static final String HISTORY_JSON_FILE = "History.json";

    private static final String KEY_HISTORY = "history";
    private static final String KEY_AVERAGE_PACE = "average_pace";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_START_TIME = "start_time";

    private ArrayList<HistoryData>  historyArray;

    // Initializer to read our data source (JSON file) into an array of course objects
    public JsonUtils(Context context) {
        processJSON(context);
    }

    private void processJSON(Context context) {
        historyArray = new ArrayList<>();

        try {
            // Create a JSON Object from file contents String
            JSONObject jsonObject = new JSONObject(loadJSONFromAssets(context));

            // Create a JSON Array from the JSON Object
            // This array is the "courses" array mentioned in the lab write-up
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_HISTORY);

            for (int i=0; i < jsonArray.length(); i++) {
                // Create a JSON Object from individual JSON Array element
                JSONObject elementObject = jsonArray.getJSONObject(i);

                JSONArray Jsonlatitude = elementObject.getJSONArray(KEY_LATITUDE);
                JSONArray Jsonlongitude = elementObject.getJSONArray(KEY_LONGITUDE);
                double[] latitude = new double[Jsonlatitude.length()];
                double[] longitude = new double[Jsonlongitude.length()];

                for (int j=0; j<Jsonlatitude.length(); j++) {
                    latitude[j] = Jsonlatitude.getDouble(j);
                    longitude[j] = Jsonlongitude.getDouble(j);
                }

                // Get data from individual JSON Object
                HistoryData historyData = new HistoryData.Builder(elementObject.getLong(KEY_START_TIME),
                        elementObject.getLong(KEY_END_TIME),
                        latitude,longitude)
                        .build();

                // Add new Course to courses ArrayList
                historyArray.add(historyData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAssets(Context context) {
        try {
            InputStream is = context.getAssets().open(HISTORY_JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public String toJSon(HistoryData history) {
        try {
            // Here we convert Java Object to JSON

            JSONObject mainObject = new JSONObject();
            JSONArray list = new JSONArray();

            JSONObject valuesObject = new JSONObject();
            valuesObject.put("start_time", history.getStartTime());
            valuesObject.put("end_time", history.getEndTime());
            valuesObject.put("duration", history.getDuration());
            valuesObject.put("distance", history.getDistance());
            valuesObject.put("avg_pace", history.getAvgPace());
            JSONArray jsonArrayLat = new JSONArray(Arrays.asList(history.getLatitude()));
            JSONArray jsonArrayLon = new JSONArray(Arrays.asList(history.getLongitude()));
            valuesObject.put("latitude", jsonArrayLat);
            valuesObject.put("longitude", jsonArrayLon);

            list.put(valuesObject);

            for (int i=0; i<historyArray.size(); i++) {
                valuesObject = new JSONObject();

                valuesObject.put("start_time", historyArray.get(i).getStartTime());
                valuesObject.put("end_time", historyArray.get(i).getEndTime());
                valuesObject.put("duration", historyArray.get(i).getDuration());
                valuesObject.put("distance", historyArray.get(i).getDistance());
                valuesObject.put("avg_pace", historyArray.get(i).getAvgPace());
                jsonArrayLat = new JSONArray(Arrays.asList(historyArray.get(i).getLatitude()));
                jsonArrayLon = new JSONArray(Arrays.asList(historyArray.get(i).getLongitude()));
                valuesObject.put("latitude", jsonArrayLat);
                valuesObject.put("longitude", jsonArrayLon);

                list.put(valuesObject);
            }

            mainObject.accumulate("history", list);

            Log.i(TAG, mainObject.toString());


            // Overwrite History.json
            try {
                FileWriter writer = new FileWriter("History.json");
                writer.write(mainObject.toString());
            }
            catch (IOException e) {
                Log.i(TAG, "Error reading History.json");
                Log.i(TAG, e.toString());
            }

            return mainObject.toString();

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // Getter method for courses ArrayList
    public ArrayList<HistoryData> getHistoryData() {return historyArray;}
}
