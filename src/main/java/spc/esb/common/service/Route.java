package spc.esb.common.service;

/**
 * 路由规则接口，目前路由规则分为静态和动态：qname & ftlRule 两种, 提出此接口，用于以后整合未来各种路由形式
 * 
 * @author chenjs
 * 
 */
public interface Route
{
	boolean isValidRoute();
	
	String getQname(); // 静态路由队列名

	String getFtlRule(); // Ftl语法的动态路由规则

	String getRouteBeanName(); // added by chenjs 2011-12-20
								// 可以动态执行spring环境中的bean进行路由
}
