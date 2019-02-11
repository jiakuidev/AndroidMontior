package com.nq.montior.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nq.montior.bean.DeviceInfo;

public class SendTapEvent extends HttpServlet {

	ServletContext application;

	/**
	 * Constructor of the object.
	 */
	public SendTapEvent() {
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
		int locationX = Integer.parseInt(request.getParameter("locationX"));
		int locationY = Integer.parseInt(request.getParameter("locationY"));
		System.out.println("serialNumber" + serialNumber);
		System.out.println("locationX" + locationX);
		System.out.println("locationY" + locationY);
		application = this.getServletContext();
		DeviceInfo deviceInfo;
		ArrayList<DeviceInfo> deviceInfos = (ArrayList<DeviceInfo>) application
				.getAttribute("deviceInfoList");
		for (int i = 0; i < deviceInfos.size(); i++) {
			deviceInfo = deviceInfos.get(i);
			if (deviceInfo.getSerialNumber().equals(serialNumber)) {
				execCommand("adb -s " + deviceInfo.getSerialNumber()
						+ " shell input tap "
						+ (int) (deviceInfo.getFactorWidth() * locationX) + " "
						+ (int) (deviceInfo.getFactorHeith() * locationY));
				break;
			}
		}

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

	/**
	 * 通过发送shell|bat脚本文件来执行必要的辅助操作
	 * 
	 * @param scriptPath
	 * @return
	 * @throws IOException
	 */
	public void execCommand(String shellScript) throws IOException {

		String result = "";
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os.contains("Windows")) {
			System.out.println("+" + shellScript);
			Process process = Runtime.getRuntime().exec(shellScript);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}

		} else {
			System.out.println("+" + shellScript);
			Process process = Runtime.getRuntime().exec(
					new String[] { "sh", "-c", shellScript });
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}

		}

	}

}
