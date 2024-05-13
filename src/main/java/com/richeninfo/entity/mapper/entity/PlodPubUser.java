/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.entity;

import java.util.Date;

import lombok.Data;

@Data
public class PlodPubUser {
    private Integer id;

    private String userId;

    private Integer userType;

    private Integer totalScore;

    private String departName;

    private String userName;

    private Date createTime;

  
}