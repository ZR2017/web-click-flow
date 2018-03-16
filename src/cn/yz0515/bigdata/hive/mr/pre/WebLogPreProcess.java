package cn.yz0515.bigdata.hive.mr.pre;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cn.yz0515.bigdata.hive.mr.bean.WebLogBean;
import cn.yz0515.bigdata.hive.mr.parsers.ValidUrlPrefixParser;
import cn.yz0515.bigdata.hive.mr.parsers.WebLogBeanParser;

/**
 * 
* Title: WebLogPreProcess 
* Description: 
* 	step 1: ��־��ϴ����
* 	�ų������ݺͷ���ȷ��ַ���û�����
* @author yangzheng  
* @date 2018��3��9��
 */
public class WebLogPreProcess {
	static class WebLogPreProcessMapper extends Mapper<LongWritable, Text, WebLogBean, NullWritable> {
		
		Collection<String> valids = new HashSet<>();
		
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {
			valids = ValidUrlPrefixParser.parse();
		}
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			WebLogBean bean = WebLogBeanParser.parse(line);
			
			if (!Optional.ofNullable(bean).isPresent()) {		//������
                return;
            }
			
			// ����js/css/ͼƬ�Ⱦ�̬��Դ�ļ�
            WebLogBeanParser.filtStaticResource(bean, valids);
            if (!bean.isValid()) {
                return;
            }
            
            context.write(bean, NullWritable.get());
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(WebLogPreProcess.class);
        job.setMapperClass(WebLogPreProcessMapper.class);
        job.setOutputKeyClass(WebLogBean.class);
        job.setOutputValueClass(NullWritable.class);
        
        job.setNumReduceTasks(0);
        
       /* Path output = new Path(args[1]);
        //���Ŀ¼���ܴ��ڣ����򱨴�
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(output)) {
            fs.delete(output, true);
        }
        
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, output);
        */
        
        //����
        FileInputFormat.setInputPaths(job, new Path("hdfs://192.168.1.100/web_click_project/input"));
        FileOutputFormat.setOutputPath(job, new Path("hdfs://192.168.1.100/web_click_project/pre_output"));
        
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
	}
}
