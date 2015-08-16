package qrnu.pcontroller.client.view;

import qrnu.pcontroller.client.JoyStickActivity;
import qrnu.pcontroller.client.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class KeysView extends View {

	private JoyStickActivity joyStick;
	private Bitmap[] buttonIdleBitmap;
	private Bitmap[] buttonPressedBitmap;
	private Bitmap[] buttonBitmap;
	private boolean[] buttonstate = { false, false, false, false, false, false,
			false, false };
	float buttonSize;
	float bbuttonSize;
	float bitmapScale;
	float pixelSizeX;
	float pixelSizeY;
	float ppixelSizeY;

	public KeysView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		joyStick = (JoyStickActivity) context;
		init();
	}

	public KeysView(Context context, AttributeSet attrs) {
		super(context, attrs);
		joyStick = (JoyStickActivity) context;
		init();
	}

	public KeysView(Context context) {
		super(context);
		joyStick = (JoyStickActivity) context;
		init();
	}

	private void init() {
		Resources resources = getContext().getResources();
		buttonIdleBitmap = new Bitmap[8];
		buttonPressedBitmap = new Bitmap[8];
		buttonBitmap = new Bitmap[8];
		buttonIdleBitmap[0] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_up);
		buttonIdleBitmap[1] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_left);
		buttonIdleBitmap[2] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_right);
		buttonIdleBitmap[3] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_down);
		buttonIdleBitmap[4] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_a);
		buttonIdleBitmap[5] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_b);
		buttonIdleBitmap[6] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_c);
		buttonIdleBitmap[7] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_d);
		buttonPressedBitmap[0] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_up_pressed);
		buttonPressedBitmap[1] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_left_pressed);
		buttonPressedBitmap[2] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_right_pressed);
		buttonPressedBitmap[3] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_down_pressed);
		buttonPressedBitmap[4] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_a_pressed);
		buttonPressedBitmap[5] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_b_pressed);
		buttonPressedBitmap[6] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_c_pressed);
		buttonPressedBitmap[7] = BitmapFactory.decodeResource(resources,
				R.drawable.gamecontrol_button_d_pressed);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		buttonBitmap[0] = getBitmapForButton(0);
		buttonBitmap[1] = getBitmapForButton(1);
		buttonBitmap[2] = getBitmapForButton(2);
		buttonBitmap[3] = getBitmapForButton(3);
		buttonBitmap[4] = getBitmapForButton(4);
		buttonBitmap[5] = getBitmapForButton(5);
		buttonBitmap[6] = getBitmapForButton(6);
		buttonBitmap[7] = getBitmapForButton(7);

		pixelSizeX = canvas.getClipBounds().width();
		pixelSizeY = canvas.getClipBounds().height();
		ppixelSizeY = pixelSizeY / 2f;

		float buttonSizeX = pixelSizeX / 7f;
		float buttonSizeY = pixelSizeY / 3f;
		buttonSize = buttonSizeX <= buttonSizeY ? buttonSizeX : buttonSizeY;
		bbuttonSize = buttonSize / 2f;
		bitmapScale = buttonSize / (buttonBitmap[0].getWidth());

		canvas.scale(bitmapScale, bitmapScale);

		canvas.drawBitmap(buttonBitmap[0], buttonSize / bitmapScale,
				(ppixelSizeY - 3 * bbuttonSize) / bitmapScale, null);
		canvas.drawBitmap(buttonBitmap[1], 0, (ppixelSizeY - bbuttonSize)
				/ bitmapScale, null);
		canvas.drawBitmap(buttonBitmap[2], 2 * buttonSize / bitmapScale,
				(ppixelSizeY - bbuttonSize) / bitmapScale, null);
		canvas.drawBitmap(buttonBitmap[3], buttonSize / bitmapScale,
				(ppixelSizeY + bbuttonSize) / bitmapScale, null);
		canvas.drawBitmap(buttonBitmap[4], (pixelSizeX - 2 * buttonSize)
				/ bitmapScale, (ppixelSizeY - 3 * bbuttonSize) / bitmapScale,
				null);
		canvas.drawBitmap(buttonBitmap[5], (pixelSizeX - 3 * buttonSize)
				/ bitmapScale, (ppixelSizeY - bbuttonSize) / bitmapScale, null);
		canvas.drawBitmap(buttonBitmap[6], (pixelSizeX - buttonSize)
				/ bitmapScale, (ppixelSizeY - bbuttonSize) / bitmapScale, null);
		canvas.drawBitmap(buttonBitmap[7], (pixelSizeX - 2 * buttonSize)
				/ bitmapScale, (ppixelSizeY + bbuttonSize) / bitmapScale, null);
	}

	private Bitmap getBitmapForButton(int buttonID) {
		return buttonstate[buttonID] ? buttonPressedBitmap[buttonID]
				: buttonIdleBitmap[buttonID];
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);

		setMeasuredDimension(chosenWidth, chosenHeight);
	}

	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPreferredSize();
		}
	}

	private int getPreferredSize() {
		return 200;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int buttonID = -1;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			buttonID = getButtonIDByCoords(event.getX(), event.getY());
			if (buttonID != -1) {
				pressButton(buttonID);
			}
			return true;
		case MotionEvent.ACTION_UP:
			// buttonID = getButtonIDByCoords(event.getX(), event.getY());
			// if(buttonID!=-1)
			// {
			// releaseButton(buttonID);
			// }
			releaseAllButtons();
			return true;
		case MotionEvent.ACTION_POINTER_DOWN:
			int index1 = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
			buttonID = getButtonIDByCoords(event.getX(index1),
					event.getY(index1));

			if (buttonID != -1) {
				pressButton(buttonID);
			}
			return true;
		case MotionEvent.ACTION_POINTER_UP:
			int index2 = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
			buttonID = getButtonIDByCoords(event.getX(index2),
					event.getY(index2));
			if (buttonID != -1) {
				releaseButton(buttonID);
			}
			return true;
		}
		return true;
	}

	private int getButtonIDByCoords(float x, float y) {

		if (y > ppixelSizeY - 3 * bbuttonSize && y < ppixelSizeY - bbuttonSize) {
			if (x > buttonSize && x < 2 * buttonSize)
				return 0;
			else if (x > pixelSizeX - 2 * buttonSize
					&& x < pixelSizeX - buttonSize)
				return 4;
		} else if (y > ppixelSizeY - bbuttonSize
				&& y < ppixelSizeY + bbuttonSize) {
			if (x < buttonSize)
				return 1;
			else if (x > 2 * buttonSize && x < 3 * buttonSize)
				return 2;
			else if (x > pixelSizeX - 3 * buttonSize
					&& x < pixelSizeX - 2 * buttonSize)
				return 5;
			else if (x > pixelSizeX - buttonSize)
				return 6;
		} else if (y > ppixelSizeY + bbuttonSize
				&& y < ppixelSizeY + 3 * bbuttonSize) {
			if (x > buttonSize && x < 2 * buttonSize)
				return 3;
			else if (x > pixelSizeX - 2 * buttonSize
					&& x < pixelSizeX - buttonSize)
				return 7;
		}

		return -1;

	}

	private void pressButton(int buttonID) {
		joyStick.pressButton(buttonID);
		buttonstate[buttonID] = true;
		invalidate();
	}

	private void releaseButton(int buttonID) {
		joyStick.releaseButton(buttonID);
		buttonstate[buttonID] = false;
		invalidate();
	}

	private void releaseAllButtons() {
		joyStick.releaseAllButtons();
		for (int i = 0; i < 8; i++)
			buttonstate[i] = false;
		invalidate();
	}
}
