package com.nq.montior.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.*;

public class JavaConnSSHUtils {

	private static String yzm = null;
	private static String hostname = "192.8.19.84"; // 测试服务器的主机
	private static int port = 22;
	private static String username = "root";
	private static String password = "xqa2014";

	public static void shutDownTomcat() throws Exception {

		Connection conn = new Connection(hostname, port);// 初始化到远程主机的连接
		conn.connect();

		boolean isAuthenticated = conn.authenticateWithPassword(username,
				password);
		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
		Session sess = conn.openSession();
		sess.requestDumbPTY();
		// ----------------------------------------执行命令-------------------------
		String sqlmicmd = "sh /usr/apache-tomcat-6.0.41/bin/shutdown.sh";
		sess.execCommand(sqlmicmd);
		InputStream stdout = new StreamGobbler(sess.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		// 循环读取控制台输出的字符串
		String line = "";
		while ((line = br.readLine()) != null) {
			// Map map = new HashMap();
			System.out.println(line);
		}
		Thread.sleep(3000);
		br.close();
		stdout.close();
		sess.close();
		conn.close();
	}

	public static void startupTomcat() throws Exception {

		Connection conn = new Connection(hostname, port);// 初始化到远程主机的连接
		conn.connect();

		boolean isAuthenticated = conn.authenticateWithPassword(username,
				password);
		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
		Session sess = conn.openSession();
		sess.requestDumbPTY();
		// ----------------------------------------执行命令-------------------------
		String sqlmicmd = "cd /usr/apache-tomcat-6.0.41/bin/;nohup ./startup.sh";
		sess.execCommand(sqlmicmd);
		InputStream stdout = new StreamGobbler(sess.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		// 循环读取控制台输出的字符串
//		String line = "";
//		while ((line = br.readLine()) != null) {
//			// Map map = new HashMap();
//			System.out.println(line);
//		}
		
		while (true)
		{
			String line = br.readLine();
			if (line == null)
				break;
			System.out.println(line);
		}
		Thread.sleep(3000);
//		br.close();
//		stdout.close();
		sess.close();
		conn.close();
	}

}
