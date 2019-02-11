package com.nq.montior.bean;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.Log;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.Log.ILogOutput;
import com.android.ddmlib.Log.LogLevel;

public class ScreenShot {

	private String[] args;

	private boolean device = false;

	private boolean emulator = false;

	private String serial = null;

	private String filepath = null;

	private boolean landscape = false;

	public IDevice target = null;

	public IDevice[] devices;

	public AndroidDebugBridge bridge;

	ArrayList arrayList = new ArrayList();

	public ScreenShot(IDevice[] devices) {
		this.devices = devices;
		// // AndroidDebugBridge.init(false /* debugger support */);
		// AndroidDebugBridge.init(false /* debugger support */);
		// String adbLocation = System
		//				.getProperty("com.android.screenshot.bindir"); //$NON-NLS-1$
		// if (adbLocation != null && adbLocation.length() != 0) {
		//			adbLocation += File.separator + "adb"; //$NON-NLS-1$
		// } else {
		//			adbLocation = "adb"; //$NON-NLS-1$
		// }
		//
		// bridge = AndroidDebugBridge
		// .createBridge(adbLocation, true /* forceNewBridge */);
		//
		// if (!bridge.hasInitialDeviceList()) {
		// int count = 0;
		// while (bridge.hasInitialDeviceList() == false) {
		// try {
		// Thread.sleep(100);
		// count++;
		// } catch (InterruptedException e) {
		// // pass
		// }
		//
		// // let's not wait > 10 sec.
		// if (count > 100) {
		// System.err.println("Timeout getting device list!");
		// return;
		// }
		// }
		// }
		//
		// // now get the devices
		// devices = bridge.getDevices();
		// bridge.addDeviceChangeListener(new IDeviceChangeListener() {
		//
		// public void deviceDisconnected(IDevice arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void deviceConnected(IDevice arg0) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void deviceChanged(IDevice arg0, int arg1) {
		// // TODO Auto-generated method stub
		// devices = bridge.getDevices();
		// }
		// });
	}

	public void init(String[] args) {

		System.out.println("设备列表长度" + devices.length);
		for (int i = 0; i < devices.length; i++) {
			arrayList.add(devices[i].getSerialNumber());
			System.out.println("第" + i + "个" + devices[i].getSerialNumber());
		}

		if (args.length == 0) {
			printUsageAndQuit();
		} else {

			// parse command line parameters.
			int index = 0;
			do {
				String argument = args[index++];

				if ("-d".equals(argument)) {
					if (emulator || serial != null) {
						printAndExit("-d conflicts with -e and -s", false /* terminate */);
					}
					device = true;
				} else if ("-e".equals(argument)) {
					if (device || serial != null) {
						printAndExit("-e conflicts with -d and -s", false /* terminate */);
					}
					emulator = true;
				} else if ("-s".equals(argument)) {
					// quick check on the next argument.
					if (index == args.length) {
						printAndExit("Missing serial number after -s", false /* terminate */);
					}

					if (device || emulator) {
						printAndExit("-s conflicts with -d and -e", false /* terminate */);
					}

					serial = args[index++];
					System.out.println(serial);
				} else if ("-l".equals(argument)) {
					landscape = true;
				}
			} while (index < args.length);

			Log.setLogOutput(new ILogOutput() {
				public void printAndPromptLog(LogLevel logLevel, String tag,
						String message) {
					System.err.println(logLevel.getStringValue() + ":" + tag
							+ ":" + message);
				}

				public void printLog(LogLevel logLevel, String tag,
						String message) {
					System.err.println(logLevel.getStringValue() + ":" + tag
							+ ":" + message);
				}
			});

			// init the lib
			// [try to] ensure ADB is running
			// String adbLocation = System
			// .getProperty("com.android.screenshot.bindir"); //$NON-NLS-1$
			// if (adbLocation != null && adbLocation.length() != 0) {
			// adbLocation += File.separator + "adb"; //$NON-NLS-1$
			// } else {
			// adbLocation = "adb"; //$NON-NLS-1$
			// }
			// AndroidDebugBridge.init(false /* debugger support */);
			try {
				// AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(
				// adbLocation, true /* forceNewBridge */);
				// AndroidDebugBridge bridge =
				// AndroidDebugBridge.createBridge();

				// we can't just ask for the device list right away, as the
				// internal
				// thread getting
				// them from ADB may not be done getting the first list.
				// Since we don't really want getDevices() to be blocking, we
				// wait
				// here manually.

				if (devices.length == 0) {
					printAndExit("No devices found!", true /* terminate */);
				}

				if (emulator || device) {
					for (IDevice d : devices) {
						// this test works because emulator and device can't
						// both be
						// true at the same
						// time.
						if (d.isEmulator() == emulator) {
							// if we already found a valid target, we print an
							// error
							// and return.
							if (target != null) {
								if (emulator) {
									printAndExit(
											"Error: more than one emulator launched!",
											true /* terminate */);
								} else {
									printAndExit(
											"Error: more than one device connected!",
											true /* terminate */);
								}
							}
							target = d;
						}
					}
				} else if (serial != null) {
					for (IDevice d : devices) {
						if (serial.equals(d.getSerialNumber())) {
							System.out.println(d.getSerialNumber());
							target = d;
							break;
						}
					}
				} else {
					if (devices.length > 1) {
						printAndExit(
								"Error: more than one emulator or device available!",
								true /* terminate */);
					}
					target = devices[0];
				}

				if (target != null) {
					// try {
					// System.out.println("Taking screenshot from: "
					// + target.getSerialNumber());
					// getDeviceImage(target, filepath, landscape);
					// System.out.println("Success.");
					// bridge.hasInitialDeviceList();
					// // bridge.init(true);
					//
					// } catch (IOException e) {
					// e.printStackTrace();
					// }
				} else {
					printAndExit("Could not find matching device/emulator.",
							true /* terminate */);
				}
			} finally {
				AndroidDebugBridge.terminate();
			}
		}

	}

