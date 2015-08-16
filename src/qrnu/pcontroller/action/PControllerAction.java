package qrnu.pcontroller.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ProtocolException;

public abstract class PControllerAction {
	public static final byte MOUSE_MOVE = 0;
	public static final byte MOUSE_CLICK = 1;
	public static final byte MOUSE_WHEEL = 2;
	public static final byte AUTHENTIFICATION = 3;
	public static final byte AUTHENTIFICATION_RESPONSE = 4;
	public static final byte DIRECTKEY = 5;
	public static final byte FILE_EXPLORE_REQUEST = 6;
	public static final byte FILE_EXPLORE_RESPONSE = 7;
	public static final byte SCREEN_CAPTURE_REQUEST = 8;
	public static final byte SCREEN_CAPTURE_RESPONSE = 9;
	public static final byte KEYBOARD = 10;

	public static PControllerAction parse(DataInputStream dis)
			throws IOException {
		byte type = dis.readByte();

		switch (type) {
		case MOUSE_MOVE:
			return MouseMoveAction.parse(dis);
		case MOUSE_CLICK:
			return MouseClickAction.parse(dis);
		case MOUSE_WHEEL:
			return MouseWheelAction.parse(dis);
		case AUTHENTIFICATION:
			return AuthentificationAction.parse(dis);
		case AUTHENTIFICATION_RESPONSE:
			return AuthentificationResponseAction.parse(dis);
		case DIRECTKEY:
			return DirectKeyAction.parse(dis);
		case FILE_EXPLORE_REQUEST:
			return FileExploreRequestAction.parse(dis);
		case FILE_EXPLORE_RESPONSE:
			return FileExploreResponseAction.parse(dis);
		case SCREEN_CAPTURE_REQUEST:
			return ScreenCaptureRequestAction.parse(dis);
		case SCREEN_CAPTURE_RESPONSE:
			return ScreenCaptureResponseAction.parse(dis);
		case KEYBOARD:
			return KeyboardAction.parse(dis);
		default:
			throw new ProtocolException();
		}
	}

	public abstract void toDataOutputStream(DataOutputStream dos)
			throws IOException;
}
