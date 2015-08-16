package qrnu.pcontroller.action;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileExploreRequestAction extends PControllerAction {
	public String directory;
	public String file;
	public boolean showHidden;

	public FileExploreRequestAction(String directory, String file,
			boolean showHidden) {
		this.directory = directory;
		this.file = file;
		this.showHidden = showHidden;
	}

	public static FileExploreRequestAction parse(DataInputStream dis)
			throws IOException {
		String directory = dis.readUTF();
		String file = dis.readUTF();
		boolean showHidden = dis.readBoolean();

		return new FileExploreRequestAction(directory, file, showHidden);
	}

	public void toDataOutputStream(DataOutputStream dos) throws IOException {
		dos.writeByte(FILE_EXPLORE_REQUEST);
		dos.writeUTF(this.directory);
		dos.writeUTF(this.file);
		dos.writeBoolean(this.showHidden);
	}
}
