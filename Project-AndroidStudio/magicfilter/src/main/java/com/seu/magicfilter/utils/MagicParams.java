package com.seu.magicfilter.utils;

import android.content.Context;
import android.os.Environment;

import com.seu.magicfilter.widget.base.MagicBaseView;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by why8222 on 2016/2/26.
 */
public class MagicParams {
    public static Context context;
    public static MagicBaseView magicBaseView;

    public static String videoPath = Environment.getExternalStorageDirectory().getPath();
    public static String videoName = "MagicCamera_test.mp4";

    public static int beautyLevel = 5;

    public MagicParams() {

    }

    public static int mGPUPower = 1;

    public static void initMagicFilterParam(GL10 gl){
        mGPUPower = getGPUPower(gl.glGetString(GL10.GL_RENDERER));
    }

    private static int getGPUPower(String gpu){
        //for Mali GPU
        if(gpu.contains("Mali-T880"))
            return 1;
        else if(gpu.contains("Mali-T760"))
            return 1;
        else if(gpu.contains("Mali-T628"))
            return 1;
        else if(gpu.contains("Mali-T624"))
            return 1;
        else if(gpu.contains("Mali"))
            return 0;

        //for PowerVR
        if(gpu.contains("PowerVR SGX 544"))
            return 0;
        else if(gpu.contains("PowerVR"))
            return 1;

        if(gpu.contains("Exynos 8"))
            return 2;
        else if(gpu.contains("Exynos 7"))
            return 1;
        else if(gpu.contains("Exynos"))
            return 0;

        if(gpu.contains("Adreno 330"))
            return 1;
        else if(gpu.contains("Adreno 510"))
            return 1;
        else if(gpu.contains("Adreno 320"))
            return 1;
        else if(gpu.contains("Adreno 306"))
            return 0;
        else if(gpu.contains("Adreno 405"))
            return 0;
        return 1;
    }
}
