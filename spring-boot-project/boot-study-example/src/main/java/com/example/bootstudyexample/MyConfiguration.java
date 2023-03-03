package com.example.bootstudyexample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：xjh
 * @date ：Created in 2023/3/2 17:01
 * @description：
 * @modified By：
 * @version:
 */
@Configuration
public class MyConfiguration {

	@Bean
	public String myString(){
		return "myString";
	}


}
