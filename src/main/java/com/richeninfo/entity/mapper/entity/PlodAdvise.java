/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import java.util.Date;


@Data
public class PlodAdvise {
    private Integer id;

    private String userId;

    private String title;

    private String msgText;

    private Integer uploadFile;

    private String message;

    private String fileUrl;

    private Integer status;

    private Integer adviseScore;

    private String departName;

    private String userName;

    private String approver;
    
    private Integer approverScore;

    private String videoPath;
    
    private String raceType;
    
    private String raceContent;
    
    private String endRaceType;
    
    private String endRaceContent;
    
    private Date createTime;

   
}