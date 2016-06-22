package org.upay.service.sample;
import com.alibaba.dubbo.config.annotation.Service;

@Service(version="1.0.0")
public class SampleServiceImpl implements SampleService{

	public String getName() {
		return "this is a sample service.";
	}
}
