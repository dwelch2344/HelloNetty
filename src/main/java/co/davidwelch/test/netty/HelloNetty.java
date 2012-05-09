package co.davidwelch.test.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
	
}
