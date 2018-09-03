/*
 *
 * Copyright 2014 CDSF Corporation, Inc. All rights reserved.
 */
package com.fily.activiti.api.util;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class Tools {
	
	private Tools(){
		
	}

	
	public static boolean isNotNull(Object obj) {
		return obj != null && !"null".equals(obj) && !"".equals(obj);
	}

	/**
	 * <p>isNull.</p>
	 *
	 * @param str a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isNull(String str) {
		return str == null || "".equals(str.trim());
	}

	/**
	 * <p>isNotNull.</p>
	 *
	 * @param str a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isNotNull(String str) {
		return str != null && !"".equals(str.trim());
	}

	/**
	 * <p>isEmpty.</p>
	 *
	 * @param array an array of {@link java.lang.Object} objects.
	 * @return a boolean.
	 */
	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * <p>isNotEmpty.</p>
	 *
	 * @param array an array of {@link java.lang.Object} objects.
	 * @return a boolean.
	 */
	public static boolean isNotEmpty(Object[] array) {
		return array != null && array.length > 0;
	}
	
	/**
	 * <p>isEmpty.</p>
	 *
	 * @param list a {@link java.util.List} object.
	 * @return a boolean.
	 */
	public static boolean isEmpty(List<?> list){
	    if(list == null || list.isEmpty()){
	        return true;
	    }
	    return false;
	}

	public static boolean isNull(Object obj) {
		return obj == null || "null".equals(obj) || "".equals(obj);
	}

	
	public static String formatDateToStr(int type, Date time) {
		if (time == null) {
			return "";
		}
		String format = getPattern(type);
		return new SimpleDateFormat(format)
				.format(Long.valueOf(time.getTime()));
	}

	/**
	 * 格式化字符串为时间
	 *
	 * @param type a int.
	 * @param time a {@link java.lang.String} object.
	 * @throws java.text.ParseException if any.
	 * @return a {@link java.util.Date} object.
	 */
	public static Date formateStrToDate(int type, String time)
			throws ParseException {
		if (Tools.isNull(time)) {
			return null;
		}
		String format = getPattern(type);
		return new SimpleDateFormat(format).parse(time);
	}
	
	public static String turnUTCToDate(String utcTime) throws ParseException{
		SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date parse = formatUTC.parse(utcTime);
		SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatYYYYMMDD.setTimeZone(TimeZone.getDefault());
		return formatYYYYMMDD.format(parse);
	}

	/**
	 * <p>getPattern.</p>
	 *
	 * @param formatIndex a int.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getPattern(int formatIndex) {
		String format = null;
		switch (formatIndex) {
		case -2:
			format = "HH:mm:ss.S";
			break;
		case -1:
			format = "yyyy-MM-dd HH:mm:ss.S";
			break;
		case 0:
			format = "yyyy-MM-dd HH:mm:ss";
			break;
		case 1:
			format = "yyyy-MM-dd 00:00:00";
			break;
		case 2:
			format = "yyyy-MM-dd 23:59:59";
			break;
		case 3:
			format = "yyyy-MM-dd";
			break;
		case 4:
			format = "yyyy-MM";
			break;
		case 5:
			format = "yyyy-MM-dd HH:mm:ss";
			break;
		case 6:
			format = "yyyy-01-01 00:00:00";
			break;
		case 7:
			format = "yyyy-12-31 23:59:59";
			break;
		case 8:
			format = "yyyy-MM-01 00:00:00";
			break;
		case 10:
			format = "yyyyMMddHHmmss";
			break;
		case 11:
			format = "yyyyMMddHHmmssS";
			break;
		case 12:
			format = "yyyy年MM月dd日";
			break;
		case 13:
			format = "yyyyMMdd-HHmmssS";
			break;
		case 14:
			format = "yyyy";
			break;
		case 15:
			format = "yyyyMMdd";
			break;
		default:
			format = "yyyy-MM-dd HH:mm:ss";
		}
		return format;
	}
	
	/**
	 * 拼装删除字符串
	 *
	 * @author wangshuang
	 * @creaetime Jul 1, 2014 3:33:29 PM
	 * @param object a {@link java.lang.Object} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<String> getFlowIds(Object object){
	    String flowIds = String.valueOf(object);
	    String[] split = flowIds.split(",");
	    return Arrays.asList(split);
	}
	
	/**
	 * 创建uuid
	 *
	 * @author wangshuang
	 * @creaetime Jul 2, 2014 6:12:50 PM
	 * @return a {@link java.lang.String} object.
	 */
	public static String createUUID(){
	    return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * 获取通用格式时间对象
	 *
	 * @author CodingBoy
	 * @creaetime 2014年7月18日 上午9:18:29
	 * @throws java.text.ParseException if any.
	 * @return a {@link java.util.Date} object.
	 */
	public static Date getCommonDate() throws ParseException{
		return Tools.formateStrToDate(0,Tools.formatDateToStr(0, new Date()));
	}
	
	public static boolean startTimeLowerThanEndTime(int type, String startDate, String endDate) throws ParseException{
	    Date startD = Tools.formateStrToDate(type, startDate);
	    Date endD = Tools.formateStrToDate(type, endDate);
	    if(startD.getTime() > endD.getTime()){
	        return true;
	    }
	    return false;
	}
	
	/**
	 *方法描述：
	 *
	 * @creator(methods创建人) CodingBoy
	 * @creaetime(生成日期) 2014年8月27日 上午11:03:35
	 * @return a {@link java.lang.String} object.
	 */
	public static String createProcessCode(){
	    String ap = "0123456789QWERTYUIOPLKJHGFDSAZXCVBNM";
	    char[] charArray = ap.toCharArray();
	    Random r = new Random();
	    int nextInt = 0;
	    String code = "";
	    for(int i=0;i<8;i++){
	        nextInt = r.nextInt(ap.length());
	        code = code+charArray[nextInt];
	    }
	    return code+"-"+System.currentTimeMillis();
	}
	
	/** 
	* @Title: mapToStr 
	* @Description: 将map里的值转化字符串
	* @param @param map
	* @param @return
	* @return String    返回类型 
	* @throws 
	*/ 
	public static String mapToStr(Map<String, Object> map){
		String str = "";//用于存放遍历出来的字符串 
		Iterator<String>  iterator = map.keySet().iterator(); 
		while (iterator.hasNext()) { 
			String key=iterator.next();
          str += key+":"+map.get(key).toString()+","; 
		} 
		return str;
	}
	
	
	/**当前系统时间的下一秒
	 * @author liulin
	 * @creaetime 2015年9月9日 下午3:13:05
	 * @return
	 */
	public static Date getAfterTime(){
	    Calendar calendar = Calendar.getInstance ();
        calendar.add (Calendar.SECOND, 1);
        return calendar.getTime ();
	}
    /**
     * 得到字符串中，两个字符串中间那一段字符
     * @creaetime 2016年4月20日 下午1:12:35
     * @param str
     * @param preStr
     * @param subStr
     * @return
     */
    public static String getMiddleString(String str,String preStr,String subStr){
        String result=null;
        if(subStr==null){
            result=str.substring(str.indexOf(preStr)+preStr.length());
        }else{
            result=str.substring(str.indexOf(preStr)+preStr.length(), str.indexOf(subStr));
        }
        return result;
    }
    /**
     * 检测一段select  from之间的字符串，根据列名获取别名
     * @creaetime 2016年4月20日 下午1:13:08
     * @param str
     * @param oldProName
     * @return
     */
    public static String getReturnColumnName(String str,String oldProName){
        String midName=oldProName;
        String [] proNames=str.split(",");
        for (int i = 0; i < proNames.length; i++) {
            String [] strs=proNames[i].trim().split("(\\s+(as|AS)\\s+)|(\\s+)");
            if(strs.length>1&&
                    (strs[0].trim().equals(oldProName)||strs[1].trim().equals(oldProName))){
                midName=strs[1].trim();
                break;
            }
        }
        return midName;
    }
    /**
     * 得到排序语句，数据权限规则文件用到
     * @creaetime 2016年4月20日 下午1:14:18
     * @param sql
     * @return
     */
    public static String getOrderByStatement(StringBuilder sql){
        //获取排序条件
        String sqlStr=sql.toString().toUpperCase();
        String oldOrderBy=getMatchesStr(sqlStr, "(order|ORDER)\\s+(by|BY)\\s+\\S+(\\s+(desc|DESC))?");
        if(isNull(oldOrderBy)) return "";
        oldOrderBy=oldOrderBy.toUpperCase().replaceAll("\\s","");
        //获取排序条件字段的外层名字
        String oldOrderPro=oldOrderBy.substring(oldOrderBy.indexOf("ORDERBY")+7);
        if(oldOrderPro.endsWith("DESC")){
            oldOrderPro=oldOrderPro.substring(0,oldOrderPro.length()-4);
        }
        String str=Tools.getMiddleString(sqlStr, "SELECT", "FROM");
        String newOrderPro=Tools.getReturnColumnName(str, oldOrderPro);
        if(newOrderPro.matches(".+\\..+")){
            newOrderPro=newOrderPro.replaceAll(".+\\.", "THIS_.");
        }else{
            newOrderPro="THIS_."+newOrderPro;
        }
        //生成新的排序条件
        String orderBy=" ORDER BY "+newOrderPro;
        if(oldOrderBy.endsWith("DESC")) orderBy+=" DESC";
        return orderBy;
    }
    /**
     * 得到最后一个匹配字符串
     * @author pandeng
     * @creaetime 2016年4月20日 下午5:15:51
     * @param str 被匹配的字符串
     * @param regex 要匹配的正则表达式
     * @return
     */
    public static String getMatchesStr(String str,String regex){
        Pattern p=Pattern.compile(regex);
        Matcher m=p.matcher(str);
        String result=null;
        while(m.find()){
            result=m.group();
        }
        return result;
    }
    
    /**
	 * @Description：<P>判断是否为数字</P>
	 * @param str
	 * @return
	 * @date 2016年11月10日下午3:15:27
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
//		Pattern pattern = Pattern.compile("-?[A-Za-z]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
//			System.out.println("false");
			return false;
		}
//		System.out.println("true");
		return true;
	}
	/**
	 * @param str
	 * @return
	 * @date 2017年1月11日上午9:03:20
	 */
	public static boolean isNumStart(String str){
		
		Pattern pattern = Pattern.compile("^(\\d+)(.*)");
		Matcher matcher = pattern.matcher(str);
		if(matcher.matches()){
//			System.out.println("false");
			return false;
		}
//		System.out.println("true");
		return true;
	}
	/**
	 * @param num
	 * @return
	 * @author SF2136
	 * @date 2017年2月7日下午5:51:47
	 */
	public static boolean isNum(String num){
		
		String rex = "^[\\+]?[\\d]*$";
		Pattern p = Pattern.compile(rex);
		Matcher m = p.matcher(num);
		if (m.find()){
			//System.out.println("是数字");
			return true;
		}else{
			//System.out.println("不是数字");
			return false;
		}
	}
	public static boolean isMatched(String regex,String str){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if(matcher.matches()){
            return true;
        }
        return false;
    }
	public static Timestamp getSqlDate(Date date){
		if(date==null) return null;
		return new Timestamp(date.getTime());
	}
	public static Date getUtilDate(Timestamp date){
		if(date==null) return null;
		return new Date(date.getTime());
	}
	public static Date plusDays(String dateStr,int type,int n)throws ParseException{
		if(dateStr==null) return null;
		Date date=formateStrToDate(type, dateStr);
		return new DateTime(date.getTime()).plusDays(n).toDate();
	}
	
	public static String generateRegularByMask(String mask){
		if(Tools.isNull(mask)) return null;
		String preRegular="^-?[0-9]+";
		String subRegular="(\\.[0-9]+)$";
		String[] parts=mask.split("\\.");
		String preMask=parts[0];
		int preZeroNum=0;
		if(preMask.contains("0")){
			preZeroNum=preMask.length();
			preRegular=preRegular.replaceFirst("+", "{0,"+preZeroNum+"}");
		}
		String subMask=parts[1];
		int subZeroNum=0;
		if(subMask.contains("0")){
			subZeroNum=subMask.length();
			subRegular=subRegular.replace("+", "{"+subZeroNum+"}");
		}
		return preRegular+subRegular;
	}
    /** 
     * 方法名: addNonEmptyCrossjoin
     * 描述：<方法的功能和实现思路>
     * @param list
     * @return String
     *
     * 创建日期: 2016年11月24日
     */
    public static String addNonEmptyCrossjoin(List<String> list){
        if(Tools.isEmpty(list)){
            return "";
        }
        if(list.size() == 1){
            return list.get(0);
        }else{
            String mdxString = "NonEmptyCrossjoin(" + list.get(0) +"," + list.get(1) + ")";
            for(int i = 2;i<list.size() ;i++){
                mdxString = "NonEmptyCrossjoin(" + list.get(i) +"," + mdxString + ")";
            }
            return mdxString;
        }
    }
    /**
     * 追加字符串
     * @param str 待修改字符串
     * @param addStr 追加字符
     * @param num 追加个数
     * @return
     * @throws Exception
     */
    public static String addSuffixStr(String str,String addStr,int num)throws Exception{
    	StringBuilder sb=new StringBuilder(str.length()+addStr.length()*num);
    	sb.append(str);
    	for (int i = 0; i < num; i++) {
    		sb.append(addStr);
		}
    	return sb.toString();
    }
    
    /**
     * 将javaBean 转为map
     * @createtime 2017-6-22 下午4:10:09
     * @param obj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {    
        if(obj == null){    
            return null;    
        }   
  
        Map<String, Object> map = new HashMap<String, Object>();    
  
        Field[] declaredFields = obj.getClass().getDeclaredFields();    
        for (Field field : declaredFields) {    
            field.setAccessible(true);  
            map.put(field.getName(), field.get(obj));  
        }    
        return map;  
    }
    static Map<String,String> XML_REPLACE_CHAR = new HashMap<>();
    static{
    	XML_REPLACE_CHAR.put("&amp;", "&");
    	XML_REPLACE_CHAR.put("&lt;", "<");
    	XML_REPLACE_CHAR.put("&gt;", ">");
    	XML_REPLACE_CHAR.put("&quot;", "\"");
    	XML_REPLACE_CHAR.put("&apos;", "'");
    	XML_REPLACE_CHAR.put("&nbsp;", " ");
    	XML_REPLACE_CHAR.put("&copy;", "©");
    	XML_REPLACE_CHAR.put("&reg;", "®");
    }
	public static String getTextsFromSentence(String sentence){
		if(Tools.isNull(sentence)) return sentence;
		//1,把转义的东西去掉
		sentence=sentence.replaceAll("\"(.*?)\"", "#");
		//2,把xml转义字符替换一下
		Matcher matcher = Pattern.compile("(&amp;|&lt;|&gt;|&quot;|&apos;|&nbsp;|&copy;|&reg;)").matcher(sentence);
		String desStr = null;
		while(matcher.find()){
			desStr = matcher.group(1);
			sentence=sentence.replace(desStr, XML_REPLACE_CHAR.get(desStr));
		}
		Pattern pattern = Pattern.compile("<span(.*?)>(.+)</span>");
		matcher = pattern.matcher(sentence);
		String middleStr = null;
		while(matcher.find()){
			middleStr = matcher.group(2);
			break;
//			System.out.println(middleStr);
		}
		String[] strs =middleStr.split("</span><span(.*?)>");
		StringBuilder sb = new StringBuilder(512);
		for (int i = 0; i < strs.length; i++) {
			sb.append(" ").append(strs[i]).append(" ");
		}
		return sb.toString();
	}
	public static String formatMonthOrDay(int year){
		DecimalFormat format = new DecimalFormat("00");
		return format.format(year);
	}
	
	/**
	 * 方法描述：String to Hex
	 */
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }
	public static String toString(Object obj,String defaultValue){
		if(obj==null) return defaultValue;
		return obj.toString();
	}

	public static String replaceCharacter(String keyword){
		return keyword.replace("'", "\\'").replace("%", "\\%");
	}

	/**
	 * 判断字符串中是否包含中文
	 * @param str
	 * 待校验字符串
	 * @return 是否为中文
	 * @warn 不能校验是否为中文标点符号 
	 */
	public static boolean isContainChinese(String str) {
	 Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
	 Matcher m = p.matcher(str);
	 if (m.find()) {
		 return true;
	 }
	 return false;
	}
	
	public static boolean isChinese(char c) {
	    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
	    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
	      || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	      || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
	      || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
	      || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
	      || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
	     return true;
	    }
	    return false;
	}
	
	public static boolean containChinese(String str) {
	    if(!isContainChinese(str)){
	    	char[] chars = str.toCharArray();
	    	for (char aChar : chars) {
                if(isChinese(aChar)){
                	return true;
                }
            }
	    	return false;
	    }
	    return true;
	}
	public static DateTime toUtc(Date localDate){
	    return new DateTime(localDate.getTime(), DateTimeZone.UTC);
	}
	
	public static DateTime toUtc(String localDateStr,String pattern) throws ParseException{
	    Date localDate = new SimpleDateFormat(pattern).parse(localDateStr);
	    return toUtc(localDate);
	}
//	public static void main(String[] args) {
//		String  lhs="<span flowid=\"c47b4f51e3aa48d68227395bb6cfb089\" holder=\"$0\" contenteditable=\"false\">SEX</span><span holder=\"${==}\">==</span><span holder=\"&quot;1&quot;\">1</span><span holder=\"${&amp;&amp;}\">And</span><span flowid=\"86e64c7d07cd401ba6ca228df39188ab\" holder=\"$1\" contenteditable=\"false\">FAMILY</span><span holder=\"${>=}\">&gt;=</span><span holder=\"&quot;500&quot;\">500</span>"
//				+ "<span flowid=\"86e64c7d07cd401ba6ca228df39188ab\" holder=\"$1\" contenteditable=\"false\">FAMILY</span><span holder=\"${>=}\">&gt;=</span><span holder=\"&quot;500&quot;\">500</span>";
//		System.out.println(getTextsFromSentence(lhs));
//	}
	
    public static<T> boolean contains(T[] arr, T targetValue) {
        for(T s: arr){
            if(s.equals(targetValue))
                return true;
        }
        return false;
    }
    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }
}
