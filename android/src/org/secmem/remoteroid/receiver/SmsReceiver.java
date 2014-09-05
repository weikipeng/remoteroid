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

package org.secmem.remoteroid.receiver;

import java.util.ArrayList;

import org.secmem.remoteroid.data.RDSmsMessage;
import org.secmem.remoteroid.service.RemoteroidService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	
	public static final String EXTRA_MSGS = "msg";
		
	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		
		if(extras != null){
		    Object[] pdus = (Object[])extras.get("pdus");
		    SmsMessage[] messages = new SmsMessage[pdus.length];
		     
		    for(int i=0; i<pdus.length; i++){
		    	messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
		    }
		    
		    ArrayList<RDSmsMessage> rdMsgs = new ArrayList<RDSmsMessage>();
		    
		    for(SmsMessage message: messages){
		    	RDSmsMessage rdMsg = new RDSmsMessage();
		    	
		    	rdMsg.setPhoneNumber(message.getOriginatingAddress());
		    	rdMsg.setMessageBody(message.getMessageBody());
		    	rdMsg.setDeliveredAt(message.getTimestampMillis());
		    	rdMsg.setDisplayedName(context);
		    	
		    	rdMsgs.add(rdMsg);
		    }
		    // In broadcast receiver, you cannot send broadcast or bind to service.
		    // To send SMS data to Remoteroid by avoiding limitations as described,
		    // I just decided to pass data with startService.
		    // SMS data sent from this broadcast will processed on RemoteroidService's
		    // onStartCommand() method.
		    context.startService(new Intent(context, RemoteroidService.class)
		    	.putParcelableArrayListExtra(EXTRA_MSGS, rdMsgs));
		 }
	}

}
