package net.myspring.future.modules.basic.service;

import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.domain.DepotShop;
import net.myspring.future.modules.basic.dto.DepotDto;
import net.myspring.future.modules.basic.mapper.DepotShopMapper;
import net.myspring.future.modules.basic.web.query.DepotShopQuery;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liuj on 2017/5/12.
 */
@Service
public class DepotShopService {
    @Autowired
    private DepotShopMapper depotShopMapper;

    public List<DepotDto> findDepotDtoList(DepotShopQuery depotShopQuery) {
        return depotShopMapper.findDepotDtoList(depotShopQuery);
    }

    public  List<DepotDto> findByLikeName(String name,String category){
        return depotShopMapper.findByLikeName(name,category);
    };

    public List<DepotDto> findByListIds(List<String> ids){
        List<DepotShop> depotShopList = depotShopMapper.findByIds(ids);
        return BeanUtil.map(depotShopList,DepotDto.class);
    }

}