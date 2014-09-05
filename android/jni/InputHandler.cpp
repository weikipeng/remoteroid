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

#include "include/InputHandler.h"
#include "include/Input.h"

extern int inputFd;

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    openInputDevice
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_secmem_remoteroid_natives_InputHandler_openInputDevice(JNIEnv* env, jobject thiz, jint scrWidth, jint scrHeight){
	return (jboolean)openInput(scrWidth-1, scrHeight-1);
}

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    closeInputDevice
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_closeInputDevice(JNIEnv* env, jobject thiz){
	closeInput();
}

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    keyDown
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_keyDown(JNIEnv *env, jobject thiz, jint keyCode){
	sendNativeEvent(inputFd, EV_KEY, keyCode, 1);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    keyUp
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_keyUp(JNIEnv *env, jobject thiz, jint keyCode){
	sendNativeEvent(inputFd, EV_KEY, keyCode, 0);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    keyStroke
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_keyStroke(JNIEnv *env, jobject thiz, jint keyCode){
	Java_org_secmem_remoteroid_natives_InputHandler_keyDown(env, thiz, keyCode);
	Java_org_secmem_remoteroid_natives_InputHandler_keyUp(env, thiz, keyCode);
}

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    touchDown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_touchDown(JNIEnv *env, jobject thiz){
	sendNativeEvent(inputFd, EV_KEY, BTN_TOUCH, 1);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);

}

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    touchUp
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_touchUp(JNIEnv *env, jobject thiz){
	sendNativeEvent(inputFd, EV_KEY, BTN_TOUCH, 0);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}

/*
 * Class:     org_secmem_remoteroid_natives_InputHandler
 * Method:    touchSetPtr
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_secmem_remoteroid_natives_InputHandler_touchSetPtr(JNIEnv *env, jobject thiz, jint x, jint y){
	sendNativeEvent(inputFd, EV_ABS, ABS_X, x);
	sendNativeEvent(inputFd, EV_ABS, ABS_Y, y);
	sendNativeEvent(inputFd, EV_SYN, SYN_REPORT, 0);
}
