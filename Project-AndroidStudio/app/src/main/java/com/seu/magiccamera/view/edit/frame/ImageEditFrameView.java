package com.seu.magiccamera.view.edit.frame;

/**
 * Created by kouseishouganzhoushizenminamixianxianjoushinseigentanleijou on 16/7/14.
 */
import android.content.Context;
import android.content.DialogInterface;

import com.seu.magiccamera.view.edit.ImageEditFragment;
import com.seu.magicfilter.display.MagicImageDisplay;

public class ImageEditFrameView extends ImageEditFragment{

    public ImageEditFrameView(Context context, MagicImageDisplay magicDisplay) {
        super(context, magicDisplay);

    }

    @Override
    protected boolean isChanged() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void onDialogButtonClick(DialogInterface dialog) {
        // TODO Auto-generated method stub

    }
}
