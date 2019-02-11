package com.nq.montior.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetDeviceList extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public GetDeviceList() {
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
		this.getDevices();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println((JSONArray.fromObject(this.getDevices())).toString());
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
		doGet(request, response);
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
	 * 获得当前系统当前可用连接的android设备
	 */
	public ArrayList getDevices() {
		ArrayList deviceList = new ArrayList();
		try {
			ArrayList arrayList = new ArrayList();
			Process devicesProcess = Runtime.getRuntime().exec("adb devices");
			BufferedReader devicesBufferedReader = new BufferedReader(
					new InputStreamReader(devicesProcess.getInputStream()));
			String devicesline;
			while ((devicesline = devicesBufferedReader.readLine()) != null) {
				System.out.println(devicesline + "----");
				arrayList.add(devicesline);
			}
			devicesProcess.destroy();

			System.out.println("当前的列表大小:" + arrayList.size());
			for (int i = 1; i < arrayList.size() - 1; i++) {
				String device = arrayList.get(i).toString();
				device = device.substring(0, device.indexOf("\t"));
				deviceList.add(device);
				System.out.println("device:" + device);
			}

			return deviceList;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return deviceList;
	}
}
