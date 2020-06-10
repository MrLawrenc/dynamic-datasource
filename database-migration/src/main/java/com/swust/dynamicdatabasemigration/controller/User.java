package com.swust.dynamicdatabasemigration.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author : MrLawrenc
 * @date : 2020/5/16 0:37
 * @description : TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String realName;
    private String img;
    private boolean isDel;

    private LocalDateTime createTime;
    private LocalDateTime delTime;
}