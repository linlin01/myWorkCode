package com.ragentek.copy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CopyTestActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void doTest(View view) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.BOOT_COMPLETED");
		sendBroadcast(intent);
	}
}
