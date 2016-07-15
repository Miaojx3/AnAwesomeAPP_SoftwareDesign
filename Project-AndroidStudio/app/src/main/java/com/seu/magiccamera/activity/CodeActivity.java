package com.seu.magiccamera.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.seu.magiccamera.code.SVD;
import com.seu.magiccamera.code.Wheel_Switch;
import com.seu.magiccamera.code.ncm;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.display.MagicImageDisplay;
import com.seu.magicfilter.widget.MagicImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Jama.Matrix;

/**
 * Created by kouseishouganzhoushizenminamixianxianjoushinseigentanleijou on 16/7/15.
 */
public class CodeActivity extends AppCompatActivity {

    MagicImageView imageView;
    Button codeBtn, decodeBtn;
    Bitmap bitmap;
    Bitmap result;
    int[][] rChannel;
    private MagicEngine magicEngine;
    private MagicImageDisplay mMagicImageDisplay;

    final int REQUEST_PICK_IMAGE = 1;

    private static final File tempFile = new File(Environment.getExternalStorageDirectory()
            + "/MySecret_TempFile.jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        codeBtn = (Button) findViewById(R.id.codeBtn);
        decodeBtn = (Button) findViewById(R.id.decodeBtn);

        imageView = (MagicImageView) findViewById(R.id.imageView);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder
                .build((MagicImageView)findViewById(R.id.imageView));

        MagicImageView glSurfaceView = (MagicImageView) findViewById(R.id.imageView);
        mMagicImageDisplay = new MagicImageDisplay(this, glSurfaceView);

        /*Intent intent = getIntent();
        if (intent != null) {
            String imageResource = intent.getStringExtra("ImageSource");
            if (imageResource.equals("Album")) {
                byte [] bis = intent.getByteArrayExtra("Bitmap");
                bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
                imageView.setImageBitmap(bitmap);
            } else if (imageResource.equals("Camera")) {
                //byte [] bis=intent.getByteArrayExtra("Bitmap");
                //Bitmap bitmap= BitmapFactory.decodeByteArray(bis, 0, bis.length);
                String filePath = intent.getStringExtra("FilePath");
                bitmap = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(bitmap);
            }
        }*/
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);

        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText inputServer = new EditText(CodeActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(CodeActivity.this);
                builder.setTitle("请输入要加密的文字");
                builder.setIcon(R.drawable.code_image);
                builder.setView(inputServer);
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // get the input
                        String inputStr = inputServer.getText().toString();
                        // compute the md5 of input
                        String md5Str = getMD5(inputStr);
                        // hide the information in the image
                        result = codeBySingularValue(md5Str, inputStr, 20, 8, 8);
                        imageView.setImageBitmap(result);
                    }
                });
                builder.create().show();

            }
        });

        decodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decodeBySingularValue(20, 8, 8);
            }
        });
    }

    public String decodeBySingularValue(int T, int block_h, int block_w) {
        int w = result.getWidth();
        int h = result.getHeight();

        int count = 0;
        // get the R channel
        /*int[][] rChannel = new int[w][h];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = result.getPixel(j,i);
                rChannel[j][i] = Color.red(color);
                count++;
                if (count < 6) {
                    Log.d("r", ""+rChannel[j][i]);
                }
            }
        }*/

        // get the information
        int count1 = 0;
        int count2 = 0;
        int len = 0;

        String header = "";
        String info = "";
        String md5="", key="", length="";

        for (int i = 0; i < h/block_h; i++) {

            if (len != 0 && count2 > len) {
                break;
            }

            for (int j = 0; j < w/block_w; j++) {

                if (len != 0 && count2 > len) {
                    break;
                }

                double[][] block = new double[block_h][block_w];
                for (int p = 0; p < block_h; p++) {
                    for (int q = 0; q < block_w; q++) {
                        block[p][q] = rChannel[(h/block_h*p)+i][(w/block_w*q)+j];
                    }
                }
                Matrix B = new Matrix(block);
                SVD svd = new SVD(B);
                Matrix S = svd.getS();
                double lam_max = S.get(0,0);

                if (lam_max % (2*T) >= T) {
                    if (count1 < 524) {
                        header += '0';
                    } else {
                        info += '0';
                    }

                } else {
                    if (count1 < 524) {
                        header += '1';
                    } else {
                        info += '1';
                    }
                }

                if (count1 < 524 && count1 != 523) {
                    count1++;
                } else if (count1 == 523) {
                    md5 = header.substring(0, 256);
                    //md5 = bitToString(md5);
                    key = header.substring(256, 512);
                    length = header.substring(512, 524);
                    len = Integer.parseInt(length, 2);
                    Log.d("md5", ""+md5.length());
                    Log.d("md5xx", md5);
                    Log.d("key", ""+key.length());
                    Log.d("length", ""+length.length());
                    Log.d("length2", length);
                    Log.d("len", ""+len);
                    Log.d("header", ""+header.length());
                    count1++;
                }
                if (len != 0 && count2 <= len) {
                    count2++;
                }
            }
        }

        info = bitToString(info);

        ncm my_ncm = new ncm();
        Wheel_Switch wheel_switch = new Wheel_Switch();
        char[] ke = new char[] { 'a', 'b', 'c', 'd' };
        char[] kd = wheel_switch.gen_kd(ke, info);

        double x0 = 0.566;
        double r = 3.588;
        char[] P_get1 = wheel_switch.ws_de(info, kd, x0, r, 8);
        String P_get1_ = String.valueOf(P_get1);
        char[] P_get = my_ncm.ncm_de(P_get1_, 8, 1.1, 6, 0.5432106789717177, 1, 1.1, 6.001, 0.5432106789717177, 1);
        md5 = bitToString(md5);
        String info_ = String.valueOf(P_get);

        if (getMD5(info_).equals(md5)) {
            TextView tv = new TextView(CodeActivity.this);
            tv.setText(info_);

            AlertDialog.Builder builder = new AlertDialog.Builder(CodeActivity.this);
            builder.setTitle("解密后的文字");
            builder.setIcon(R.drawable.code_image);
            builder.setView(tv);
            builder.setNegativeButton("返回", null);
            builder.create().show();
        } else {
            Toast.makeText(CodeActivity.this, "信息已被破坏", Toast.LENGTH_SHORT).show();
        }

        return info_;
    }

    public Bitmap codeBySingularValue(String md5Str, String info, int T, int block_h, int block_w) {

        // get the bits
        String bits1 = stringToBit(md5Str);
        Log.d("md5xx", bits1);

        ncm my_ncm = new ncm();
        char[] M = my_ncm.ncm_en(info, 8, 1.1, 6, 0.5432106789717177, 1, 1.1, 6.001, 0.5432106789717177, 1);
        String M_ = String.valueOf(M);
        Wheel_Switch wheel_switch = new Wheel_Switch();
        char[] ke = new char[] { 'a', 'b', 'c', 'd' };
        char[] kd = wheel_switch.gen_kd(ke, M_);

        double x0 = 0.566;
        double r = 3.588;
        char[] M1 = wheel_switch.ws_en(M_, kd, x0, r, 8);



        String M1_ = String.valueOf(M1);


        String bits4  = stringToBit(M1_);

        String bits3 = "";
        int bits4_length = bits4.length();
        String temp = Integer.toBinaryString(bits4_length);
        if (temp.length() == 4) bits3 = "00000000"+temp;
        if (temp.length() == 5) bits3 = "0000000"+temp;
        if (temp.length() == 6) bits3 = "000000" +temp;
        if (temp.length() == 7) bits3 = "00000"+temp;
        if (temp.length() == 8) bits3 = "0000" +temp;
        if (temp.length() == 9) bits3 = "000" +temp;
        if (temp.length() == 8) bits3 = "00" +temp;
        if (temp.length() == 9) bits3 = "0" +temp;


        String bits2 = bits1;
        Log.d("length", temp);

        String bits = bits1 + bits2 + bits3 + bits4;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        rChannel = new int[w][h];
        int[][] gChannel = new int[w][h];
        int[][] bChannel = new int[w][h];
        //int[][] alphaChannel = new int[w][h];

        int[] arrayColor = new int[w*h];

        //int count = 0;
        // get the R channel
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int color = bitmap.getPixel(j,i);
                rChannel[j][i] = Color.red(color);
                gChannel[j][i] = Color.green(color);
                bChannel[j][i] = Color.blue(color);
                //arrayColor[count] = Color.rgb(rChannel[j][i], 0, 0);
                //count++;
            }
        }

        int bit_count = 0;
        // hide the information
        for (int i = 0; i < h/block_h; i++) {
            if (bit_count == bits.length()) break;
            for (int j = 0; j < w/block_w; j++) {
                if (bit_count == bits.length()) break;
                double[][] block = new double[block_h][block_w];
                for (int p = 0; p < block_h; p++) {
                    for (int q = 0; q < block_w; q++) {
                        block[p][q] = rChannel[(h/block_h*p)+i][(w/block_w*q)+j];
                    }
                }

                Matrix B = new Matrix(block);
                SVD svd = new SVD(B);
                Matrix S = svd.getS();
                double lam_max = S.get(0,0);

                Matrix U = svd.getU();
                Matrix V = svd.getV();

                // Situation 1
                if ((lam_max % T <= T/2) && (Math.ceil(lam_max/T) % 2) == 1) {
                    if (bits.charAt(i*(w/block_w)+j) == '0') {
                        lam_max = lam_max - (lam_max % T) - T/2;
                    } else {
                        lam_max = lam_max - (lam_max % T) + T/2;
                    }
                }
                // Situation 2
                else if ((lam_max % T > T /2) && (Math.ceil(lam_max/T) % 2) == 1 ) {
                    if (bits.charAt(i*(w/block_w)+j) == '0') {
                        lam_max = lam_max - (lam_max % T) + 3*T/2;
                    } else {
                        lam_max = lam_max - (lam_max % T) + T/2;
                    }
                }
                // Situation 3
                else if ((lam_max % T <= T/2) && (Math.ceil(lam_max/T)% 2 == 0)) {
                    if (bits.charAt(i*(w/block_w)+j) == '0') {
                        lam_max = lam_max - (lam_max % T) + T/2;
                    } else {
                        lam_max = lam_max - (lam_max % T) - T/2;
                    }
                }
                // Situation 4
                else if ((lam_max % T > T/2) && (Math.ceil(lam_max/T) % 2 == 0)) {
                    if (bits.charAt(i*(w/block_w)+j) == '0') {
                        lam_max = lam_max - (lam_max % T) + T/2;
                    } else {
                        lam_max = lam_max - (lam_max % T) + 3*T/2;
                    }
                }

                S.set(0,0,lam_max);

                // recover
                Matrix A = U.times(S).times(V.transpose());
                for (int p = 0; p < block_h; p++) {
                    for (int q = 0; q < block_w; q++) {
                        rChannel[h/block_h*p+i][w/block_w*q+j] = (int)A.get(p,q);
                        String temp1 = "" + rChannel[h/block_h*p+i][w/block_w*q+j];
                        //Log.d("rchannel", temp1);
                    }
                }
                bit_count++;
            }
        }

        int count = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                arrayColor[count] = Color.rgb(rChannel[j][i], gChannel[j][i], bChannel[j][i]);
                count++;
                /*if (count < 6) {
                    Log.d("r", ""+rChannel[j][i]);
                }*/
            }
        }

        Bitmap newBitmap = Bitmap.createBitmap(arrayColor, w, h, Bitmap.Config.RGB_565);
        saveBitmap(newBitmap);

        return newBitmap;

    }

    private void saveBitmap(Bitmap save_bitmap) {
        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        save_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(tempFile);
        intent.setData(uri);
        CodeActivity.this.sendBroadcast(intent);
    }



    // String to bit
    public static  String stringToBit(String s) {
        // string to byte to bit
        byte[] s_byte = s.getBytes();
        String s_bit = "";

        for (int i = 0; i < s_byte.length; i++) {
            s_bit += byteToBit(s_byte[i]);
        }

        return s_bit;
    }

    // bit to String
    public static String bitToString(String s) {
        // bit to byte to string
        int a = s.length() / 8;
        byte[] info_recover = new byte[a];
        for (int i = 0, j = 0; i < s.length(); i+=8, j++) {
            info_recover[j] = BitToByte(s.substring(i, i+8));
        }

        String info_recover_str = new String(info_recover);
        return info_recover_str;
    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" +(byte)((b >> 7) & 0x1) +
                (byte)((b >> 6) & 0x1) +
                (byte)((b >> 5) & 0x1) +
                (byte)((b >> 4) & 0x1) +
                (byte)((b >> 3) & 0x1) +
                (byte)((b >> 2) & 0x1) +
                (byte)((b >> 1) & 0x1) +
                (byte)((b >> 0) & 0x1);
    }

    /**
     * Bit转Byte
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }


    public String getMD5(String info)
    {
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++)
            {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1)
                {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                }
                else
                {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            return "";
        }
        catch (UnsupportedEncodingException e)
        {
            return "";
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri mUri = data.getData();
                        InputStream inputStream;
                        if (mUri.getScheme().startsWith("http") || mUri.getScheme().startsWith("https")) {
                            inputStream = new URL(mUri.toString()).openStream();
                        } else {
                            inputStream = CodeActivity.this.getContentResolver().openInputStream(mUri);
                        }
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        MagicImageView imageView = (MagicImageView)findViewById(R.id.imageView);
                        imageView.setImageBitmap(bitmap);
                        //mMagicImageDisplay.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    finish();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
