package com.nq.montior.servlet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice.DeviceState;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.nq.montior.bean.DeviceInfo;
import com.nq.montior.bean.Message;
import com.nq.montior.bean.ScreenShot;
import com.nq.montior.servlet.StartMontior.MonitorThread;

public class ConfigServlet extends HttpServlet {

	private String[] args;

	private boolean device = false;

	private boolean emulator = false;

	private String serial = null;

	private String filepath = null;

	private boolean landscape = false;

	public IDevice target = null;

	public IDevice[] devices;

	public ArrayList<IDevice> deviceList = new ArrayList<IDevice>();

	public AndroidDebugBridge bridge;

	public ServletContext application;

	private static Message message;

	ArrayList threadList = new ArrayList();

	ArrayList deviceInfoList = new ArrayList();

	/**
	 * Constructor of the object.
	 */
	public ConfigServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	@SuppressWarnings("static-access")
	public void init() throws ServletException {
		// Put your code here
		super.init();
		System.out.println("系统初始化开始");
		message = new Message();
		message.setContent("");
		message.setType("");
		application = this.getServletContext();
		AndroidDebugBridge.init(false /* debugger support */);
		String adbLocation = System
				.getProperty("com.android.screenshot.bindir"); //$NON-NLS-1$
		if (adbLocation != null && adbLocation.length() != 0) {
			adbLocation += File.separator + "adb"; //$NON-NLS-1$
		} else {
			adbLocation = "adb"; //$NON-NLS-1$
		}

		bridge = AndroidDebugBridge
				.createBridge(adbLocation, true /* forceNewBridge */);

		if (!bridge.hasInitialDeviceList()) {
			int count = 0;
			while (bridge.hasInitialDeviceList() == false) {
				try {
					Thread.sleep(100);
					count++;
				} catch (InterruptedException e) {
					// pass
				}

				// let's not wait > 10 sec.
				if (count > 100) {
					System.err.println("Timeout getting device list!");
					return;
				}
			}
		}

		bridge.addDeviceChangeListener(new IDeviceChangeListener() {

			public void deviceDisconnected(IDevice arg0) {
				// TODO Auto-generated method stub
				System.out.println(arg0.getSerialNumber() + "断开连接");
				message.setType("deviceDisconnected");
				message.setContent(arg0.getSerialNumber());
				System.out.println("下线：" + devices.length);
				devices = bridge.getDevices();
				application.setAttribute("devices", devices);
				ArrayList<DeviceInfo> deviceInfos = (ArrayList<DeviceInfo>) application
						.getAttribute("deviceInfoList");
				for (int i = 0; i < deviceInfos.size(); i++) {
					DeviceInfo deviceInfo = deviceInfos.get(i);
					if (deviceInfo.getSerialNumber().equals(
							arg0.getSerialNumber())) {
						deviceInfos.remove(i);
						application.setAttribute("deviceInfoList", deviceInfos);
						break;
					}
				}
			}

			public void deviceConnected(IDevice arg0) {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				RawImage rawImage;
				try {
					rawImage = arg0.getScreenshot();
					int width = rawImage.width;
					int height = rawImage.height;
					System.out.println(arg0.getSerialNumber()
							+ "连接️连接"
							+ "设备名称："
							+ arg0.getPropertySync(IDevice.PROP_DEVICE_MANUFACTURER)
							+ " "
							+ arg0.getPropertySync(IDevice.PROP_DEVICE_MODEL)
							+ "分辨率:" + width + "*" + height + "版本:"
							+ arg0.getPropertySync(IDevice.PROP_BUILD_VERSION));
					DeviceInfo deviceInfo = new DeviceInfo();
					deviceInfo.setManufacturer(arg0
							.getPropertySync(IDevice.PROP_DEVICE_MANUFACTURER));
					deviceInfo.setModel(arg0
							.getPropertySync(IDevice.PROP_DEVICE_MODEL));
					deviceInfo.setSerialNumber(arg0.getSerialNumber());
					deviceInfo.setVersion(arg0
							.getPropertySync(IDevice.PROP_BUILD_VERSION));
					deviceInfo.setWidth(width);
					deviceInfo.setHeight(height);
					double x = width / 240.0;
					deviceInfo.setFactorWidth(x);
					double y = height / 320.0;
					deviceInfo.setFactorHeith(y);
					deviceInfoList.add(deviceInfo);

				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AdbCommandRejectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ShellCommandUnresponsiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				application.setAttribute("deviceInfoList", deviceInfoList);
				message.setType("deviceConnected");
				message.setContent(arg0.getSerialNumber());
				// deviceList.add(arg0);
				MonitorThread monitorThread = new MonitorThread(arg0,
						getServletConfig().getServletContext().getRealPath("/"));
				monitorThread.start();

			}

			public void deviceChanged(IDevice arg0, int arg1) {
				// TODO Auto-generated method stub
				devices = bridge.getDevices();
				System.out.println("设备有变化");
				application.setAttribute("devices", devices);
				System.out.println("上线：" + devices.length);
			}
		});

		// now get the devices
		devices = bridge.getDevices();
		for (int i = 0; i < devices.length; i++) {
			RawImage rawImage;
			try {
				rawImage = devices[i].getScreenshot();
				int width = rawImage.width;
				int height = rawImage.height;
				System.out
						.println(devices[i].getSerialNumber()
								+ "连接️连接"
								+ "设备名称："
								+ devices[i]
										.getPropertySync(IDevice.PROP_DEVICE_MANUFACTURER)
								+ " "
								+ devices[i]
										.getPropertySync(IDevice.PROP_DEVICE_MODEL)
								+ "分辨率:"
								+ width
								+ "*"
								+ height
								+ "版本:"
								+ devices[i]
										.getPropertySync(IDevice.PROP_BUILD_VERSION));
				DeviceInfo deviceInfo = new DeviceInfo();
				deviceInfo.setManufacturer(devices[i]
						.getPropertySync(IDevice.PROP_DEVICE_MANUFACTURER));
				deviceInfo.setModel(devices[i]
						.getPropertySync(IDevice.PROP_DEVICE_MODEL));
				deviceInfo.setSerialNumber(devices[i].getSerialNumber());
				deviceInfo.setVersion(devices[i]
						.getPropertySync(IDevice.PROP_BUILD_VERSION));
				deviceInfo.setWidth(width);
				deviceInfo.setHeight(height);
				double x = width / 240.0;
				deviceInfo.setFactorWidth(x);
				double y = height / 320.0;
				deviceInfo.setFactorHeith(y);
				deviceInfoList.add(deviceInfo);

			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AdbCommandRejectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ShellCommandUnresponsiveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			application.setAttribute("deviceInfoList", deviceInfoList);
			message.setType("deviceConnected");
			message.setContent(devices[i].getSerialNumber());
			// deviceList.add(arg0);
			MonitorThread monitorThread = new MonitorThread(devices[i],
					getServletConfig().getServletContext().getRealPath("/"));
			monitorThread.start();

			// MonitorThread monitorThread = new MonitorThread(devices[i],
			// getServletConfig().getServletContext().getRealPath("/"));
			// monitorThread.start();
		}
		application.setAttribute("bridge", bridge);
		application.setAttribute("devices", devices);
		System.out.println("系统初始化结束");

		// this.monitorDevice();
	}

	// public void monitorDevice() {
	// System.out.println("进入调用了");
	// new Thread(new Runnable() {
	// public void run() {
	// while (true) {
	//
	// if (message != null) {
	// // System.out.println("message:" + message.getType() +
	// // ","
	// // + message.getContent());
	// if (message.getType().contains("deviceDisconnected")) {
	//
	// if (threadList != null) {
	// int count = threadList.size();
	// // System.out.println("线程列表不为空" + count);
	// for (int i = 0; i < count; i++) {
	// // System.out.println("当前是第" + i + "个线程");
	// MonitorThread monitorThread = ((MonitorThread) threadList
	// .get(i));
	// if (monitorThread.getName().contains(
	// message.getContent())) {
	// System.out.println("进入到关闭截图");
	// monitorThread.setStopPlayer();
	// // System.out.print("已关闭截图线程"
	// // + message.getContent());
	// threadList.remove(i);
	// application.setAttribute("threadList",
	// threadList);
	// break;
	// }
	//
	// }
	// }
	// } else if (message.getType()
	// .contains("deviceConnected")) {
	// String filePath = getServletConfig()
	// .getServletContext().getRealPath("/");
	// if (threadList != null) {
	// boolean isExist = false;
	// int count = threadList.size();
	// // System.out.println("线程列表不为空" + count);
	// for (int i = 0; i < count; i++) {
	// // System.out.println("当前是第" + i + "个线程");
	// MonitorThread monitorThread = ((MonitorThread) threadList
	// .get(i));
	// if (monitorThread.getName().contains(
	// message.getContent())) {
	// isExist = true;
	// }
	// }
	// if (!isExist) {
	// // System.out.println("线程列表不为空");
	// ScreenShot screenShot = new ScreenShot(
	// devices);
	// MonitorThread monitorThread = new MonitorThread(
	// screenShot, filePath);
	// monitorThread.setName(message.getContent());
	// monitorThread.setPlayer();
	// monitorThread.setDevices(new String[] {
	// "-s", message.getContent() });
	// monitorThread.start();
	// threadList.add(monitorThread);
	// } else {
	// // System.out.println("这个截图线程已存在！");
	// }
	//
	// } else {
	// // System.out.println("线程列表为空");
	// threadList = new ArrayList();
	// ScreenShot screenShot = new ScreenShot(devices);
	// MonitorThread monitorThread = new MonitorThread(
	// screenShot, filePath);
	// monitorThread.setName(message.getContent());
	// monitorThread.setPlayer();
	// monitorThread.setDevices(new String[] { "-s",
	// message.getContent() });
	// monitorThread.start();
	// threadList.add(monitorThread);
	// }
	// } else if (message.getType().equals("deviceChanged")) {
	//
	// }
	//
	// } else {
	// // System.out.println("message为空");
	// }
	// }
	// }
	// }).start();
	// }

	public class MonitorThread extends Thread {

		public IDevice screenShot;

		public boolean isScreenShot = false;

		public String[] devices;

		private BufferedImage bufferedImage;

		public String filePath;

		public void setDevices(String args[]) {

		}

		public void setPlayer() {
			isScreenShot = true;
		}

		public void setStopPlayer() {
			isScreenShot = false;
		}

		public MonitorThread(IDevice screenShot, String filePath) {
			// TODO Auto-generated constructor stub
			this.screenShot = screenShot;
			this.filePath = filePath;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			while (true) {
				try {
					bufferedImage = getDeviceBufferImage(this.filePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				} catch (AdbCommandRejectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("截图线程准备结束" + this.getName());
					break;
				}

			}

		}

		public BufferedImage getDeviceBufferImage(String filePath)
				throws IOException, AdbCommandRejectedException {
			RawImage rawImage;
			BufferedImage bufferedImage = null;
			try {
				long time1 = System.currentTimeMillis();
				rawImage = screenShot.getScreenshot();
				long time2 = System.currentTimeMillis();
				System.out.println("截图消耗：" + (time2 - time1) + "ms");
			} catch (TimeoutException e) {
				return bufferedImage;
			} catch (IOException ioe) {
				return bufferedImage;
			}

			// device/adb not available?
			if (rawImage == null)
				return bufferedImage;

			if (landscape) {
				rawImage = rawImage.getRotated();
			}

			// convert raw data to an Image
			bufferedImage = new BufferedImage(rawImage.width, rawImage.height,
					BufferedImage.TYPE_INT_ARGB);

			int index = 0;
			int IndexInc = rawImage.bpp >> 3;
			for (int y = 0; y < rawImage.height; y++) {
				for (int x = 0; x < rawImage.width; x++) {
					int value = rawImage.getARGB(index);
					index += IndexInc;
					bufferedImage.setRGB(x, y, value);
				}
			}

			bufferedImage = zoom(bufferedImage, 240, 320);

			// long time1=System.currentTimeMillis();
			if (!ImageIO.write(bufferedImage, "png", new File(filePath
					+ File.separator + screenShot.getSerialNumber() + ".png"))) {
				throw new IOException("Failed to find png writer");
			}

			try {

				// read image file
				bufferedImage = ImageIO.read(new File(filePath + File.separator
						+ screenShot.getSerialNumber() + ".png"));

				// create a blank, RGB, same width and height, and a white
				// background
				BufferedImage newBufferedImage = new BufferedImage(
						bufferedImage.getWidth(), bufferedImage.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				newBufferedImage.createGraphics().drawImage(bufferedImage, 0,
						0, Color.WHITE, null);

				// write to jpeg file
				ImageIO.write(newBufferedImage, "jpg", new File(filePath
						+ File.separator + screenShot.getSerialNumber()
						+ ".jpg"));

				System.out.println("Done");

			} catch (IOException e) {

				e.printStackTrace();

			}

			// long time2=System.currentTimeMillis();
			//
			// System.out.println("消耗："+(time2-time1)+"ms");

			// new ScreenShotThread(bufferedImage).start();

			return bufferedImage;

		}

		/**
		 * 压缩图片
		 * 
		 * @param sourceImage
		 *            待压缩图片
		 * @param width
		 *            压缩图片高度
		 * @param heigt
		 *            压缩图片宽度
		 */
		private BufferedImage zoom(BufferedImage sourceImage, int width,
				int height) {
			BufferedImage zoomImage = new BufferedImage(width, height,
					sourceImage.getType());
			Image image = sourceImage.getScaledInstance(width, height,
					Image.SCALE_SMOOTH);
			Graphics gc = zoomImage.getGraphics();
			gc.setColor(Color.WHITE);
			gc.drawImage(image, 0, 0, null);
			return zoomImage;
		}

	}

}
