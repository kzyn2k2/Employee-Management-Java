package com.ta.framework;

import java.math.BigInteger;
import java.util.UUID;

public class KeyGenerator {

	public static String getKey() {
		
		String key = String.format("%025d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
		return key;
	
	}
	
}
