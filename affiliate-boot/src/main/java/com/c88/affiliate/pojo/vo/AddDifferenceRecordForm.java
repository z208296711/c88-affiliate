package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * 調整前期差額記錄表
 * @TableName aff_difference_record
 */
@Data
@Schema(title = "AddDifferenceRecordVO")
public class AddDifferenceRecordForm {



    /**
     * 代理帳號
     */
    @NotBlank
    @Schema(title = "代理帳號")
    private String username;

    /**
     * 異動值
     */
    @Schema(title = "異動值 可填正/負")
    private BigDecimal amount;

    /**
     * 備註
     */
    @Schema(title = "備註")
    private String note;



}