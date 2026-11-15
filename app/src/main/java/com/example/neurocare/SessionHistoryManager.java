package com.example.neurocare;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionHistoryManager {

    public static void saveSession(
            Context context,
            String gameName,
            int score,
            String result
    ) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "NeuroCareStats",
                        Context.MODE_PRIVATE
                );

        String historyData = preferences.getString(
                "sessionHistory",
                "[]"
        );

        try {

            JSONArray historyArray =
                    new JSONArray(historyData);

            JSONObject session = new JSONObject();

            session.put("gameName", gameName);
            session.put("score", score);
            session.put("result", result);

            String currentDate =
                    new SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                    ).format(new Date());

            session.put("date", currentDate);

            JSONArray updatedHistory = new JSONArray();

            updatedHistory.put(session);

            for (int i = 0; i < historyArray.length(); i++) {
                updatedHistory.put(historyArray.getJSONObject(i));
            }

            preferences.edit()
                    .putString(
                            "sessionHistory",
                            updatedHistory.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}