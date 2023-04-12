package com.allen.tool.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 分页查询结果对象
 *
 * @param <T>
 * @author allen
 * @date 2022-08-11
 */
@Data
@ToString(callSuper = true)
@ApiModel("分页查询结果对象")
public class PagingQueryResult<T> {

    /**
     * 业务对象查询结果
     */
    @ApiModelProperty(value = "业务对象查询结果")
    private List<T> items;

    /**
     * 总数量
     */
    @ApiModelProperty(value = "总数量")
    private Integer quantity;

    /**
     * 当前页数
     */
    @ApiModelProperty(value = "当前页数")
    private Integer pageNo;

    /**
     * 起始行数
     */
    @ApiModelProperty(value = "起始行数")
    private Integer startNo;

    /**
     * 每页行数
     */
    @ApiModelProperty(value = "每页行数，不传时默认为10")
    private Integer pageSize;

    /**
     * 构造方法
     *
     * @param items    业务查询参数
     * @param quantity 总数量
     * @param pageNo   当前页数
     * @param pageSize 每页行数
     */
    public PagingQueryResult(List<T> items, Integer quantity, Integer pageNo, Integer pageSize) {
        this.items = items;
        this.quantity = quantity;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
