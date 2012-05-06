package co.davidwelch.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import co.davidwelch.cdi.AnnotationProcessor;
import co.davidwelch.netty.mvc.Controller;
import co.davidwelch.netty.mvc.RequestMapping;
import co.davidwelch.netty.mvc.support.MethodMapping;
import co.davidwelch.netty.mvc.support.MethodMappingResolver;

@Configuration
public class NettyMvcConfig {

	private boolean addToIoc = false;
	
	@Autowired
	private GenericApplicationContext ctx;
	
	@Bean
	public MethodMappingResolver getMethodMappingResolver() throws InstantiationException, IllegalAccessException{
		// the mapping
		Map<String, MethodMapping> mapping = new HashMap<String, MethodMapping>();
		
		AutowireCapableBeanFactory autowireFactory = ctx.getAutowireCapableBeanFactory();
		ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
		AnnotationProcessor processor = ctx.getBean(AnnotationProcessor.class);
		Set<Class<?>> classes = processor.scan(Controller.class, "co.davidwelch");
		
		
		
		int i = 0;
		for(Class<?> klazz : classes){
			
			
			String name =  "bean" + i++; // get this from annotation
			Object bean = autowireFactory.createBean(klazz);
			System.out.println("Created " + name + " as " + bean);
			
			mapController(bean, mapping);
			
			if(addToIoc){
				beanFactory.registerSingleton(name, bean);
				System.out.println("Registered " + name + " as " + bean);
			}
			
		}
		
		MethodMappingResolver resolver = new MethodMappingResolver(mapping);
		return resolver;
	}
	
	
	private void mapController(Object handler, Map<String, MethodMapping> mapping){
		RequestMapping rm = AnnotationUtils.findAnnotation(handler.getClass(), RequestMapping.class);
		String[] baseUrls = rm.value();
		
		if(baseUrls.length > 1){
			throw new IllegalArgumentException("Only one BaseUrl is supported per class");
		}
		
		String baseUrl = baseUrls.length == 0 ? "" : baseUrls[0];
		
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(handler.getClass());
		for(Method method : methods){
			RequestMapping methodMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
			if(methodMapping != null){
				String[] urls = methodMapping.value();
				for(String url : urls){
					String mappedUrl = baseUrl + url;
					MethodMapping theMapping = new MethodMapping(handler, method);
					mapping.put(mappedUrl, theMapping);
					
					System.out.println("Mapped [" + mappedUrl + "] to " + handler.getClass().getSimpleName() + ":" + method.getName());
				}
			}
		}
		
		
		
		
	}
	
	
	
	
}
