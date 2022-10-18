package com.allen.tool.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * C端接口请求参数封装
 *
 * @author luoxuetong
 * @date 2022-09-13
 */
@Data
@ToString
@ApiModel("C端接口请求参数封装")
public class AppRequestParam<T> extends RequestParam<T>{

    /**
     * 用户登录参数，如果需要，必须加密传输
     */
    @ApiModelProperty(value = "用户登录参数")
    private LoginParam loginParam;

}
