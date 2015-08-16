package qrnu.pcontroller.client;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class AboutActivity extends Activity {

	private String langCode;
	private LinearLayout about_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		langCode = Locale.getDefault().getLanguage().toString();
		about_layout = (LinearLayout) findViewById(R.id.about_layout);
		if (langCode.equals("zh")) {
			about_layout.setBackgroundResource(R.drawable.about);
		} else {
			about_layout.setBackgroundResource(R.drawable.about_en);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (this.getSharedPreferences("PController", Context.MODE_PRIVATE)
				.getBoolean("fullscreen_on", true)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}

}
