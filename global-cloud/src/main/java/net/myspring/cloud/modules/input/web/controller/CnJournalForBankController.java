package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.input.dto.CnJournalForBankDto;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.service.CnJournalForBankService;
import net.myspring.cloud.modules.input.web.form.CnJournalForBankForm;
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
 * 手工日记账之銀行存取款日记账
 * Created by lihx on 2017/6/9.
 */
@RestController
@RequestMapping(value = "input/cnJournalForBank")
public class CnJournalForBankController {
    @Autowired
    private CnJournalForBankService cnJournalForBankService;
    @Autowired
    private KingdeeBookService kingdeeBookService;
    @Autowired
    private AccountKingdeeBookService accountKingdeeBookService;
    @Autowired
    private KingdeeSynService kingdeeSynService;

    @RequestMapping(value = "form")
    public CnJournalForBankForm form () {
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
        if (accountKingdeeBook != null) {
            KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
            return cnJournalForBankService.getForm(kingdeeBook);
        }else {
            throw new ServiceException("您没有金蝶账号，不能开单");
        }
    }

    @RequestMapping(value = "save")
    public RestResponse save(CnJournalForBankForm cnJournalForBankForm) {
        try {
            RestResponse restResponse = new RestResponse("开单失败",null);
            AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
            if (accountKingdeeBook != null) {
                KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
                KingdeeSynDto kingdeeSynDto = cnJournalForBankService.save(cnJournalForBankForm, kingdeeBook, accountKingdeeBook);
                kingdeeSynService.save(BeanUtil.map(kingdeeSynDto, KingdeeSyn.class));
                if (kingdeeSynDto.getSuccess()) {
                    restResponse = new RestResponse("銀行存取款日记账成功：" + kingdeeSynDto.getBillNo(), null, true);
                }
            }else {
                restResponse = new RestResponse("您没有金蝶账号，不能开单", null, false);
            }
            return restResponse;
        }catch (Exception e){
            return new RestResponse(e.getMessage(), ResponseCodeEnum.invalid.name(), false);
        }
    }

    @RequestMapping(value = "saveForWS", method= RequestMethod.POST)
    public List<KingdeeSynReturnDto> saveForWS(@RequestBody List<CnJournalForBankDto> cnJournalForBankDtoList) {
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
        List<KingdeeSyn> kingdeeSynList;
        if (accountKingdeeBook != null) {
            KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
            List<KingdeeSynDto> kingdeeSynDtoList = cnJournalForBankService.saveForWS (cnJournalForBankDtoList,kingdeeBook,accountKingdeeBook);
            kingdeeSynList = kingdeeSynService.save(BeanUtil.map(kingdeeSynDtoList, KingdeeSyn.class));
        }else {
            throw new ServiceException("您没有金蝶账号，不能开单");
        }
        return BeanUtil.map(kingdeeSynList,KingdeeSynReturnDto.class);
    }
}
