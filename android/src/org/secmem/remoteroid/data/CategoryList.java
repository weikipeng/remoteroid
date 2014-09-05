package org.secmem.remoteroid.data;

import java.io.File;

import android.graphics.Bitmap;

public class CategoryList {
	
	private File file;
	private String type;
	private Bitmap bitmap=null;
	private long id;
	private int albumId;

	private boolean bitmapChecked = false;
	private boolean fileSelected = false;

	public CategoryList(File file, String type) {
		this.file = file;
		this.type = type;
	
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public boolean isBitmapChecked() {
		return bitmapChecked;
	}

	public void setBitmapChecked(boolean bitmapChecked) {
		this.bitmapChecked = bitmapChecked;
	}

	public boolean isFileSelected() {
		return fileSelected;
	}

	public void setFileSelected(boolean fileSelected) {
		this.fileSelected = fileSelected;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

}
