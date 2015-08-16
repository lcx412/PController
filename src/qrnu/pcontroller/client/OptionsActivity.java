package qrnu.pcontroller.client;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OptionsActivity extends Activity {

	private CheckBox sound_alert;
	private CheckBox vibration_alert;
	private CheckBox keep_screen_on;
	private CheckBox fullscreen_on;

	private PController pController;
	private SharedPreferences sp;
	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);

		sound_alert = (CheckBox) findViewById(R.id.pref_sound_alert);
		vibration_alert = (CheckBox) findViewById(R.id.pref_vibration_alert);
		keep_screen_on = (CheckBox) findViewById(R.id.pref_keep_screen_on);
		fullscreen_on = (CheckBox) findViewById(R.id.pref_fullscreen_on);

		this.pController = (PController) this.getApplication();
		this.sp = this.pController.sp;
		this.edit = this.sp.edit();

		sound_alert.setChecked(this.sp.getBoolean("feedback_sound", false));
		vibration_alert.setChecked(this.sp.getBoolean("feedback_vibration",
				true));
		keep_screen_on.setChecked(this.sp.getBoolean("keep_screen_on", true));
		fullscreen_on.setChecked(this.sp.getBoolean("fullscreen_on", true));
		fullscreen_on.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					getWindow().addFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					getWindow()
							.clearFlags(
									WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				} else {
					getWindow().clearFlags(
							WindowManager.LayoutParams.FLAG_FULLSCREEN);
					getWindow()
							.addFlags(
									WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				}
			}
		});

		sound_alert.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					playSound(pController.mpOn);
				}
			}
		});

		vibration_alert
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							vibrate(50);
						}
					}
				});

	}

	public void savePrefs() {
		edit.putBoolean("feedback_sound", sound_alert.isChecked());
		edit.putBoolean("feedback_vibration", vibration_alert.isChecked());
		edit.putBoolean("keep_screen_on", keep_screen_on.isChecked());
		edit.putBoolean("fullscreen_on", fullscreen_on.isChecked());
		edit.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sp.getBoolean("fullscreen_on", true)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_reset).setIcon(
				android.R.drawable.ic_menu_revert);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			sound_alert.setChecked(false);
			vibration_alert.setChecked(true);
			keep_screen_on.setChecked(true);
			fullscreen_on.setChecked(true);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		savePrefs();
		super.onBackPressed();

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	private void playSound(MediaPlayer mp) {
		if (mp != null) {
			mp.seekTo(0);
			mp.start();
		}
	}

	private void vibrate(long l) {
		pController.vibrator.vibrate(l);
	}

}
