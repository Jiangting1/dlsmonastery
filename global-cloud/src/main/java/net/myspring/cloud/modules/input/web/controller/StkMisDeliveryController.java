package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.dto.StkMisDeliveryDto;
import net.myspring.cloud.modules.input.service.StkMisDeliveryService;
import net.myspring.cloud.modules.input.web.form.StkMisDeliveryForm;
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
 * 其他出库单
 * Created by lihx on 2017/4/25.
 */
@RestController
@RequestMapping(value = "input/stkMisDelivery")
public class StkMisDeliveryController {
    @Autowired
    private StkMisDeliveryService stkMisDeliveryService;
    @Autowired
    private KingdeeBookService kingdeeBookService;
    @Autowired
    private AccountKingdeeBookService accountKingdeeBookService;
    @Autowired
    private KingdeeSynService kingdeeSynService;

    @RequestMapping(value = "form")
    public StkMisDeliveryForm form () {
        return stkMisDeliveryService.getForm();
    }

    @RequestMapping(value = "save")
    public RestResponse save(StkMisDeliveryForm stkMisDeliveryForm) {
        RestResponse restResponse;
        StringBuilder message = new StringBuilder();
        try {
            AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
            KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
            if (accountKingdeeBook != null) {
                List<KingdeeSynDto> kingdeeSynDtoList = stkMisDeliveryService.save(stkMisDeliveryForm, kingdeeBook, accountKingdeeBook);
                kingdeeSynService.save(BeanUtil.map(kingdeeSynDtoList, KingdeeSyn.class));
                for (KingdeeSynDto kingdeeSynDto : kingdeeSynDtoList) {
                    if (kingdeeSynDto.getSuccess()) {
                        message.append(kingdeeSynDto.getBillNo()+",");
                    }
                }
                restResponse = new RestResponse("其他出库单开单成功：" + message, null, true);
            }else {
                restResponse = new RestResponse("您没有金蝶账号，不能开单", null, false);
            }
            return restResponse;
        }catch (Exception e){
            return new RestResponse(e.getMessage(), ResponseCodeEnum.invalid.name(), false);
        }
    }

    @RequestMapping(value = "saveForWS",method = RequestMethod.POST)
    public KingdeeSynReturnDto saveForWS(@RequestBody StkMisDeliveryDto stkMisDeliveryDto) {
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
        KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
        KingdeeSynDto kingdeeSynDto;
        if (accountKingdeeBook != null) {
            kingdeeSynDto = stkMisDeliveryService.saveForWS(stkMisDeliveryDto, kingdeeBook, accountKingdeeBook);
            kingdeeSynService.save(BeanUtil.map(kingdeeSynDto, KingdeeSyn.class));
        }else {
            throw new ServiceException("您没有金蝶账号，不能开单");
        }
        return BeanUtil.map(kingdeeSynDto,KingdeeSynReturnDto.class);
    }
}
