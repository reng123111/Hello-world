/**  

* <p>Title: FelSupport.java</p>  

* <p>Description: </p>  

* <p>Copyright: Copyright (c) 2017</p>  

* <p>Company: http://www.benefitech.cn/</p>  

* @author liulin  

* @date 2018年7月10日  

* @version 1.0  

*/ 
package com.fily.activiti.api.service;

import java.util.Map;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;

/**  

* <p>Title: FelSupport</p>  

* <p>Description:EL表达式解析器 </p>  

* @author liulin

* @date 2018年7月10日  

*/
public class FelSupport {
	public static Object result(Map<String,Object> map,String expression){
        FelEngine fel = new FelEngineImpl();
        FelContext ctx = fel.getContext();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            ctx.set(entry.getKey(),entry.getValue());
        }
        Object result = fel.eval(expression);
        return result;
    }
}
