package com.nq.montior.bean;

import com.android.ddmlib.IDevice;

public class DeviceInfo {

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	int height;

	String version;

	String serialNumber;

	String manufacturer;

	String model;

	int width;

	double factorWidth;

	public double getFactorWidth() {
		return factorWidth;
	}

	public void setFactorWidth(double factorWidth) {
		this.factorWidth = factorWidth;
	}

	public double getFactorHeith() {
		return factorHeith;
	}

	public void setFactorHeith(double factorHeith) {
		this.factorHeith = factorHeith;
	}

	double factorHeith;

	public DeviceInfo() {
		// TODO Auto-generated constructor stub
	}

}
