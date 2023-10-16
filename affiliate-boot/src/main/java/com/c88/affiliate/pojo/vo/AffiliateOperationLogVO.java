package com.c88.affiliate.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 代理異動紀錄表
 *
 * @TableName aff_affiliate_operation_log
 */
@Schema(title = "AffiliateOperationLogVO")
@Data
public class AffiliateOperationLogVO {

    /**
     * 代理帳號
     */
    @Schema(title = "代理帳號")
    private String username;

    @Schema(title = "異動類型 0:設置代理層級類型, 1:設為第一層代理 2:更改上級代理為 3:修改佣金群組為 4:修改手機號碼為 5:修改電子郵箱為 6: 修改綁定遊戲帳號為 7:修改真實姓名為 8:修改IM類型 9:啟用1/停用0")
    private Integer type;

    @Schema(title = "異動內容 對應異動類型 0:暫時無意義 1:暫時無意義 2:為上級代理的帳號 3:佣金群組名稱 4:手機號 5:信箱地址 6:會員帳號 7:真實姓名 8:{imtype (0,zalo, 1:fb)},{im帳號} 9:啟用1/停用0")
    private String content;

    @Schema(title = "操作者")
    private String updateBy;

    @Schema(title = "創建時間")
    private LocalDateTime gmtCreate;

}
