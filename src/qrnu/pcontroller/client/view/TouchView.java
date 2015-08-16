package qrnu.pcontroller.client.view;

import qrnu.pcontroller.action.MouseClickAction;
import qrnu.pcontroller.action.ScreenCaptureRequestAction;
import qrnu.pcontroller.action.ScreenCaptureResponseAction;
import qrnu.pcontroller.client.R;
import qrnu.pcontroller.client.TouchPadActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TouchView extends ImageView {
	private TouchPadActivity touchPadActivity;
	private SharedPreferences sp;
	private Vibrator vibrator;

	private ButtonView leftButtonView;

	private boolean holdPossible;

	private long holdDelay;
	private float isMobileDistance;

	private boolean mouseMoveOrWheel;

	private float moveSensitivity;
	private float moveDownX;
	private float moveDownY;
	private float movePreviousX;
	private float movePreviousY;
	private float moveResultX;
	private float moveResultY;

	private float wheelSensitivity;
	private float wheelPrevious;
	private float wheelResult;
	private float wheelBarWidth;

	private boolean screenCaptureEnabled;
	private Bitmap currentBitmap;
	private Bitmap newBitmap;
	private Paint paint;
	private boolean b = false;

	private Resources resources;
	private Bitmap cursorBitmap;

	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.resources = getContext().getResources();
		this.cursorBitmap = BitmapFactory.decodeResource(resources,
				R.drawable.cursor);
		this.touchPadActivity = (TouchPadActivity) context;
		this.vibrator = (Vibrator) touchPadActivity
				.getSystemService(Context.VIBRATOR_SERVICE);
		sp = touchPadActivity.getSharedPreferences("PController",
				Context.MODE_PRIVATE);

		this.paint = new Paint();
		this.paint.setColor(Color.RED);
		this.paint.setAntiAlias(true);
		this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
		this.paint.setStrokeWidth(2);
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		this.leftButtonView = (ButtonView) this.touchPadActivity
				.findViewById(R.id.leftButtonView);
	}

	protected synchronized void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);

		if (visibility == VISIBLE) {
			this.reloadPreferences();
		} else {
			// this.setImageBitmap(null);

			if (this.currentBitmap != null) {
				this.currentBitmap.recycle();
				this.currentBitmap = null;
			}
			if (this.newBitmap != null) {
				this.newBitmap.recycle();
				this.newBitmap = null;
			}
		}
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

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
			if (this.screenCaptureEnabled)
				this.screenCaptureRequest();
			break;
		}

		default:
			break;
		}

		return true;
	}

	private void onTouchDown(MotionEvent event) {
		this.mouseMoveOrWheel = event.getX() < this.getWidth()
				- this.wheelBarWidth;

		if (this.mouseMoveOrWheel) {
			this.onTouchDownMouseMove(event);
		} else {
			this.onTouchDownMouseWheel(event);
		}
	}

	private void onTouchDownMouseMove(MotionEvent event) {
		this.moveDownX = this.movePreviousX = event.getRawX();
		this.moveDownY = this.movePreviousY = event.getRawY();

		this.moveResultX = 0;
		this.moveResultY = 0;

		this.holdPossible = true;
	}

	private void onTouchDownMouseWheel(MotionEvent event) {
		this.wheelPrevious = event.getRawY();
		this.wheelResult = 0;
	}

	private void onTouchMove(MotionEvent event) {
		if (this.mouseMoveOrWheel) {
			this.onTouchMoveMouseMove(event);
		} else {
			this.onTouchMoveMouseWheel(event);
		}
	}

	private void onTouchMoveMouseMove(MotionEvent event) {
		if (this.holdPossible) {
			if (this.getDistanceFromDown(event) >= this.isMobileDistance) {
				this.holdPossible = false;
			} else if (event.getEventTime() - event.getDownTime() >= this.holdDelay) {
				this.touchPadActivity.mouseClick(MouseClickAction.BUTTON_LEFT,
						MouseClickAction.STATE_DOWN);

				this.holdPossible = false;
				vibrate(100);
				this.leftButtonView.setPressed(true);
				this.leftButtonView.setHolding(true);
			}
		}

		float moveRawX = event.getRawX() - this.movePreviousX;
		float moveRawY = event.getRawY() - this.movePreviousY;

		moveRawX *= this.moveSensitivity;
		moveRawY *= this.moveSensitivity;

		moveRawX += this.moveResultX;
		moveRawY += this.moveResultY;

		int moveXFinal = Math.round(moveRawX);
		int moveYFinal = Math.round(moveRawY);

		if (moveXFinal != 0 || moveYFinal != 0) {
			this.touchPadActivity.mouseMove(moveXFinal, moveYFinal);
		}

		this.moveResultX = moveRawX - moveXFinal;
		this.moveResultY = moveRawY - moveYFinal;

		this.movePreviousX = event.getRawX();
		this.movePreviousY = event.getRawY();
	}

	private void onTouchMoveMouseWheel(MotionEvent event) {
		float wheelRaw = event.getRawY() - this.wheelPrevious;
		wheelRaw *= this.wheelSensitivity;
		wheelRaw += this.wheelResult;
		int wheelFinal = Math.round(wheelRaw);

		if (wheelFinal != 0) {
			this.touchPadActivity.mouseWheel(wheelFinal);
		}

		this.wheelResult = wheelRaw - wheelFinal;
		this.wheelPrevious = event.getRawY();
	}

	private void onTouchUp(MotionEvent event) {
		if (this.mouseMoveOrWheel) {
			this.onTouchUpMouseMove(event);
		} else {
			this.onTouchUpMouseWheel(event);
		}
	}

	private void onTouchUpMouseMove(MotionEvent event) {
		if (event.getEventTime() - event.getDownTime() < this.holdDelay
				&& this.getDistanceFromDown(event) < this.isMobileDistance) {
			if (this.leftButtonView.isPressed()) {
				this.touchPadActivity.mouseClick(MouseClickAction.BUTTON_LEFT,
						MouseClickAction.STATE_UP);
				vibrate(100);
				this.leftButtonView.setPressed(false);
				this.leftButtonView.setHolding(false);
			} else {
				this.touchPadActivity.mouseClick(MouseClickAction.BUTTON_LEFT,
						MouseClickAction.STATE_DOWN);
				vibrate(50);
				this.leftButtonView.setPressed(true);

				this.postDelayed(new Runnable() {
					public void run() {
						TouchView.this.touchPadActivity.mouseClick(
								MouseClickAction.BUTTON_LEFT,
								MouseClickAction.STATE_UP);
						TouchView.this.leftButtonView.setPressed(false);
					}
				}, 50);
			}
		}
	}

	private void onTouchUpMouseWheel(MotionEvent event) {

	}

	protected void onDraw(Canvas canvas) {
		try {
			super.onDraw(canvas);

			if (this.screenCaptureEnabled) {
				if (this.b) {
					synchronized (currentBitmap) {
						canvas.drawBitmap(currentBitmap, 5, 5, null);
					}
					this.b = false;
				} else
					this.screenCaptureRequest();
				canvas.drawBitmap(cursorBitmap, this.getWidth() / 2,
						3 * this.getHeight() / 8+5, null);
				// canvas.drawCircle((this.getWidth()) / 2,
				// 3 * this.getHeight() / 8, 2, this.paint);
				// canvas.drawLine(0, 3 * this.getHeight() / 4, this.getWidth(),
				// 3 * this.getHeight() / 4, this.paint);
			}
			canvas.drawLine(this.getWidth() - this.wheelBarWidth, 75,
					this.getWidth() - this.wheelBarWidth,
					this.getHeight() - 135, this.paint);
		} catch (Exception e) {
		}
	}

	public synchronized void receiveAction(ScreenCaptureResponseAction action) {
		if (this.newBitmap != null) {
			this.newBitmap.recycle();
		}

		this.newBitmap = BitmapFactory.decodeByteArray(action.data, 0,
				action.data.length);

		this.post(new Runnable() {
			public void run() {
				TouchView touchView = TouchView.this;

				if (touchView.currentBitmap != null) {
					touchView.currentBitmap.recycle();
				}

				touchView.currentBitmap = touchView.newBitmap;
				touchView.newBitmap = null;
				if (touchView.currentBitmap != null)
				// touchView.setImageBitmap(touchView.currentBitmap);
				{
					if (!touchView.currentBitmap.isRecycled())
						touchView.invalidate();
					touchView.b = true;
				}
			}
		});
	}

	public void screenCaptureRequest() {
		if (this.screenCaptureEnabled) {
			try {
				this.touchPadActivity.conn
						.sendAction(new ScreenCaptureRequestAction(
								ScreenCaptureRequestAction.TOUCHPAD,
								(short) (this.getWidth()-10), (short) (3 * this
										.getHeight() / 4)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private double getDistanceFromDown(MotionEvent event) {
		return Math.sqrt(Math.pow(event.getRawX() - this.moveDownX, 2)
				+ Math.pow(event.getRawY() - this.moveDownY, 2));
	}

	private void reloadPreferences() {
		float screenDensity = this.getResources().getDisplayMetrics().density;
		this.holdDelay = Long.parseLong(this.sp.getString("hold_delay", "500"));
		this.isMobileDistance = Float.parseFloat(this.sp.getString(
				"is_mobile_distance", "8"));
		this.isMobileDistance *= screenDensity;
		this.moveSensitivity = Float.parseFloat(this.sp.getString(
				"move_sensitivity", "20")) / 10;
		this.moveSensitivity /= screenDensity;
		this.wheelSensitivity = Float.parseFloat(this.sp.getString(
				"wheel_sensitivity", "25")) / 100;
		this.wheelSensitivity /= screenDensity;
		this.wheelBarWidth = Float.parseFloat(this.sp.getString(
				"wheel_bar_width", "50"))
				* (touchPadActivity.getWindowManager().getDefaultDisplay()
						.getWidth() / 480f) * screenDensity;
		this.setKeepScreenOn(this.sp.getBoolean("keep_screen_on", false));
		this.screenCaptureEnabled = this.sp.getBoolean("screen_capture", false);
	}

	public void vibrate(long l) {

		if (touchPadActivity.feedbackVibration) {
			this.vibrator.vibrate(l);
		}
	}

}
