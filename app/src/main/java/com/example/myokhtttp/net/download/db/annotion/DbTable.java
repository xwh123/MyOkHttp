package com.example.myokhtttp.net.download.db.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @desc: 数据库
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/28 0028 16:39
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/28 0028 16:39
 * @UpdateRemark: 更新说明
 * @version:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)//运行时注解
public @interface  DbTable {
    String value();
}
