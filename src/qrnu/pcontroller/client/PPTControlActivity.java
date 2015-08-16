package qrnu.pcontroller.client;

import qrnu.pcontroller.action.DirectKeyAction;
import qrnu.pcontroller.action.MouseClickAction;
import qrnu.pcontroller.action.MouseMoveAction;
import qrnu.pcontroller.action.PControllerAction;
import qrnu.pcontroller.action.ScreenCaptureResponseAction;
import qrnu.pcontroller.client.view.PPTTouchView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class PPTControlActivity extends Activity implements
		InterfaceActionOnReceive {

	private static final int PLAY = 0;
	private static final int PAUSE = 1;
	private static final int ERASE = 2;
	private static final int WHITE = 3;
	private static final int BLACK = 4;
	private static final int SETTINGS = 5;
	private static final int USE_HELP = 6;

	private Button pptprevbutton;
	private Button pptnextbutton;
	public PPTTouchView ppttouchView;

	public boolean feedbackSound;
	public boolean feedbackVibration;
	private PController pController;
	public SharedPreferences sp;
	public PControllerConnection conn;

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;;

	private PControllerActionReceivers pControllerActionReceivers;
	private SendScreenCaptureRequest sendScreenCaptureRequest;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pptcontrol);

		this.pController = (PController) this.getApplication();
		this.sp = this.pController.sp;
		this.conn = ConnectionActivity.conn;
		this.pControllerActionReceivers = MainMenuActivity.pControllerActionReceivers;

		this.pptprevbutton = (Button) findViewById(R.id.pptprevious);
		this.pptnextbutton = (Button) findViewById(R.id.pptnext);
		this.ppttouchView = (PPTTouchView) findViewById(R.id.pptTouchView);
		this.sendScreenCaptureRequest = new SendScreenCaptureRequest(this);

		this.pptprevbutton.setOnClickListener(new pOnClickListener());
		this.pptnextbutton.setOnClickListener(new nOnClickListener());

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}

	protected void onResume() {
		super.onResume();
		this.feedbackSound = this.sp.getBoolean("feedback_sound", false);
		this.feedbackVibration = this.sp.getBoolean("feedback_vibration", true);
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
		if (sp.getBoolean("shake_switch", false))
			mSensorManager.registerListener(sensorEventListener,
					mAccelerometer, SensorManager.SENSOR_DELAY_GAME);

		pControllerActionReceivers.registerActionReceiver(this);
		pControllerActionReceivers.flag = true;
		new Thread(pControllerActionReceivers).start();
		if (sp.getBoolean("screen_capture", false)) {
			sendScreenCaptureRequest.flag = true;
			new Thread(sendScreenCaptureRequest).start();
		}
	}

	private SensorEventListener sensorEventListener = new SensorEventListener() {
		long lastUpdate, lastShakeTime = 0;
		float x, y, last_x = 0, last_y = 0;
		static final int SHAKE_THRESHOLD = 800;

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent e) {
			long curTime = System.currentTimeMillis();
			if ((curTime - lastUpdate) > 100) {
				long diffTime = (curTime - lastUpdate);
				lastUpdate = curTime;
				x = e.values[SensorManager.DATA_X];
				y = e.values[SensorManager.DATA_Y];
				float acceChangeRate = 0;
				if (last_x != 0)
					acceChangeRate = Math.abs(x + y - last_x - last_y)
							/ diffTime * 10000;
				if (acceChangeRate > SHAKE_THRESHOLD
						&& curTime - lastShakeTime > 800) {
					lastShakeTime = curTime;
					onShake();
				}
				last_x = x;
				last_y = y;
			}
		}
	};

	private void onShake() {
		try {
			conn.sendAction(new DirectKeyAction(34, -5, (byte) 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sp.getBoolean("screen_capture", false))
			ppttouchView.screenCaptureRequest();
		playSound(pController.mpOn);
		// playSound(pController.mpOff);
		vibrate(50);
	}

	protected void onPause() {
		if (sp.getBoolean("shake_switch", false))
			mSensorManager.unregisterListener(sensorEventListener);
		pControllerActionReceivers.unregisterActionReceiver(this);
		pControllerActionReceivers.flag = false;
		sendScreenCaptureRequest.flag = false;
		super.onPause();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, PLAY, Menu.NONE, R.string.menu_play).setIcon(
				R.drawable.ic_media_play);
		menu.add(Menu.NONE, PAUSE, Menu.NONE, R.string.menu_pause).setIcon(
				R.drawable.ic_media_pause);
		menu.add(Menu.NONE, ERASE, Menu.NONE, R.string.menu_erase).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(Menu.NONE, WHITE, Menu.NONE, R.string.menu_white).setIcon(
				R.drawable.alert_light_frame);
		menu.add(Menu.NONE, BLACK, Menu.NONE, R.string.menu_black).setIcon(
				R.drawable.alert_dark_frame);
		menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.text_options)
				.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, USE_HELP, Menu.NONE, R.string.menu_use_help)
				.setIcon(android.R.drawable.ic_menu_help);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case PLAY:
			try {
				conn.sendAction(new DirectKeyAction(16, 116, (byte) 0));
				conn.sendAction(new DirectKeyAction(116, -5, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sp.getBoolean("screen_capture", false))
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ppttouchView.screenCaptureRequest();
					}
				}, 555);

			break;
		case PAUSE:
			try {
				conn.sendAction(new DirectKeyAction(27, -5, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sp.getBoolean("screen_capture", false))
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ppttouchView.screenCaptureRequest();
					}
				}, 155);
			break;

		case ERASE:
			try {
				conn.sendAction(new DirectKeyAction(69, -5, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sp.getBoolean("screen_capture", false))
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ppttouchView.screenCaptureRequest();
					}
				}, 155);
			break;

		case WHITE:
			try {
				conn.sendAction(new DirectKeyAction(87, -5, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sp.getBoolean("screen_capture", false))
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ppttouchView.screenCaptureRequest();
					}
				}, 155);
			break;

		case BLACK:
			try {
				conn.sendAction(new DirectKeyAction(66, -5, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sp.getBoolean("screen_capture", false))
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						ppttouchView.screenCaptureRequest();
					}
				}, 155);
			break;

		case SETTINGS:
			Intent i = new Intent(this, TouchPadSettingsActivity.class);
			i.putExtra("ppt", true);
			this.startActivity(i);
			break;

		case USE_HELP:
			this.startActivity(new Intent(this, HelpActivity.class));
			break;
		default:
			break;
		}

		return true;
	}

	public void mouseClick(byte button, boolean state) {
		try {
			conn.sendAction(new MouseClickAction(button, state));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (state) {
			this.playSound(this.pController.mpOn);
		}
		// else
		// {
		// this.playSound(this.pController.mpOff);
		// }
	}

	public void mouseMove(int moveX, int moveY) {
		try {
			conn.sendAction(new MouseMoveAction((short) moveX, (short) moveY));
		} catch (Exception e) {
		}
	}

	class pOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				conn.sendAction(new DirectKeyAction(33, -5, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			playSound(pController.mpOn);
			// playSound(pController.mpOff);
			vibrate(50);
			if (sp.getBoolean("screen_capture", false))
				ppttouchView.screenCaptureRequest();
		}
	}

	class nOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				conn.sendAction(new DirectKeyAction(34, -5, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}
			playSound(pController.mpOn);
			// playSound(pController.mpOff);
			vibrate(50);
			if (sp.getBoolean("screen_capture", false))
				ppttouchView.screenCaptureRequest();
		}
	}

	public void playSound(MediaPlayer mp) {
		if (this.feedbackSound)

			if (mp != null) {
				mp.seekTo(0);
				mp.start();
			}
	}

	public void vibrate(long l) {
		if (this.feedbackVibration)
			pController.vibrator.vibrate(l);
	}

	public void receiveAction(PControllerAction action) {
		if (action instanceof ScreenCaptureResponseAction) {
			this.ppttouchView
					.receiveAction((ScreenCaptureResponseAction) action);
		}
	}
}
