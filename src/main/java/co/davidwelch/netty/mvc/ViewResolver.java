package co.davidwelch.netty.mvc;


public interface ViewResolver {

	View resolve(String viewName);
}
