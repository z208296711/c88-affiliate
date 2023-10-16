package com.c88.affiliate.mapper;

import com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.c88.affiliate.pojo.vo.CommissionTotalCountVO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author mac
* @description 针对表【aff_affiliate_commission_record】的数据库操作Mapper
* @createDate 2022-12-08 14:25:05
* @Entity com.c88.affiliate.pojo.entity.AffAffiliateCommissionRecord
*/
public interface AffAffiliateCommissionRecordMapper extends BaseMapper<AffAffiliateCommissionRecord> {

    void insertBatch(List<AffAffiliateCommissionRecord> list);

    @Select("SELECT  " +
            "  ( aaa.`level` ) AS LEVEL,  " +
            "  IFNULL( verifyCount, 0 ) verifyCount,  " +
            "  IFNULL( verifiedCount, 0 ) verifiedCount  " +
            "FROM  " +
            "  ( SELECT DISTINCT ( `level` ) AS `level` FROM aff_affiliate ) aaa  " +
            "  LEFT JOIN (  " +
            "  SELECT  " +
            "    record.`level` AS LEVEL,  " +
            "    count(*) AS verifyCount,  " +
            "    count(  " +
            "    CASE  " +
            "        WHEN record.verify_status = 1   " +
            "        OR record.verify_status = 2 THEN 1  END ) AS verifiedCount  " +
            "      FROM  " +
            "        aff_affiliate_commission_record record  " +
            "      WHERE  " +
            "        record.verify_date = #{date}   " +
            "      GROUP BY  " +
            "        record.`level`   " +
            "      ) bbb ON aaa.`level` = bbb.`level`   " +
            "   where aaa.level=1" +
            "  ORDER BY  LEVEL DESC ")
    List<CommissionTotalCountVO> countTotal(String date);
}




