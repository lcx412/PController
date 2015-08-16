package qrnu.pcontroller.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;

public class TouchPadSettingsActivity extends Activity {

	private SeekBar move_sensitivity;
	private SeekBar wheel_sensitivity;
	private EditText hold_delay;
	private CheckBox screen_capture;
	private CheckBox shake_switch;
	private Button system_options;

	private PController pController;
	private SharedPreferences sp;
	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.touchpadsettings);
		this.pController = (PController) this.getApplication();
		this.sp = this.pController.sp;
		this.edit = this.sp.edit();

		move_sensitivity = (SeekBar) findViewById(R.id.pref_move_sensitivity);
		wheel_sensitivity = (SeekBar) findViewById(R.id.pref_wheel_sensitivity);
		hold_delay = (EditText) findViewById(R.id.pref_hold_delay);
		screen_capture = (CheckBox) findViewById(R.id.pref_screen_capture);
		shake_switch = (CheckBox) findViewById(R.id.pref_shake_switch);
		move_sensitivity.setProgress(Integer.parseInt(this.sp.getString(
				"move_sensitivity", "20")));
		wheel_sensitivity.setProgress(Integer.parseInt(this.sp.getString(
				"wheel_sensitivity", "25")));
		hold_delay.setText(this.sp.getString("hold_delay", "500"));
		screen_capture.setChecked(this.sp.getBoolean("screen_capture", false));
		shake_switch.setChecked(this.sp.getBoolean("shake_switch", false));
		shake_switch.setVisibility(View.GONE);
		findViewById(R.id.pref_shake_switch_text).setVisibility(View.GONE);
		system_options = (Button) findViewById(R.id.pref_system_options);
		system_options.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TouchPadSettingsActivity.this.startActivity(new Intent(
						TouchPadSettingsActivity.this, OptionsActivity.class));
			}
		});

		Intent i = getIntent();
		boolean ppt = i.getBooleanExtra("ppt", false);
		if (ppt) {
			wheel_sensitivity.setVisibility(View.GONE);
			findViewById(R.id.pref_wheel_sensitivity_text).setVisibility(
					View.GONE);
			shake_switch.setVisibility(View.VISIBLE);
			findViewById(R.id.pref_shake_switch_text).setVisibility(
					View.VISIBLE);
		}

	}

	public void savePrefs() {
		edit.putString("move_sensitivity",
				Integer.toString(move_sensitivity.getProgress()));
		edit.putString("wheel_sensitivity",
				Integer.toString(wheel_sensitivity.getProgress()));
		edit.putString("hold_delay", hold_delay.getText().toString());
		edit.putBoolean("screen_capture", screen_capture.isChecked());
		edit.putBoolean("shake_switch", shake_switch.isChecked());
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
			move_sensitivity.setProgress(20);
			wheel_sensitivity.setProgress(25);
			hold_delay.setText("500");
			screen_capture.setChecked(false);
			shake_switch.setChecked(false);
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		savePrefs();
		super.onBackPressed();

	}
}
