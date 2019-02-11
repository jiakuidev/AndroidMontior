package com.nq.montior.servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.nq.montior.bean.ScreenShot;

public class StartMontior extends HttpServlet {

	private String[] args;

	private boolean device = false;

	private boolean emulator = false;

	private String serial = null;

	private String filepath = null;

	private boolean landscape = false;

	public IDevice target = null;

	public IDevice[] devices;

	public AndroidDebugBridge bridge;

	ServletContext application;

	/**
	 * Constructor of the object.
	 */
	public StartMontior() {
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
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String serialNumber = (String) request.getParameter("serialNumber");
		application = this.getServletContext();
		ArrayList threadList = (ArrayList) application
				.getAttribute("threadList");
		devices = (IDevice[]) application.getAttribute("devices");
		String filePath = this.getServletConfig().getServletContext()
				.getRealPath("/");
		System.out.println("从application中取出的devices:" + devices.length);
		if (threadList != null) {

			boolean isExist = false;
			int count = threadList.size();
			System.out.println("线程列表不为空" + count);
			for (int i = 0; i < count; i++) {
				System.out.println("当前是第" + i + "个线程");
				MonitorThread monitorThread = ((MonitorThread) threadList
						.get(i));
				if (monitorThread.getName().contains(serialNumber)) {
					isExist = true;
				}
			}

			if (!isExist) {
				System.out.println("线程列表不为空");
				ScreenShot screenShot = new ScreenShot(devices);
				MonitorThread monitorThread = new MonitorThread(screenShot,
						filePath);
				monitorThread.setName(serialNumber);
				monitorThread.setPlayer();
				monitorThread.setDevices(new String[] { "-s", serialNumber });
				monitorThread.start();
				threadList.add(monitorThread);
				application.setAttribute("threadList", threadList);
			} else {
				out.println("这个截图线程已存在！");
			}

		} else {
			System.out.println("线程列表为空");
			threadList = new ArrayList();
			ScreenShot screenShot = new ScreenShot(devices);
			MonitorThread monitorThread = new MonitorThread(screenShot,
					filePath);
			monitorThread.setName(serialNumber);
			monitorThread.setPlayer();
			monitorThread.setDevices(new String[] { "-s", serialNumber });
			monitorThread.start();
			threadList.add(monitorThread);
			application.setAttribute("threadList", threadList);
		}

		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("已启动截图线程");
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
	public void init() throws ServletException {
		// Put your code here
	}

	public class MonitorThread extends Thread {

		public ScreenShot screenShot;

		public boolean isScreenShot = false;

		public String[] devices;

		private BufferedImage bufferedImage;

		public String filePath;

		public void setDevices(String args[]) {
			screenShot.init(args);
		}

		public void setPlayer() {
			isScreenShot = true;
		}

		public void setStopPlayer() {
			isScreenShot = false;
		}

		public MonitorThread(ScreenShot screenShot, String filePath) {
			// TODO Auto-generated constructor stub
			this.screenShot = screenShot;
			this.filePath = filePath;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (isScreenShot) {
				try {
					bufferedImage = screenShot
							.getDeviceBufferImage(this.filePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				System.out.println("截图了" + this.getName());

			}
			System.out.println("截图线程准备结束" + this.getName());
		}

	}

}
