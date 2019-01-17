package com.example.hdh.smgproject.Activity.for_Pop;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.example.hdh.smgproject.Fragment.for_User.PreviouslyScheduleFragment;
import com.example.hdh.smgproject.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by hwangdahyeon on 2018. 5. 21..
 */

public class PopFeedBackOfUser extends Activity {

    TextView feedbackContentText;

   @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_feedback_of_user);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.85));
       feedbackContentText = (TextView) findViewById(R.id.feedbackContentText);

       new BackGroundTask().execute();
    }

    class BackGroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            try {
                target = "http://kjg123kg.cafe24.com/FeedBackContent_SYG.php?ptID=" + URLEncoder.encode(PreviouslyScheduleFragment.ptID + "", "UTF-8");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String content = "";

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    content = object.getString("FeedBack").toString();
                    count++;
                }

                feedbackContentText.setText(content);

                if(content.equals("")){
                    feedbackContentText.setText("해당 트레이너가 아직 작성을 하지 않았습니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}