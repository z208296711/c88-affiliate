package com.c88.affiliate.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 調整前期差額記錄表
 * @TableName aff_difference_record
 */
@Data
public class DifferenceRecordVO  {



    /**
     * 代理帳號
     */
    @NotBlank
    @Schema(title = "代理帳號")
    private String username;

    /**
     * 調整前缺額
     */
    @Schema(title = "調整前缺額")
    private BigDecimal beforeAmount;

    /**
     * 異動值
     */
    @Schema(title = "異動值")
    private BigDecimal amount;

    /**
     * 調整後缺額
     */
    @Schema(title = "調整後缺額")
    private BigDecimal afterAmount;

    /**
     * 備註
     */
    @Schema(title = "備註")
    private String note;

    /**
     * 操作者
     */
    @Schema(title = "操作者")
    private String modifiedBy;


    /**
     * 創建時間
     */
    @Schema(title = "創建時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

}