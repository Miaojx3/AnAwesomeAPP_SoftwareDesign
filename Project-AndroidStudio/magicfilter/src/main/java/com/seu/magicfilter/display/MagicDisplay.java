package com.seu.magicfilter.display;

/**
 * Created by kouseishouganzhoushizenminamixianxianjoushinseigentanleijou on 16/7/14.
 */
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.helper.MagicFilterFactory;
import com.seu.magicfilter.filter.helper.MagicFilterAdjuster;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.helper.SavePictureTask;
import com.seu.magicfilter.utils.TextureRotationUtil;
import com.seu.magicfilter.widget.MagicImageView;

public abstract class MagicDisplay implements Renderer{

    protected GPUImageFilter mFilters;

    protected final MagicImageView mGLSurfaceView;

    protected int mTextureId = OpenGlUtils.NO_TEXTURE;

    protected final FloatBuffer mGLCubeBuffer;

    protected final FloatBuffer mGLTextureBuffer;

    protected SavePictureTask mSaveTask;

    protected int mSurfaceWidth, mSurfaceHeight;

    protected int mImageWidth, mImageHeight;

    protected Context mContext;

    private MagicFilterAdjuster mFilterAdjust;

    public MagicDisplay(Context context, MagicImageView glSurfaceView){
        mContext = context;
        mGLSurfaceView = glSurfaceView;

        mFilters = MagicFilterFactory.initFilters(MagicFilterType.NONE);
        mFilterAdjust = new MagicFilterAdjuster(mFilters);

        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        /*mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(this);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);*/
    }


    public void setFilter(final MagicFilterType filterType) {
        mGLSurfaceView.queueEvent(new Runnable() {

            @Override
            public void run() {
                if(mFilters != null)
                    mFilters.destroy();
                mFilters = null;
                mFilters = MagicFilterFactory.initFilters(filterType);
                if(mFilters != null)
                    mFilters.init();
                onFilterChanged();
                mFilterAdjust = new MagicFilterAdjuster(mFilters);
            }
        });
        mGLSurfaceView.requestRender();
    }

    protected void onFilterChanged(){
        if(mFilters == null)
            return;
        mFilters.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
        mFilters.onInputSizeChanged(mImageWidth, mImageHeight);
    }

    protected void onResume(){

    }

    protected void onPause(){
        if(mSaveTask != null)
            mSaveTask.cancel(true);
    }

    protected void onDestroy(){

    }

    protected void getBitmapFromGL(final Bitmap bitmap,final boolean newTexture){
        mGLSurfaceView.queueEvent(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] mFrameBuffers = new int[1];
                int[] mFrameBufferTextures = new int[1];
                GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
                GLES20.glGenTextures(1, mFrameBufferTextures, 0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
                GLES20.glViewport(0, 0, width, height);
                mFilters.onInputSizeChanged(width, height);
                mFilters.onDisplaySizeChanged(mImageWidth, mImageHeight);
                int textureId = OpenGlUtils.NO_TEXTURE;
                if(newTexture)
                    textureId = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, true);
                else
                    textureId = mTextureId;
                mFilters.onDrawFrame(textureId);
                IntBuffer ib = IntBuffer.allocate(width * height);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ib);
                Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                mBitmap.copyPixelsFromBuffer(IntBuffer.wrap(ib.array()));
                if(newTexture)
                    GLES20.glDeleteTextures(1, new int[]{textureId}, 0);
                GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
                GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
                GLES20.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
                mFilters.destroy();
                mFilters.init();
                mFilters.onInputSizeChanged(mImageWidth, mImageHeight);
                onGetBitmapFromGL(mBitmap);
            }
        });
    }

    protected void onGetBitmapFromGL(Bitmap bitmap){

    }

    protected void deleteTextures() {
        if(mTextureId != OpenGlUtils.NO_TEXTURE)
            mGLSurfaceView.queueEvent(new Runnable() {

                @Override
                public void run() {
                    GLES20.glDeleteTextures(1, new int[]{
                            mTextureId
                    }, 0);
                    mTextureId = OpenGlUtils.NO_TEXTURE;
                }
            });
    }

    public void adjustFilter(int percentage){
        if(mFilterAdjust != null && mFilterAdjust.canAdjust()){
            mFilterAdjust.adjust(percentage);
            mGLSurfaceView.requestRender();
        }
    }

    public void adjustFilter(int percentage, MagicFilterType type){
        if(mFilterAdjust != null && mFilterAdjust.canAdjust()){
            mFilterAdjust.adjust(percentage, type);
            mGLSurfaceView.requestRender();
            //Log.i("test", "done");
        }
    }
}

