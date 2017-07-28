package net.myspring.cloud.modules.kingdee.service;

import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.modules.kingdee.domain.CnBankAcnt;
import net.myspring.cloud.modules.kingdee.repository.CnBankAcntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 银行账户
 * Created by lihx on 2017/6/16.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class CnBankAcntService {
    @Autowired
    private CnBankAcntRepository cnBankAcntRepository;

    public List<CnBankAcnt> findAll(){
        return cnBankAcntRepository.findAll();
    }

    public List<CnBankAcnt> findByMaxModifyDate(LocalDateTime modifyDate){
        if (modifyDate != null) {
            return cnBankAcntRepository.findByMaxModifyDate(modifyDate);
        }
        return null;
    }
}
