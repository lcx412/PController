package qrnu.pcontroller.client;

import java.util.HashSet;

import qrnu.pcontroller.action.PControllerAction;

public class PControllerActionReceivers implements Runnable {
	private PControllerConnection conn;
	public boolean flag = true;
	private HashSet<InterfaceActionOnReceive> actionReceivers;

	public PControllerActionReceivers() {
		// TODO Auto-generated method stub
		this.conn = ConnectionActivity.conn;
		this.actionReceivers = new HashSet<InterfaceActionOnReceive>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (flag) {
			try {
				PControllerAction action = conn.receiveAction();

				synchronized (this.actionReceivers) {
					for (InterfaceActionOnReceive actionReceiver : this.actionReceivers) {
						actionReceiver.receiveAction(action);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void registerActionReceiver(InterfaceActionOnReceive actionReceiver) {
		synchronized (this.actionReceivers) {
			this.actionReceivers.add(actionReceiver);
		}
	}

	public void unregisterActionReceiver(InterfaceActionOnReceive actionReceiver) {
		synchronized (this.actionReceivers) {
			this.actionReceivers.remove(actionReceiver);
		}
	}
}
