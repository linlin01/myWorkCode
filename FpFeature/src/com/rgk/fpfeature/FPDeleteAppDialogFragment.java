package com.rgk.fpfeature;

import com.rgk.fpfeature.bean.BaseAppItem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class FPDeleteAppDialogFragment extends DialogFragment {
	interface OnDeleteListener {
		void onClick(); 
	}
	
	OnDeleteListener mListener;
	
	public void setOnDeleteListener(OnDeleteListener mListener) {
		this.mListener = mListener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.delete);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if (mListener != null) {
					mListener.onClick();
				}
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}
}
