package com.fily.activiti.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;


public class CollectionUtils {
	/**
	 * 把list转换为map
	 * @param objs
	 * @param keyName
	 * @return
	 * @throws Exception
	 */
	public static <T> Map<String,T> list2map(List<T> objs,String keyName)throws Exception{
		Map<String,T> map=new HashMap<String,T>();
		String keyValue=null;
		if(!Tools.isEmpty(objs)){
			for(T obj:objs){
				keyValue=BeanUtils.getProperty(obj, keyName);
				map.put(keyValue, obj);//潘登,去掉空判断,如果是空,也可以往map里面放
			}
		}
		return map;
	}
	public static <T> void list2map(List<T> objs,String keyName,Map<String,T> map)throws Exception{
		String keyValue=null;
		if(!Tools.isEmpty(objs)){
			for(T obj:objs){
				keyValue=BeanUtils.getProperty(obj, keyName);
				if(Tools.isNotNull(keyValue)) map.put(keyValue, obj);
			}
		}
	}
	/**
	 * 从对象中获取某个属性的List
	 * @param tList 目标list
	 * @param keyName 要获取的属性名称
	 * @param c 属性类型
	 * @return
	 * @throws Exception
	 */
	public static <T,U> List<U> getIdsFromList(List<T> tList,
			String keyName,Class<U> c)throws Exception{
		Set<U> ids=new LinkedHashSet<U>();
		for (int i = 0; i < tList.size(); i++) {
			T t=tList.get(i);
			if(Tools.isNull(t)) continue;
			String keyValue=BeanUtils.getProperty(t, keyName);
			if(keyValue!=null&&!"null".equals(keyValue)){
				U id=c.cast(keyValue);
				ids.add(id);
			}
		}
		return new ArrayList<>(ids);
		
	}
	/**
	 * 求相同flowId对象交集
	 * @param downList
	 * @param upList
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> retainAll(List<T> downList,List<T> upList,String keyName)throws Exception{
		List<T> mixedList=new ArrayList<>();
		for (int i = 0; i < downList.size(); i++) {
			T ti=downList.get(i);
			for (int j = 0; j < upList.size(); j++) {
				T tj=upList.get(j);
				if(BeanUtils.getProperty(ti, keyName).equals(BeanUtils.getProperty(tj, keyName))){
					mixedList.add(ti);
					break;
				}
			}
		}
		return mixedList;
	}
	/**
	 * 从列表中根据id删除对象
	 * @param parentId
	 * @param rangeList
	 * @return
	 * @throws Exception
	 */
	public static <T> void delByFlowId(String flowId,List<T> rangeList,String keyName)throws Exception{
		if(rangeList==null) return;
		for (int i = 0; i < rangeList.size(); i++) {
			T t=rangeList.get(i);
			if(t==null) continue;
			if(flowId.equals(BeanUtils.getProperty(t, keyName))){
				rangeList.remove(t);
				break;
			}
		}
	}
	/**
	 * 根据属性值集合删除数据
	 * @author pandeng
	 * @creaetime 2016年10月25日 下午2:04:26
	 * @param fieldValues 属性值集合
	 * @param rangeList 待过滤数据
	 * @param fieldName 属性名
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> delByFieldValue(List<String> fieldValues,List<T> rangeList,String fieldName)throws Exception{
		List<T> list=new ArrayList<>();
		if(rangeList==null) return list;
		for (int i = 0; i < rangeList.size(); i++) {
			T t=rangeList.get(i);
			if(t==null) continue;
			if(!fieldValues.contains(BeanUtils.getProperty(t, fieldName))){
				list.add(t);
			}
		}
		return list;
	}	
	/**
	 * 根据id查询对象
	 * @param flowId
	 * @param rangeList
	 * @param keyName
	 * @return
	 * @throws Exception
	 */
	public static <T> T findByFlowId(String flowId,List<T> rangeList,String keyName)throws Exception{
		if(rangeList==null) return null;
		for (int i = 0; i < rangeList.size(); i++) {
			T t=rangeList.get(i);
			if(flowId.equals(BeanUtils.getProperty(t, keyName))){
				return t;
			}
		}
		return null;
	}
	
	/**
	 * 得到两个集合的补集
	 * @param destList 目标集合
	 * @param origList 初始集合
	 * @param keyName 主键名称
	 * @return
	 */
	public static <T> List<T> getComplementary(List<T> destList,List<T> origList,
			String keyName)throws Exception{
		Map<String, T> map=list2map(destList, keyName);
		List<T> returnList=new ArrayList<>();
		for (int i = 0; i < origList.size(); i++) {
			T t=origList.get(i);
			String flowId=BeanUtils.getProperty(t, keyName);
			if(map.get(flowId)==null)
				returnList.add(t);
		}
		return returnList;
	}
	/**
	 * 把一个字符串list中的对象用分隔符连接起来
	 * @param strList
	 * @return
	 * @throws Exception
	 */
	public static  String joinStrList(List<String> strList,String seperator)throws Exception{
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < strList.size(); i++) {
			String str=strList.get(i);
			sb.append(str).append(seperator);
		}
		if(sb.length()!=0)
			sb.setLength(sb.length()-1);
		return sb.toString();
	}
	
	/**
	 * 模糊匹配数据
	 * @author pandeng
	 * @creaetime 2016年10月25日 下午1:51:21
	 * @param data
	 * @param fieldName 匹配字段
	 * @param str 匹配字符串
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> fuzzyMatching(List<T> data,String fieldName,String str)throws Exception{
		if(str==null) return data;
		List<T> list=new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			T obj=data.get(i);
			String fieldValue=BeanUtils.getProperty(obj, fieldName);
			if(fieldValue.contains(str))
				list.add(obj);
		}
		return list;
	}
	
	
	public static<T> T  convertMap2Object(Map<String,Object> map,Class<T> tClass)throws Exception{
		T t=tClass.newInstance();
		for(Map.Entry<String, Object> entry:map.entrySet()){
			BeanUtils.setProperty(t, entry.getKey(), entry.getValue());
		}
		return t;
	}
	public static <T,U> List<T> convert2VO(List<U> list,Class<T> voclass)throws Exception{
		List<T> voList=new ArrayList<T>();
		if(null==list||list.isEmpty()) return voList;
		for (U po:list) {
			T vo=voclass.newInstance();
			org.springframework.beans.BeanUtils.copyProperties(po, vo);
			voList.add(vo);
		}
		return voList;
	}
	public static <T,U> T convert2VO(U po,Class<T> voclass)throws Exception{
		T vo=voclass.newInstance();
		if(po==null) return vo;
		org.springframework.beans.BeanUtils.copyProperties(po, vo);
		return vo;
	}
}
