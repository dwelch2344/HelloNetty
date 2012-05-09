package co.davidwelch.netty.mvc;

import java.io.IOException;
import java.io.OutputStream;

public interface View {
	
	void render(OutputStream os) throws IOException;
	String getContentType(); 
	
}
