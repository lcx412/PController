package qrnu.pcontroller.client;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import qrnu.pcontroller.action.PControllerAction;

public class PControllerConnection {
	public DataInputStream dataInputStream;
	public OutputStream outputStream;
	public Socket socket;

	public PControllerConnection(Socket socket) throws IOException {
		this.socket = socket;
		this.socket.setPerformancePreferences(1, 2, 0);
		this.socket.setTcpNoDelay(true);
		this.socket.setReceiveBufferSize(1024 * 1024);
		this.socket.setSendBufferSize(1024 * 1024);
		this.dataInputStream = new DataInputStream(this.socket.getInputStream());
		this.outputStream = this.socket.getOutputStream();
	}

	public PControllerConnection(String host) throws IOException {
		this.socket = new Socket();
		this.socket.connect(new InetSocketAddress(host, 58585), 1000);
		this.socket.setPerformancePreferences(0, 2, 1);
		this.socket.setTcpNoDelay(true);
		this.socket.setReceiveBufferSize(1024 * 1024);
		this.socket.setSendBufferSize(1024 * 1024);
		this.dataInputStream = new DataInputStream(this.socket.getInputStream());
		this.outputStream = this.socket.getOutputStream();
	}

	public void close() throws IOException {
		this.dataInputStream.close();
		this.outputStream.close();
		this.socket.shutdownInput();
		this.socket.shutdownOutput();
		this.socket.close();
	}

	public PControllerAction receiveAction() throws IOException {
		synchronized (this.dataInputStream) {
			PControllerAction action = PControllerAction
					.parse(this.dataInputStream);
			return action;
		}
	}

	public void sendAction(PControllerAction action) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		action.toDataOutputStream(new DataOutputStream(baos));

		synchronized (this.outputStream) {
			this.outputStream.write(baos.toByteArray());
			this.outputStream.flush();
		}
	}

	public InetAddress getInetAddress() {
		return this.socket.getInetAddress();
	}
}
