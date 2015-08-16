package qrnu.pcontroller.client;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

public class HelpActivity extends Activity {
	private ImageView iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String langCode = Locale.getDefault().getLanguage().toString();
		Intent i = getIntent();
		if (i.getBooleanExtra("connhelp", false)) {
			if (langCode.equals("zh")) {
				setContentView(R.layout.help);
				iv = (ImageView) findViewById(R.id.help_iv);
				iv.setImageResource(R.drawable.connection_help);
			} else {
				setContentView(R.layout.help_en);
				iv = (ImageView) findViewById(R.id.help_en_iv);
				iv.setImageResource(R.drawable.connection_help_en);
			}
		} else {
			if (langCode.equals("zh"))
				setContentView(R.layout.help);
			else {
				setContentView(R.layout.help_en);
			}

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
