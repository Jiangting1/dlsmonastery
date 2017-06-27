package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.input.dto.CnJournalEntityForBankDto;
import net.myspring.cloud.modules.input.dto.CnJournalForBankDto;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.service.CnJournalForBankService;
import net.myspring.cloud.modules.input.web.form.CnJournalForBankForm;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.cloud.modules.sys.service.AccountKingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeBookService;
import net.myspring.common.exception.ServiceException;
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

    @RequestMapping(value = "form")
    public CnJournalForBankForm form () {
        KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        return cnJournalForBankService.getForm(kingdeeBook);
    }

    @RequestMapping(value = "save")
    public RestResponse save(CnJournalForBankForm cnJournalForBankForm) {
        RestResponse restResponse;
        KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        KingdeeSynDto kingdeeSynDto = cnJournalForBankService.save(cnJournalForBankForm,kingdeeBook,accountKingdeeBook);
        if (kingdeeSynDto.getSuccess()){
            restResponse = new RestResponse("銀行存取款日记账成功：" + kingdeeSynDto.getBillNo(),null,true);
        } else {
            throw new ServiceException("銀行存取款日记账失败："+kingdeeSynDto.getResult());
        }
        return restResponse;
    }

    @RequestMapping(value = "saveForEmployeePhoneDeposit", method= RequestMethod.POST)
    public List<KingdeeSynReturnDto>  saveForEmployeePhoneDeposit(@RequestBody List<CnJournalForBankDto> cnJournalForBankDtoList) {
        KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        List<KingdeeSynDto> kingdeeSynDtoList = cnJournalForBankService.saveForEmployeePhoneDeposit(cnJournalForBankDtoList,kingdeeBook,accountKingdeeBook);
        for (KingdeeSynDto kingdeeSynDto : kingdeeSynDtoList){
            if (!kingdeeSynDto.getSuccess()){
                throw new ServiceException("銀行存取款日记账失败："+kingdeeSynDto.getResult());
            }
        }
        return BeanUtil.map(kingdeeSynDtoList,KingdeeSynReturnDto.class);
    }

    @RequestMapping(value = "saveForShopDeposit", method= RequestMethod.POST)
    public List<KingdeeSynReturnDto> saveForShopDeposit(@RequestBody List<CnJournalForBankDto> cnJournalForBankDtoList) {
        KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        List<KingdeeSynDto> kingdeeSynDtoList = cnJournalForBankService.saveForShopDeposit(cnJournalForBankDtoList,kingdeeBook,accountKingdeeBook);
        for (KingdeeSynDto kingdeeSynDto : kingdeeSynDtoList){
            if (!kingdeeSynDto.getSuccess()){
                throw new ServiceException("銀行存取款日记账失败："+kingdeeSynDto.getResult());
            }
        }
        return BeanUtil.map(kingdeeSynDtoList,KingdeeSynReturnDto.class);
    }
}
