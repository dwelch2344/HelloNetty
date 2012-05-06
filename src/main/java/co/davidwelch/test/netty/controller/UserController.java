package co.davidwelch.test.netty.controller;

import org.springframework.beans.factory.annotation.Autowired;

import co.davidwelch.netty.mvc.Controller;
import co.davidwelch.netty.mvc.RequestMapping;
import co.davidwelch.test.netty.SomeBean;

@Controller
@RequestMapping(value="/users")
public class UserController {

	private final SomeBean someBean;
	
	@Autowired
	public UserController(SomeBean someBean) {
		this.someBean = someBean;
		System.out.println("Created userController with " + someBean);
	}
	
	@RequestMapping("/list")
	public void buh(){
		System.out.println("Users controller called!");
	}
}
