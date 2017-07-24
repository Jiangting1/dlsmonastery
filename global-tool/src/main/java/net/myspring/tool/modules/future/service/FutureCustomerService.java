package net.myspring.tool.modules.future.service;

import net.myspring.tool.common.dataSource.annotation.FutureDataSource;
import net.myspring.tool.common.utils.CacheUtils;
import net.myspring.tool.modules.future.dto.CustomerDto;
import net.myspring.tool.modules.future.repository.FutureCustomerRepository;
import net.myspring.tool.modules.vivo.dto.SCustomerDto;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@FutureDataSource
@Transactional(readOnly = true)
public class FutureCustomerService {
    @Autowired
    private FutureCustomerRepository futureCustomerRepository;
    @Autowired
    private CacheUtils cacheUtils;


    public List<SCustomerDto> getVivoCustomersData(String date){
        List<SCustomerDto> sCustomerDtoList = futureCustomerRepository.findVivoCustomers(LocalDateUtils.parse(date));
        cacheUtils.initCacheInput(sCustomerDtoList);
        return sCustomerDtoList;
    }

    public List<CustomerDto> getOppoCustomers(){
        List<CustomerDto> customerDtoList = futureCustomerRepository.findOppoCustomers();
        return customerDtoList;
    }

}