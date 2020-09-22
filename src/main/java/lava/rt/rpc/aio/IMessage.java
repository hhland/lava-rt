package lava.rt.rpc.aio;

import java.io.Serializable;
import java.util.Arrays;





/**
 * 消息接口
 * 消息依靠序列号来标识一次请求和一次响应
 *
 * @author peiyu
 */
public interface IMessage extends Serializable {

    /**
     * 设置序列号
     *
     * @param seq 序列号
     */
    void setSeq(String seq);

    /**
     * 获取序列号
     *
     * @return 序列号
     */
    String getSeq();
    
    
    
    
    public enum ResultCode {

        SUCCESS(0, "成功"),
        TIMEOUT(1001, "超时"),
        OTHER(9999, "其他错误");

        private int    code;
        private String desc;

        ResultCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return this.code;
        }

        public String getDesc() {
            return this.desc;
        }

        @Override
        public String toString() {
            return "ResultCode{" +
                    "code=" + code +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
    
    
    
    
    public class RequestMessage implements IMessage {

        private String seq;

        private String serverName;

        private String methodName;

        private Object[] args;

        private Class[] argsClassTypes;

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }

        public Class[] getArgsClassTypes() {
            return argsClassTypes;
        }

        public void setArgsClassTypes(Class[] argsClassTypes) {
            this.argsClassTypes = argsClassTypes;
        }

        @Override
        public void setSeq(String seq) {
            this.seq = seq;
        }

        @Override
        public String getSeq() {
            return this.seq;
        }

        @Override
        public String toString() {
            return "RequestMessage{" +
                    "seq='" + seq + '\'' +
                    ", serverName='" + serverName + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", args=" + Arrays.toString(args) +
                    ", argsClassTypes=" + Arrays.toString(argsClassTypes) +
                    '}';
        }
    

}
    
    
    
    
    
    
    public class ResponseMessage implements IMessage {

        private String seq;

        private ResultCode resultCode;

        private String errorMessage;

        private Object responseObject;

        public ResultCode getResultCode() {
            return resultCode;
        }

        public void setResultCode(ResultCode resultCode) {
            this.resultCode = resultCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Object getResponseObject() {
            return responseObject;
        }

        public void setResponseObject(Object responseObject) {
            this.responseObject = responseObject;
        }

        @Override
        public void setSeq(String seq) {
            this.seq = seq;
        }

        @Override
        public String getSeq() {
            return this.seq;
        }

        @Override
        public String toString() {
            return "ResponseMessage{" +
                    "seq='" + seq + '\'' +
                    ", resultCode=" + resultCode +
                    ", errorMessage='" + errorMessage + '\'' +
                    ", responseObject=" + responseObject +
                    '}';
        }
    }
    
    
    
    

}