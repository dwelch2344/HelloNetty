package co.davidwelch.netty.mvc.support;

public class MethodMappingNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public MethodMappingNotFoundException() {
		super();
	}

	public MethodMappingNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MethodMappingNotFoundException(String arg0) {
		super(arg0);
	}

	public MethodMappingNotFoundException(Throwable arg0) {
		super(arg0);
	}

	
}
