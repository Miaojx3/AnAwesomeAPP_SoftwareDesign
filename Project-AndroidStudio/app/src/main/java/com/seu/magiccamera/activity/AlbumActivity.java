package com.seu.magiccamera.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.utils.Constants;
import com.seu.magiccamera.view.edit.ImageEditFragment;
import com.seu.magiccamera.view.edit.ImageEditFragment.onHideListener;
import com.seu.magiccamera.view.edit.add.ImageEditAddView;
import com.seu.magiccamera.view.edit.adjust.ImageEditAdjustView;
import com.seu.magiccamera.view.edit.beauty.ImageEditBeautyView;
import com.seu.magiccamera.view.edit.filter.ImageEditFilterView;
import com.seu.magiccamera.view.edit.frame.ImageEditFrameView;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.display.MagicImageDisplay;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.helper.SavePictureTask.OnPictureSaveListener;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;
import com.seu.magicfilter.widget.MagicImageView;

public class AlbumActivity extends Activity {

    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;
    private final int REQUEST_PICK_IMAGE = 1;

    private RadioGroup mRadioGroup;
    private Fragment[] mFragments;
    private int mFragmentTag = -1;
    private MagicImageDisplay mMagicImageDisplay;

    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        MagicEngine.Builder builder = new MagicEngine.Builder();
        magicEngine = builder
                .build((MagicImageView)findViewById(R.id.glsurfaceview_image));

