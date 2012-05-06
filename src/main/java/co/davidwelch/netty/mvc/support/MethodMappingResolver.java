package co.davidwelch.netty.mvc.support;

import java.lang.reflect.Method;
import java.util.Map;

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
		
		try {
			method.invoke(handler); // TODO process args
		} catch (Exception e) {
			throw new RuntimeException("Failed executing method", e);
		} 
		
		
	}
}
