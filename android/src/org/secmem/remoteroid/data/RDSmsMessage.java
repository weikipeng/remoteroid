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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract.PhoneLookup;

public class RDSmsMessage implements Parcelable{
	
	private String displayedName;
	private String phoneNumber;
	private String messageBody;
	private long deliveredAt;
	
	public RDSmsMessage() {
		super();
	}

	/**
	 * Construct RDSmsMessage object with Displayed name based on user's contacts.
	 * @param context A context
	 * @param phoneNumber SMS sender's Phone number
	 * @param messageBody SMS body text
	 * @param deleiveredAt When this message has delivered
	 */
	public RDSmsMessage(Context context, String phoneNumber, String messageBody,
			long deleiveredAt) {
		super();
		this.phoneNumber = phoneNumber;
		this.messageBody = messageBody;
		this.deliveredAt = deleiveredAt;
	}
	
	public String getDisplayedName() {
		return displayedName;
	}
	
	/**
	 * Set displayed name which matches own phone number based on user's contacts.
	 * @param context
	 */
	public void setDisplayedName(Context context){
		if(this.phoneNumber==null)
			throw new IllegalStateException("Phone number should be set first.");
		
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor c = context.getContentResolver().query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		
		if(c==null || c.getCount()==0){
			this.displayedName = this.phoneNumber;
		}else{
			// Get display name of the first result
			c.moveToFirst();
			this.displayedName = c.getString(c.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}
		c.close();
	}
	
	public void setDisplayedName(String displayedName) {
		this.displayedName = displayedName;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		try{
			Integer.parseInt(phoneNumber);
			this.phoneNumber = phoneNumber;
		}catch(NumberFormatException e){
			throw new IllegalArgumentException("Phone number can be consisted of decimals.");
		}
	}
	
	public String getMessageBody() {
		return messageBody;
	}
	
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	public long getDeliveredAt() {
		return deliveredAt;
	}
	
	public void setDeliveredAt(long deleiveredAt) {
		this.deliveredAt = deleiveredAt;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DisplayedName=").append(displayedName).append(" PhoneNumber=").append(phoneNumber)
				.append(" Body=").append(messageBody).append(" deliveredAt=").append(deliveredAt);
		return builder.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(displayedName);
		parcel.writeString(phoneNumber);
		parcel.writeString(messageBody);
		parcel.writeLong(deliveredAt);
	}
	
	public static final Parcelable.Creator<RDSmsMessage> CREATOR 
		= new Parcelable.Creator<RDSmsMessage>() {
		
			public RDSmsMessage createFromParcel(Parcel in) {
			    return new RDSmsMessage(in);
			}
			
			public RDSmsMessage[] newArray(int size) {
			    return new RDSmsMessage[size];
			}
		};

	public RDSmsMessage(Parcel in) {
		displayedName = in.readString();
		phoneNumber = in.readString();
		messageBody = in.readString();
		deliveredAt = in.readLong();
	}
	
	
}
