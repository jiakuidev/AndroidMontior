package com.nq.montior.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nq.montior.util.JavaConnSSHUtils;

public class TestUtil {

	public TestUtil() {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testShutDownTomcat() {
		try {
			JavaConnSSHUtils.shutDownTomcat();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testStartTomcat() {
		try {
			JavaConnSSHUtils.startupTomcat();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testIp() throws SocketException {
		// 根据网卡取本机配置的IP
		Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress addr = null;
		while (netInterfaces.hasMoreElements()) {
			NetworkInterface ni = (NetworkInterface) netInterfaces
					.nextElement();
			System.out.println(ni.getName());
			addr = (InetAddress) ni.getInetAddresses().nextElement();
			if (!addr.isSiteLocalAddress() && !addr.isLoopbackAddress()
					&& addr.getHostAddress().indexOf(":") == -1) {
				System.out.println("本机的ip=" + addr.getHostAddress());
				break;
			} else {
				addr = null;
			}
		}

		String ip = addr.getHostAddress().toString();
	}

	@Test
	public void testPngToJpg() throws IOException {
		// RenderedImage img = ImageIO.read(new File(
		// "/Users/admin/Downloads/A-1.png"));
		// ImageIO.write(img, "jpg", new File(
		// "/Users/admin/Downloads/A4994799154D-3.jpg"));
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		BufferedImage bufferedImage;

		try {

			// read image file
			bufferedImage = ImageIO.read(new File(
					"/Users/admin/Downloads/A4994799154D.png"));

			// create a blank, RGB, same width and height, and a white
			// background
			BufferedImage newBufferedImage = new BufferedImage(
					bufferedImage.getWidth(), bufferedImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
					Color.WHITE, null);

			// write to jpeg file
			ImageIO.write(newBufferedImage, "jpg", new File(
					"/Users/admin/Downloads/A4994799154D-3.jpg"));

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	@Test
	public void testFactor() {
		int width = 480;
		int height = 800;
		double x = width / 240.0;
		double y = height / 320.0;
		System.out.println("x" + x);
		System.out.println("y" + y);

		System.out.println((int)(320 * 2.5));

	}

}
