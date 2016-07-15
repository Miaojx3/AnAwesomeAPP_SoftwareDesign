package com.seu.magiccamera.view.edit;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.seu.magicfilter.display.MagicImageDisplay;

public abstract class ImageEditFragment extends Fragment{
	protected Context mContext;
	protected onHideListener mOnHideListener;
	protected MagicImageDisplay mMagicDisplay;

	public ImageEditFragment(Context context){
		this.mContext = context;
	}

	public ImageEditFragment(Context context, MagicImageDisplay magicDisplay){
		this.mMagicDisplay = magicDisplay;
		this.mContext = context;
	}

	public void onHide(){
		if(isChanged()){
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setTitle("ÌáÊ¾").setMessage("ÊÇ·ñÓ¦ÓÃÐÞ¸Ä£¿").setNegativeButton("ÊÇ", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					onDialogButtonClick(dialog);
					mMagicDisplay.commit();
				}
			}).setPositiveButton("·ñ", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					onDialogButtonClick(dialog);
					mMagicDisplay.restore();
				}
			}).create().show();
		}else{
			mOnHideListener.onHide();
		}
	}
	
	public void setOnHideListener(onHideListener l){
		this.mOnHideListener = l;
	}
	
	protected abstract boolean isChanged();
	
	protected void onDialogButtonClick(DialogInterface dialog){
		if(mOnHideListener != null)
			mOnHideListener.onHide();
		dialog.dismiss();
	}
	
	public interface onHideListener{
		void onHide();
	}
}
