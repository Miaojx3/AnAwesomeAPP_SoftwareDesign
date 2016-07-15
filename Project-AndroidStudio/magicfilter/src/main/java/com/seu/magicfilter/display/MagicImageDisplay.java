package com.seu.magicfilter.display;

/**
 * Created by kouseishouganzhoushizenminamixianxianjoushinseigentanleijou on 16/7/14.
 */
import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;

import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.MagicSDK;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.TextureRotationUtil;
import com.seu.magicfilter.helper.SavePictureTask.OnPictureSaveListener;
import com.seu.magicfilter.widget.MagicImageView;

public class MagicImageDisplay extends MagicDisplay{

    private final GPUImageFilter mImageInput;

    private final MagicSDK mMagicSDK;

    private Bitmap mOriginBitmap;

    private boolean mIsSaving = false;

    @SuppressLint("HandlerLeak")
    private class MagicSDKHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MagicSDK.MESSAGE_OPERATION_END:
                    refreshDisplay();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    }

    public MagicImageDisplay(Context context, MagicImageView glSurfaceView){
        super(context, glSurfaceView);
        mImageInput = new GPUImageFilter();
        mMagicSDK = MagicSDK.getInstance();
        mMagicSDK.setMagicSDKHandler(new MagicSDKHandler());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0,0,0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        MagicParams.initMagicFilterParam(gl);
        mImageInput.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        adjustImageDisplaySize();
        onFilterChanged();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if(mTextureId == OpenGlUtils.NO_TEXTURE)
            mTextureId = OpenGlUtils.loadTexture(mMagicSDK.getBitmap(), OpenGlUtils.NO_TEXTURE);
        if(mFilters == null){
            mImageInput.onDrawFrame(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
        }else{
            mFilters.onDrawFrame(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled())
            return;
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mOriginBitmap = bitmap;
        adjustImageDisplaySize();
        mMagicSDK.storeBitmap(mOriginBitmap, false);
        refreshDisplay();
    }

    private void refreshDisplay(){
        deleteTextures();
        mGLSurfaceView.requestRender();
    }

    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
    }

    public void onDestroy(){
        super.onDestroy();
        if(mMagicSDK != null)
            mMagicSDK.onDestroy();
    }

    private void adjustImageDisplaySize() {
        float ratio1 = (float)mSurfaceWidth / mImageWidth;
        float ratio2 = (float)mSurfaceHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float)mSurfaceWidth;
        float ratioHeight = imageHeightNew / (float)mSurfaceHeight;

        float[] cube = new float[]{
                TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
        };
        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
    }

    protected void onGetBitmapFromGL(Bitmap bitmap){
        mOriginBitmap = bitmap;
        if(mIsSaving){
            mSaveTask.execute(mOriginBitmap);
            mIsSaving = false;
        }else{
            mMagicSDK.storeBitmap(mOriginBitmap, false);
        }
    }

    //»¹Ô­
    public void restore(){
        if(mFilters != null){
            setFilter(MagicFilterType.NONE);
        }else{
            setImageBitmap(mOriginBitmap);
        }
    }

    //Ó¦ÓÃ
    public void commit(){
        if(mFilters != null){
            getBitmapFromGL(mOriginBitmap, false);
            deleteTextures();
            setFilter(MagicFilterType.NONE);
        }else{
            mOriginBitmap.recycle();
            mOriginBitmap = mMagicSDK.getBitmap();
        }
    }

    public void savaImage(File output, OnPictureSaveListener listener){
        mSaveTask = new SavePictureTask(output, listener);
        mIsSaving = true;
        if(mFilters != null)
            getBitmapFromGL(mOriginBitmap, false);
        else
            onGetBitmapFromGL(mOriginBitmap);
    }
}
