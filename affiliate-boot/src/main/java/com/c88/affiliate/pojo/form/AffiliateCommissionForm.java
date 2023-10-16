package com.c88.affiliate.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import lombok.Data;

@Data
public class AffiliateCommissionForm extends BasePageQuery {
    String date;
    Integer level;
    String username;
    Integer status;
}
