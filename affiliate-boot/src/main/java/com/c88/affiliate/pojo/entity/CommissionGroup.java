package com.c88.affiliate.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.c88.affiliate.handler.CommissionGroupDetailHandler;
import com.c88.affiliate.pojo.vo.CommissionGroupDetailVO;
import com.c88.common.core.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 佣金群組
 *
 * @TableName aff_commission_group
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "aff_commission_group", autoResultMap = true)
public class CommissionGroup extends BaseEntity {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名稱
     */
    @TableField(value = "name")
    private String name;

    /**
     * 佣金群組階級資訊
     */
    @TableField(value = "details", typeHandler = CommissionGroupDetailHandler.class)
    private List<CommissionGroupDetailVO> details;


}