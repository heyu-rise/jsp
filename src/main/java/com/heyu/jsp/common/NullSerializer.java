package com.heyu.jsp.common;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author heyu
 *
 * 在接口中把null转为""
 */
public class NullSerializer extends JsonSerializer<Object>{

	@Override
	public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		jsonGenerator.writeString("");
	}

}
