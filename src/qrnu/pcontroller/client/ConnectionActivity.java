package qrnu.pcontroller.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import qrnu.pcontroller.action.AuthentificationAction;
import qrnu.pcontroller.action.AuthentificationResponseAction;
import qrnu.pcontroller.action.PControllerAction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectionActivity extends Activity {

	private EditText pEtIP;
	private EditText pEtPwd;
	private Button pbtnConnect;
	private ListView plvHosts;
	private LinkedList<ConnectionItem> connections;
	public static PControllerConnection conn;
	private Handler handler;

	private PController pController;
	private SharedPreferences sp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.pController = (PController) this.getApplication();
		this.sp = pController.sp;

		setContentView(R.layout.main);
		pbtnConnect = (Button) this.findViewById(R.id.pbtnConnect);
		pbtnConnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onConnect();
			}
		});

		plvHosts = (ListView) findViewById(R.id.plvHosts);
		initRecentConnectionList();
		pEtIP = (EditText) findViewById(R.id.pEtIP);
		pEtPwd = (EditText) findViewById(R.id.pEtPwd);

		findViewById(R.id.pbtReset).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						pEtIP.setText("");
						pEtPwd.setText("");

					}
				});
	}

	private void initRecentConnectionList() {
		connections = new LinkedList<ConnectionItem>();
		connections.clear();
		for (int i = 0; i < 5; ++i) {
			String ip = sp.getString(
					"ConnectionItemIP" + ((Integer) i).toString(), null);
			String pwd = sp.getString(
					"ConnectionItemPwd" + ((Integer) i).toString(), null);
			if (ip != null) {
				connections.add(new ConnectionItem(ip, pwd));
			}
		}
		String[] from = new String[] { "ip" };
		int[] to = new int[] { R.id.connectionItem };
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (ConnectionItem s : connections) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("ip", s.ip);
			data.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.connectionlist, from, to) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				TextView text = (TextView) v.findViewById(R.id.connectionItem);
				final CharSequence str = text.getText();
				ImageButton b = (ImageButton) v.findViewById(R.id.deletebutton);
				text.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						onSaveList(str);
					}
				});

				b.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						onRemoveList(str);
					}
				});

				return v;
			}
		};
		plvHosts.setAdapter(adapter);

	}

	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		if (getIntent().getIntExtra("flag", 0) == ExitApp.EXIT_APPLICATION)
			finish();
		super.onStart();
	}

	public void onStop() {
		super.onStop();
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_conn_help).setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.menu_about).setIcon(
				android.R.drawable.ic_menu_myplaces);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.menu_exit).setIcon(
				android.R.drawable.ic_lock_power_off);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent i = new Intent(this, HelpActivity.class);
			i.putExtra("connhelp", true);
			startActivity(i);
			break;
		case 1:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case 2:
			try {
				ConnectionActivity.conn.close();
			} catch (Exception e) {
			}
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onConnect() {
		handler = new Handler();
		SharedPreferences sp = this.getSharedPreferences("PController",
				Context.MODE_PRIVATE);
		String ip = pEtIP.getText().toString();
		String pwd = pEtPwd.getText().toString();
		if (ip.matches("^[0-9]{1,4}\\.[0-9]{1,4}\\.[0-9]{1,4}\\.[0-9]{1,4}$")) {
			try {
				SharedPreferences.Editor edit = sp.edit();
				try {
					String[] octets = ip.split("\\.");
					for (String s : octets) {
						int i = Integer.parseInt(s);
						if (i > 255 || i < 0) {
							throw new NumberFormatException();
						}
					}
				} catch (NumberFormatException e) {
					throw new Exception("Illegal IP address!");
				}
				try {
					for (int i = 0; i <= 5; i++) {
						if (ip.equals(connections.get(i).ip)) {
							connections.remove(i);
							connections.addFirst(new ConnectionItem(ip, pwd));
							break;
						}
					}
				} catch (IndexOutOfBoundsException e) {
					if (connections.size() < 5) {
						connections.addFirst(new ConnectionItem(ip, pwd));
					} else {
						connections.removeLast();
						connections.addFirst(new ConnectionItem(ip, pwd));
					}
				}
				String s, p;
				for (int i = 0; i < 5; ++i) {
					try {
						s = connections.get(i).ip;
						p = connections.get(i).pwd;
					} catch (IndexOutOfBoundsException e) {
						s = null;
						p = null;
					}
					edit.putString(
							"ConnectionItemIP" + ((Integer) i).toString(), s);
					edit.putString(
							"ConnectionItemPwd" + ((Integer) i).toString(), p);
				}
				edit.commit();
				initRecentConnectionList();

				Toast.makeText(
						ConnectionActivity.this,
						ConnectionActivity.this.getResources().getText(
								R.string.toast_connection_establishing),
						Toast.LENGTH_SHORT).show();

				try {
					conn = new PControllerConnection(ip);
					conn.sendAction(new AuthentificationAction(pwd));

					PControllerAction action = conn.receiveAction();
					if (action instanceof AuthentificationResponseAction) {
						if (((AuthentificationResponseAction) action).authentificated) {
							Toast.makeText(
									ConnectionActivity.this,
									ConnectionActivity.this
											.getResources()
											.getText(
													R.string.toast_connection_success),
									Toast.LENGTH_SHORT).show();
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									Intent i = new Intent(
											ConnectionActivity.this,
											MainMenuActivity.class);
									startActivity(i);
								}
							}, 500);
						} else {
							Toast.makeText(
									ConnectionActivity.this,
									ConnectionActivity.this
											.getResources()
											.getText(
													R.string.toast_connection_failed),
									Toast.LENGTH_SHORT).show();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e);
					Toast.makeText(
							this,
							this.getResources().getText(
									R.string.toast_connection_failed),
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e);
				Toast.makeText(this,
						this.getResources().getText(R.string.toast_invalidIP),
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this,
					this.getResources().getText(R.string.toast_invalidIP),
					Toast.LENGTH_SHORT).show();
		}

	}

	private synchronized void onRemoveList(CharSequence str) {

		SharedPreferences sp = this.getSharedPreferences("PController",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();

		try {
			String s;
			String p;
			for (int i = 0; i < 5; ++i) {
				try {
					s = connections.get(i).ip;
					if (s.equals(str.toString())) {
						connections.remove(i);
						break;
					}
				} catch (IndexOutOfBoundsException e) {
				}
			}
			for (int i = 0; i < 5; ++i) {
				try {
					s = connections.get(i).ip;
					p = connections.get(i).pwd;
				} catch (IndexOutOfBoundsException e) {
					s = null;
					p = null;
				}
				edit.putString("ConnectionItemIP" + ((Integer) i).toString(), s);
				edit.putString("ConnectionItemPwd" + ((Integer) i).toString(),
						p);

			}
			edit.commit();
			initRecentConnectionList();
		} catch (Exception e) {
		}

	}

	private void onSaveList(CharSequence str) {
		String s;
		String p;
		pEtIP.setText(str.toString());
		try {
			for (int i = 0; i < 5; i++) {
				s = connections.get(i).ip;
				if (s.equals(str.toString())) {
					p = connections.get(i).pwd;
					pEtPwd.setText(p);
					break;
				}
			}
		} catch (IndexOutOfBoundsException ex) {

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (getIntent().getIntExtra("flag", 0) == ExitApp.EXIT_APPLICATION) {
			finish();
		}
		super.onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.text_op_confirm);
		builder.setIcon(R.drawable.tipsicon);
		builder.setMessage(R.string.text_exit_confirm);
		builder.setPositiveButton(R.string.text_yes, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					ConnectionActivity.conn.close();
				} catch (Exception e) {
				}
				ConnectionActivity.this.finish();
			}
		});
		builder.setNegativeButton(R.string.text_no, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();

	}
}
