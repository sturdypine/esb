package demo;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.alibaba.dubbo.config.annotation.Service;

import spc.webos.util.MethodUtil.ParamNames;

@Service(version = "1.1")
public interface DemoService
{
	String sayHello(@Size(min = 3, max = 8) String name);

	String sayHello(String name, int age);

	ValidationParameter save(ValidationParameter p);

	@ParamNames("name,param")
	ValidationParameter save(@NotNull String name, List<ValidationParameter> p);

//	String say(List<Map<String, String>> list);
}
