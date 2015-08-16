package qrnu.pcontroller.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DirectKeyAction extends PControllerAction {
	public int directcode1;
	public int directcode2;
	public byte state;

	public DirectKeyAction(int directcode1, int directcode2, byte state) {
		this.directcode1 = directcode1;
		this.directcode2 = directcode2;
		this.state = state;
	}

	public static DirectKeyAction parse(DataInputStream dis) throws IOException {
		int directcode1 = dis.readInt();
		int directcode2 = dis.readInt();
		byte state = dis.readByte();

		return new DirectKeyAction(directcode1, directcode2, state);
	}

	public void toDataOutputStream(DataOutputStream dos) throws IOException {
		dos.writeByte(DIRECTKEY);
		dos.writeInt(this.directcode1);
		dos.writeInt(this.directcode2);
		dos.writeByte(this.state);
	}
}
