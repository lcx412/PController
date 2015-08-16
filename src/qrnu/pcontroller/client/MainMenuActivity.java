package qrnu.pcontroller.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainMenuActivity extends Activity {

	private Button touchPad;
	private Button ppt_Control;
	private Button game_Control;
	private Button options;
	private Button file_Explorer;
	private Button use_Help;
	private Intent i;
	private Handler handler;

	public static PControllerActionReceivers pControllerActionReceivers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pControllerActionReceivers = new PControllerActionReceivers();
		setContentView(R.layout.mainmenu);
		this.handler = new Handler();
		touchPad = (Button) findViewById(R.id.MainMenu_Button_TouchPad);
		ppt_Control = (Button) findViewById(R.id.MainMenu_Button_PPTControl);
		game_Control = (Button) findViewById(R.id.Mainmenu_Button_JoyStick);
		options = (Button) findViewById(R.id.MainMenu_Button_Options);
		use_Help = (Button) findViewById(R.id.MainMenu_Button_Help);
		file_Explorer = (Button) findViewById(R.id.MainMenu_Button_File_Explorer);

		final Animation anim_menu = AnimationUtils.loadAnimation(this,
				R.anim.anim_menu);

		touchPad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				touchPad.startAnimation(anim_menu);
				startActivityDelayed(TouchPadActivity.class);
			}
		});

		ppt_Control.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ppt_Control.startAnimation(anim_menu);
				startActivityDelayed(PPTControlActivity.class);
			}
		});

		game_Control.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				game_Control.startAnimation(anim_menu);
				startActivityDelayed(JoyStickActivity.class);
			}
		});

		options.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				options.startAnimation(anim_menu);
				startActivityDelayed(OptionsActivity.class);
			}
		});

		use_Help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				use_Help.startAnimation(anim_menu);
				startActivityDelayed(HelpActivity.class);
			}
		});

		file_Explorer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				file_Explorer.startAnimation(anim_menu);
				startActivityDelayed(FileExplorerActivity.class);
			}
		});
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

	private void startActivityDelayed(Class<?> cls) {
		i = new Intent(this, cls);
		this.handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startActivity(i);
				// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
			}
		}, 500);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_about).setIcon(
				android.R.drawable.ic_menu_myplaces);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_exit).setIcon(
				android.R.drawable.ic_lock_power_off);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 0:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case 1:
			try {
				ConnectionActivity.conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			new ExitApp(this).exit();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.text_op_confirm);
		builder.setIcon(R.drawable.tipsicon);
		builder.setMessage(R.string.text_reconnect_confirm);
		builder.setPositiveButton(R.string.text_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							ConnectionActivity.conn.close();
						} catch (Exception e) {
						}
						MainMenuActivity.super.onBackPressed();
					}
				});
		builder.setNegativeButton(R.string.text_no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}

}