	public void setDevicesTarget() {

	}

	public void getDeviceImage(IDevice device, String filepath,
			boolean landscape) throws IOException {
		RawImage rawImage;

		try {
			rawImage = device.getScreenshot();
		} catch (TimeoutException e) {
			printAndExit("Unable to get frame buffer: timeout", true /* terminate */);
			return;
		} catch (Exception ioe) {
			printAndExit("Unable to get frame buffer: " + ioe.getMessage(),
					true /* terminate */);
			return;
		}

		// device/adb not available?
		if (rawImage == null)
			return;

		if (landscape) {
			rawImage = rawImage.getRotated();
		}

		// convert raw data to an Image
		BufferedImage image = new BufferedImage(rawImage.width,
				rawImage.height, BufferedImage.TYPE_INT_ARGB);

		int index = 0;
		int IndexInc = rawImage.bpp >> 3;
		for (int y = 0; y < rawImage.height; y++) {
			for (int x = 0; x < rawImage.width; x++) {
				int value = rawImage.getARGB(index);
				index += IndexInc;
				image.setRGB(x, y, value);
			}
		}

		if (!ImageIO.write(image, "png", new File(filepath))) {
			throw new IOException("Failed to find png writer");
		}
	}

	public void getDeviceImage(String filepath) throws IOException {
		RawImage rawImage;

		try {
			rawImage = target.getScreenshot();
		} catch (TimeoutException e) {
			printAndExit("Unable to get frame buffer: timeout", true /* terminate */);
			return;
		} catch (Exception ioe) {
			System.out.println("断开了");
			printAndExit("Unable to get frame buffer: " + ioe.getMessage(),
					true /* terminate */);
			return;
		}

		// device/adb not available?
		if (rawImage == null)
			return;

		if (landscape) {
			rawImage = rawImage.getRotated();
		}

		// convert raw data to an Image
		BufferedImage image = new BufferedImage(rawImage.width,
				rawImage.height, BufferedImage.TYPE_INT_ARGB);

		int index = 0;
		int IndexInc = rawImage.bpp >> 3;
		for (int y = 0; y < rawImage.height; y++) {
			for (int x = 0; x < rawImage.width; x++) {
				int value = rawImage.getARGB(index);
				index += IndexInc;
				image.setRGB(x, y, value);
			}
		}

		if (!ImageIO.write(image, "png", new File(filepath))) {
			throw new IOException("Failed to find png writer");
		}

	}

