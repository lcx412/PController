package qrnu.pcontroller.client;

import qrnu.pcontroller.action.DirectKeyAction;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;

public class JoyStickActivity extends Activity implements SensorEventListener {

	private ImageButton esc;
	private ImageButton enter;

	private int up_key;
	private int down_key;
	private int left_key;
	private int right_key;
	private int a_key;
	private int b_key;
	private int c_key;
	private int d_key;

	private int x0 = 15;
	private int y1 = 15;
	private int y2 = 35;

	private ImageButton sensorswitch;

	private boolean sensor_on;

	private boolean feedbackSound;
	private boolean feedbackVibration;
	private boolean highsensitivity;
	private PController pController;
	private SharedPreferences sp;

	private PControllerConnection conn;

	private SensorManager mSensorManager;
	private Sensor mOrientation;;

	private float x = 0, y = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joystick);

		this.pController = (PController) this.getApplication();
		this.sp = this.pController.sp;
		this.conn = ConnectionActivity.conn;

		sp = this.getSharedPreferences("PController", Context.MODE_PRIVATE);
		esc = (ImageButton) findViewById(R.id.GameControl_Button_ESC);
		enter = (ImageButton) findViewById(R.id.GameControl_Button_Enter);
		sensorswitch = (ImageButton) findViewById(R.id.GameControl_Button_SensorSwitch);

		sensor_on = false;

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		esc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					conn.sendAction(new DirectKeyAction(27, -5, (byte) 0));
				} catch (Exception e) {
					e.printStackTrace();
				}
				playSoundandVibrate();

			}
		});

		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					conn.sendAction(new DirectKeyAction(10, -5, (byte) 0));
				} catch (Exception e) {
					e.printStackTrace();
				}
				playSoundandVibrate();

			}
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.feedbackSound = this.sp.getBoolean("feedback_sound", false);
		this.feedbackVibration = this.sp.getBoolean("feedback_vibration", true);
		this.highsensitivity = this.sp.getBoolean("high_sensitivity_on", false);
		this.x0 = this.sp.getInt("ss_lr", 15);
		this.y1 = this.sp.getInt("ss_up", 15);
		this.y2 = this.sp.getInt("ss_down", 35);
		sensorswitch.setImageResource(R.drawable.gamecontrol_sensor_off_xml);
		if (sp.getBoolean("fullscreen_on", true)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}

		if (sp.getBoolean("keep_screen_on", true))
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		else
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		left_key = sp.getInt("left_key", 65);
		right_key = sp.getInt("right_key", 68);
		up_key = sp.getInt("up_key", 87);
		down_key = sp.getInt("down_key", 83);
		a_key = sp.getInt("a_key", 74);
		b_key = sp.getInt("b_key", 75);
		c_key = sp.getInt("c_key", 76);
		d_key = sp.getInt("d_key", 73);

		// if(mOrientation==null){
		// Toast.makeText(GameControlActivity.this,getResources().getString(R.string.toast_noSensor),Toast.LENGTH_LONG).show();
		// }
		// else
		sensorswitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sensor_on) {
					mSensorManager.unregisterListener(JoyStickActivity.this);
					sensorswitch
							.setImageResource(R.drawable.gamecontrol_sensor_off_xml);
					sensor_on = false;

					try {
						conn.sendAction(new DirectKeyAction(left_key, -5,
								(byte) 2));
						conn.sendAction(new DirectKeyAction(right_key, -5,
								(byte) 2));
						conn.sendAction(new DirectKeyAction(down_key, -5,
								(byte) 2));
						conn.sendAction(new DirectKeyAction(up_key, -5,
								(byte) 2));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (highsensitivity)
						mSensorManager.registerListener(JoyStickActivity.this,
								mOrientation,
								SensorManager.SENSOR_DELAY_FASTEST);
					else
						mSensorManager.registerListener(JoyStickActivity.this,
								mOrientation, SensorManager.SENSOR_DELAY_GAME);

					sensorswitch
							.setImageResource(R.drawable.gamecontrol_sensor_on_xml);
					sensor_on = true;
				}
				playSoundandVibrate();

			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSensorManager.unregisterListener(this);

		try {
			conn.sendAction(new DirectKeyAction(left_key, -5, (byte) 2));
			conn.sendAction(new DirectKeyAction(right_key, -5, (byte) 2));
			conn.sendAction(new DirectKeyAction(down_key, -5, (byte) 2));
			conn.sendAction(new DirectKeyAction(up_key, -5, (byte) 2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.text_button_settings)
				.setIcon(android.R.drawable.ic_menu_sort_alphabetically);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.text_sensor_settings)
				.setIcon(android.R.drawable.ic_menu_always_landscape_portrait);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.text_system_options)
				.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, 3, Menu.NONE, R.string.menu_use_help).setIcon(
				android.R.drawable.ic_menu_help);
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			this.startActivity(new Intent(this, ButtonSettingsActivity.class));
			break;
		case 1:
			this.startActivity(new Intent(this, SensorSettingsActivity.class));
			break;
		case 2:
			this.startActivity(new Intent(this, OptionsActivity.class));
			break;
		case 3:
			this.startActivity(new Intent(this, HelpActivity.class));
			break;
		default:
			break;
		}

		return true;
	}

	public void pressButton(int buttonID) {
		switch (buttonID) {
		case 0:
			this.sendAction(up_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		case 1:
			this.sendAction(left_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		case 2:
			this.sendAction(right_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		case 3:
			this.sendAction(down_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		case 4:
			this.sendAction(a_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		case 5:
			this.sendAction(b_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		case 6:
			this.sendAction(c_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		case 7:
			this.sendAction(d_key, (byte) 1);
			playSound(pController.mpOn);
			vibrate(50);
			break;
		default:
			break;
		}
	}

	public void releaseButton(int buttonID) {
		switch (buttonID) {
		case 0:
			this.sendAction(up_key, (byte) 2);
			break;
		case 1:
			this.sendAction(left_key, (byte) 2);
			break;
		case 2:
			this.sendAction(right_key, (byte) 2);
			break;
		case 3:
			this.sendAction(down_key, (byte) 2);
			break;
		case 4:
			this.sendAction(a_key, (byte) 2);
			break;
		case 5:
			this.sendAction(b_key, (byte) 2);
			break;
		case 6:
			this.sendAction(c_key, (byte) 2);
			break;
		case 7:
			this.sendAction(d_key, (byte) 2);
			break;
		default:
			break;
		}
	}

	public void releaseAllButtons() {
		this.sendAction(up_key, (byte) 2);
		this.sendAction(left_key, (byte) 2);
		this.sendAction(right_key, (byte) 2);
		this.sendAction(down_key, (byte) 2);
		this.sendAction(a_key, (byte) 2);
		this.sendAction(b_key, (byte) 2);
		this.sendAction(c_key, (byte) 2);
		this.sendAction(d_key, (byte) 2);
	}

	public void sendAction(int key, byte state) {
		try {
			conn.sendAction(new DirectKeyAction(key, -5, state));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		x = event.values[1];// +Îª×ó
		y = event.values[2];// +Îªºó
		if (x > x0 && x < 85) {
			try {
				conn.sendAction(new DirectKeyAction(left_key, -5, (byte) 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (x <= x0 && x >= -x0) {
			try {
				conn.sendAction(new DirectKeyAction(left_key, -5, (byte) 2));
				conn.sendAction(new DirectKeyAction(right_key, -5, (byte) 2));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (x < -x0 && x > -85) {
			try {
				conn.sendAction(new DirectKeyAction(right_key, -5, (byte) 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (y > y2 && y < 85) {
			try {
				conn.sendAction(new DirectKeyAction(down_key, -5, (byte) 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (y <= y2 && y >= y1) {
			try {
				conn.sendAction(new DirectKeyAction(down_key, -5, (byte) 2));
				conn.sendAction(new DirectKeyAction(up_key, -5, (byte) 2));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (y < y1 && y > -85) {
			try {
				conn.sendAction(new DirectKeyAction(up_key, -5, (byte) 1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	private void playSoundandVibrate() {
		playSound(pController.mpOn);
		// playSound(pController.mpOff);
		vibrate(50);
	}

	private void playSound(MediaPlayer mp) {
		if (this.feedbackSound)

			if (mp != null) {
				mp.seekTo(0);
				mp.start();
			}
	}

	private void vibrate(long l) {
		if (this.feedbackVibration)
			pController.vibrator.vibrate(l);
	}

}
