package co.davidwelch.netty.mvc.support;

import java.lang.reflect.Method;

public class MethodMapping {

	private final Object handler;
	private final Method method;
	public MethodMapping(Object handler, Method method) {
		super();
		this.handler = handler;
		this.method = method;
	}
	public Object getHandler() {
		return handler;
	}
	public Method getMethod() {
		return method;
	}
	
}
