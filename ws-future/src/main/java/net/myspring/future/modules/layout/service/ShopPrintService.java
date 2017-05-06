package net.myspring.future.modules.layout.service;

import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.modules.layout.domain.ShopPrint;
import net.myspring.future.modules.layout.dto.ShopPrintDto;
import net.myspring.future.modules.layout.mapper.ShopPrintMapper;
import net.myspring.future.modules.layout.web.form.ShopPrintForm;
import net.myspring.future.modules.layout.web.query.ShopPrintQuery;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = false)
public class ShopPrintService {

    @Autowired
    private ShopPrintMapper shopPrintMapper;
    @Autowired
    private CacheUtils cacheUtils;


    public Page<ShopPrintDto> findPage(Pageable pageable, ShopPrintQuery shopPrintQuery) {
        Page<ShopPrintDto> page = shopPrintMapper.findPage(pageable, shopPrintQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public ShopPrintForm findForm(ShopPrintForm  shopPrintForm){
        if(!shopPrintForm.isCreate()){
            ShopPrint shopPrint = shopPrintMapper.findOne(shopPrintForm.getId());
            ShopPrintDto shopPrintDto = BeanUtil.map(shopPrint,ShopPrintDto.class);
            cacheUtils.initCacheInput(shopPrintDto);
            shopPrintForm = BeanUtil.map(shopPrintDto,ShopPrintForm.class);
        }
        return shopPrintForm;
    }

    public ShopPrint save(ShopPrintForm shopPrintForm) {
        ShopPrint shopPrint;
        if(shopPrintForm.isCreate()){
            shopPrint = BeanUtil.map(shopPrintForm,ShopPrint.class);
            shopPrintMapper.save(shopPrint);
        }else{
            shopPrint = shopPrintMapper.findOne(shopPrintForm.getId());
            ReflectionUtil.copyProperties(shopPrintForm,shopPrint);
            shopPrintMapper.update(shopPrint);
        }
        return shopPrint;
    }

    public void audit(ShopPrint shopPrint, boolean pass, String comment) {
        shopPrintMapper.update(shopPrint);
    }

    public void notify(ShopPrint shopPrint) {
    }
}
