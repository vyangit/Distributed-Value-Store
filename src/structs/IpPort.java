package structs;

import enums.IpType;
import exceptions.UnidentifiedIpException;

public class IpPort {
	IpType ipType;
	String ipAddress;
	String port;
	
	public IpPort(String socketAddress) throws UnidentifiedIpException{
		String[] ipAndPortVals = splitIpPortAddress(socketAddress);
		this.ipType = checkSocketAddressType(ipAndPortVals[0]);
		if (ipType == IpType.UNDEFINED) {
			throw new UnidentifiedIpException(String.format("'%s' could not be resolved to an IP", ipAddress));
		}
		this.ipAddress = ipAndPortVals[0];
		this.port = ipAndPortVals[1];
	}
	
	
	public static String[] splitIpPortAddress(String ipPortAddress) {
		String[] vals = ipPortAddress.split(":");
		if (vals.length != 2) {
			return new String[2];
		}
		return vals;
	}
	
	public static IpType checkSocketAddressType(String ipAddress) {
		IpType type = IpType.UNDEFINED;
		// Check if socket address is null or empty
		if (ipAddress == null || ipAddress.isEmpty()) { 
			return type;
		}
		
		// Split the address and see if the socket address matches a ip type
		String[] vals = ipAddress.split(".");
		int len = vals.length;
		
		if (len == 4) { // Check if ip4
			type = IpType.IP4;
		} else if (len == 6) { // Check if ip6
			type = IpType.IP6;
		} else if (vals.length != 4 && vals.length != 6) {
			return type;
		}
		
		// Check if the numbers ranges for addr
		for (String val: vals) {
			int castedVal = Integer.parseInt(val);
			if (castedVal > 255 || castedVal < 0) {
				type = IpType.UNDEFINED;
				break;
			}
		}
		
		return type;
	}
}
