/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.secmem.remoteroid.data;

import java.io.File;

import org.secmem.remoteroid.util.HongUtil;

import android.graphics.Bitmap;

public class FileList extends ExplorerType{
	
	private String mimeType="";
	private Bitmap bitmap=null;
	private boolean bitmapChecked=false;
	private boolean fileSelected = false;
	private File file;

	public FileList(String name, int type){
		super(name, type);
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = HongUtil.getMimeType(new File(mimeType));
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
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
