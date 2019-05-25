package com.example.administrator.qrcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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
    public TextView textView;
    public CardView btnCardView;
    public EditText txtID,txtEmail;
    public   static  final  String SHARE_PREF_ID = "id";
    public  static final  String SHARE_PREF_EMAIL = "email";
    public  static  final  String KEY_ID = "key_id";
    public  static  final  String KEY_EMAIL = "key_email";
    UserInterface userInterface;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        configureUI(view);
        disPlayName();

        btnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveName();
            }
        });
        return view;
    }

    private void configureUI(View view) {
        textView = (TextView)view.findViewById(R.id.textfont);
        txtID = (EditText)view.findViewById(R.id.txtID);
        txtEmail = (EditText)view.findViewById(R.id.txtEmail);
        btnCardView = (CardView) view.findViewById(R.id.btnCreate);
    }
    public void disPlayName(){
        SharedPreferences sp = this.getActivity().getSharedPreferences(SHARE_PREF_ID,Context.MODE_PRIVATE);
        String id = sp.getString(KEY_ID,null);
        String email = sp.getString(KEY_EMAIL,null);
        //String []separated;
        if(id!=null){
          //  separated = id.split("/");
           // txtID.setText(separated[0]);
           // txtEmail.setText(separated[1]);
            txtID.setText(id);
        }
        if(email!=null){
            txtEmail.setText(email);
        }
    }
    public void saveName() {
        String id = txtID.getText().toString().trim();
        //id += "/";
       // id += txtEmail.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();

        if(id.isEmpty() || email.isEmpty()){
            if(id.isEmpty()){
                txtID.setText("");

            }
            if(email.isEmpty()){
                txtEmail.setText("");
            }
        }
        else {
            connectServer(id,email);
        }
        SharedPreferences sp = this.getActivity().getSharedPreferences(SHARE_PREF_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor  edit = sp.edit();
        edit.putString(KEY_ID,id);
        edit.putString(KEY_EMAIL,email);
        edit.apply();
        txtID.setText("");
        txtEmail.setText("");
        disPlayName();

    }
    private  void connectServer(String cmnd, String email){

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder().add("cmnd",cmnd)
                .add("email", email);

        RequestBody reqBody = formBuilder.build();

        Request request = new Request.Builder().url("http://172.16.4.96:3001/user/signin").post(reqBody).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("connect", "failed" + e );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.body().string().contains("error")) {
                    Log.d("connect", "failed"  );
                }
                else {
//                    Fragment_ScanQR.barcode = "http://172.16.4.96:3001/qrcode/generator/" +cmnd+ "/" + email;
                    getActivity().runOnUiThread(() ->
                            MainActivity.mViewPager.setCurrentItem(1)
                    );
                }
            }

        });
    }

    public interface UserInterface {

        // Đây là phương thức trừu tượng
        // phương thức trừu tượng của Interface không cần khai báo từ khóa abstract và public
        void updateUser();

    }
}
