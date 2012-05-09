package co.davidwelch.netty.mvc.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import co.davidwelch.netty.mvc.ModelAndView;

public class MethodMappingResolver {

	private final Map<String, MethodMapping> mappings;

	public MethodMappingResolver(Map<String, MethodMapping> mappings) {
		super();
		this.mappings = mappings;
	}
	
	public ModelAndView invoke(String path, List<Object> intrinsicParameters){
		MethodMapping mapping = mappings.get(path);
		if(mapping == null) throw new MethodMappingNotFoundException("Unhandled route:" + path);
		
		Method method = mapping.getMethod();
		Object handler = mapping.getHandler();
		
		
		List<Object> params = new ArrayList<Object>();
		
		Class<?>[] paramTypes = method.getParameterTypes();
		StringBuilder sb = new StringBuilder();
		for(Class<?> param : paramTypes){
			sb.append(param.getSimpleName()).append(", ");
			addParam(params, param, intrinsicParameters);
		}
		System.out.println("Parameter types: " + sb.toString());
		
		try {
			Object returnVal = method.invoke(handler, params.toArray()); // TODO process args
			if(returnVal == null){
				return null;
			}else if(returnVal instanceof ModelAndView){
				return (ModelAndView) returnVal;
			}else if(returnVal instanceof Map<?,?>){
				throw new UnsupportedOperationException("Maps aren't supported return types -- yet");
			} 
			// TODO handle other return types here
			throw new IllegalStateException("Unhandled return value: " + returnVal.getClass());
		} catch (Exception e) {
			throw new RuntimeException("Failed executing method", e);
		} 
		
		
	}
	
	private void addParam(List<Object> params, Class<?> klazz, List<Object> intrinsicParameters){ // todo include other types that might end up there
		for(Object o : intrinsicParameters){
			if(klazz.isAssignableFrom( o.getClass() )){
				params.add(o);
				return;
			}
		}
		
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
