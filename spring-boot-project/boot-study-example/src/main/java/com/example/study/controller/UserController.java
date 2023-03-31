package com.example.study.controller;

import com.example.study.entity.User;
import com.example.study.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：xjh
 * @date ：Created in 2023/3/28 11:14
 * @description：
 * @modified By：
 * @version:
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Resource
	private UserService userService;

	@GetMapping("list")
	public List<User> list(){
		return userService.list();
	}

	@GetMapping("update")
	public void updateUser(@RequestParam("name") String name, @RequestParam("id") Integer id){
		userService.updateUser(name, id);
	}

}
