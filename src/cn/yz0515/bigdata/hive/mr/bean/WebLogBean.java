/**  
* Title: WebLogBean.java
* Description:
* Copyright: Copyright (c) 2018
* Company: yz0515.cn
* @author yangzheng  
* @date 2018��3��9��  
* @version 1.0  
*/
package cn.yz0515.bigdata.hive.mr.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import cn.yz0515.bigdata.hive.mr.parsers.WebLogBeanParser;

/**  
* Title: WebLogBean 
* Description:
* 	�Խ��ⲿ���ݵĲ㣬��ṹ������ø��ⲿ����Դ����һ��
* 	��־��Դ��
* 	������ϴԭʼ��־����
* @author yangzheng  
* @date 2018��3��9��  	
*/
public class WebLogBean implements Writable {
	/*
	 * ��־���ݸ�ʽʾ��
	 * 58.248.178.212 - - [18/Sep/2013:06:51:37 +0000] "GET /nodejs-grunt-intro/ HTTP/1.1" 200 51770 "http://blog.fens.me/series-nodejs/" "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; MDDR; InfoPath.2; .NET4.0C)"
    */
    private boolean valid = true;	// �ж������Ƿ�Ϸ�
    private String remote_addr;		// ��¼�ͻ��˵�ip��ַ
    private String remote_user;		// ��¼�ͻ����û�����,��������"-"
    private String time_local;		// ��¼����ʱ����ʱ��
    private String request;			// ��¼�����url��httpЭ��
    private String status;			// ��¼����״̬���ɹ���200
    private String body_bytes_sent;	// ��¼���͸��ͻ����ļ��������ݴ�С
    private String http_referer;	// ������¼���Ǹ�ҳ�����ӷ��ʹ�����
    private String http_user_agent;	// ��¼�ͻ�������������Ϣ
	
    public void set(boolean valid, String remote_addr, String remote_user, String time_local, String request, String status, String body_bytes_sent, String http_referer, String http_user_agent) {
        this.valid = valid;
        this.remote_addr = remote_addr;
        this.remote_user = remote_user;
        this.time_local = time_local;
        this.request = request;
        this.status = status;
        this.body_bytes_sent = body_bytes_sent;
        this.http_referer = http_referer;
        this.http_user_agent = http_user_agent;
    }
    
	@Override
	public void readFields(DataInput in) throws IOException {
		this.valid = in.readBoolean();
        this.remote_addr = in.readUTF();
        this.remote_user = in.readUTF();
        this.time_local = in.readUTF();
        this.request = in.readUTF();
        this.status = in.readUTF();
        this.body_bytes_sent = in.readUTF();
        this.http_referer = in.readUTF();
        this.http_user_agent = in.readUTF();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeBoolean(this.valid);
        out.writeUTF(null == remote_addr ? "" : remote_addr);
        out.writeUTF(null == remote_user ? "" : remote_user);
        out.writeUTF(null == time_local ? "" : time_local);
        out.writeUTF(null == request ? "" : request);
        out.writeUTF(null == status ? "" : status);
        out.writeUTF(null == body_bytes_sent ? "" : body_bytes_sent);
        out.writeUTF(null == http_referer ? "" : http_referer);
        out.writeUTF(null == http_user_agent ? "" : http_user_agent);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
        sb.append(this.valid);
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getRemote_addr());
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getRemote_user());
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getTime_local());
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getRequest());
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getStatus());
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getBody_bytes_sent());
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getHttp_referer());
        sb.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.getHttp_user_agent());
        return sb.toString();
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the remote_addr
	 */
	public String getRemote_addr() {
		return remote_addr;
	}

	/**
	 * @param remote_addr the remote_addr to set
	 */
	public void setRemote_addr(String remote_addr) {
		this.remote_addr = remote_addr;
	}

	/**
	 * @return the remote_user
	 */
	public String getRemote_user() {
		return remote_user;
	}

	/**
	 * @param remote_user the remote_user to set
	 */
	public void setRemote_user(String remote_user) {
		this.remote_user = remote_user;
	}

	/**
	 * @return the time_local
	 */
	public String getTime_local() {
		return time_local;
	}

	/**
	 * @param time_local the time_local to set
	 */
	public void setTime_local(String time_local) {
		this.time_local = time_local;
	}

	/**
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the body_bytes_sent
	 */
	public String getBody_bytes_sent() {
		return body_bytes_sent;
	}

	/**
	 * @param body_bytes_sent the body_bytes_sent to set
	 */
	public void setBody_bytes_sent(String body_bytes_sent) {
		this.body_bytes_sent = body_bytes_sent;
	}

	/**
	 * @return the http_referer
	 */
	public String getHttp_referer() {
		return http_referer;
	}

	/**
	 * @param http_referer the http_referer to set
	 */
	public void setHttp_referer(String http_referer) {
		this.http_referer = http_referer;
	}

	/**
	 * @return the http_user_agent
	 */
	public String getHttp_user_agent() {
		return http_user_agent;
	}

	/**
	 * @param http_user_agent the http_user_agent to set
	 */
	public void setHttp_user_agent(String http_user_agent) {
		this.http_user_agent = http_user_agent;
	}
}
