package qrnu.pcontroller.client.view;

import qrnu.pcontroller.action.MouseClickAction;
import qrnu.pcontroller.client.R;
import qrnu.pcontroller.client.TouchPadActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class ButtonView extends Button {
	private TouchPadActivity touchPadActivity;

	private byte button;
	private boolean holding;
	private long holdDelay;
	private Vibrator vibrator;
	private SharedPreferences sp;

	public ButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.touchPadActivity = (TouchPadActivity) context;
		this.vibrator = (Vibrator) touchPadActivity
				.getSystemService(Context.VIBRATOR_SERVICE);
		this.sp = this.touchPadActivity.getSharedPreferences("PController",
				Context.MODE_PRIVATE);
		
		switch (this.getId()) {
		case R.id.leftButtonView:
			this.button = MouseClickAction.BUTTON_LEFT;
			break;
		case R.id.rightButtonView:
			this.button = MouseClickAction.BUTTON_RIGHT;
			break;
		default:
			this.button = MouseClickAction.BUTTON_NONE;
			break;
		}

		this.holding = false;

		this.holdDelay = Long.parseLong(touchPadActivity.getSharedPreferences(
				"PController", Context.MODE_PRIVATE).getString("hold_delay",
				"1000"));
	}

	public boolean isHolding() {
		return holding;
	}

	public void setHolding(boolean holding) {
		this.holding = holding;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE: {
			this.onTouchMove(event);
			break;
		}

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
		if (!this.holding) {
			touchPadActivity.mouseClick(this.button,
					MouseClickAction.STATE_DOWN);

			this.setPressed(true);

			vibrate(50);
		} else {
			this.holding = false;
		}
	}

	private void onTouchMove(MotionEvent event) {
		if (!this.holding
				&& event.getEventTime() - event.getDownTime() >= this.holdDelay) {
			this.holding = true;

			vibrate(100);
		}
	}

	private void onTouchUp(MotionEvent event) {
		if (!this.holding) {
			touchPadActivity.mouseClick(this.button, MouseClickAction.STATE_UP);
			this.setPressed(false);
		}
		if (sp.getBoolean("screen_capture", false))
			this.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					touchPadActivity.touchView.screenCaptureRequest();
				}
			}, 250);
	}

	public void vibrate(long l) {

		if (touchPadActivity.feedbackVibration) {
			this.vibrator.vibrate(l);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (this.holding) {
			this.setPressed(true);
		}
	}
}