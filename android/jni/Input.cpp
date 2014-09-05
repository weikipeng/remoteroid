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


#include "include/Input.h"
#include "include/suinput.h"
#include <stdlib.h>

/**
 * Input File descriptor
 */
int inputFd = -1;

bool openInput(int scrWidth, int scrHeight){
	system("su -c \"chmod 666 /dev/uinput\"");
	return openInputWithoutPermission(scrWidth, scrHeight);
}

bool openInputWithoutPermission(int scrWidth, int scrHeight){
	LOGD(LOGTAG, "Opening input device...");
	struct input_id id = {
			BUS_VIRTUAL, /* Bus type. */
			1, /* Vendor id. */
			1, /* Product id. */
			1 /* Version id. */
	};

	if((inputFd = suinput_open("qwerty", &id, scrWidth, scrHeight)) == -1){
		LOGD(LOGTAG, "Cannot open device - 'qwerty'");
		return false;
	}
	LOGI(LOGTAG, "Opened device 'qwerty'");
	return true;
}

void closeInput(){
	LOGD(LOGTAG, "Closing input device...");
	if(inputFd!=-1){
		if(suinput_close(inputFd)==-1){
			LOGD(LOGTAG, "Error closing input device..");
		}
		LOGI(LOGTAG, "Device closed.");
		system("su -c \"chmod 660 /dev/uinput\"");
		inputFd = -1;
	}else{
		LOGI(LOGTAG, "Nothing to close. (Device has not opened)");
	}
}

void closeInputWithoutRevertPermission(){
	LOGD(LOGTAG, "Closing input device...");
	if(inputFd==-1){
		if(suinput_close(inputFd)==-1){
			LOGD(LOGTAG, "Error closing input device..");
		}
		LOGI(LOGTAG, "Device closed.");
	}else{
		LOGI(LOGTAG, "Nothing to close.");
	}
}

int sendNativeEvent(int uinput_fd, uint16_t type, uint16_t code, int32_t value){
	return suinput_write(uinput_fd, type, code, value);
}
