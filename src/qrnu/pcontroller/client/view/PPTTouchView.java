package qrnu.pcontroller.client.view;

import qrnu.pcontroller.action.DirectKeyAction;
import qrnu.pcontroller.action.MouseClickAction;
import qrnu.pcontroller.action.ScreenCaptureRequestAction;
import qrnu.pcontroller.action.ScreenCaptureResponseAction;
import qrnu.pcontroller.client.ConnectionActivity;
import qrnu.pcontroller.client.PControllerConnection;
import qrnu.pcontroller.client.PPTControlActivity;
import qrnu.pcontroller.client.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PPTTouchView extends ImageView {
	private PPTControlActivity pptcontrolActivity;
	private SharedPreferences sp;
	private Vibrator vibrator;
	private PControllerConnection conn;

	private PPTButtonView pptleftButtonView;

	private boolean holdPossible;

	private long holdDelay;
	private float isMobileDistance;

	private float moveSensitivity;
	private float moveDownX;
	private float moveDownY;
	private float movePreviousX;
	private float movePreviousY;
	private float moveResultX;
	private float moveResultY;

	private boolean screenCaptureEnabled;
	private Bitmap currentBitmap;
	private Bitmap newBitmap;
	private boolean b = false;

	public PPTTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.pptcontrolActivity = (PPTControlActivity) context;
		this.vibrator = (Vibrator) pptcontrolActivity
				.getSystemService(Context.VIBRATOR_SERVICE);
		this.conn = ConnectionActivity.conn;
		this.sp = this.pptcontrolActivity.getSharedPreferences("PController",
				Context.MODE_PRIVATE);
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		this.pptleftButtonView = (PPTButtonView) this.pptcontrolActivity
				.findViewById(R.id.pptleftButtonView);
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
			break;
		}

		default:
			break;
		}

		return true;
	}

	private void onTouchDown(MotionEvent event) {
		this.onTouchDownMouseMove(event);
	}

	private void onTouchDownMouseMove(MotionEvent event) {
		this.moveDownX = this.movePreviousX = event.getRawX();
		this.moveDownY = this.movePreviousY = event.getRawY();

		this.moveResultX = 0;
		this.moveResultY = 0;

		this.holdPossible = true;
	}

	private void onTouchMove(MotionEvent event) {
		this.onTouchMoveMouseMove(event);
	}

	private void onTouchMoveMouseMove(MotionEvent event) {
		if (this.holdPossible) {
			if (this.getDistanceFromDown(event) >= this.isMobileDistance) {
				this.holdPossible = false;
			} else if (event.getEventTime() - event.getDownTime() >= this.holdDelay) {
				try {
					conn.sendAction(new DirectKeyAction(17, 80, (byte) 0));
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.postDelayed(new Runnable() {
					public void run() {
						PPTTouchView.this.pptcontrolActivity.mouseClick(
								MouseClickAction.BUTTON_LEFT,
								MouseClickAction.STATE_DOWN);
					}
				}, 50);
				this.holdPossible = false;
				vibrate(100);
				this.pptleftButtonView.setPressed(true);
				this.pptleftButtonView.setHolding(true);
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
			this.pptcontrolActivity.mouseMove(moveXFinal, moveYFinal);
		}

		this.moveResultX = moveRawX - moveXFinal;
		this.moveResultY = moveRawY - moveYFinal;

		this.movePreviousX = event.getRawX();
		this.movePreviousY = event.getRawY();
	}

	private void onTouchUp(MotionEvent event) {
		this.onTouchUpMouseMove(event);
		if (this.screenCaptureEnabled)
			this.screenCaptureRequest();
	}

	private void onTouchUpMouseMove(MotionEvent event) {
		if (event.getEventTime() - event.getDownTime() < this.holdDelay
				&& this.getDistanceFromDown(event) < this.isMobileDistance) {

			this.pptcontrolActivity.mouseClick(MouseClickAction.BUTTON_LEFT,
					MouseClickAction.STATE_DOWN);
			vibrate(50);
			this.pptleftButtonView.setPressed(true);

			this.postDelayed(new Runnable() {
				public void run() {
					PPTTouchView.this.pptcontrolActivity.mouseClick(
							MouseClickAction.BUTTON_LEFT,
							MouseClickAction.STATE_UP);
					PPTTouchView.this.pptleftButtonView.setPressed(false);
				}
			}, 50);

		} else {
			this.pptleftButtonView.setPressed(false);
			this.pptleftButtonView.setHolding(false);
			PPTTouchView.this.pptcontrolActivity.mouseClick(
					MouseClickAction.BUTTON_LEFT, MouseClickAction.STATE_UP);
			try {
				conn.sendAction(new DirectKeyAction(17, 65, (byte) 0));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	protected void onDraw(Canvas canvas) {
		try {
			super.onDraw(canvas);

			if (this.screenCaptureEnabled) {
				if (this.b) {
					synchronized (currentBitmap) {
						canvas.drawBitmap(
								currentBitmap,
								0,
								(this.getHeight() - 3 * this.getWidth() / 4) / 2,
								null);
					}
					this.b = false;
				} else
					this.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							screenCaptureRequest();
						}
					}, 50);
			}
		} catch (Exception e) {
		}
	}

	public void screenCaptureRequest() {
		if (this.screenCaptureEnabled) {

			this.postDelayed(new Runnable() {
				public void run() {
					try {
						pptcontrolActivity.conn
								.sendAction(new ScreenCaptureRequestAction(
										ScreenCaptureRequestAction.PPT,
										(short) (PPTTouchView.this.getWidth()),
										(short) (3 * PPTTouchView.this
												.getWidth() / 4)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 150);
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
				PPTTouchView ppttouchView = PPTTouchView.this;

				if (ppttouchView.currentBitmap != null) {
					ppttouchView.currentBitmap.recycle();
				}

				ppttouchView.currentBitmap = ppttouchView.newBitmap;
				ppttouchView.newBitmap = null;
				if (ppttouchView.currentBitmap != null)
				// touchView.setImageBitmap(touchView.currentBitmap);
				{
					if (!ppttouchView.currentBitmap.isRecycled())
						ppttouchView.invalidate();
					ppttouchView.b = true;
				}
			}
		});
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
		this.setKeepScreenOn(this.sp.getBoolean("keep_screen_on", false));
		this.screenCaptureEnabled = this.sp.getBoolean("screen_capture", false);
	}

	public void vibrate(long l) {

		if (pptcontrolActivity.feedbackVibration) {
			this.vibrator.vibrate(l);
		}
	}

}
