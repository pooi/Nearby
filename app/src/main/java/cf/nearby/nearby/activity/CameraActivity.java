package cf.nearby.nearby.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cf.nearby.nearby.BaseActivity;
import cf.nearby.nearby.Information;
import cf.nearby.nearby.R;
import cf.nearby.nearby.nurse.NurseRecordActivity;
import cf.nearby.nearby.util.AdditionalFunc;
import cf.nearby.nearby.util.ParsePHP;


public class CameraActivity extends BaseActivity{

    private MyHandler handler = new MyHandler();
    private final int MSG_MESSAGE_SHOW_PROGRESS = 500;
    private final int MSG_MESSAGE_HIDE_PROGRESS = 501;
    private final int MSG_MESSAGE_ERROR_DIALOG = 502;
    private final int MSG_MESSAGE_CHANGE_PROGRESS = 503;

    private CameraView cameraView;
    private CardView cv_takePhoto;
    private TextView tv_takePhoto;

    private Button saveBtn;
    private ImageView img;

    private MaterialDialog progressDialog;

    public static byte[] photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

//        photo = getIntent().getByteArrayExtra("photo");

        init();

//        checkPhoto();

    }

    private void init(){

        initProgressDialog();

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(NurseRecordActivity.UPDATE_PHOTO);
                finish();
            }
        });
        saveBtn = (Button)findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.putExtra("photo", photo);
                setResult(NurseRecordActivity.UPDATE_PHOTO);
                finish();
            }
        });

        cameraView = (CameraView)findViewById(R.id.cameraView);
        cv_takePhoto = (CardView) findViewById(R.id.cv_take_photo);
        tv_takePhoto = (TextView)findViewById(R.id.tv_take_photo);
        img = (ImageView)findViewById(R.id.img);

        cv_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(photo.length > 0){
                    photo = new byte[0];
                    checkPhoto();
                }else{
                    cameraView.captureImage();
                }
            }
        });

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    checkPhoto();
                    cameraView.start();
                }else{
                    finish();
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        // cameraview library has its own permission check method
        cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE);

        // invoke tensorflow inference when picture taken from camera
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(final byte[] picture) {
//                super.onPictureTaken(picture);

//                handler.sendMessage(handler.obtainMessage(MSG_MESSAGE_SHOW_PROGRESS));


//                Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
//                System.out.println(bitmap.getHeight());
                photo = picture;

                checkPhoto();

            }
        });

//        cameraView.start();

    }

    private void initProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
        progressDialog = new MaterialDialog.Builder(this)
                .content("잠시만 기다려주세요.")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .build();
    }

    private void checkPhoto(){

        if(photo.length > 0){

            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);

            img.setImageBitmap(bitmap);
            img.setVisibility(View.VISIBLE);
            cameraView.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
            tv_takePhoto.setText(R.string.take_photo_again_srt);

        }else{

            cameraView.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
            tv_takePhoto.setText(R.string.take_photo_srt);


        }

    }

    private class MyHandler extends Handler {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_MESSAGE_SHOW_PROGRESS:
                    progressDialog.show();
                    break;
                case MSG_MESSAGE_HIDE_PROGRESS:
                    progressDialog.hide();
                    break;
                case MSG_MESSAGE_CHANGE_PROGRESS:
                    progressDialog.setTitle("명소 정보를 불러오는 중입니다...");
                    break;
                case MSG_MESSAGE_ERROR_DIALOG:
                    new MaterialDialog.Builder(CameraActivity.this)
                            .title("실패")
                            .content("인식에 실패하였습니다.")
                            .positiveText("확인")
                            .show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        setResult(NurseRecordActivity.UPDATE_PHOTO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.stop();
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
