package spc.esb.flowctrl.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import spc.esb.constant.ESBConfig;
import spc.esb.model.FlowCtrlPO;
import spc.webos.service.resallocate.PoolableResource;
import spc.webos.service.resallocate.Resource;
import spc.webos.service.resallocate.ResourceGroup;
import spc.webos.service.resallocate.impl.AbstractResourcePoolServiceImpl;

/**
 * 流控资源池服务
 * 
 * @author chenjs
 * 
 */
@Service("esbFCResourcePoolService")
public class FCResourcePoolServiceImpl extends AbstractResourcePoolServiceImpl
{
	public FCResourcePoolServiceImpl()
	{
		versionKey = ESBConfig.DB_flowCtrlVerDt;
	}

	protected Map initResPool(List resources) throws Exception
	{
		Map resourcePool = new HashMap();
		if (resources == null || resources.size() <= 0)
		{
			log.warn("initResPool: resources is null or empty!!!");
			return resourcePool;
		}
		if (log.isInfoEnabled()) log.info("initResPool: res: " + resources);
		for (int i = 0; i < resources.size(); i++)
		{
			Resource res = (Resource) resources.get(i);
			PoolableResource pres = new PoolableResource(res);
			ResourceGroup group = (ResourceGroup) resourcePool.get(res.group());
			if (group == null)
			{
				group = new ResourceGroup(pres);
				resourcePool.put(res.group(), group);
				if (log.isDebugEnabled()) log.debug("put group res: " + group.group());
				// 将一个总流量值分为多个资源
				FlowCtrlPO fcVO = (FlowCtrlPO) pres.resource;
				if (fcVO.getCount().intValue() == 0)
					log.warn("flowctrl resource is zero: " + group.group() + "!!!");
				for (int j = 0; j < fcVO.getCount().intValue(); j++)
				{
					FlowCtrlPO nfcVO = (FlowCtrlPO) fcVO.clone();
					nfcVO.setCount(new Integer(j));
					group.add(new PoolableResource(nfcVO));
				}
			}
		}
		return resourcePool;
	}

	// 根据ESB_FLOWCTRL表中的数据加载全部流控资源
	public List loadResource() throws Exception
	{
		FlowCtrlPO flowCtrlVO = new FlowCtrlPO();
		flowCtrlVO.setStatus("1");
		return (List) persistence.get(flowCtrlVO);
	}
}
