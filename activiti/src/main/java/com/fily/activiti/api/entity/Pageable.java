/**  

* <p>Title: Pageable.java</p>  

* <p>Description: </p>  

* <p>Copyright: Copyright (c) 2017</p>  

* <p>Company: http://www.benefitech.cn/</p>  

* @author liulin  

* @date 2018年7月14日  

* @version 1.0  

*/
package com.fily.activiti.api.entity;

import java.io.Serializable;

/**
 * 
 * <p>
 * Title: Pageable
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
public class Pageable implements Serializable {
	private static final long serialVersionUID = -3198048449643774660L;
	private int pageNow = 1; // 当前页数

	private int pageSize = 10; // 每页显示记录的条数

	private long totalCount; // 总的记录条数

	private long totalPageCount; // 总的页数

	@SuppressWarnings("unused")
	private int startPos; // 开始位置，从0开始

	public Pageable() {
	}
	// 通过构造函数 传入 总记录数 和 当前页
	public Pageable(long totalCount, int pageNow) {
		this.totalCount = totalCount;
		this.pageNow = pageNow;
	}

	// 取得总页数，总页数=总记录数/每页显示记录的条数
	public long getTotalPageCount() {
		totalPageCount = getTotalCount() / getPageSize();
		return (totalCount % pageSize == 0) ? totalPageCount : totalPageCount + 1;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getPageNow() {
		return pageNow;
	}

	public void setPageNow(int pageNow) {
		this.pageNow = pageNow;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}



	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public int getStartPos() {
		return (pageNow - 1) * pageSize;
	}
}
