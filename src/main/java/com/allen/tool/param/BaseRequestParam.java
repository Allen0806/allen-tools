package com.allen.tool.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import jakarta.validation.Valid;

/**
 * 接口请求参数封装
 *
 * @author allen
 * @date 2022-09-13
 */
@Data
@ToString
@ApiModel("接口请求参数封装")
public class BaseRequestParam<T> {

    /**
     * 业务请求参数
     */
    @ApiModelProperty(value = "业务请求参数")
    @Valid
    private T bizParam;

    /**
     * 业务参数是否加密：0-否，1-是，默认为0
     */
    @ApiModelProperty(value = "业务参数是否加密：0-否，1-是，默认为0")
    private String bizEncrypted = "0";
}
