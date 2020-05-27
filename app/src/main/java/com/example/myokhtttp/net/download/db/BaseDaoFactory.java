package com.example.myokhtttp.net.download.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc: 工厂模式
 * @projectName:MyOkHtttp
 * @author:xuwh
 * @date:2019/7/28 0028 17:22
 * @UpdateUser： 更新者
 * @UpdateDate: 2019/7/28 0028 17:22
 * @UpdateRemark: 更新说明
 * @version:
 */
public class BaseDaoFactory {

    private String sqliteDatabasePath;

    private SQLiteDatabase sqLiteDatabase;
    private Map<String,BaseDao> map= Collections.synchronizedMap(new HashMap<String, BaseDao>());

    private static  BaseDaoFactory instance=new BaseDaoFactory();

    public BaseDaoFactory() {
        File file=new File(Environment.getExternalStorageDirectory(),"update");
        if(!file.exists())
        {
            file.mkdirs();
        }
        sqliteDatabasePath= file.getAbsolutePath()+"/user.db";
        openDatabase();

    }

    public  synchronized  <T extends  BaseDao<M>,M> T
    getDataHelper(Class<T> clazz,Class<M> entityClass)
    {
        BaseDao baseDao=null;
        if(map.get(clazz.getSimpleName())!=null)
        {
            return (T) map.get(clazz.getSimpleName());
        }
        try {
            baseDao=clazz.newInstance();
            baseDao.init(entityClass,sqLiteDatabase);
            map.put(clazz.getSimpleName(),baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) baseDao;
    }

    private void openDatabase() {
        this.sqLiteDatabase=SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath,null);
    }

    public  static  BaseDaoFactory getInstance()
    {
        return instance;
    }

}
