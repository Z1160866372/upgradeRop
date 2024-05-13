/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 *
 */

package com.richeninfo.entity.mapper.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PlodLoginUser implements Serializable {
    private Integer id;

    private String userId;

    private String passWord;

    private String departName;

    private String userName;

    private Date createTime;


}