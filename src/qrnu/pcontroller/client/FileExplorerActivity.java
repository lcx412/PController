package qrnu.pcontroller.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import qrnu.pcontroller.action.FileExploreRequestAction;
import qrnu.pcontroller.action.FileExploreResponseAction;
import qrnu.pcontroller.action.PControllerAction;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileExplorerActivity extends Activity implements
		OnItemClickListener, InterfaceActionOnReceive {
	private static final int REFRESH = 0;
	private static final int EXPLORE_ROOTS = 1;
	private static final int TOUCHPAD = 2;
	private static final int USE_HELP = 3;

	private PControllerConnection conn;
	private PControllerActionReceivers pControllerActionReceivers;

	private String directory;
	private ArrayList<String> files;
	private ArrayAdapter<String> adapter;
	private ListView listView;
	private TextView textView;

	private boolean feedbackSound;
	private boolean feedbackVibration;
	private PController pController;
	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.pController = (PController) this.getApplication();
		this.sp = this.pController.sp;
		this.conn = ConnectionActivity.conn;

		this.pControllerActionReceivers = MainMenuActivity.pControllerActionReceivers;

		this.setContentView(R.layout.fileexplorer);
		this.listView = (ListView) this.findViewById(R.id.files);
		this.textView = (TextView) this.findViewById(R.id.directory);
		this.files = new ArrayList<String>();
		this.adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, this.files);
		this.listView.setAdapter(this.adapter);
		this.listView.setOnItemClickListener(this);
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

		this.directory = this.sp.getString("fileExplore_directory", "");
		this.refresh();
		this.refresh();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, REFRESH, Menu.NONE, R.string.menu_refresh).setIcon(
				android.R.drawable.ic_menu_rotate);
		menu.add(Menu.NONE, EXPLORE_ROOTS, Menu.NONE,
				R.string.menu_explore_roots).setIcon(
				android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, TOUCHPAD, Menu.NONE, R.string.menu_touchpad)
				.setIcon(android.R.drawable.ic_menu_upload_you_tube);
		menu.add(Menu.NONE, USE_HELP, Menu.NONE, R.string.menu_use_help)
				.setIcon(android.R.drawable.ic_menu_help);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case REFRESH:
			this.refresh();
			break;
		case EXPLORE_ROOTS:
			this.exploreRoots();
			break;
		case TOUCHPAD:
			startActivity(new Intent(this, TouchPadActivity.class));
			break;
		case USE_HELP:
			startActivity(new Intent(this, HelpActivity.class));
			break;
		default:
			break;
		}

		return true;
	}

	protected void onPause() {
		super.onPause();

		pControllerActionReceivers.unregisterActionReceiver(this);
		pControllerActionReceivers.flag = false;

		Editor editor = this.sp.edit();
		editor.putString("fileExplore_directory", this.directory);
		editor.commit();
	}

	public void receiveAction(PControllerAction action) {
		if (action instanceof FileExploreResponseAction) {
			FileExploreResponseAction fera = (FileExploreResponseAction) action;

			this.directory = fera.directory;

			this.files.clear();
			if (fera.files[0].equals("..")) {
				if (Locale.getDefault().getLanguage().toString().equals("zh"))
					fera.files[0] = "..返回上级目录";
				else
					fera.files[0] = "..Back to parent directory";
			}
			this.files.addAll(Arrays.asList(fera.files));

			this.runOnUiThread(new Runnable() {
				public void run() {
					FileExplorerActivity.this.textView
							.setText(FileExplorerActivity.this.directory);
					FileExplorerActivity.this.adapter.notifyDataSetChanged();
					FileExplorerActivity.this.listView
							.setSelectionAfterHeaderView();
				}
			});
		}
	}

	private void sendFileExploreRequest(String fileString) {
		try {
			if (fileString.substring(0, 2).equals(".."))
				fileString = "..";
		} catch (Exception e) {
		}
		try {
			this.conn.sendAction(new FileExploreRequestAction(this.directory,
					fileString, sp.getBoolean("showHidden", false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		playSound(pController.mpOn);
		// playSound(pController.mpOff);
		vibrate(50);
		this.sendFileExploreRequest(this.files.get(position));
	}

	private void refresh() {
		this.sendFileExploreRequest("");

	}

	private void exploreRoots() {
		this.directory = "";
		this.refresh();
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
