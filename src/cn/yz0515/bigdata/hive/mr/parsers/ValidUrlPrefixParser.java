/**  
* Title: ValidUrlPrefixParser.java
* Description:
* Copyright: Copyright (c) 2018
* Company: yz0515.cn
* @author yangzheng  
* @date 2018��3��9��  
* @version 1.0  
*/
package cn.yz0515.bigdata.hive.mr.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.yz0515.bigdata.exception.ExceptionCode;
import cn.yz0515.bigdata.exception.ServiceRuntimeException;

/**  
* Title: ValidUrlPrefixParser 
* Description:
* 	����valid-url-prefix.conf�ļ�
* 	��ȡ��ȷ�ķ���·��ǰ׺
* @author yangzheng  
* @date 2018��3��9��  
*/
public class ValidUrlPrefixParser {
	private static final String CONFIG_NAME = "valid-url-prefix.conf";
	
	public static Collection<String> parse() {
		InputStream input = ValidUrlPrefixParser.class.getClassLoader().getResourceAsStream(CONFIG_NAME);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		
		Set<String> lines = new HashSet<>();
		
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				if (StringUtils.isEmpty(line)) {
					continue;
				}
				lines.add(line);
			}
		} catch (IOException e) {
			throw new ServiceRuntimeException(ExceptionCode.IOCode.IO_EXCEPTION, "�����ļ�valid-url-prefix.conf��ȡ�쳣");
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				throw new ServiceRuntimeException(ExceptionCode.IOCode.IO_EXCEPTION, "���ر��쳣");
			}
		}
		
		return lines;
	}
	
	//����
	public static void main(String[] args) {
        System.out.println(ValidUrlPrefixParser.parse());
    }
}
