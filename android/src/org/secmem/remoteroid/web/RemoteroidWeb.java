package org.secmem.remoteroid.web;

import java.io.IOException;
import java.net.MalformedURLException;

import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.request.Request;

public class RemoteroidWeb {
	
	public static Request addAccount(String email, String password) throws MalformedURLException, IOException{
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(password);
		
		Request req = Request.Builder.setRequest(API.Account.ADD_ACCOUNT).setPayload(account).build();
		return req;
	}
	
	public static Request doLogin(String email, String securedPassword) throws MalformedURLException, IOException{
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(securedPassword);
		
		Request req = Request.Builder.setRequest(API.Account.LOGIN).setPayload(account).build();
		
		return req;
				
	}
	
	public static Request addDevice(String nickname, String email, String securedPwd, String reg, String deviceUUID) throws MalformedURLException, IOException{
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(securedPwd);
		
		Device dev = new Device();
		dev.setNickname(nickname);
		dev.setRegistrationKey(reg);
		dev.setOwnerAccount(account);
		//dev.setDeviceUUID(deviceUUID); // Use DeviceUUIDGeneratorImpl instead
		
		Request req = Request.Builder.setRequest(API.Device.ADD_DEVICE).setPayload(dev).build();
		
		return req;
	}
	
	public static void deleteAccount(){
		
	}
	
	public static Request updateInfo(String uuid, String nickname, String email, String pwd, String reg) throws MalformedURLException, IOException{
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(pwd);
		
		Device dev = new Device();
		dev.setNickname(nickname);
		//dev.setDeviceUUID(uuid); // Use DeviceUUIDGeneratorImpl instead
		dev.setRegistrationKey(reg);
		dev.setOwnerAccount(account);
		
		Request req = Request.Builder.setRequest(API.Device.UPDATE_DEVICE_INFO).setPayload(dev).build();
		
		return req;
	}
	
	

}
