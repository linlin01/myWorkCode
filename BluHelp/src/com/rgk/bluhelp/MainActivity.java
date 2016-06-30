package com.rgk.bluhelp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Uri uri = Uri.parse(getResources().getString(R.string.blu_uri));
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		//intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
		startActivity(intent);
		finish();
	}
}
