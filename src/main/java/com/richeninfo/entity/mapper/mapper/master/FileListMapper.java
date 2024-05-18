/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.mapper.master;

import com.richeninfo.entity.mapper.entity.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author : zhouxiaohu
 * @create 2024/4/30 14:42
 */
@Repository
@Mapper
public interface FileListMapper {

    @Select("select * from activity_fileList where principal = #{principal}")
    ActivityFileList selectActivityFileList(String principal);

    @Insert("insert into activity_fileList(name,fileName,number,principal,createTime)values(#{name},#{fileName},#{number},#{principal},now())")
    int insertActivityFileList(ActivityFileList fileList);//初始化用户

}
