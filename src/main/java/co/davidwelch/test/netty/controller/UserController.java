package co.davidwelch.test.netty.controller;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;

import co.davidwelch.netty.mvc.Controller;
import co.davidwelch.netty.mvc.ModelAndView;
import co.davidwelch.netty.mvc.RequestMapping;
import co.davidwelch.test.netty.SomeBean;

@Controller
@RequestMapping(value="/users")
public class UserController {

	private final SomeBean someBean;
	
	@Autowired
	public UserController(SomeBean someBean) {
		this.someBean = someBean;
		System.out.println("Created userController with " + this.someBean);
	}
	
	@RequestMapping("/list")
	public ModelAndView buh(HttpRequest request){
		System.out.println("Users controller called for " + request.getUri());
		
		return new ModelAndView("test")
			.add("foo", "bar");
	}
}
