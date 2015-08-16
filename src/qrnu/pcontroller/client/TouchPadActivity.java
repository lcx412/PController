package qrnu.pcontroller.client;

import qrnu.pcontroller.action.KeyboardAction;
import qrnu.pcontroller.action.MouseClickAction;
import qrnu.pcontroller.action.MouseMoveAction;
import qrnu.pcontroller.action.MouseWheelAction;
import qrnu.pcontroller.action.PControllerAction;
import qrnu.pcontroller.action.ScreenCaptureResponseAction;
import qrnu.pcontroller.client.view.ButtonView;
import qrnu.pcontroller.client.view.TouchView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class TouchPadActivity extends Activity implements
		InterfaceActionOnReceive {
	private static final int TOGGLEKEYBOARD = 0;
	private static final int FILEEXPLORER = 1;
	private static final int SETTINGS = 2;
	private static final int USE_HELP = 3;

	public boolean feedbackSound;
	public boolean feedbackVibration;
	private PController pController;
	private SharedPreferences sp;
	public PControllerConnection conn;

	private ButtonView leftButton;
	private ButtonView rightButton;
	public TouchView touchView;

	private PControllerActionReceivers pControllerActionReceivers;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.touchpad);
		this.leftButton = (ButtonView) findViewById(R.id.leftButtonView);
		this.rightButton = (ButtonView) findViewById(R.id.rightButtonView);
		this.touchView = (TouchView) findViewById(R.id.TouchView);

		this.pController = (PController) this.getApplication();
		this.sp = this.pController.sp;
		this.conn = ConnectionActivity.conn;
		this.pControllerActionReceivers = MainMenuActivity.pControllerActionReceivers;
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

		pControllerActionReceivers.registerActionReceiver(this);
		pControllerActionReceivers.flag = true;
		new Thread(pControllerActionReceivers).start();

	}

	protected void onPause() {
		super.onPause();
		pControllerActionReceivers.unregisterActionReceiver(this);
		pControllerActionReceivers.flag = false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		leftButton.setHolding(false);
		rightButton.setHolding(false);
		this.mouseClick(MouseClickAction.BUTTON_LEFT, MouseClickAction.STATE_UP);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, TOGGLEKEYBOARD, Menu.NONE,
				R.string.menu_togglekeyboard).setIcon(R.drawable.menu_keyboard);
		menu.add(Menu.NONE, FILEEXPLORER, Menu.NONE, R.string.menu_fileexplorer)
				.setIcon(android.R.drawable.ic_menu_slideshow);
		menu.add(Menu.NONE, SETTINGS, Menu.NONE, R.string.text_options)
				.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, USE_HELP, Menu.NONE, R.string.menu_use_help)
				.setIcon(android.R.drawable.ic_menu_help);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case TOGGLEKEYBOARD:
			this.toggleKeyboard();
			break;
		case FILEEXPLORER:
			this.startActivity(new Intent(this, FileExplorerActivity.class));
			break;
		case SETTINGS:
			this.startActivity(new Intent(this, TouchPadSettingsActivity.class));
			break;
		case USE_HELP:
			this.startActivity(new Intent(this, HelpActivity.class));
			break;
		default:
			break;
		}

		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int unicode = event.getUnicodeChar();

		if (unicode == 0 && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			unicode = KeyboardAction.UNICODE_BACKSPACE;
		}

		if (unicode != 0) {
			try {
				this.conn.sendAction(new KeyboardAction(unicode));
			} catch (Exception e) {
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleKeyboard() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, 0);
	}

	public void mouseClick(byte button, boolean state) {
		try {
			conn.sendAction(new MouseClickAction(button, state));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (state) {
			this.playSound(pController.mpOn);
		}
		// else
		// {
		// this.playSound(pController.mpOff);
		// }
	}

	public void mouseMove(int moveX, int moveY) {
		try {
			conn.sendAction(new MouseMoveAction((short) moveX, (short) moveY));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mouseWheel(int amount) {
		try {
			conn.sendAction(new MouseWheelAction((byte) amount));
		} catch (Exception e) {
			e.printStackTrace();
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
			this.touchView.receiveAction((ScreenCaptureResponseAction) action);
		}
	}
}
