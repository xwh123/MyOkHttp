package com.example.myokhtttp.net.download.db;

import java.util.List;

/**
 * @desc: 数据库接口基类  增删改查
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/28 0028 16:22
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/28 0028 16:22
 * @UpdateRemark: 更新说明
 * @version:
 */
public interface IBaseDao<T> {

    //插入数据
    Long insert(T entity);

    //更新数据
    int update(T entity,T where);

    //删除数据
    int delete(T entity);

    //查询数据
    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);

    List<T> query(String sql);

}
