package co.davidwelch.test.netty;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import co.davidwelch.cdi.AnnotationProcessor;
import co.davidwelch.cdi.SomeAnnotation;

public class HelloNetty {

	public static void main(String[] args) {
		ApplicationContext app = new AnnotationConfigApplicationContext("co.davidwelch");
		
//		MethodMappingResolver resolver = app.getBean(MethodMappingResolver.class);
//		resolver.invoke("/users/list");

		// Bind and start to accept incoming connections.
		ServerBootstrap bootstrap = app.getBean(ServerBootstrap.class);		
		int port = 8080;
		bootstrap.bind(new InetSocketAddress(port));
		System.out.println("Binded on " + port);
	}
	
	public static void main3(String[] args) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
		AnnotationProcessor processor = new AnnotationProcessor(resolver);
		
		Set<Class<?>> foo = processor.scan(SomeAnnotation.class, Arrays.asList("co.davidwelch.test.netty") );
		System.out.println("I got " + foo);
		
	}
	
}
