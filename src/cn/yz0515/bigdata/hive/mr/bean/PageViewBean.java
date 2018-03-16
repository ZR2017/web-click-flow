/**  
* Title: PageViewBean.java
* Description:
* Copyright: Copyright (c) 2018
* Company: yz0515.cn
* @author yangzheng  
* @date 2018��3��12��  
* @version 1.0  
*/
package cn.yz0515.bigdata.hive.mr.bean;

/**  
* Title: PageViewBean 
* Description:
* @author yangzheng  
* @date 2018��3��12��  
*/

import org.apache.hadoop.io.Writable;

import cn.yz0515.bigdata.hive.mr.parsers.WebLogBeanParser;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * �����ģ��pageviews��
 * 
 */
public class PageViewBean implements Writable {
	/**
	 * �û�session
	 */
	private String session;
	/**
	 * �û�ip
	 */
	private String remote_addr;
	/**
	 * ����ʱ��
	 */
	private String timestr;
	/**
	 * ����url
	 */
	private String request;
	/**
	 * ���������Ự�ĵڼ���
	 */
	private int step;
	/**
	 * ͣ��ʱ��
	 */
	private String staylong;
	/**
	 * ��Դҳ��
	 */
	private String referal;
	/**
	 * �û������ϵͳ��Ϣ
	 */
	private String useragent;
	/**
	 * ���ʸõ�ַ���͵����ݴ�С
	 */
	private String bytes_send;
	/**
	 * ���ʽ��״̬
	 */
	private String status;

	public void set(String session, String remote_addr, String useragent, String timestr, String request, int step, String staylong, String referal, String bytes_send, String status) {
		this.session = session;
		this.remote_addr = remote_addr;
		this.useragent = useragent;
		this.timestr = timestr;
		this.request = request;
		this.step = step;
		this.staylong = staylong;
		this.referal = referal;
		this.bytes_send = bytes_send;
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.session)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.remote_addr)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.request)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.timestr)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.step)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.staylong)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(referal)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(bytes_send)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.status)
				.append(WebLogBeanParser.SPLIT_DELIMITER).append(this.useragent);
		return sb.toString();
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getRemote_addr() {
		return remote_addr;
	}

	public void setRemote_addr(String remote_addr) {
		this.remote_addr = remote_addr;
	}

	public String getTimestr() {
		return timestr;
	}

	public void setTimestr(String timestr) {
		this.timestr = timestr;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getStaylong() {
		return staylong;
	}

	public void setStaylong(String staylong) {
		this.staylong = staylong;
	}

	public String getReferal() {
		return referal;
	}

	public void setReferal(String referal) {
		this.referal = referal;
	}

	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public String getBytes_send() {
		return bytes_send;
	}

	public void setBytes_send(String bytes_send) {
		this.bytes_send = bytes_send;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.session = in.readUTF();
		this.remote_addr = in.readUTF();
		this.timestr = in.readUTF();
		this.request = in.readUTF();
		this.step = in.readInt();
		this.staylong = in.readUTF();
		this.referal = in.readUTF();
		this.useragent = in.readUTF();
		this.bytes_send = in.readUTF();
		this.status = in.readUTF();

	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(session);
		out.writeUTF(remote_addr);
		out.writeUTF(timestr);
		out.writeUTF(request);
		out.writeInt(step);
		out.writeUTF(staylong);
		out.writeUTF(referal);
		out.writeUTF(useragent);
		out.writeUTF(bytes_send);
		out.writeUTF(status);
	}
}

