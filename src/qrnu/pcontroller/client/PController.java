package qrnu.pcontroller.client;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;

public class PController extends Application {

	public SharedPreferences sp;
	public MediaPlayer mpOn;
	public Vibrator vibrator;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		sp = this.getSharedPreferences("PController", Context.MODE_PRIVATE);
		this.mpOn = MediaPlayer.create(this, R.raw.on);
		this.vibrator = (Vibrator) this
				.getSystemService(Context.VIBRATOR_SERVICE);

	}

}
