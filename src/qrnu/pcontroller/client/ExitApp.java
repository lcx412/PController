package qrnu.pcontroller.client;

import android.content.Context;
import android.content.Intent;

public class ExitApp {

	public static final int EXIT_APPLICATION = 1;

	private Context mContext;

	public ExitApp(Context context) {
		this.mContext = context;
	}

	public void exit() {
		Intent mIntent = new Intent();
		mIntent.setClass(mContext, ConnectionActivity.class);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mIntent.putExtra("flag", EXIT_APPLICATION);
		mContext.startActivity(mIntent);
	}

}
