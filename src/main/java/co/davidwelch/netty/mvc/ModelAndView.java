package co.davidwelch.netty.mvc;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {

	private final Map<String, Object> model = new HashMap<String, Object>();
	private String viewName;
	private View view;
	// private View view;
	
	public ModelAndView() {}
	
	public ModelAndView(String view) {
		this();
		this.viewName = view;
	}
	
	public ModelAndView(View view) {
		this();
		this.view = view;
	}
	
	public ModelAndView add(String name, Object value){
		model.put(name, value);
		return this;
	}

	public Map<String, Object> getModel() {
		return new HashMap<String, Object>(model);
	}

	public String getViewName() {
		return viewName;
	}
	
	public View getView() {
		return view;
	}
	
}
