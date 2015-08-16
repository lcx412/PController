package qrnu.pcontroller.client;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

public class SensorSettingsActivity extends Activity {

	private CheckBox high_sensitivity;
	private EditText lr;
	private EditText up;
	private EditText down;
	private SharedPreferences sp;
	private SharedPreferences.Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = this.getSharedPreferences("PController", Context.MODE_PRIVATE);
		edit = sp.edit();
		setContentView(R.layout.sensorsettings);
		high_sensitivity = (CheckBox) findViewById(R.id.ss_high_sensitivity_on);
		lr = (EditText) findViewById(R.id.ss_lr);
		up = (EditText) findViewById(R.id.ss_up);
		down = (EditText) findViewById(R.id.ss_down);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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
		high_sensitivity
				.setChecked(sp.getBoolean("high_sensitivity_on", false));
		lr.setText(Integer.toString(sp.getInt("ss_lr", 15)));
		up.setText(Integer.toString(sp.getInt("ss_up", 15)));
		down.setText(Integer.toString(sp.getInt("ss_down", 35)));
	}

	@Override
	public void onBackPressed() {
		savePrefs();
		super.onBackPressed();

	}

	private void savePrefs() {
		edit.putBoolean("high_sensitivity_on", high_sensitivity.isChecked());
		edit.putInt("ss_lr", Integer.parseInt(lr.getText().toString()));
		edit.putInt("ss_up", Integer.parseInt(up.getText().toString()));
		edit.putInt("ss_down", Integer.parseInt(down.getText().toString()));
		edit.commit();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_reset).setIcon(
				android.R.drawable.ic_menu_revert);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			high_sensitivity.setChecked(false);
			lr.setText("15");
			up.setText("15");
			down.setText("35");
		}
		return true;
	}
}
