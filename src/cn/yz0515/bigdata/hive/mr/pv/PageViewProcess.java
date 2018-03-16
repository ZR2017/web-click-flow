/**  
* Title: PageViewProcess.java
* Description:
* Copyright: Copyright (c) 2018
* Company: yz0515.cn
* @author yangzheng  
* @date 2018��3��12��  
* @version 1.0  
*/
package cn.yz0515.bigdata.hive.mr.pv;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cn.yz0515.bigdata.exception.ExceptionCode;
import cn.yz0515.bigdata.exception.ServiceRuntimeException;
import cn.yz0515.bigdata.hive.mr.bean.PageViewBean;
import cn.yz0515.bigdata.hive.mr.bean.WebLogBean;
import cn.yz0515.bigdata.hive.mr.parsers.WebLogBeanParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**  
* Title: PageViewProcess 
* Description:
* 	�����ģ��(pv)����
* 	����session�ۼ�����ҳ����Ϣ
* 	��������Ϊû��sessionid������ֻ��ͨ��ip��ַ��������
* 	ͬʱ�������ʱ���������sessionid����visitģ������
* @author yangzheng  
* @date 2018��3��12��  
*/
public class PageViewProcess {
    
    static class PageViewMapper extends Mapper<LongWritable, Text, Text, WebLogBean> {

        private Text k = new Text();
        private WebLogBean v = new WebLogBean();
        
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            // true61.148.244.188-2013-09-18 07:10:50/hadoop-hive-intro/20014764"-""Mozilla/5.0(Linux;U;Android4.1.2;zh-cn;GT-I9300Build/JZO54K)AppleWebKit/534.30(KHTML,likeGecko)Version/4.0MobileSafari/534.30V1_AND_SQ_4.2.1_3_YYB_D"
            String[] arr = line.split(WebLogBeanParser.SPLIT_DELIMITER);
            
            // �������ip�ۼ�����ͬһ���û��ķ���ҳ��ۺ���һ����reduce����
            v.setValid(Boolean.valueOf(arr[0]));
            String ip = arr[1];
            v.setRemote_addr(ip);
            v.setRemote_user(arr[2]);
            v.setTime_local(arr[3]);
            v.setRequest(arr[4]);
            v.setStatus(arr[5]);
            v.setBody_bytes_sent(arr[6]);
            v.setHttp_referer(arr[7]);
            v.setHttp_user_agent(arr[8]);
            
            k.set(ip);
            context.write(k, v);
        }
    }
    
    static class PageViewReducer extends Reducer<Text, WebLogBean, PageViewBean, NullWritable> {
        /**
         * Ĭ����ҳ��βҳͣ��ʱ��Ϊ60s
         */
        private static final String DEFAULT_STEPLONG = "60";
        
        @Override
        protected void reduce(Text key, Iterable<WebLogBean> values, Context context) throws IOException, InterruptedException {
            // ����bean����
            List<WebLogBean> beans = new ArrayList<>();
            for (WebLogBean value : values) {
                WebLogBean bean = new WebLogBean();
                try {
                    BeanUtils.copyProperties(bean, value);
                } catch (Exception e) {
                    throw new ServiceRuntimeException(ExceptionCode.COMMON.BEAN_ATTR_COPY_EXCEPTION, "�޷�����webbean����");
                }
                beans.add(bean);
            }
            
            // ���ݷ���ʱ���ͬһ���û��ķ��ʼ�¼����
            beans.sort((WebLogBean o1, WebLogBean o2) -> {
                Date time1 = WebLogBeanParser.toDate(o1.getTime_local());
                Date time2 = WebLogBeanParser.toDate(o2.getTime_local());

                if (!Optional.ofNullable(time1).isPresent()
                        || !Optional.ofNullable(time2).isPresent()) {
                    return 0;
                }

                return time1.compareTo(time2);	// -1 1
            });
            
            // Ϊÿһ���û�����һ����ʱ��sessionid
            String session = createSession();
            PageViewBean pvBean = new PageViewBean();
            WebLogBean bean;
            WebLogBean preBean;
            int step = 1;
            for(int i = 0; i < beans.size(); i++) {
                bean = beans.get(i);
                if (beans.size() == 1) {
                    pvBean.set(session, bean.getRemote_addr(), bean.getHttp_user_agent(), bean.getTime_local(), 
                            bean.getRequest(), 1, DEFAULT_STEPLONG, bean.getHttp_referer(), bean.getBody_bytes_sent(), bean.getStatus());
                    context.write(pvBean, NullWritable.get());
                    break;
                }
                
                if (i == 0) {
                    continue;
                }
                
                // ������һҳ���ͣ��ʱ�䣬�ɵ�ǰҳ����ʱ��-��һҳ����ʱ��
                preBean = beans.get(i - 1);
                long timeDiff = WebLogBeanParser.timeDiff(bean.getTime_local(), preBean.getTime_local());
                // ������Ҫע����ǣ�session��Ϊ30����ʧЧ�����Գ���30����֮����Ҫ�������ɻỰ
                if (timeDiff > 30 * 60 * 1000) {
                    pvBean.set(session, preBean.getRemote_addr(), preBean.getHttp_user_agent(), preBean.getTime_local(),
                            preBean.getRequest(), step, DEFAULT_STEPLONG, preBean.getHttp_referer(), preBean.getBody_bytes_sent(), preBean.getStatus());
                    context.write(pvBean, NullWritable.get());
                    step = 1;
                    session = createSession();
                } else {
                    pvBean.set(session, preBean.getRemote_addr(), preBean.getHttp_user_agent(), preBean.getTime_local(),
                            preBean.getRequest(), step, (timeDiff / 1000)+"", preBean.getHttp_referer(), preBean.getBody_bytes_sent(), preBean.getStatus());
                    context.write(pvBean, NullWritable.get());
                    step++;
                }
                
                // �����ǰ���������һҳ������Ҫֱ�Ӱ����һ����¼ֱ�����
                if (i == beans.size() - 1) {
                    pvBean.set(session, bean.getRemote_addr(), bean.getHttp_user_agent(), bean.getTime_local(),
                            preBean.getRequest(), step, DEFAULT_STEPLONG, bean.getHttp_referer(), bean.getBody_bytes_sent(), bean.getStatus());
                    context.write(pvBean, NullWritable.get());
                }
            }
        }

        /**
         * ����session
         * @return
         */
        private String createSession() {
            return UUID.randomUUID().toString();
        }
    }
    
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(PageViewProcess.class);
        
        job.setMapperClass(PageViewMapper.class);
        job.setReducerClass(PageViewReducer.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(WebLogBean.class);
        
        job.setOutputKeyClass(PageViewBean.class);
        job.setOutputValueClass(NullWritable.class);

        /*Path output = new Path(args[1]);
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(output)) {
            fs.delete(output, true);
        }

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, output);*/

        //����
        FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.1.100/web_click_project/pre_output"));
        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.1.100/web_click_project/pv_output"));
        
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
