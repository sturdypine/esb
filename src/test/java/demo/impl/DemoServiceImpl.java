package demo.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import demo.DemoService;
import demo.ValidationParameter;
import spc.webos.persistence.jdbc.datasource.DataSource.ColumnPath;
import spc.webos.service.BaseService;

@Service("demoService")
public class DemoServiceImpl extends BaseService implements DemoService {
	@Override
	public String sayHello(String name) {
		// try {
		// Thread.sleep(200l);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		return "Hello " + name;
	}

	public String sayHello(String name, int age) {
		return "Hello " + name + ":" + age;
	}

	public ValidationParameter save(@ColumnPath("name") ValidationParameter p) {
		log.info("vp:{}", p.getName());
		return p;
	}

	public ValidationParameter save(String name, List<ValidationParameter> p) {
		log.info("name:{}, vp:{}", name, p);
		p.get(0).setName("hello, " + p.get(0).getName());
		return p.get(0);
	}
}
