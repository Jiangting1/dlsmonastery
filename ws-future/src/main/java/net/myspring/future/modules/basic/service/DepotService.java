package net.myspring.future.modules.basic.service;

import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.client.OfficeClient;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.dto.DepotDto;
import net.myspring.future.modules.basic.mapper.DepotMapper;
import net.myspring.future.modules.basic.web.query.DepotQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DepotService {
    @Autowired
    private DepotMapper depotMapper;
    @Autowired
    private OfficeClient officeClient;

    @Autowired
    private CacheUtils cacheUtils;


    public List<DepotDto> findShopList(DepotQuery depotQuery) {
        List<Depot> depotList = depotMapper.findByAccountId(RequestUtils.getAccountId());
        depotQuery.setOfficeIdList(officeClient.getOfficeFilterIds(RequestUtils.getRequestEntity().getOfficeId()));
        if(CollectionUtil.isNotEmpty(depotList)) {
            depotQuery.setDepotIdList(CollectionUtil.extractToList(depotList,"id"));
        }
        return depotMapper.findShopList(depotQuery);
    }

    public List<DepotDto> findStoreList(DepotQuery depotQuery) {
        List<Depot> depotList = depotMapper.findByAccountId(RequestUtils.getAccountId());
        depotQuery.setOfficeIdList(officeClient.getOfficeFilterIds(RequestUtils.getRequestEntity().getOfficeId()));
        if(CollectionUtil.isNotEmpty(depotList)) {
            depotQuery.setDepotIdList(CollectionUtil.extractToList(depotList,"id"));
        }
        return depotMapper.findStoreList(depotQuery);
    }

    public List<DepotDto> findByIds(List<String> ids){
        List<Depot> depotList=depotMapper.findByIds(ids);
        List<DepotDto> depotDtoList= BeanUtil.map(depotList,DepotDto.class);
        return depotDtoList;
    }

    public DepotDto findById(String id) {
        List<String> ids = new ArrayList<>();
        ids.add(id);
        List<DepotDto> depotList=findByIds(ids);
        if(depotList!=null && depotList.size() >=1){
            return depotList.get(0);
        }else{
            return null;
        }
    }

    public DepotDto findShopByGoodsOrderId(String goodsOrderId) {
        DepotDto depotDto =  depotMapper.findShopByGoodsOrderId(goodsOrderId);
        if(depotDto != null){
            cacheUtils.initCacheInput(depotDto);
        }

        return depotDto;
    }

    public DepotDto findStoreByGoodsOrderId(String goodsOrderId) {
        DepotDto depotDto =  depotMapper.findStoreByGoodsOrderId(goodsOrderId);
        if(depotDto != null){
            cacheUtils.initCacheInput(depotDto);
        }
        return depotDto;
    }
}
