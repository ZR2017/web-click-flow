package cn.yz0515.bigdata.exception;

/**
 * �����ҵ���쳣��Ӧ��
 * Created by zhenglian on 2017/11/26.
 */
public class ExceptionCode {

    /**
     * IOͨ���쳣
     */
    public static class IOCode {
        public static final Integer IO_EXCEPTION = 1000;
    }

    /**
     * mr������
     */
    public static class COMMON {
        /**
         * �����Ʊ���
         */
        public static final Integer BEAN_ATTR_COPY_EXCEPTION = 2000;
        
        /**
         * ���ڸ�ʽת������
         */
        public static final Integer DATE_FORMAT_EXCEPTION = 2001;
    }
}

