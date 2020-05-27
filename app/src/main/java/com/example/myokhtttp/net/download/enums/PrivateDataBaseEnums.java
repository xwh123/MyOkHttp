package com.example.myokhtttp.net.download.enums;

/**
 * @desc: 私有数据枚举
 * @className:PrivateDataBaseEnums
 * @author:xuwh
 * @date:2019/7/28 0028 17:23
 * @version 1.0
 */
public enum PrivateDataBaseEnums {

    /**
     * 存放本地数据库的路径
     */
    database("local/data/database/");

    /**
     * 文件存储的文件路径
     */
    private String value;
    PrivateDataBaseEnums(String value )
    {
        this.value = value;
    }

//    public String getValue()
//    {
//        UserDao userDao= BaseDaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
//        if(userDao!=null)
//        {
//            User currentUser=userDao.getCurrentUser();
//            if(currentUser!=null)
//            {
//                File file=new File(Environment.getExternalStorageDirectory(),"update");
//                if(!file.exists())
//                {
//                    file.mkdirs();
//                }
//                return file.getAbsolutePath()+"/"+currentUser.getUser_id()+"/logic.db";
//            }
//
//        }
//        return value;
//    }



}
