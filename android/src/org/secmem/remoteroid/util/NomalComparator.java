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

package org.secmem.remoteroid.util;

import java.util.Comparator;

public class NomalComparator  implements Comparator<String> {

	public int compare(String l, String r) {
		// TODO Auto-generated method stub
		if (isHangul(l)) {
			if (isHangul(r)) {
				return generalCompare(l, r);
			}
			else {
				return -1;
			}
		}
		else if (isAlphabat(l)) {
			if (isHangul(r)) {
				return 1;
			}
			else if (isAlphabat(r)) {
				return generalCompare(l, r);
			}
			else {
				return -1;
			}
		}
		else if (isNumber(l)) {
			if (isHangul(r)) {
				return 1;
			}
			else if (isAlphabat(r)) {
				return 1;
			}
			else if (isNumber(r)) {
				return generalCompare(l, r);
			}
			else {
				return -1;
			}
		}
		else {
			if (isEtc(r)) {
				return generalCompare(l, r);
			}
			else {
				return 1;
			}
		}
	}
	
	public static int generalCompare(String a, String b) {
		char l = Character.toUpperCase(a.charAt(0));
		char r = Character.toUpperCase(b.charAt(0));
		if (l > r) {
			return 1;
		}
		else if (r > l) {
			return -1;
		}
		else {
			return 0;
		}
	}
	
	public static boolean isHangul(String p) {
		if (p.charAt(0) >= 44032 && p.charAt(0) < 55203 ) {
			return true;
		}
		else 
			return false;
	}
	
	public static boolean isAlphabat(String p) {
		if (p.charAt(0) >= 'a' && p.charAt(0) <= 'z') {
			return true;
		}
		else if (p.charAt(0) >= 'A' && p.charAt(0) <= 'Z') {
			return true;
		}
		else 
			return false;
	}
	
	public static boolean isEtc(String p) {
		if (!isHangul(p) && !isAlphabat(p) && !isNumber(p)) {
			return true;
		}
		else 
			return false;
	}

	public static boolean isNumber(String p) {
		// TODO Auto-generated method stub
		if (p.charAt(0) >= '0' && p.charAt(0) <= '9') {
			return true;
		}
		else
			return false;
	}
}
