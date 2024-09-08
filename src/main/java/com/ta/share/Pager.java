package com.ta.share;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pager {

	private int size;
	private int current;
	private String searchVal;
	
}
