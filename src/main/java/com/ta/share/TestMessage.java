package com.ta.share;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestMessage {

	
	public String message;
	
	public Date date;
	
	public TestMessage(String message) {
		this.message = message;
	}
	
	public void messageDate() {
		
		this.message = this.date.toString();
	}
	
}
