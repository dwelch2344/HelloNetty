package co.davidwelch.netty.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface View {
	
	String getContentType();
	void render(OutputStream os, Map<String, Object> model) throws IOException; 
	
}
