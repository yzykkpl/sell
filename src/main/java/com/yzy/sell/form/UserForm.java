package com.yzy.sell.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @description:
 * @author: yzy
 * @create: 2018-04-03 18:05
 */
@Data
public class UserForm {

    @NotEmpty(message = "openid必填")
    private String openid;
    @NotEmpty(message = "名字必填")
    private String name;
}
