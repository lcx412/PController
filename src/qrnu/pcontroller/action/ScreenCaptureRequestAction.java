package qrnu.pcontroller.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ScreenCaptureRequestAction extends PControllerAction {

	public static boolean TOUCHPAD = true;
	public static boolean PPT = false;
	public boolean type;
	public short width;
	public short height;

	public ScreenCaptureRequestAction(boolean type, short width, short height) {
		this.type = type;
		this.width = width;
		this.height = height;
	}

	public static ScreenCaptureRequestAction parse(DataInputStream dis)
			throws IOException {
		boolean type = dis.readBoolean();
		short width = dis.readShort();
		short height = dis.readShort();

		return new ScreenCaptureRequestAction(type, width, height);
	}

	public void toDataOutputStream(DataOutputStream dos) throws IOException {
		dos.writeByte(SCREEN_CAPTURE_REQUEST);
		dos.writeBoolean(this.type);
		dos.writeShort(this.width);
		dos.writeShort(this.height);
	}
}
