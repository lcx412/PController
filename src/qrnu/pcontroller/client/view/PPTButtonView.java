package qrnu.pcontroller.client.view;

import qrnu.pcontroller.action.MouseClickAction;
import qrnu.pcontroller.client.PPTControlActivity;
import qrnu.pcontroller.client.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class PPTButtonView extends Button {
	private PPTControlActivity pptcontrolActivity;

	private byte button;
	private boolean holding;
	private Vibrator vibrator;
	private SharedPreferences sp;

	public PPTButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.pptcontrolActivity = (PPTControlActivity) context;
		this.vibrator = (Vibrator) pptcontrolActivity
				.getSystemService(Context.VIBRATOR_SERVICE);
		this.sp = this.pptcontrolActivity.getSharedPreferences("PController",
				Context.MODE_PRIVATE);

		switch (this.getId()) {
		case R.id.pptleftButtonView:
			this.button = MouseClickAction.BUTTON_LEFT;
			break;
		case R.id.pptrightButtonView:
			this.button = MouseClickAction.BUTTON_RIGHT;
			break;
		default:
			this.button = MouseClickAction.BUTTON_NONE;
			break;
		}

		this.holding = false;

	}

	public boolean isHolding() {
		return holding;
	}

	public void setHolding(boolean holding) {
		this.holding = holding;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {
			this.onTouchDown(event);
			break;
		}

		case MotionEvent.ACTION_UP: {
			this.onTouchUp(event);
			break;
		}

		default:
			break;
		}

		return true;
	}

	private void onTouchDown(MotionEvent event) {

		pptcontrolActivity.mouseClick(this.button, MouseClickAction.STATE_DOWN);

		this.setPressed(true);

		vibrate(50);

	}

	private void onTouchUp(MotionEvent event) {
		{
			pptcontrolActivity.mouseClick(this.button,
					MouseClickAction.STATE_UP);
			if (sp.getBoolean("screen_capture", false))
				this.pptcontrolActivity.ppttouchView.screenCaptureRequest();
			this.setPressed(false);
		}
	}

	public void vibrate(long l) {

		if (pptcontrolActivity.feedbackVibration) {
			this.vibrator.vibrate(l);
		}
	}

}