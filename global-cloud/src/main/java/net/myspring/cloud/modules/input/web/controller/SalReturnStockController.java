package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.input.dto.SalReturnStockDto;
import net.myspring.cloud.modules.input.service.SalReturnStockService;
import net.myspring.cloud.modules.input.web.form.SalStockForm;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeSyn;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.cloud.modules.sys.service.AccountKingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeSynService;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 退货单
 * Created by lihx on 2017/4/25.
 */
@RestController
@RequestMapping(value = "input/salReturnStock")
public class SalReturnStockController {
    @Autowired
    private SalReturnStockService salReturnStockService;
    @Autowired
    private KingdeeBookService kingdeeBookService;
    @Autowired
    private AccountKingdeeBookService accountKingdeeBookService;
    @Autowired
    private KingdeeSynService kingdeeSynService;

    @RequestMapping(value = "form")
    public SalStockForm form () {
        KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        return salReturnStockService.getForm(kingdeeBook);
    }

    @RequestMapping(value = "save")
    public RestResponse save(SalStockForm salStockForm) {
        RestResponse restResponse;
        StringBuilder message = new StringBuilder();
        try {
            KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
            AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountId(RequestUtils.getAccountId());
            if (accountKingdeeBook != null) {
                List<KingdeeSynReturnDto> kingdeeSynExtendDtoList = salReturnStockService.save(salStockForm, kingdeeBook, accountKingdeeBook);
                kingdeeSynService.save(BeanUtil.map(kingdeeSynExtendDtoList, KingdeeSyn.class));
                for (KingdeeSynReturnDto kingdeeSynExtendDto : kingdeeSynExtendDtoList) {
                    if (kingdeeSynExtendDto.getSuccess()) {
                        message.append(kingdeeSynExtendDto.getBillNo()+",");
                    }
                }
                restResponse = new RestResponse("开单退货成功：" + message, null, true);
            }else {
                restResponse = new RestResponse("您没有金蝶账号，不能开单", null, false);
            }
            return restResponse;
        }catch (Exception e){
            return new RestResponse(e.getMessage(), ResponseCodeEnum.invalid.name(), false);
        }
    }

    @RequestMapping(value = "saveForXSTHD",method = RequestMethod.POST)
    public List<KingdeeSynReturnDto> saveForXSCKD(@RequestBody List<SalReturnStockDto> salReturnStockDtoList) {
        KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        List<KingdeeSynReturnDto> kingdeeSynExtendDtoList;
        if (accountKingdeeBook != null) {
            kingdeeSynExtendDtoList = salReturnStockService.saveForXSTHD(salReturnStockDtoList, kingdeeBook, accountKingdeeBook);
            kingdeeSynService.save(BeanUtil.map(kingdeeSynExtendDtoList, KingdeeSyn.class));
        }else{
            throw new ServiceException("您没有金蝶账号，不能开单");
        }
        return kingdeeSynExtendDtoList;
    }
}
