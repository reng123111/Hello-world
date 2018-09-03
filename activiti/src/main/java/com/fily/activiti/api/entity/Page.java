/**  

* <p>Title: Page.java</p>  

* <p>Description: </p>  

* <p>Copyright: Copyright (c) 2017</p>  

* <p>Company: http://www.benefitech.cn/</p>  

* @author liulin  

* @date 2018年7月14日  

* @version 1.0  

*/
package com.fily.activiti.api.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * <p>
 * Title: Page
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * @author liulin
 * 
 * @date 2018年7月14日
 * 
 */
public class Page implements Serializable{
	/** serialVersionUID*/  
	private static final long serialVersionUID = -4879024438196520702L;
	private Pageable pageable=null;
	private List<?> list=null;

	/**  
	
	* <p>Title: </p>  
	
	* <p>Description: </p>  
	
	* @param page
	* @param list  
	
	*/  
	
	public Page(Pageable pageable, List<?> list) {
		super();
		this.pageable = pageable;
		this.list = list;
	}

	

	/**  
	
	* <p>Title: </p>  
	
	* <p>Description: </p>  
	  
	
	*/  
	public Page() {
	}
    public Page( List<?> list){
    	this.list = list;
    }


	public Pageable getPageable() {
		return pageable;
	}



	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}



	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
}
