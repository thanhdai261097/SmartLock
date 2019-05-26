package com.example.administrator.qrcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 23/05/2019.
 */

public class Fragment_CreateQR extends Fragment {
    public Button loginButton;
    public EditText cmndTextView, emailTextView;
    public   static  final  String SHARE_PREF_ID = "id";
    public  static final  String SHARE_PREF_EMAIL = "email";

    public static  final  String KEY_CMND = "key_cmnd";
    public static  final  String KEY_EMAIL = "key_email";
    public static final String KEY_ISLOCK = "key_isLock";
    public static  final  String KEY_PHONE = "key_phone";
    public static final String KEY_DATEEXP = "key_dateExp";
    public static final String KEY_NAME = "key_name";

    private String email = null;
    private String cmnd = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        getDataFromSharePreference();
        configureUI(view);
        setViewListener();

        return view;
    }

    private void configureUI(View view) {
        cmndTextView = view.findViewById(R.id.txtID);
        emailTextView = view.findViewById(R.id.txtEmail);
        loginButton = view.findViewById(R.id.btnCreate);

        if ( cmnd != null)
            cmndTextView.setText(cmnd);

        if (email != null)
            emailTextView.setText(email);
    }

    private void setViewListener() {
        loginButton.setOnClickListener(v -> {
            cmnd = cmndTextView.getText().toString().trim();
            email = emailTextView.getText().toString().trim();
            if(cmnd.isEmpty() || email.isEmpty()){
                Toast.makeText(getContext(), "Email or ID must not be empty!", Toast.LENGTH_LONG).show();
            }
            else {
                connectServer(cmnd,email);
            }
        });
    }
    private void getDataFromSharePreference(){
        SharedPreferences sp = this.getActivity().getSharedPreferences(SHARE_PREF_ID,Context.MODE_PRIVATE);
        cmnd = sp.getString(KEY_CMND,null);
        email = sp.getString(KEY_EMAIL,null);
    }

    public void saveUserToSharePreference(String mCmnd, String mEmail,String mName, String mPhone, Boolean mLock, String mDateExp) {

        SharedPreferences sp = this.getActivity().getSharedPreferences(SHARE_PREF_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor  edit = sp.edit();
        edit.putString(KEY_CMND,mCmnd);
        edit.putString(KEY_EMAIL,mEmail);
        edit.putString(KEY_PHONE,mPhone);
        edit.putBoolean(KEY_ISLOCK,mLock);
        edit.putString(KEY_DATEEXP,mDateExp);
        edit.putString(KEY_NAME,mName);
        edit.apply();

    }
    private  void connectServer(String cmnd, String email){

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder().add("cmnd",cmnd)
                .add("email", email);

        RequestBody reqBody = formBuilder.build();

        Request request = new Request.Builder().url(domain.ip + "/user/signin").post(reqBody).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(),"Something went wrong!", Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                if (jsonData.contains("error")) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(),"Invalid email or Identify", Toast.LENGTH_LONG).show());
                }
                else {
                    try {
                        JSONObject Jobject = new JSONObject(jsonData);
                        JSONObject data = Jobject.getJSONObject("data");

                        Boolean isLock = data.getBoolean("isLock");
                        String phone = data.getString("phone");
                        String dateExp = data.getString("date_exp");
                        String name = data.getString("name");

                        saveUserToSharePreference(cmnd,email,name, phone,isLock,dateExp);
                    } catch (JSONException e) {
                        Log.d("create", e.toString());
                    }

                    getActivity().runOnUiThread(() -> MainActivity.mViewPager.setCurrentItem(1));
                }
            }

        });
    }

}

