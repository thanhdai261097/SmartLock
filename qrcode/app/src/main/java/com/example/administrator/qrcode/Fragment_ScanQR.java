package com.example.administrator.qrcode;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.administrator.qrcode.lib.HandleDateTime;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 21/05/2019.
 */

public class Fragment_ScanQR extends Fragment implements UserInterface {
    public Button btnLoad,btnScan;
    public TextView nameTextView, emailTextView, phoneTextView, cmndTextView, dateExpTextView;
    public Bitmap bitmap;
    public LottieAnimationView lockLottieView;

    public static final int REQUEST_CODE_QR_SCAN = 100;
    protected Uri imageUri;

    public String barcode = "wqd";

    private String email = null;
    private String phone = null;
    private String name = null;
    private String cmnd = null;
    private String dateExp = null;
    private boolean isLock = false;

    private boolean isSendingCode = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        getDataFromSharePreference();
        configureUI(view);
        setViewListener();

        displayUserProfile();
        return view;
    }

    private void configureUI(View view) {
        btnLoad = view.findViewById(R.id.btnLoad);
        btnScan = view.findViewById(R.id.btnScan);

        cmndTextView = view.findViewById(R.id.cmnd_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        nameTextView = view.findViewById(R.id.name_text_view);
        phoneTextView = view.findViewById(R.id.phone_text_view);
        dateExpTextView = view.findViewById(R.id.date_exp_text_view);


        lockLottieView = view.findViewById(R.id.lock_lottie_view);
        lockLottieView.setSpeed(1.5f);
        lockLottieView.useHardwareAcceleration(true);

        MainActivity main = (MainActivity) getActivity();
        main.setUserInterface(this);

        getDataFromActivity();

    }

    private void getDataFromActivity() {
        MainActivity main = (MainActivity) getActivity();
        isLock = main.getIsLock();
    }

    private void getDataFromSharePreference(){
        SharedPreferences sp = this.getActivity().getSharedPreferences(Fragment_CreateQR.SHARE_PREF_ID,Context.MODE_PRIVATE);

        cmnd = sp.getString(Fragment_CreateQR.KEY_CMND,null);
        email = sp.getString(Fragment_CreateQR.KEY_EMAIL,null);
        dateExp = sp.getString(Fragment_CreateQR.KEY_DATEEXP,null);
        phone = sp.getString(Fragment_CreateQR.KEY_PHONE,null);
        name = sp.getString(Fragment_CreateQR.KEY_NAME,null);
        isLock = sp.getBoolean(Fragment_CreateQR.KEY_ISLOCK,true);

        this.barcode = domain.ip + "/qrcode/generator/" +cmnd+ "/" + email;
    }

    private void displayUserProfile() {
        nameTextView.setText(name);
        phoneTextView.setText(phone);
        emailTextView.setText(email);
        cmndTextView.setText(cmnd);

        String timeDate = HandleDateTime.getInstance().milisToDate(Long.parseLong(dateExp), "dd/MM/yyyy hh:mm");
        dateExpTextView.setText(timeDate);

        if (!isLock)
            lockLottieView.setFrame(120);
        else
            lockLottieView.setFrame(0);
    }
    private void setViewListener() {
        btnLoad.setOnClickListener(v -> openGallery());
        btnScan.setOnClickListener(v -> {
            getDataFromSharePreference();
            if ( !lockLottieView.isAnimating() || !isSendingCode) {
                isSendingCode = true;
                try {
                    Picasso.get().load(this.barcode).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            String code = scanQRImage(bitmap);
                            sendLockCodeToServer(code);
                            saveQrCodeToInteralMemory(bitmap);

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                }
                catch (Exception e){
                    Log.e("QRcode","Error QRcode",e);
                }
            }


        });
    }

    private void sendLockCodeToServer(String code){
        OkHttpClient client = new OkHttpClient();
        Log.d("scan", code);
        String lockURL = domain.ip + "/user/verify/"+ code;
        Request request = new Request.Builder().url(lockURL).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("scan",e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                getActivity().runOnUiThread(() ->handleLockLottieAnimation());
                isSendingCode = false;
            }
        });
    }

    private void saveQrCodeToInteralMemory(Bitmap mBitmap) {

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "qrcode";
        File outputDir= new File(path);
        outputDir.mkdirs();
        File newFile = new File(path + File.separator + "qrcode.jpg");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newFile);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void openGallery() {
        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,REQUEST_CODE_QR_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==  Activity.RESULT_OK &&requestCode ==  REQUEST_CODE_QR_SCAN ){
            imageUri = data.getData();

            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(imageUri, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(imagePath, options);

            String code = scanQRImage(bitmap);
            sendLockCodeToServer(code);
            // Do something with the bitmap
        }

    }

    public static String scanQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        }
        catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
        }
        return contents;
    }

    private void handleLockLottieAnimation(){
//        lockLottieView.apply {
//            speed = -speed
//            setMinAndMaxFrame(0, 30)
//            if (!isAnimating) {
//                playAnimation()
//            }
//        }
        float speed = -lockLottieView.getSpeed();
        lockLottieView.setSpeed(speed);
        lockLottieView.playAnimation();
    }

    @Override
    public void update() {
        getDataFromSharePreference();
        displayUserProfile();
    }
}
