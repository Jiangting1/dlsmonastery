package net.myspring.cloud.modules.kingdee.service;

import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.modules.kingdee.domain.BdCustomer;
import net.myspring.cloud.modules.kingdee.repository.BdCustomerRepository;
import net.myspring.cloud.modules.kingdee.web.query.BdCustomerQuery;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户
 * Created by lihx on 2017/5/12.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class BdCustomerService {
    @Autowired
    private BdCustomerRepository bdCustomerRepository;

    public Page<BdCustomer> findPageIncludeForbid(Pageable pageable, BdCustomerQuery bdCustomerQuery) {
        bdCustomerQuery.setSort("t1.fcustid,DESC");
         Page<BdCustomer> bdCustomerPage= bdCustomerRepository.findPageIncludeForbid(pageable,bdCustomerQuery);
        return bdCustomerPage;
    }

    public List<BdCustomer> findByNameLike(String name) {
        if (StringUtils.isNotBlank(name)){
            return bdCustomerRepository.findByNameLike(name);
        }
        return null;
    }

    public List<BdCustomer> findAll(){
        return bdCustomerRepository.findAll();
    }

    public List<BdCustomer> findByMaxModifyDate(LocalDateTime modifyDate){
        if (modifyDate != null) {
            return bdCustomerRepository.findByMaxModifyDate(modifyDate);
        }
        return null;
    }

    //应收报表
    public BdCustomerQuery getQueryForCustomerReceive(){
        BdCustomerQuery bdCustomerQuery = new BdCustomerQuery();
        bdCustomerQuery.setSort("t1.fcustid,DESC");
        bdCustomerQuery.getExtra().put("customerGroupList",bdCustomerRepository.findPrimaryGroupAndPrimaryGroupName());
        return bdCustomerQuery;
    }
}
