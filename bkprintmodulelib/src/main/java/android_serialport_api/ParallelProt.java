package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ParallelProt {
	private static final String TAG = "ParallelProt";

	private OutputStream mFileOutputStream;

	public ParallelProt(File device, int baudrate, int flags) throws SecurityException, IOException {
		Process su = null;
		/* Check access permission */
		// if (!device.canRead() || !device.canWrite())
		// {
		try {
			/* Missing read/write permission, trying to chmod the file */
			su = Runtime.getRuntime().exec("/system/bin/sh", null, new File("/system/bin"));
			// String cmd = "chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n";
			// su.getOutputStream().write(cmd.getBytes());
			// if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite())
			// {
			// throw new SecurityException();
			// }
		} catch (Exception e) {
			e.printStackTrace();
			throw new SecurityException();
		}
		// }

		if (su != null) {
			mFileOutputStream = su.getOutputStream();
		}
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}
}
