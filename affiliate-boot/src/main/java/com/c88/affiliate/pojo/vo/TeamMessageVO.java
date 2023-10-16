package com.c88.affiliate.pojo.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.c88.common.core.result.PageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "團隊訊息")
public class TeamMessageVO {

    @Schema(title = "主線帳號", description = "登入的代理帳號")
    private String username;

    @Schema(title = "團隊名稱", description = "總代的帳號")
    private String masterUsername;

    @Schema(title = "團隊代理")
    private Integer parentCount;

    @Schema(title = "下級會員數")
    private Integer memberCount;

    @Schema(title = "首存人數")
    private Long firstRechargeMemberCount;

    @Schema(title = "詳細資料")
    private PageResult.Data<TeamMessageDetailVO> pageDetail;

}
