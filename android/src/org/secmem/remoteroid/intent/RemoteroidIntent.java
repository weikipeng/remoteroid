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

package org.secmem.remoteroid.intent;

/**
 * A class represents action, category, extras related to Intent.
 * @author Taeho Kim
 *
 */
public final class RemoteroidIntent {
	/**
	 * An action used for PendingIntent to notify user about SMS has been sent successful or not.<div/>
	 * <strong>Extras:</strong><br/>
	 * {@link RemoteroidIntent#EXTRA_PHONE_NUMBER}
	 */
	public static final String ACTION_SMS_SENT = "org.secmem.remoteroid.intent.action.SMS_SENT";
	
	/**
	 * An action that successfully connected to server. (Broadcast)
	 * @see RemoteroidIntent#EXTRA_IP_ADDESS
	 */
	public static final String ACTION_CONNECTED = "org.secmem.remoteroid.intent.action.CONNECTED";
	
	/**
	 * An action that a client has disconnected from server.
	 */
	public static final String ACTION_DISCONNECTED = "org.secmem.remoteroid.intent.action.DISCONNECTED";
	
	/**
	 * An action that client failed to connect to server.
	 */
	public static final String ACTION_CONNECTION_FAILED = "org.secmem.remoteroid.intent.action.CONNECTION_FAILED";
	
	/**
	 * An action that there connection was interrupted by some reason.
	 */
	public static final String ACTION_INTERRUPTED = "org.secmem.remoteroid.intent.action.INTERRUPTED";
	
	public static final String ACTION_DEVICE_OPEN_FAILED = "org.secmem.remoteroid.intent.action.DEVICE_OPEN_FAILED";
	
	public static final String ACTION_LOGIN = "org.secmem.remoteroid.intent.action.LOGIN";
	public static final String ACTION_REGISTER = "org.secmem.remoteroid.intent.action.REGISTER";
	
	/**
	 * @see #EXTRA_IP_ADDESS
	 */
	public static final String ACTION_REMOTE_CONNECT = "org.secmem.remoteroid.intent.action.REMOTE_CONNECT";
	
	/**
	 * Broadcast action
	 */
	public static final String ACTION_DEVICE_REGISTRATION_COMPLETE = "org.secmem.remoteroid.intent.action.DEVICE_REGISTRATION_COMPLETE";
	
	/**
	 * Broadcast action
	 */
	public static final String ACTION_DEVICE_REGISTRATION_FAILED = "org.secmem.remoteroid.intent.action.DEVICE_REGISTRATION_FAILED";
	
	public static final String CATEGORY_UNIVERSAL = "org.secmem.remoteroid.intent.category.UNIVERSAL";
	
	/**
	 * Key for extra data contains server's ip address.
	 * @see RemoteroidIntent#ACTION_CONNECTED
	 */
	public static final String EXTRA_IP_ADDESS = "org.secmem.remoteroid.intent.extra.IP_ADDRESS";
	/**
	 * Key for extra data contains phone number.
	 * @see RemoteroidIntent#ACTION_SMS_SENT
	 */
	public static final String EXTRA_PHONE_NUMBER = "org.secmem.remoteroid.intent.extra.PHONE_NUMBER";
	
	/**
	 * An action that client succedded send file.
	 * @see RemoteroidIntent#ACTION_FILE_TRANSMISSION_SECCESS
	 */
	public static final String ACTION_FILE_TRANSMISSION_SECCESS = "org.secmem.remoteroid.intent.action.FILE_TRANSMISSION_SUCCESS";
	public static final String ACTION_ALL_FILE_TRANSMISSION_SECCESS = "org.secmem.remoteroid.intent.action.ALL_FILE_TRANSMISSION_SUCCESS";
	
	
	public static final String ACTION_SHOW_DRIVER_INSTALLATION_FRAGMENT = "org.secmem.remoteroid.intent.action.SHOW_DRIVER_FRAGMENT";
	public static final String ACTION_SHOW_CONNECT_FRAGMENT = "org.secmem.remoteroid.intent.action.SHOW_CONNECT_FRAGMENT";
	public static final String ACTION_SHOW_CONNECTED_FRAGMENT = "org.secmem.remoteroid.intent.action.SHOW_CONNECTED_FRAGMENT";
}
