package com.c88.affiliate.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(title = "修改佣金群組")
public class ModifyCommissionGroupForm {

    @NotNull(message = "ID不得為空")
    @Schema(title = "佣金群組ID")
    private Long id;

    @Schema(title = "佣金群組名稱")
    private String name;

    @Schema(title = "新增佣金群組組合表單")
    private List<AddOrModifyCommissionGroupForm> addOrModifyAffCommissionGroupForms;
}
