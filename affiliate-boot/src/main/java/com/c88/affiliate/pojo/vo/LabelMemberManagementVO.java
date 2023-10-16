package com.c88.affiliate.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "會員管理-會員標籤Option", description = "未使用(unused)ID=-1 全部(all)ID=0")
public class LabelMemberManagementVO {

    @Schema(title = "標籤ID")
    private Long labelId;

    @Schema(title = "標籤名稱")
    private String labelName;

    @Schema(title = "標籤會員數量")
    private Long labelCount;

}