        MagicImageView glSurfaceView = (MagicImageView) findViewById(R.id.glsurfaceview_image);
        mMagicImageDisplay = new MagicImageDisplay(this, glSurfaceView);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_PICK_IMAGE);

        initView();
        initFragments();
        initRadioButtons();
    }

    private void initView(){
        mFilterLayout = (LinearLayout)findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);

        //findViewById(R.id.image_edit_adjust).setOnClickListener(btn_listener);
        findViewById(R.id.image_edit_beauty).setOnClickListener(btn_listener);
        findViewById(R.id.image_edit_adds).setOnClickListener(btn_listener);
        findViewById(R.id.image_edit_frame).setOnClickListener(btn_listener);

        findViewById(R.id.image_edit_filter).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilters();
            }
        });

        findViewById(R.id.btn_camera_closefilter).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFilters();
            }
        });

        findViewById(R.id.image_edit_save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //magicEngine.savePicture(getOutputMediaFile(),null);
                MagicImageView magicImageView = (MagicImageView)findViewById(R.id.glsurfaceview_image);
                Bitmap bitmap = magicImageView.getBitmap();

                saveBitmap(bitmap);
                Toast.makeText(AlbumActivity.this, "图片已保存到相册", Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.image_edit_frame).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MagicImageView magicImageView = (MagicImageView)findViewById(R.id.glsurfaceview_image);
                Bitmap bitmap = magicImageView.getBitmap();
                Bitmap frame = BitmapFactory.decodeResource(getResources(), R.drawable.i_frame_blue);

                //Bitmap bitmap_new = addBigFrame(bitmap, frame);
                Bitmap bitmap_new = toRoundCorner(bitmap, 50);
                magicImageView.setImageBitmap(bitmap_new);
            }
        });

        /*findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_mode).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        MagicImageView imageView = (MagicImageView)findViewById(R.id.glsurfaceview_image);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3;
        imageView.setLayoutParams(params);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap addBigFrame(Bitmap bm, Bitmap frame) {
        Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(bm, 0, 0, paint);
        frame = Bitmap.createScaledBitmap(frame, bm.getWidth(), bm.getHeight(), true);
        canvas.drawBitmap(frame, 0, 0, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBitmap;
    }

    public static Bitmap handleImage(Bitmap bm, int saturation, int hue, int lum) {
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ColorMatrix mLightnessMatrix = new ColorMatrix();
        ColorMatrix mSaturationMatrix = new ColorMatrix();
        ColorMatrix mHueMatrix = new ColorMatrix();
        ColorMatrix mAllMatrix = new ColorMatrix();
        float mSaturationValue = saturation * 1.0F / 127;
        float mHueValue = hue * 1.0F / 127;
        float mLumValue = (lum - 127) * 1.0F / 127 * 180;
        mHueMatrix.reset();
        mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);

        mSaturationMatrix.reset();
        mSaturationMatrix.setSaturation(mSaturationValue);
        mLightnessMatrix.reset();

        mLightnessMatrix.setRotate(0, mLumValue);
        mLightnessMatrix.setRotate(1, mLumValue);
        mLightnessMatrix.setRotate(2, mLumValue);

        mAllMatrix.reset();
        mAllMatrix.postConcat(mHueMatrix);
        mAllMatrix.postConcat(mSaturationMatrix);
        mAllMatrix.postConcat(mLightnessMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
        canvas.drawBitmap(bm, 0, 0, paint);
        return bmp;
    }

    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener(){

        @Override
        public void onFilterChanged(MagicFilterType filterType) {
            magicEngine.setFilter(filterType);
        }
    };

    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                /*case R.id.btn_camera_shutter:
                    if (PermissionChecker.checkSelfPermission(CameraActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(CameraActivity.this,
                                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                v.getId());
                    } else {
                        if(mode == MODE_PIC)
                            takePhoto();
                        else
                            takeVideo();
                    }
                    break;*/
                case R.id.image_edit_beauty:
                    new AlertDialog.Builder(AlbumActivity.this)
                            .setSingleChoiceItems(new String[] { "关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            magicEngine.setBeautyLevel(which);
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("取消", null)
                            .show();
                    break;
                case R.id.image_edit_frame:

            }
        }
    };

    private void initFragments(){
        mFragments = new Fragment[5];
        ImageEditBeautyView mImageEditBeautyView = new ImageEditBeautyView(this, mMagicImageDisplay);
        mImageEditBeautyView.setOnHideListener(mOnHideListener);
        mFragments[0] = mImageEditBeautyView;
        ImageEditAddView mImageEditAddsView = new ImageEditAddView(this, mMagicImageDisplay);
        mImageEditAddsView.setOnHideListener(mOnHideListener);
        mFragments[1] = mImageEditAddsView;
        ImageEditAdjustView mImageEditAdjustView = new ImageEditAdjustView(this, mMagicImageDisplay);
        mImageEditAdjustView.setOnHideListener(mOnHideListener);
        mFragments[2] = mImageEditAdjustView;
        ImageEditFilterView mImageEditFilterView = new ImageEditFilterView(this, mMagicImageDisplay);
        mImageEditFilterView.setOnHideListener(mOnHideListener);
        mFragments[3] = mImageEditFilterView;
        ImageEditFrameView mImageEditFrameView = new ImageEditFrameView(this, mMagicImageDisplay);
        mImageEditFrameView.setOnHideListener(mOnHideListener);
        mFragments[4] = mImageEditFrameView;
    }

    private void initRadioButtons(){
        mRadioGroup = (RadioGroup)findViewById(R.id.image_edit_radiogroup);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                switch (checkedId) {
                    case R.id.image_edit_adjust:
                        if(!mFragments[2].isAdded())
                            getFragmentManager().beginTransaction().add(R.id.image_edit_fragment_container, mFragments[2])
                                    .show(mFragments[2]).commit();
                        else
                            getFragmentManager().beginTransaction().show(mFragments[2]).commit();
                        mFragmentTag = 2;
                        break;
                    /*case R.id.image_edit_filter:
                        if(!mFragments[3].isAdded())
                            getFragmentManager().beginTransaction().add(R.id.image_edit_fragment_container, mFragments[3])
                                    .show(mFragments[3]).commit();
                        else
                            getFragmentManager().beginTransaction().show(mFragments[3]).commit();
                        mFragmentTag = 3;
                        break;*/
                    /*case R.id.image_edit_frame:
                        if(!mFragments[4].isAdded())
                            getFragmentManager().beginTransaction().add(R.id.image_edit_fragment_container, mFragments[4])
                                    .show(mFragments[4]).commit();
                        else
                            getFragmentManager().beginTransaction().show(mFragments[4]).commit();
                        mFragmentTag = 4;
                        break;
                    case R.id.image_edit_adds:
                        if(!mFragments[1].isAdded())
                            getFragmentManager().beginTransaction().add(R.id.image_edit_fragment_container, mFragments[1])
                                    .show(mFragments[1]).commit();
                        else
                            getFragmentManager().beginTransaction().show(mFragments[1]).commit();
                        mFragmentTag = 1;
                        break;*/
                    /*case R.id.image_edit_beauty:
                        if(!mFragments[0].isAdded())
                            getFragmentManager().beginTransaction().add(R.id.image_edit_fragment_container, mFragments[0])
                                    .show(mFragments[0]).commit();
                        else
                            getFragmentManager().beginTransaction().show(mFragments[0]).commit();
                        mFragmentTag = 0;
                        break;*/
                    default:
                        if(mFragmentTag != -1)
                            getFragmentManager().beginTransaction()
                                    .hide(mFragments[mFragmentTag])
                                    .commit();
                        mFragmentTag = -1;
                        break;
                }
            }
        });
    }

    private onHideListener mOnHideListener = new onHideListener() {

        @Override
        public void onHide() {
            mRadioGroup.check(View.NO_ID);
        }
    };




    private void showFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                //findViewById(R.id.btn_camera_shutter).setClickable(false);
                mFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private void hideFilters(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0 ,  mFilterLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                //findViewById(R.id.btn_camera_shutter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                //findViewById(R.id.btn_camera_shutter).setClickable(true);
            }
        });
        animator.start();
    }

    public File getOutputMediaFile() {
        /*File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MagicCamera");*/
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "AnAwesomeApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public void saveBitmap(Bitmap bitmap) {
        File f = getOutputMediaFile();
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                            inputStream = AlbumActivity.this.getContentResolver().openInputStream(mUri);
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        MagicImageView imageView = (MagicImageView)findViewById(R.id.glsurfaceview_image);
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