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
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 21/05/2019.
 */

public class Fragment_ScanQR extends Fragment {
    public Button btnLoad,btnScan;
    public static final int REQUEST_CODE_QR_SCAN = 100;
    public final String LOGTAG = "ScanYourQR";
    public ImageView imageView;
    public Bitmap bitmap;
    protected Uri imageUri;
    public String barcode = "wqd";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        configureUI(view);
        setViewListener();

        return view;
    }

    private void configureUI(View view) {
        btnLoad = view.findViewById(R.id.btnLoad);
        btnScan = view.findViewById(R.id.btnScan);
        imageView = view.findViewById(R.id.imageView);
        if(barcode!= null){
            try {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(barcode, BarcodeFormat.QR_CODE,300,300);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromCreateQRFragment(){
        SharedPreferences sp = this.getActivity().getSharedPreferences(Fragment_CreateQR.SHARE_PREF_ID,Context.MODE_PRIVATE);
        String cmnd = sp.getString(Fragment_CreateQR.KEY_ID,null);
        String email = sp.getString(Fragment_CreateQR.KEY_EMAIL,null);
        this.barcode = "http://172.16.4.96:3001/qrcode/generator/" +cmnd+ "/" + email;
    }

    private void setViewListener() {
        btnLoad.setOnClickListener(v -> openGallery());
        btnScan.setOnClickListener(v -> {
            getDataFromCreateQRFragment();
            try {
                Picasso.get().load(barcode).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        String code = scanQRImage(bitmap);
                        sendLockCodeToServer(code);
                        saveQrCodeToInteralMemory(bitmap);
                        imageView.setImageBitmap(bitmap);
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

        });
    }


    private void sendLockCodeToServer(String code){
        OkHttpClient client = new OkHttpClient();
        Log.d("scan", code);
        String lockURL = "http://172.16.4.96:3001/user/verify/"+ code;
        Request request = new Request.Builder().url(lockURL).get().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("scan",e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("scan", response.body().string());
            }
        });
    }

    private void saveQrCodeToInteralMemory(Bitmap mBitmap) {


        File mypath=new File(MediaStore.Images.Media.DATA,"qrcode.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 300, fos);
        } catch (Exception e) {
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

            // Do something with the bitmap
            imageView.setImageBitmap(bitmap);
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

}
