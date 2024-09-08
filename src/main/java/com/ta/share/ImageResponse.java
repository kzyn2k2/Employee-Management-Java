package com.ta.share;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageResponse {

	public boolean state;
	public String mess;
	public byte[] barr;
}
