package com.example.myokhtttp;

/**
 * @desc: 作用描述
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/26 0026 21:20
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/26 0026 21:20
 * @UpdateRemark: 更新说明
 * @version:
 */
public class ResponceData {


    /**
     * resultcode : 201
     * reason : 影片标题不能为空!
     * result : null
     * error_code : 204201
     */

    private String resultcode;
    private String reason;
    private Object result;
    private int error_code;

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    @Override
    public String toString() {
        return "ResponceData{" +
                "resultcode='" + resultcode + '\'' +
                ", reason='" + reason + '\'' +
                ", result=" + result +
                ", error_code=" + error_code +
                '}';
    }
}
