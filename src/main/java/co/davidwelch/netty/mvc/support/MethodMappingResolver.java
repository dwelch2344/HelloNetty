package co.davidwelch.netty.mvc.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class MethodMappingResolver {

	private final Map<String, MethodMapping> mappings;

	public MethodMappingResolver(Map<String, MethodMapping> mappings) {
		super();
		this.mappings = mappings;
	}
	
	public void invoke(String path){
		MethodMapping mapping = mappings.get(path);
		if(mapping == null) throw new IllegalStateException("Unhandled route");
		
		Method method = mapping.getMethod();
		Object handler = mapping.getHandler();
		
		
		List<Object> params = new ArrayList<Object>();
		
		Class<?>[] paramTypes = method.getParameterTypes();
		StringBuilder sb = new StringBuilder();
		for(Class<?> param : paramTypes){
			sb.append(param.getSimpleName()).append(", ");
			addParam(params, param);
		}
		System.out.println("Parameter types: " + sb.toString());
		
		try {
			method.invoke(handler, params.toArray()); // TODO process args
		} catch (Exception e) {
			throw new RuntimeException("Failed executing method", e);
		} 
		
		
	}
	
	private void addParam(List<Object> params, Class<?> klazz){ // todo include other types that might end up there
		if(klazz.isAssignableFrom(HttpRequest.class)){
			// TODO add the request
		}else if(klazz.isAssignableFrom(HttpResponse.class)){
			// TODO add the response
		}else{
			// at this point, we have no idea what it is. 
			// try creating a new instance and adding it!
			try {
				Object o = klazz.newInstance();
				params.add(o);
			} catch (InstantiationException e) {
				throw new RuntimeException("Couldn't create bean of type " + klazz.getSimpleName() + " for handler.", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Couldn't create bean of type " + klazz.getSimpleName() + " for handler.", e);
			}
			
		}
	}
	
}
