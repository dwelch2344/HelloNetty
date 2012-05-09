package co.davidwelch.netty.mvc.impl;

import co.davidwelch.netty.mvc.View;

public interface ViewResolver {

	View resolve(String viewName);
}
