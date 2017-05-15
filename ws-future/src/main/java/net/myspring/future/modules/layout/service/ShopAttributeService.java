package net.myspring.future.modules.layout.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.modules.sys.dto.DictEnumDto;
import net.myspring.basic.modules.sys.dto.DictMapDto;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.DictEnumCategoryEnum;
import net.myspring.future.common.enums.DictMapCategoryEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.modules.basic.client.DictEnumClient;
import net.myspring.future.modules.basic.client.DictMapClient;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.dto.DepotDto;
import net.myspring.future.modules.basic.mapper.DepotMapper;
import net.myspring.future.modules.basic.web.query.DepotQuery;
import net.myspring.future.modules.layout.domain.ShopAttribute;
import net.myspring.future.modules.layout.dto.ShopAttributeDetailDto;
import net.myspring.future.modules.layout.mapper.ShopAttributeMapper;
import net.myspring.future.modules.layout.web.form.ShopAttributeForm;
import net.myspring.future.modules.layout.web.query.ShopAttributeQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShopAttributeService {

    @Autowired
    private ShopAttributeMapper shopAttributeMapper;
    @Autowired
    private DepotMapper depotMapper;
    @Autowired
    private DictEnumClient dictEnumClient;
    @Autowired
    private DictMapClient dictMapClient;
    @Autowired
    private CacheUtils cacheUtils;
    public ShopAttribute findOne(String id) {
        return shopAttributeMapper.findOne(id);
    }

    public  List<ShopAttribute>  findByShopId(String shopId) {
        List<ShopAttribute> list = Lists.newArrayList();
        return list;
    }

    public Page<DepotDto> findPage(Pageable pageable, ShopAttributeQuery shopAttributeQuery){
        DepotQuery depotQuery=new DepotQuery();
        ReflectionUtil.copyProperties(shopAttributeQuery,depotQuery);
        Page<DepotDto> page = depotMapper.findPage(pageable, depotQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }


    public ShopAttributeForm findForm(ShopAttributeForm shopAttributeForm){
        if(StringUtils.isNotBlank(shopAttributeForm.getShopId())){
            shopAttributeForm.setShop(depotMapper.findOne(shopAttributeForm.getShopId()));
            List<ShopAttribute> shopAttributeList=shopAttributeMapper.findByShopId(shopAttributeForm.getShopId());
            Map<String,ShopAttribute> shopAttributeMap= CollectionUtil.extractToMap(shopAttributeList,"typeName");
            List<DictEnumDto> dictEnumList=dictEnumClient.findByCategory(DictEnumCategoryEnum.SHOP_ATTRIBUTE_TYPE.getValue());
            List<ShopAttribute> shopAttributes=Lists.newArrayList();
            for(DictEnumDto dictEnumDto:dictEnumList){
                ShopAttribute shopAttribute=shopAttributeMap.get(dictEnumDto.getValue());
                if(shopAttribute==null){
                    shopAttribute=new ShopAttribute();
                    shopAttribute.setTypeName(dictEnumDto.getValue());
                    shopAttribute.setTypeValue(0D);
                }
                shopAttributes.add(shopAttribute);
            }
            shopAttributeForm.setShopAttributeDetailList(BeanUtil.map(shopAttributes,ShopAttributeDetailDto.class));
        }
        return shopAttributeForm;
    }

    public ShopAttributeForm save(ShopAttributeForm shopAttributeForm) {
        Depot shop = depotMapper.findOne(shopAttributeForm.getShop().getId());
        depotMapper.update(shop);
        List<ShopAttribute> shopAttributes=shopAttributeMapper.findByShopId(shopAttributeForm.getShop().getId());
        Map<String,ShopAttribute> shopAttributeMap=CollectionUtil.extractToMap(shopAttributes,"typeName");
        List<ShopAttribute> shopAttributeList=Lists.newArrayList();
        for(ShopAttributeDetailDto shopAttributeDetailDto:shopAttributeForm.getShopAttributeDetailList()){
            ShopAttribute shopAttribute = shopAttributeMap.get(shopAttributeDetailDto.getTypeName());
            if(shopAttribute!=null){
                if(!shopAttribute.getTypeValue().equals(shopAttributeDetailDto.getTypeValue())){
                    shopAttribute.setEnabled(false);
                    shopAttributeMapper.update(shopAttribute);
                }
            }
            if((shopAttribute==null||!shopAttribute.getTypeValue().equals(shopAttributeDetailDto.getTypeValue()))&&shopAttributeDetailDto.getTypeValue()>0){
                shopAttribute=BeanUtil.map(shopAttributeDetailDto,ShopAttribute.class);
                shopAttribute.setShopId(shop.getId());
                shopAttributeList.add(shopAttribute);
            }
        }
        if(CollectionUtil.isNotEmpty(shopAttributeList)){
            shopAttributeMapper.batchSave(shopAttributeList);
        }

        return shopAttributeForm;
    }

    public RestResponse check(ShopAttributeForm shopAttributeForm) {
        for(int i=0 ; i<shopAttributeForm.getShopAttributeDetailList().size();i++){
            ShopAttributeDetailDto shopAttributeDetailDto=shopAttributeForm.getShopAttributeDetailList().get(i);
            if(shopAttributeDetailDto.getTypeValue()<0){
                new RestResponse("填写的值不能为负数",null,false);
            }
        }
        return new RestResponse("验证成功",null);
    }

    private String getValueByTurnoverType(Long turnoverType){
        Map<Long,String> turnoverTypeMap =getTurnoverTypeMap();
        List<Long> keyList=new ArrayList<>(turnoverTypeMap.keySet());
        Collections.sort(keyList, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return (int)(o1-o2);
            }
        });
        if(turnoverType>keyList.get(keyList.size()-1)){
            return turnoverTypeMap.get(keyList.get(keyList.size()-1));
        }
        for(int i=0;i<keyList.size();i++){
            Long key=keyList.get(i);
            if(key<=turnoverType&&turnoverType<keyList.get(i+1)){
                return turnoverTypeMap.get(key);
            }
        }
        return null;
    }

    private Map<Long,String> getTurnoverTypeMap(){
        Map<Long,String>turnoverTypeMap=Maps.newHashMap();
        for(DictMapDto dictMap:dictMapClient.findByCategory(DictMapCategoryEnum.门店_营业额分类.name())){
            String value=dictMap.getValue().trim();
            String key="";
            if(dictMap.getValue().contains("+")){
                key=value.substring(3,dictMap.getValue().indexOf("+"));
            }else {
                if(value.indexOf("-")+1==value.length()){
                    key="0";
                }else {
                    key=value.substring(3,value.indexOf("-"));
                }
            }
            turnoverTypeMap.put(Long.valueOf(key.trim()),value);
        }
        return turnoverTypeMap;
    }

}
