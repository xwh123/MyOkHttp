package com.example.myokhtttp.net.download.db.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @desc: 文件 注解  运行时
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/28 0028 16:38
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/28 0028 16:38
 * @UpdateRemark: 更新说明
 * @version:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbFiled {

    String value();
}
