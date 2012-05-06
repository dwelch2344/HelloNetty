package co.davidwelch.test.netty;

import co.davidwelch.cdi.SomeAnnotation;

@SomeAnnotation(tld="davidwelch.co")
public class SomeBean {

	public SomeBean() {
		System.out.println("Created SOME BEAN");
	}
}
