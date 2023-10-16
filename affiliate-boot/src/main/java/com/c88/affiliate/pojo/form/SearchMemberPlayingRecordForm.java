package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(title = "代理會員管理表單")
public class SearchMemberPlayingRecordForm extends BasePageQuery {

    @Schema(title = "代理帳號")
    private String affiliateName;

    @Schema(title = "所有代理Id")
    private List<Long> parentIds;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "平台")
    private String platformCode;

    @Schema(title = "類型")
    private String gameCategoryCode;

    @Schema(title = "狀態")
    private Integer status;

    @Schema(title = "投注起始時間,前端使用")
    private String betStartTime;

    @Schema(title = "投注結束時間,前端使用")
    private String betEndTime;

    @Schema(title = "投注起始時間")
    private LocalDateTime startTime;

    @Schema(title = "投注結束時間")
    private LocalDateTime endTime;

}
