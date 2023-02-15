package com.allen.tool.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户登录信息封装类
 *
 * @author allen
 * @date 2022-08-17
 */
@Data
@ToString
@ApiModel(description = "用户登录信息")
public class LoginParam implements Serializable {

    private static final long serialVersionUID = 348472013175768733L;

    /**
     * 访问令牌
     */
    @ApiModelProperty("访问令牌")
    @NotBlank(message = "访问令牌不能为空")
    private String accessToken;

    /**
     * 用户编号
     */
    @ApiModelProperty("用户编号")
    @NotBlank(message = "用户编号不能为空")
    private String userId;

    /**
     * 应用标识
     */
    @ApiModelProperty("应用标识")
    @NotBlank(message = "应用标识不能为空")
    private String appId;

    /**
     * 租户号，固定为：lczq
     */
    @ApiModelProperty("租户号，固定为：lczq")
    @NotBlank(message = "租户号不能为空")
    private String tenantId;

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    @NotBlank(message = "设备编号不能为空")
    private String deviceId;

    /**
     * 终端设备类型
     */
    @ApiModelProperty("终端设备类型")
    @NotBlank(message = "终端设备类型站点地址不能为空")
    private String deviceType;

    /**
     * 认证登录类型
     */
    @ApiModelProperty("认证登录类型")
    @NotBlank(message = "认证登录类型不能为空")
    private String identifyType;

    /**
     * 站点地址
     */
    @ApiModelProperty("站点地址")
    @NotBlank(message = "站点地址不能为空")
    private String opStation;

}
