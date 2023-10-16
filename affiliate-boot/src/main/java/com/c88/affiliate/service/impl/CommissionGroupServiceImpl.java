package com.c88.affiliate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.c88.affiliate.mapper.CommissionGroupMapper;
import com.c88.affiliate.pojo.entity.CommissionGroup;
import com.c88.affiliate.pojo.vo.CommissionGroupDetailVO;
import com.c88.affiliate.service.ICommissionGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author user
 * @description 针对表【aff_commission_group(佣金群組表)】的数据库操作Service实现
 * @createDate 2022-11-15 10:24:15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionGroupServiceImpl extends ServiceImpl<CommissionGroupMapper, CommissionGroup>
        implements ICommissionGroupService {

    public BigDecimal getCommissionRate(Long groupId, int activeMembers, BigDecimal winLoss){
        CommissionGroup commissionGroup = this.lambdaQuery().eq(CommissionGroup::getId, groupId).oneOpt().orElse(null);
        BigDecimal rate = BigDecimal.ZERO;
        if(commissionGroup !=null) {
            List<CommissionGroupDetailVO> details = commissionGroup.getDetails();
            for (CommissionGroupDetailVO vo : details) {
                if (activeMembers >= vo.getActivityMember() && (winLoss.compareTo(vo.getMinProfit()) >= 0)) {
                    rate =  vo.getRate();
//                    continue;
                }
            }
            //如果連第一級都沒有，則檢查原因，因呼叫後，會用佣金乘佣金比例，故這邊只需回傳人數不足
            if(rate.compareTo(BigDecimal.ZERO)==0){
                CommissionGroupDetailVO commissionGroupDetailVO = commissionGroup.getDetails().get(0);
                if(activeMembers< commissionGroupDetailVO.getActivityMember()) return new BigDecimal(-1);
//                else if(winLoss.compareTo(commissionGroupDetailVO.getMinProfit())<0) return new BigDecimal(-2);
            }
            return rate;
        }
        return rate;
    }
}