	public BufferedImage getDeviceBufferImage() throws IOException,
			AdbCommandRejectedException {
		RawImage rawImage;
		BufferedImage bufferedImage = null;

		try {
			long time1 = System.currentTimeMillis();
			rawImage = target.getScreenshot();
			long time2 = System.currentTimeMillis();
			System.out.println("截图消耗：" + (time2 - time1) + "ms");
		} catch (TimeoutException e) {

			printAndExit("Unable to get frame buffer: timeout", true /* terminate */);
			return bufferedImage;
		} catch (IOException ioe) {
			System.out.println("断开了");
			printAndExit("Unable to get frame buffer: " + ioe.getMessage(),
					true /* terminate */);
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

		bufferedImage = zoom(bufferedImage, 320, 480);
		// long time1=System.currentTimeMillis();
		if (!ImageIO.write(bufferedImage, "png",
				new File(System.getProperty("user.home") + File.separator
						+ target.getSerialNumber() + ".png"))) {
			throw new IOException("Failed to find png writer");
		}
		// long time2=System.currentTimeMillis();
		//
		// System.out.println("消耗："+(time2-time1)+"ms");

		// new ScreenShotThread(bufferedImage).start();

		return bufferedImage;

	}

	public BufferedImage getDeviceBufferImage(String filePath)
			throws IOException {
		RawImage rawImage;
		BufferedImage bufferedImage = null;

		try {
			long time1 = System.currentTimeMillis();
			try {
				rawImage = target.getScreenshot();
			} catch (AdbCommandRejectedException e) {
				e.printStackTrace();
				return bufferedImage;
			}
			long time2 = System.currentTimeMillis();
			System.out.println("截图消耗：" + (time2 - time1) + "ms");
		} catch (TimeoutException e) {

			printAndExit("Unable to get frame buffer: timeout", true /* terminate */);
			return bufferedImage;
		} catch (IOException ioe) {
			System.out.println("断开了");
			printAndExit("Unable to get frame buffer: " + ioe.getMessage(),
					true /* terminate */);
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
				+ File.separator + target.getSerialNumber() + ".png"))) {
			throw new IOException("Failed to find png writer");
		}

		try {

			// read image file
			bufferedImage = ImageIO.read(new File(filePath + File.separator
					+ target.getSerialNumber() + ".png"));

			// create a blank, RGB, same width and height, and a white
			// background
			BufferedImage newBufferedImage = new BufferedImage(
					bufferedImage.getWidth(), bufferedImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
					Color.WHITE, null);

			// write to jpeg file
			ImageIO.write(newBufferedImage, "jpg", new File(filePath
					+ File.separator + target.getSerialNumber() + ".jpg"));

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
	private static BufferedImage zoom(BufferedImage sourceImage, int width,
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

	/**
	 * 压缩图片
	 * 
	 * @param sourceImage
	 *            待压缩图片
	 * @param width
	 *            压缩图片高度
	 * @param heigt
	 *            压缩图片宽度
	 * @throws IOException
	 */
	private static BufferedImage tranferJPGFromPNG(BufferedImage bufferedImage,
			int width, int height) throws IOException {
		BufferedImage newBufferedImage = new BufferedImage(
				bufferedImage.getWidth(), bufferedImage.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
				Color.WHITE, null);
		ImageIO.write(newBufferedImage, "jpg", new File(
				"/Users/admin/Downloads/A4994799154D-3.jpg"));
		return newBufferedImage;
	}

	private void printUsageAndQuit() {
		// 80 cols marker:
		// 01234567890123456789012345678901234567890123456789012345678901234567890123456789
		System.out
				.println("Usage: screenshot2 [-d | -e | -s SERIAL] [-l] OUT_FILE");
		System.out.println("");
		System.out.println("    -d      Uses the first device found.");
		System.out.println("    -e      Uses the first emulator found.");
		System.out.println("    -s      Targets the device by serial number.");
		System.out.println("");
		System.out.println("    -l      Rotate images for landscape mode.");
		System.out.println("");

		System.exit(1);
	}

	private void printAndExit(String message, boolean terminate) {
		System.out.println(message);
		if (terminate) {
			AndroidDebugBridge.terminate();
		}
		// System.exit(1);
	}

	public void main(String[] args) {
		String[] args1 = new String[] { "-s", "emulator-5554", "test112.png" };
		// ScreenShot screenShot = new ScreenShot(args1);
	}

	public class ScreenShotThread extends Thread {

		BufferedImage bufferedImage;

		public ScreenShotThread() {
			// TODO 自动生成构造函数存根
		}

		public ScreenShotThread(BufferedImage bufferedImage) {
			this.bufferedImage = bufferedImage;
		}

		@Override
		public void run() {
			// TODO 自动生成方法存根
			try {
				if (!ImageIO.write(this.bufferedImage, "png",
						new File("12.png"))) {
					try {
						throw new IOException("Failed to find png writer");
					} catch (IOException e) {
						// TODO 自动生成 catch 块
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
			}

		}
	}

}
