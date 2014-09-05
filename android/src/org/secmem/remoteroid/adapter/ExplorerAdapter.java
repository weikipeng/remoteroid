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

package org.secmem.remoteroid.adapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.activity.ExplorerActivity;
import org.secmem.remoteroid.data.CategoryList;
import org.secmem.remoteroid.data.ExplorerType;
import org.secmem.remoteroid.data.FileList;
import org.secmem.remoteroid.expinterface.OnFileLongClickListener;
import org.secmem.remoteroid.util.HongUtil;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ExplorerAdapter extends BaseAdapter{

	private static final String CATEGORY_TYPE_IMAGE="0";
	private static final String CATEGORY_TYPE_VIDEO="1";
	private static final String CATEGORY_TYPE_MUSIC="2";
	private static final String CATEGORY_TYPE_CUSTOM="3";
	
	private static int threadCount=0;
	private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    
	private Context context;
	private int layout;
	private int type;
	
	private String categoryType="4";
	
	private DataList dataList;
	private ArrayList<CategoryList> categoryList = new ArrayList<CategoryList>();
	
	private OnFileLongClickListener onFileLongClick=null;
	
	private GridView gridview;
	
	public ExplorerAdapter(Context context, int layout, DataList dataList, int type, OnFileLongClickListener onFileLongClick, GridView gridview) {
		this.context = context;
		this.layout = layout;
		this.dataList = dataList;
		this.type = type;
		this.onFileLongClick = onFileLongClick;
		this.gridview = gridview;
	}

	@Override
	public int getCount() {
		int result=0;
		if(type==ExplorerActivity.ADAPTER_TYPE_EXPLORER)
			result = dataList.getExpList().size();
		else if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY)
			result = categoryList.size();
		
		return result;
	}

	@Override
	public Object getItem(int arg0) {
		Object result=null;
		if(type==ExplorerActivity.ADAPTER_TYPE_EXPLORER)
			result = dataList.getExpList().get(arg0);
		else if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY)
			result = categoryList.get(arg0);
		
		return dataList.getExpList().get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View viewItem=convertView;
		final int pos = position;
		
		final ImageViewHolder holder;
		
		if (viewItem == null) {
			holder = new ImageViewHolder();
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewItem = vi.inflate(layout, null);
            holder.imgHolder = (ImageView)viewItem.findViewById(R.id.grid_explorer_img);
            holder.titleHolder = (TextView)viewItem.findViewById(R.id.grid_explorer_tv);
            viewItem.setTag(holder);
        }
		else{
			holder = (ImageViewHolder)viewItem.getTag();
		}
		
		if(type==ExplorerActivity.ADAPTER_TYPE_EXPLORER)							// ���湲���� ����
		{
			final String path = dataList.getPath();
			final String fileName = dataList.getExpList().get(pos).getName();
			
			holder.titleHolder.setTextColor(Color.WHITE);
			if(dataList.getExpList().get(pos).getType()==ExplorerType.TYPE_FOLDER){					// �대�����
				holder.imgHolder.setImageBitmap(null);
				holder.imgHolder.setBackgroundResource(R.drawable.img_folder);
			}
			
			else{																													// ��� ����
				FileList f = (FileList)dataList.getExpList().get(pos);
				holder.imgHolder.setImageBitmap(f.getBitmap());
				File file = new File(path+fileName);
				
				String type = HongUtil.getMimeType(file);
				if(type.equals(HongUtil.TYPE_PICTURE)){						// ������ъ��대㈃ �ъ� �몃���異��
					if(!(ExplorerActivity.SCROLL_STATE)){
						if(f.isBitmapChecked()){
							holder.imgHolder.setBackgroundResource(0x00000000);
							if(f.getBitmap()!=null){
								holder.imgHolder.setImageBitmap(f.getBitmap());
							}
							else{
								holder.imgHolder.setBackgroundResource(R.drawable.photo_camera);
							}
						}
						else{
							holder.imgHolder.setBackgroundResource(R.drawable.photo_camera);
							if(threadCount<15){
								f.setBitmapChecked(true);
								threadCount++;
								categoryType = CATEGORY_TYPE_IMAGE;
								new ThumbAsync().execute(path, fileName, String.valueOf(pos), CATEGORY_TYPE_IMAGE, String.valueOf(this.type));
							}
						}
					}
					else{
						holder.imgHolder.setBackgroundResource(R.drawable.photo_camera);
					}
				}
				else if(file.getPath().endsWith(".apk")){															// �����.APK ����APK �몃���異��
					
					if(!(ExplorerActivity.SCROLL_STATE)){
						
						if(f.isBitmapChecked()){
							holder.imgHolder.setBackgroundResource(0x00000000);
							if(f.getBitmap()!=null)
								holder.imgHolder.setImageBitmap(f.getBitmap());
							else
								holder.imgHolder.setBackgroundResource(R.drawable.img_apk);
						}
						else{
							holder.imgHolder.setBackgroundResource(R.drawable.img_apk);
							f.setBitmapChecked(true);
							new ApkBitmapAsync().execute(path,fileName, String.valueOf(pos));
						}
					}
					else{
						holder.imgHolder.setBackgroundResource(R.drawable.img_apk);
					}
					
				}
				else if(type.equals(HongUtil.TYPE_VIDEO)){
					holder.imgHolder.setBackgroundResource(R.drawable.video_camera);
				}
				else if(type.equals(HongUtil.TYPE_MUSIC)){
					holder.imgHolder.setBackgroundResource(R.drawable.music_note);
				}
				else{
					holder.imgHolder.setBackgroundResource(R.drawable.img_file);
				}
				
				if(f.isFileSelected()){
					holder.titleHolder.setTextColor(Color.GREEN);
				}
			}
			holder.titleHolder.setText(dataList.getExpList().get(pos).getName());
		}
		
		else if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY)						// 寃�����몃��ㅼ� ��� �������
		{
			CategoryList item = categoryList.get(pos);
			holder.imgHolder.setImageBitmap(item.getBitmap());
			holder.titleHolder.setTextColor(Color.WHITE);
			
			if(item.getType().equals(ExplorerActivity.TYPE_IMAGE)){
				if(!(ExplorerActivity.SCROLL_STATE)){
					
					if(item.isBitmapChecked()){
						holder.imgHolder.setBackgroundResource(0x00000000);
						if(item.getBitmap()!=null)
							holder.imgHolder.setImageBitmap(item.getBitmap());
						else
							holder.imgHolder.setBackgroundResource(R.drawable.photo_camera);
					}
					else{
						holder.imgHolder.setBackgroundResource(R.drawable.photo_camera);
						if(threadCount<15){
							item.setBitmapChecked(true);
							threadCount++;
							categoryType = CATEGORY_TYPE_IMAGE;
							new ThumbAsync().execute(item.getFile().getParent()+"/",item.getFile().getName(), String.valueOf(pos), CATEGORY_TYPE_IMAGE, String.valueOf(this.type) );
						}
					}
				}
				else{
					holder.imgHolder.setBackgroundResource(R.drawable.photo_camera);
				}
			}
			else if(item.getType().equals(ExplorerActivity.TYPE_VIDEO)){
				if(!(ExplorerActivity.SCROLL_STATE)){
					
					if(item.isBitmapChecked()){
						holder.imgHolder.setBackgroundResource(0x00000000);
						if(item.getBitmap()!=null)
							holder.imgHolder.setImageBitmap(item.getBitmap());
						else
							holder.imgHolder.setBackgroundResource(R.drawable.video_camera);
					}
					else{
						holder.imgHolder.setBackgroundResource(R.drawable.video_camera);
						if(threadCount<15){
							item.setBitmapChecked(true);
							threadCount++;
							categoryType = CATEGORY_TYPE_VIDEO;
							new VideoThumbAsync().execute(String.valueOf(item.getId()), categoryType, String.valueOf(pos));
						}
					}
				}
				else{
					holder.imgHolder.setBackgroundResource(R.drawable.video_camera);
				}
			}
			else if(item.getType().equals(ExplorerActivity.TYPE_MUSIC)){
				if(!(ExplorerActivity.SCROLL_STATE)){
					if(item.isBitmapChecked()){
						holder.imgHolder.setBackgroundResource(0x00000000);
						if(item.getBitmap()!=null)
							holder.imgHolder.setImageBitmap(item.getBitmap());
						else
							holder.imgHolder.setBackgroundResource(R.drawable.music_note);
					}
					else{
						holder.imgHolder.setBackgroundResource(R.drawable.music_note);
						if(threadCount<15){
							item.setBitmapChecked(true);
							threadCount++;
							categoryType = CATEGORY_TYPE_MUSIC;
							new MusicThumbAsync().execute(String.valueOf(item.getAlbumId()), categoryType, String.valueOf(pos));
						}
					}
				}
				else{
					holder.imgHolder.setBackgroundResource(R.drawable.music_note);
				}
			}
			else if(item.getType().equals(ExplorerActivity.TYPE_CUTSOM)){
				holder.imgHolder.setBackgroundResource(R.drawable.img_file);
			}
			if(item.isFileSelected()){
				holder.titleHolder.setTextColor(Color.GREEN);
			}
			holder.titleHolder.setText(categoryList.get(pos).getFile().getName());
		}
		viewItem.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				if(ExplorerActivity.fileInfo.size()==0){
					if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY){
						CategoryList category = categoryList.get(pos);
						File f = new File(category.getFile().getParent()+"/"+category.getFile().getName());
						ExplorerActivity.fileInfo.add(f.getAbsolutePath());
					}
					else if(type == ExplorerActivity.ADAPTER_TYPE_EXPLORER && dataList.getExpList().get(pos).getType()== ExplorerType.TYPE_FILE){
						String fileName = ((ExplorerType)getItem(pos)).getName();
						FileList fl = (FileList)dataList.getExpList().get(pos);
						File f = new File(dataList.getPath()+fileName);
						ExplorerActivity.fileInfo.add(f.getAbsolutePath());
					}
				}
				onFileLongClick.onLongclick();
				
				return true;
			}
		});
		
		viewItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(type==ExplorerActivity.ADAPTER_TYPE_EXPLORER){
					String fileName = ((ExplorerType)getItem(pos)).getName();
					
					if (dataList.getExpList().get(pos).getType()== ExplorerType.TYPE_FOLDER) {
						dataList.setPath(dataList.getRealPathName(fileName));
						ExplorerActivity.fileInfo.clear();
					} 
					else {
						if (dataList.getOnFileSelected() != null){ 
							FileList fl = (FileList)dataList.getExpList().get(pos);
							File f = new File(dataList.getPath()+fileName);
							if(fl.isFileSelected()){
								fl.setFileSelected(false);
								ExplorerActivity.fileInfo.remove(getFilePos(f));
								holder.titleHolder.setTextColor(Color.WHITE);
							}
							
							else{
								fl.setFileSelected(true);
								ExplorerActivity.fileInfo.add(f.getAbsolutePath());
								holder.titleHolder.setTextColor(Color.GREEN);
							}
						}
					}
					
				}
				else if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY){
					CategoryList category = categoryList.get(pos);
					File f = new File(category.getFile().getParent()+"/"+category.getFile().getName());
					if(category.isFileSelected()){
						category.setFileSelected(false);
						ExplorerActivity.fileInfo.remove(getFilePos(f));
						holder.titleHolder.setTextColor(Color.WHITE);
					}
					else{
						category.setFileSelected(true);
						ExplorerActivity.fileInfo.add(f.getAbsolutePath());
						holder.titleHolder.setTextColor(Color.GREEN);
					}
				}
				LayoutAnimationController gridAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_wave_not_scale);
				gridview.setLayoutAnimation(gridAnimation);
				notifyDataSetChanged();
			}
		});
		
		return viewItem;
	}
	
	private class ThumbAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
		
			int result=setBitmap(params[0],params[1],params[2], params[3], params[4]);	   // path,  file,  position, cType	, explorer type
			
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result==1){
				notifyDataSetChanged();
			}
			if(--threadCount==0)
			{
				LayoutAnimationController gridAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_wave_not_scale);
				gridview.setLayoutAnimation(gridAnimation);
			}
			
		}
	}
	
	private class VideoThumbAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			
			int result=1;
			
			long id = Long.parseLong(params[0]);
			String vType = params[1];
			int pos = Integer.parseInt(params[2]);
		
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inSampleSize=2;
			
			Bitmap b= MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),  id,5000,
					MediaStore.Video.Thumbnails.MINI_KIND ,option);
			Bitmap tmp = null;
			if(b!=null){
				tmp = Bitmap.createScaledBitmap(b, 300, 300, true);
				if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY && categoryType.equals(vType) && !ExplorerActivity.isSearched)
					categoryList.get(pos).setBitmap(tmp);
				else
					result=0;
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result==1){
				notifyDataSetChanged();
			}
			if(--threadCount==0){
				LayoutAnimationController gridAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_wave_not_scale);
				gridview.setLayoutAnimation(gridAnimation);
			}
		}
	}
	
	private class MusicThumbAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			
			int result=1;
		
			Bitmap bitmap = getArtworkQuick(context, Integer.parseInt(params[0]), 100, 100);
			if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY && categoryType.equals(params[1])&& !ExplorerActivity.isSearched){
				categoryList.get(Integer.parseInt(params[2])).setBitmap(bitmap);
			}
			else
				result=0;
			
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if(result==1){
				notifyDataSetChanged();
			}
			if(--threadCount==0){
				LayoutAnimationController gridAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_wave_not_scale);
				gridview.setLayoutAnimation(gridAnimation);
			}
		}
	}
	
	private int setBitmap(String path, String file, String position, String cType, String exType) {
		// TODO Auto-generated method stub
//		String path = FileListManager.FilePhoto_List.get(position).getPath();
		
		int result=1;
		
		BitmapFactory.Options option = new BitmapFactory.Options();
		int pos = Integer.parseInt(position);
		CategoryList list = null;
		if(type == ExplorerActivity.ADAPTER_TYPE_CATEGORY){
			if(categoryList.size()==0)
				return 0;
			list = categoryList.get(pos);
		}
		
		if (new File(path+file).length() > 200000)
			option.inSampleSize = 7;
		else
			option.inSampleSize = 4;
		
		if(type==ExplorerActivity.ADAPTER_TYPE_EXPLORER){
			if(path.equals(dataList.get_Path())){
				if(BitmapFactory.decodeFile(path+file, option)==null){
					Bitmap bitmap = BitmapFactory.decodeFile(path+file, option);
					if(path.equals(dataList.get_Path())){
						((FileList)dataList.getExpList().get(pos)).setBitmap(bitmap);
					}
					else{
						result=0;
					}
				}
				else{
					Bitmap tmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path+file, option), 72, 72, true);
					if(path.equals(dataList.get_Path()) && (Integer.valueOf(exType) == type)){
						((FileList)dataList.getExpList().get(pos)).setBitmap(tmp);
					}
					else{
						result=0;
					}
				}
			}
			else{
				result=0;
			}
		}
		
		else if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY){
			if(BitmapFactory.decodeFile(path+file, option)==null){
				Bitmap bitmap = BitmapFactory.decodeFile(path+file, option);
				if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY && categoryType.equals(cType) && !ExplorerActivity.isSearched){
					list.setBitmap(bitmap);
				}
				else{
					result=0;
				}
			}
			else{
				Bitmap tmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path+file, option), 72, 72, true);
				if(type==ExplorerActivity.ADAPTER_TYPE_CATEGORY && categoryType.equals(cType)&& !ExplorerActivity.isSearched){
					list.setBitmap(tmp);
				}
				else{
					result=0;
				}
			}
		}
		
		return result;
	}
	
	private class ApkBitmapAsync extends AsyncTask<String, Void, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			
			int result=1;
			
			Bitmap b = HongUtil.getApkBitmap(new File(params[0]+params[1]),context);
			if(params[0].equals(dataList.get_Path())){
				((FileList)dataList.getExpList().get(Integer.parseInt(params[2]))).setBitmap(b);
			}
			else{
				result = 0;							// 寃쎈�媛�諛����� 寃쎌� 泥댄�
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result==1)
				notifyDataSetChanged();
			LayoutAnimationController gridAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_wave_not_scale);
			gridview.setLayoutAnimation(gridAnimation);
		}
	}
	
	public static Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
        w -= 2;
        h -= 2;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;
                // Compute the closest power-of-two scale factor 
                // and pass that to sBitmapOptionsCache.inSampleSize, which will
                // result in faster decoding and better quality
                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth>w && nextHeight>h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }
                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        b.recycle();
                        b = tmp;
                    }
                }
                
                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
			
	
	private int getFilePos(File f){
		int result=0;
		for(int i = 0 ; i < ExplorerActivity.fileInfo.size() ; i++){
			if(ExplorerActivity.fileInfo.get(i).equals(f.getAbsolutePath())){
				return i;
			}
		}
		return result;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public ArrayList<CategoryList> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<CategoryList> categoryList) {
		this.categoryList = categoryList;
	}
	public String getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}
	
	
	
	static class ImageViewHolder{
		ImageView imgHolder;
		TextView titleHolder;
	}

}
