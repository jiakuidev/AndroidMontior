package com.nq.montior.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.print.attribute.standard.Severity;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.android.ddmlib.IDevice;
import com.nq.montior.bean.DeviceInfo;
import com.nq.montior.dto.DeviceDto;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetScreenShot extends HttpServlet {

	public IDevice[] devices;

	/**
	 * Constructor of the object.
	 */
	public GetScreenShot() {
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
		ServletContext application = this.getServletContext();
		InetAddress addr = InetAddress.getLocalHost();

		// 根据网卡取本机配置的IP
		// Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
		// InetAddress addr = null;
		// while (netInterfaces.hasMoreElements()) {
		// NetworkInterface ni = (NetworkInterface) netInterfaces
		// .nextElement();
		// System.out.println(ni.getName());
		// addr = (InetAddress) ni.getInetAddresses().nextElement();
		// if (!addr.isSiteLocalAddress() && !addr.isLoopbackAddress()
		// && addr.getHostAddress().indexOf(":") == -1) {
		// System.out.println("本机的ip=" + addr.getHostAddress());
		// break;
		// } else {
		// addr = null;
		// }
		// }

		String ip = addr.getHostAddress().toString();
		devices = (IDevice[]) application.getAttribute("devices");
		int count = devices.length;
		ArrayList<DeviceDto> deviceDtos = new ArrayList<DeviceDto>();
		String servletContextName = this.getServletContext()
				.getServletContextName();

		for (int i = 0; i < count; i++) {
			DeviceDto deviceDto = new DeviceDto();
			// deviceDto.setUrl("http://" + ip + ":8080/" + servletContextName
			// + "/" + devices[i].getSerialNumber() + ".jpg");
			deviceDto.setUrl(devices[i].getSerialNumber() + ".jpg");
			deviceDto.setName(devices[i].getSerialNumber());
			deviceDtos.add(deviceDto);
		}
		JSONArray js = JSONArray.fromObject(deviceDtos);
		System.out.println(js.toString());
		
		
		application = this.getServletContext();
		ArrayList<DeviceInfo> deviceInfos = (ArrayList<DeviceInfo>) application
				.getAttribute("deviceInfoList");
		JSONArray js1 = JSONArray.fromObject(deviceInfos);
		System.out.println(js1.toString());
		
		out.println(js1.toString());
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

}
