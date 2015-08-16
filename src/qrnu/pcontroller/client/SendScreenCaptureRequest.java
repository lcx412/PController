package qrnu.pcontroller.client;


public class SendScreenCaptureRequest implements Runnable {
	public boolean flag = true;
	public PPTControlActivity pptControlActivity;

	public SendScreenCaptureRequest(PPTControlActivity pptControlActivity) {
		// TODO Auto-generated method stub
		this.pptControlActivity = pptControlActivity;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (flag) {
			try {
				pptControlActivity.ppttouchView.screenCaptureRequest();
				Thread.sleep(1500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
