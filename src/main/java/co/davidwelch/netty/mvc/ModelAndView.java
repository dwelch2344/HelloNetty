package co.davidwelch.netty.mvc;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

	private final Map<String, Object> attributes = new HashMap<String, Object>();
	private final String viewName;
	// private View view;
	
	public ModelAndView() {
		this(null);
	}
	
	public ModelAndView(String view) {
		this.viewName = view;
	}
	
	public void add(String name, Object value){
		attributes.put(name, value);
	}

	public Map<String, Object> getAttributes() {
		return new HashMap<String, Object>(attributes);
	}

	public String getViewName() {
		return viewName;
	}
	
}
