package com.example.hdh.smgproject.Activity.for_CEO;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hdh.smgproject.Activity.LoginActivity;
import com.example.hdh.smgproject.Activity.for_Trainer.TRregisterActivity;
import com.example.hdh.smgproject.R;
import com.example.hdh.smgproject.Class.User;
import com.example.hdh.smgproject.Class.UserInfoChange;
import com.example.hdh.smgproject.Adapter.for_User.UserListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CEOMainActivity extends AppCompatActivity {

    private ListView listView;
    static public UserListAdapter adapter;
    private List<User> userList;
    private List<User> saveList;

    static public Boolean CEOCHECk = true;

    public static Context CONTEXT;

    private ImageView logoutImage;



    //userInfoChange의 변수를 받아오기 위함.
    UserInfoChange userInfoChange = new UserInfoChange();

    public CEOMainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceomain);

        CONTEXT = this;

        //트레이너회원가입 버튼 초기화.
        TextView TRregisterButton = (TextView) findViewById(R.id.TRregisterButton);
        //트레이너회원가입 버튼 클릭리스너
        TRregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(CEOMainActivity.this , TRregisterActivity.class);    //LoginActivity에서 RegisterActivity로 화면전환
                CEOMainActivity.this.startActivity(registerIntent);
            }
        });

        logoutImage = (ImageView) findViewById(R.id.logout);

        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sf = getSharedPreferences("LoginSaveData", 0);
                SharedPreferences.Editor editor = sf.edit();//저장하려면 editor가 필요
                editor.putBoolean("Value", false);
                editor.commit();
                Intent intent = new Intent(CEOMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        //리스트뷰 초기화
        listView = (ListView) findViewById(R.id.listView);

        userList = new ArrayList<User>();
        saveList = new ArrayList<User>();
        adapter = new UserListAdapter(getApplicationContext(), userList , this , saveList);
        listView.setAdapter(adapter);



        //어드민으로 접속 종료시 admin권한을 해제
        userInfoChange.adminCheck = false;



        final EditText search = (EditText) findViewById(R.id.searchIDText);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //리스트뷰 갱신
        new BackGroundTaskofUserList().execute();
    }

    public void searchUser(String search) {
        userList.clear();
        for( int i = 0 ; i < saveList.size(); i++){
            if (saveList.get(i).getUserID().contains(search)){
                userList.add(saveList.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    class BackGroundTaskofUserList extends AsyncTask<Void, Void, String> {
        String target;
        TextView textView;

        @Override
        protected void onPreExecute() {
            target = "http://kjg123kg.cafe24.com/UserList_SYG.php";
            textView = (TextView) findViewById(R.id.Practice);

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
                String userID =  "" , userPassword  = "", userName = "", userEmail = "",userGender = "", userHeight = "", userWeight = "", userAge = "" ;
                int userPT = 0;


                while (count < jsonArray.length()) {


                    JSONObject object = jsonArray.getJSONObject(count);

                    userID = object.getString("userID");
                    userPassword = object.getString("userPassword");
                    userName = object.getString("userName");
                    userEmail = object.getString("userEmail");
                    userGender = object.getString("userGender");
                    userHeight = object.getString("userHeight");
                    userWeight  = object.getString("userWeight");
                    userAge = object.getString("userAge") + "세";
                    userPT = object.getInt("userPT");

                    if (userName.equals("")){
                        userName = "정보없음";
                    }
                    if( userGender.equals("")){
                        userGender = "정보없음";
                    }
                    if( userAge.equals("세")){
                        userAge = "정보없음";
                    }

                    User user = new User(userID , userPassword , userName, userEmail ,userGender, userHeight, userWeight, userAge, userPT);
                    if(!userID.contains("admin")) {
                        userList.add(user);
                        saveList.add(user);
                    }
                    count++;
                }


            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
