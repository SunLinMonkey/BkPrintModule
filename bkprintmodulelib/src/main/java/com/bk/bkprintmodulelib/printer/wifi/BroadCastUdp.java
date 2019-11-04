package com.example.pekonprinter.printer.wifi;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
	 * UDP广播打印
	 */
	public class BroadCastUdp extends Thread {
		public static final int DEFAULT_PORT = 43708;
		private static final int MAX_DATA_PACKET_LENGTH = 20000;
		private String dataString;
		private DatagramSocket udpSocket;
		private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

		public BroadCastUdp(String dataString) {
			this.dataString = dataString;
		}

		public void run() {
			DatagramPacket dataPacket = null;

			try {
				udpSocket = new DatagramSocket(DEFAULT_PORT);

				dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				byte[] data = dataString.getBytes("GBK");
				dataPacket.setData(data);
				dataPacket.setLength(data.length);
				dataPacket.setPort(DEFAULT_PORT);

				InetAddress broadcastAddr;

				broadcastAddr = InetAddress.getByName("255.255.255.255");
				dataPacket.setAddress(broadcastAddr);
			} catch (Exception e) {
				Log.e("LHT", e.toString());
			}
			// while( start ){
			try {
				udpSocket.send(dataPacket);
				sleep(10);
			} catch (Exception e) {
				Log.e("LHT", e.toString());
			}
			// }

			udpSocket.close();
		}
	}