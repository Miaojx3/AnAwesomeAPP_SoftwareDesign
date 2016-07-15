package com.seu.magiccamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;

import com.seu.magiccamera.activity.AlbumActivity;
import com.seu.magiccamera.activity.CameraActivity;
import com.seu.magiccamera.activity.CodeActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    final int PHOTO_REQUEST_ALBUM = 1;
    private Bitmap bitmap;
    private Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA },
                            v.getId());
                } else {
                    startActivity(v.getId());
                }
            }
        });

        findViewById(R.id.button_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AlbumActivity.class));
            }
        });

        findViewById(R.id.button_code_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_REQUEST_ALBUM);*/
                startActivity(new Intent(MainActivity.this, CodeActivity.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                     int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(requestCode);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startActivity(int id) {
        switch (id) {
            case R.id.button_camera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.button_album:
                startActivity(new Intent(this, AlbumActivity.class));
                break;
            default:
                break;
        }
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PHOTO_REQUEST_ALBUM) {
            if (data != null) {
                Uri uri = data.getData();
                try{
                    if (bitmap != null) bitmap.recycle();
                    bitmap = BitmapFactory.decodeStream(MainActivity.this.getContentResolver().openInputStream(uri));
                    //bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                    intent.putExtra("ImageSource", "Album");
                    //intent.putExtra("Bitmap", new Parcelable[]{bitmap});

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                    byte [] bitmapByte =baos.toByteArray();
                    intent.putExtra("Bitmap", bitmapByte);

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}
