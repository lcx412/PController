package qrnu.pcontroller.client;

import qrnu.pcontroller.client.Keys.Key;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class ButtonSettingsActivity extends Activity {

	private EditText left;
	private EditText right;
	private EditText up;
	private EditText down;
	private EditText a;
	private EditText b;
	private EditText c;
	private EditText d;

	private Button left_button;
	private Button right_button;
	private Button up_button;
	private Button down_button;
	private Button a_button;
	private Button b_button;
	private Button c_button;
	private Button d_button;

	private SharedPreferences sp;
	private Keys keys;

	private Editor edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buttonsettings);
		sp = this.getSharedPreferences("PController", Context.MODE_PRIVATE);
		edit = sp.edit();
		keys = new Keys();

		left = (EditText) findViewById(R.id.butsetting_btEditActionLeft);
		right = (EditText) findViewById(R.id.butsetting_btEditActionRight);
		up = (EditText) findViewById(R.id.butsetting_btEditActionUp);
		down = (EditText) findViewById(R.id.butsetting_btEditActionDown);

		left_button = (Button) findViewById(R.id.butsetting_btSelectActionLeft);
		right_button = (Button) findViewById(R.id.butsetting_btSelectActionRight);
		up_button = (Button) findViewById(R.id.butsetting_btSelectActionUp);
		down_button = (Button) findViewById(R.id.butsetting_btSelectActionDown);

		a = (EditText) findViewById(R.id.butsetting_btEditActionA);
		b = (EditText) findViewById(R.id.butsetting_btEditActionB);
		c = (EditText) findViewById(R.id.butsetting_btEditActionC);
		d = (EditText) findViewById(R.id.butsetting_btEditActionD);

		a_button = (Button) findViewById(R.id.butsetting_btSelectActionA);
		b_button = (Button) findViewById(R.id.butsetting_btSelectActionB);
		c_button = (Button) findViewById(R.id.butsetting_btSelectActionC);
		d_button = (Button) findViewById(R.id.butsetting_btSelectActionD);

		left_button.setOnClickListener(new SelectAction(left, "left_key"));
		right_button.setOnClickListener(new SelectAction(right, "right_key"));
		up_button.setOnClickListener(new SelectAction(up, "up_key"));
		down_button.setOnClickListener(new SelectAction(down, "down_key"));
		a_button.setOnClickListener(new SelectAction(a, "a_key"));
		b_button.setOnClickListener(new SelectAction(b, "b_key"));
		c_button.setOnClickListener(new SelectAction(c, "c_key"));
		d_button.setOnClickListener(new SelectAction(d, "d_key"));

		reloadsp();
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

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.menu_reset).setIcon(
				android.R.drawable.ic_menu_revert);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			left.setText("A");
			right.setText("D");
			up.setText("W");
			down.setText("S");
			a.setText("J");
			b.setText("K");
			c.setText("L");
			d.setText("I");
			edit.putInt("left_key", 65);
			edit.putInt("right_key", 68);
			edit.putInt("up_key", 87);
			edit.putInt("down_key", 83);
			edit.putInt("a_key", 74);
			edit.putInt("b_key", 75);
			edit.putInt("c_key", 76);
			edit.putInt("d_key", 73);
			edit.commit();
		}
		return true;
	}

	private void reloadsp() {
		left.setText(keys.getName(sp.getInt("left_key", 65)));
		right.setText(keys.getName(sp.getInt("right_key", 68)));
		up.setText(keys.getName(sp.getInt("up_key", 87)));
		down.setText(keys.getName(sp.getInt("down_key", 83)));
		a.setText(keys.getName(sp.getInt("a_key", 74)));
		b.setText(keys.getName(sp.getInt("b_key", 75)));
		c.setText(keys.getName(sp.getInt("c_key", 76)));
		d.setText(keys.getName(sp.getInt("d_key", 73)));
	}

	class SelectAction implements android.view.View.OnClickListener {

		EditText action;
		String s;

		Key[] data;

		String[] options = { getResources().getString(R.string.text_arrows),
				getResources().getString(R.string.text_characters),
				getResources().getString(R.string.text_numbers),
				getResources().getString(R.string.text_specials) };

		public SelectAction(EditText action, String s) {
			this.action = action;
			this.s = s;
		}

		@Override
		public void onClick(View v) {
			AlertDialog.Builder selectKeyGroup = new AlertDialog.Builder(
					ButtonSettingsActivity.this);
			selectKeyGroup.setTitle(getResources().getString(
					R.string.text_btActionSelect)
					+ ""
					+ getResources().getString(R.string.text_btActionGroup));
			final AlertDialog.Builder selectKey = new AlertDialog.Builder(
					ButtonSettingsActivity.this);

			selectKeyGroup.setItems(options,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							switch (which) {
							case 0:
								data = keys.getArrows();
								selectKey.setTitle(getResources().getString(
										R.string.text_btActionSelect)
										+ " "
										+ getResources().getString(
												R.string.text_arrows));
								break;
							case 1:
								data = keys.getCharacters();
								selectKey.setTitle(getResources().getString(
										R.string.text_btActionSelect)
										+ " "
										+ getResources().getString(
												R.string.text_characters));
								break;
							case 2:
								data = keys.getNumbers();
								selectKey.setTitle(getResources().getString(
										R.string.text_btActionSelect)
										+ " "
										+ getResources().getString(
												R.string.text_numbers));
								break;
							case 3:
								data = keys.getSpecials();
								selectKey.setTitle(getResources().getString(
										R.string.text_btActionSelect)
										+ " "
										+ getResources().getString(
												R.string.text_specials));
								break;
							}

							selectKey.setItems(Keys.getNames(data),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											action.setText(data[which]
													.getName());
											edit.putInt(s,
													data[which].getKeyEvent());
											edit.commit();
										}
									});
							selectKey.show();

						}

					});
			selectKeyGroup.show();
		}

	}

}
