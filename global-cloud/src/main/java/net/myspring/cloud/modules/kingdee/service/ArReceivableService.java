package net.myspring.cloud.modules.kingdee.service;

import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.modules.kingdee.domain.ArReceivable;
import net.myspring.cloud.modules.kingdee.repository.ArReceivableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 下推应收单
 * Created by lihx on 2017/6/17.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class ArReceivableService {
    @Autowired
    private ArReceivableRepository arReceivableRepository;

    public ArReceivable findTopOneBySourceBillNo(String sourceBillNo){
        List<ArReceivable> receivableList = arReceivableRepository.findTopOneBySourceBillNo(sourceBillNo);
        return receivableList.get(0);
    }

    public List<ArReceivable> findBySourceBillNoList(List<String> sourceBillNoList){
        List<ArReceivable> receivableList = arReceivableRepository.findBySourceBillNoList(sourceBillNoList);
        return receivableList;
    }
}
