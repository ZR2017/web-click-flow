/**  
* Title: WebLogBeanParser.java
* Description:
* Copyright: Copyright (c) 2018
* Company: yz0515.cn
* @author yangzheng  
* @date 2018��3��9��  
* @version 1.0  
*/
package cn.yz0515.bigdata.hive.mr.parsers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import cn.yz0515.bigdata.exception.ExceptionCode;
import cn.yz0515.bigdata.exception.ServiceRuntimeException;
import cn.yz0515.bigdata.hive.mr.bean.WebLogBean;

/**  
* Title: WebLogBeanParser 
* Description:
* 	��־������
* @author yangzheng  
* @date 2018��3��9��  
*/
public class WebLogBeanParser {
	public static SimpleDateFormat df1 = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
    public static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    
    /**
     * �зָ���
     */
    public static final String SPLIT_DELIMITER = "\001";
    
    public static WebLogBean parse(String line) {
    	if (StringUtils.isEmpty(line)) {
    		return null;
    	}
    	
    	WebLogBean bean = new WebLogBean();
    	
    	String[] fields = line.split(" ");
    	
    	if (fields.length < 12) {	//��¼�����ݱ���Ҫ�������е����ݣ�������Ϊ��������
    		bean.setValid(false);
    		return bean;
    	}
    	
    	bean.setRemote_addr(fields[0]);
        bean.setRemote_user(fields[1]);
    	String time_local = getFormatDate(fields[3].substring(1));
    	if (time_local == null) {
    		bean.setValid(false);	//ʱ��ת�����󣬶�Ϊ������
    		return bean;
    	}
    	bean.setTime_local(time_local);
        bean.setRequest(fields[6]);
        bean.setStatus(fields[8]);
        bean.setBody_bytes_sent(fields[9]);
        bean.setHttp_referer(fields[10]);
        
        // ��ȡ�ͻ��������ϵͳ��Ϣ
        StringBuilder sb = new StringBuilder();
        for(int i= 11; i < fields.length; i++) {
            sb.append(fields[i]);
        }
        bean.setHttp_user_agent(sb.toString());
        
        if (Integer.parseInt(bean.getStatus()) >= 400) {	// ����400��HTTP���󣬶�Ϊ������
            bean.setValid(false);
        }
        
    	return bean;
    }
    
    public static String getFormatDate(String time_local) {
    	try {
			return df2.format(df1.parse(time_local));
		} catch (ParseException e) {
			return null;
		}
    }
    
    public static void filtStaticResource(WebLogBean bean, Collection<String> pages) {
        if (!pages.contains(bean.getRequest())) {
            bean.setValid(false);
        }
    }
    
    public static Date toDate(String timeStr) {
        if (StringUtils.isEmpty(timeStr)) {
            return null;
        }
        
        try {
            return df2.parse(timeStr);
        } catch (ParseException e) {
            throw new ServiceRuntimeException(ExceptionCode.COMMON.DATE_FORMAT_EXCEPTION, "���ڸ�ʽ������");
        }
    }
    
    public static long timeDiff(String time1, String time2) {
        Date d1 = toDate(time1);
        Date d2 = toDate(time2);
        long millSeconds = d1.getTime() - d2.getTime();
        return millSeconds;
    }
}
