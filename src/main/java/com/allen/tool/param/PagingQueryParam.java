package com.allen.tool.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

/**
 * 分页查询参数对象
 *
 * @param <T>
 * @author luoxuetong
 * @date 2022-08-11
 */
@Data
@ToString
@ApiModel("分页查询参数对象")
public class PagingQueryParam<T> {

    /**
     * 业务查询参数
     */
    @ApiModelProperty(value = "业务查询参数")
    private T param;

    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页数，不传时默认为1")
    @Range(min = 1, message = "当前页数最小值为1")
    private Integer pageNo;

    /**
     * 起始行数，不传时根据pageNo和pageSize计算，如果pageNo不为空，则startNo失效
     */
    @ApiModelProperty(value = "起始行数，不传时根据pageNo和pageSize计算，如果pageNo不为空，则startNo失效")
    @Range(min = 0, message = "起始行数最小值为0")
    private Integer startNo;

    /**
     * 每页行数
     */
    @ApiModelProperty(value = "每页行数，不传时默认为10")
    @Range(min = 1, max = 100, message = "每页行数最小值为1，最大值为100")
    private Integer pageSize;

    /**
     * 构造方法
     *
     * @param param    业务查询参数
     * @param pageNo   当前页数
     * @param pageSize 每页行数
     */
    public PagingQueryParam(T param, Integer pageNo, Integer pageSize) {
        this.param = param;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
