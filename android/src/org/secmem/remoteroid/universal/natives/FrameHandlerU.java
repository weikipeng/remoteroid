package org.secmem.remoteroid.universal.natives;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.CommandLine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class FrameHandlerU {
	
	private static final int BYTE_PER_PIXEL = 4;
	
	private int width;
	private int height;
	private int pixelFormat;
	private int screenDataSizeInBytes;
	
	private byte[] frameByteArray;
	private ByteBuffer frameBuffer;
	private Bitmap bitmap;
	private ByteArrayOutputStream bitmapOutStream;
	
	private Bitmap dev1;
	private Bitmap dev2;
	private byte[] dev1Byte;
	private byte[] dev2Byte;
	
	static{
		System.loadLibrary("fbuffer");
	}
	
	/**
	 * Read frame buffer from device.
	 * @param buff Byte buffer where frame buffer's data will be stored
	 * @param pixelformat
	 * @return
	 */
	//private native int getFrameBuffer(byte[] buff, int pixelformat);
	private boolean toggle = false;
	private Context context;
	
	public FrameHandlerU(Context context){
		this.context = context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		pixelFormat = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getPixelFormat();
		screenDataSizeInBytes = width*height*BYTE_PER_PIXEL;
		
		frameByteArray = new byte[screenDataSizeInBytes];
		frameBuffer = ByteBuffer.allocate(screenDataSizeInBytes); 
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmapOutStream = new ByteArrayOutputStream();
		
		// TEST
		dev1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.dev1);
		dev2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.dev2);
		
		ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
		dev1.compress(CompressFormat.JPEG, 100, stream1);
		dev1Byte = stream1.toByteArray();
		
		ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
		dev2.compress(CompressFormat.JPEG, 100, stream2);
		dev2Byte = stream2.toByteArray();
	}
	
	public synchronized byte[] readScreenBuffer(){
		// FIXME simple test code for screen xmission
		
		//getFrameBuffer(frameByteArray, pixelFormat);
		
		/*frameBuffer.put(frameByteArray, 0, screenDataSizeInBytes);
		frameBuffer.rewind();
		bitmap.copyPixelsFromBuffer(frameBuffer);
		
		frameBuffer.reset();
		bitmap.compress(CompressFormat.JPEG, 100, bitmapOutStream);*/
		toggle = !toggle;
		if(toggle){
			return dev1Byte;
		}else{
			return dev2Byte;
		}
		
		//bitmap.compress(CompressFormat.JPEG, 100, bitmapOutStream);
		//return bitmapOutStream.toByteArray();
		
	}
	
	public void acquireFrameBufferPermission(){
		CommandLine.execAsRoot("chmod 664 /dev/graphics/fb0");
		CommandLine.execAsRoot("chmod 664 /dev/graphics/fb1");
	}
	
	public void revertFrameBufferPermission(){
		CommandLine.execAsRoot("chmod 660 /dev/graphics/fb0");
		CommandLine.execAsRoot("chmod 660 /dev/graphics/fb1");
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}

}
