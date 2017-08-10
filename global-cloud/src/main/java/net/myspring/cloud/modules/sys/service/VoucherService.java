package net.myspring.cloud.modules.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.annotation.LocalDataSource;
import net.myspring.cloud.common.enums.ExtendTypeEnum;
import net.myspring.cloud.common.enums.VoucherFlexEnum;
import net.myspring.cloud.common.enums.VoucherStatusEnum;
import net.myspring.cloud.common.utils.CacheUtils;
import net.myspring.cloud.modules.input.dto.GlVoucherDto;
import net.myspring.cloud.modules.input.dto.GlVoucherFEntityDto;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.service.GlVoucherService;
import net.myspring.cloud.modules.kingdee.domain.*;
import net.myspring.cloud.modules.sys.domain.*;
import net.myspring.common.dto.NameValueDto;
import net.myspring.common.utils.HandsontableUtils;
import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.sys.dto.VoucherDto;
import net.myspring.cloud.modules.sys.repository.*;
import net.myspring.cloud.modules.sys.web.form.VoucherForm;
import net.myspring.cloud.modules.sys.web.query.VoucherQuery;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.RestResponse;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.*;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 凭证
 * Created by lihx on 2017/4/5.
 */
@Service
@LocalDataSource
@Transactional(readOnly = true)
public class VoucherService {
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherEntryRepository voucherEntryRepository;
    @Autowired
    private VoucherEntryFlowRepository voucherEntryFlowRepository;
    @Autowired
    private AccountKingdeeBookRepository accountKingdeeBookRepository;
    @Autowired
    private KingdeeBookRepository kingdeeBookRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private GlVoucherService glVoucherService;

    public Page<VoucherDto> findPage(Pageable pageable, VoucherQuery voucherQuery) {
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookRepository.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
        if(accountKingdeeBook == null){
            voucherQuery.setCreatedBy(RequestUtils.getAccountId());
        }
        Page<VoucherDto> page = voucherRepository.findPage(pageable, voucherQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public VoucherDto findOne(String id) {
        Voucher voucher = voucherRepository.findOne(id);
        VoucherDto voucherDto = BeanUtil.map(voucher,VoucherDto.class);
        cacheUtils.initCacheInput(voucherDto);
        return voucherDto;
    }

    @Transactional
    public Voucher save(VoucherForm voucherForm,List<BdFlexItemGroup> bdFlexItemGroupList,List<BdFlexItemProperty> bdFlexItemPropertyList){
        LocalDate date = voucherForm.getFdate();
        String json = HtmlUtils.htmlUnescape(voucherForm.getJson());
        List<List<Object>> data = ObjectMapperUtils.readValue(json, ArrayList.class);
        Boolean isCreate = StringUtils.isBlank(voucherForm.getId());
        Voucher voucher;
        if (isCreate){
            voucher = new Voucher();
            voucher.setRemarks(voucherForm.getRemarks());
            voucher.setFDate(date);
            voucher.setCompanyName(RequestUtils.getCompanyName());
            AccountKingdeeBook accountKingdeeBook = accountKingdeeBookRepository.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
            if (accountKingdeeBook != null){
                voucher.setKingdeeBookId(accountKingdeeBook.getKingdeeBookId());
                voucher.setCreatedName(accountKingdeeBook.getUsername());
                voucher.setStatus(VoucherStatusEnum.省公司财务审核.name());
            }else{
                voucher.setKingdeeBookId(kingdeeBookRepository.findByCompanyName(RequestUtils.getCompanyName()).getId());
                voucher.setCreatedName(RequestUtils.getAccountId());
                voucher.setStatus(VoucherStatusEnum.地区财务审核.name());
            }
        }else{
            voucher = voucherRepository.findOne(voucherForm.getId());
            voucher.setRemarks(voucherForm.getRemarks());
            voucher.setFDate(date);
            //删除原有数据
            List<VoucherEntry> voucherEntryList = voucherEntryRepository.findByVoucherId(voucher.getId());
            if (CollectionUtil.isNotEmpty(voucherEntryList)){
                for (VoucherEntry voucherEntry : voucherEntryList){
                    List<VoucherEntryFlow> voucherEntryFlowList = voucherEntryFlowRepository.findByVoucherEntryId(voucherEntry.getId());
                    if (CollectionUtil.isNotEmpty(voucherEntryFlowList)){
                        voucherEntryFlowRepository.deleteInBatch(voucherEntryFlowList);
                    }
                }
                voucherEntryRepository.deleteInBatch(voucherEntryList);
            }
        }
        voucher = voucherRepository.save(voucher);
        List<String> headers = getHeaders(bdFlexItemGroupList);
        Map<String,BdFlexItemProperty> bdFlexItemPropertyNameMap = bdFlexItemPropertyList.stream().collect(Collectors.toMap(BdFlexItemProperty::getFName,BdFlexItemProperty->BdFlexItemProperty));
        List<String> flexItemNameList = getFlexItemGroupAllName(bdFlexItemGroupList);
        Map<String, Map<String, String>> flexNameMap = glVoucherService.getFlexNameMap(flexItemNameList);
        for (List<Object> row : data){
            VoucherEntry voucherEntry = new VoucherEntry();
            voucherEntry.setFExplanation(HandsontableUtils.getValue(row,0));
            String accountName = HandsontableUtils.getValue(row,1);
            voucherEntry.setFAccountName(accountName);
            voucherEntry.setFAccountid(flexNameMap.get(VoucherFlexEnum.科目.name()).get(accountName));
            String debitStr = HandsontableUtils.getValue(row,row.size()-2);
            String creditStr = HandsontableUtils.getValue(row,row.size()-1);
            if (StringUtils.isNoneEmpty(debitStr)){
                voucherEntry.setFDebit(new BigDecimal(debitStr));
            }else {
                voucherEntry.setFCredit(new BigDecimal(creditStr));
            }
            voucherEntry.setGlVoucherId(voucher.getId());
            voucherEntryRepository.save(voucherEntry);
            //核算维度明细
            List<VoucherEntryFlow> voucherEntryFlowList = Lists.newArrayList();
            for (int i=2; i<row.size()-2; i++){
                String value = HandsontableUtils.getValue(row,i);
                String header = headers.get(i);
                if (StringUtils.isNotBlank(value)){
                    String name = "FDetailID__"+bdFlexItemPropertyNameMap.get(header).getFFlexNumber();
                    VoucherEntryFlow voucherEntryFlow = new VoucherEntryFlow();
                    voucherEntryFlow.setName(name);
                    voucherEntryFlow.setValue(value);//科目名称
                    voucherEntryFlow.setCode(flexNameMap.get(header).get(value));//科目编码
                    voucherEntryFlow.setGlVoucherEntryId(voucherEntry.getId());
                    voucherEntryFlowList.add(voucherEntryFlow);
                    voucherEntryFlowRepository.save(voucherEntryFlow);
                }
            }
        }
        return voucher;
    }

    public RestResponse check(VoucherForm voucherForm,List<BdAccount> bdAccountList,List<BdFlexItemGroup> bdFlexItemGroupList) {
        String json = HtmlUtils.htmlUnescape(voucherForm.getJson());
        List<List<Object>> data = ObjectMapperUtils.readValue(json, ArrayList.class);
        StringBuilder sb = new StringBuilder();
        //所有科目
        Map<String, List<String>> map = accountNameToFlexGroupNamesMap(bdAccountList,bdFlexItemGroupList);
        List<String> header = getHeaders(bdFlexItemGroupList);
        BigDecimal debitAmount = BigDecimal.ZERO;
        BigDecimal creditAmount = BigDecimal.ZERO;
        for (int i = 0; i < data.size(); i++) {
            int index = i + 1;
            List<Object> row = data.get(i);
            //科目
            String accountNumberName = HandsontableUtils.getValue(row,1);
            if(StringUtils.isNotBlank(accountNumberName)) {
                if (map.containsKey(accountNumberName)) {
                    //科目对应核算维度
                    List<String> FlexGroupNames = map.get(accountNumberName);
                    for (int j = 2; j < row.size() - 2; j++) {
                        String FlexGroupNameToElement = HandsontableUtils.getValue(row, j);
                        String FlexGroupName = header.get(j);
                        if (StringUtils.isBlank(FlexGroupNameToElement)) {
                            if (FlexGroupNames.contains(FlexGroupName)) {
                                sb.append("第" + index + "行，" + FlexGroupName + "为必填");
                            }
                        } else {
                            if (!FlexGroupNames.contains(FlexGroupName)) {
                                sb.append("第" + index + "行，" + FlexGroupName + "必须为空");
                            }
                        }
                    }
                }
            } else {
                sb.append("第" + index + "行，"  + "科目必填");
            }
            //借方金额
            String debitStr = HandsontableUtils.getValue(row,header.size() - 2);
            if(StringUtils.isNotEmpty(debitStr)){
                debitAmount = debitAmount.add(new BigDecimal(debitStr));
            }
            //贷方金额
            String creditStr = HandsontableUtils.getValue(row,header.size() - 1);
            if(StringUtils.isNotEmpty(creditStr)){
                creditAmount = creditAmount.add(new BigDecimal(creditStr));
            }
            if ((StringUtils.isBlank(debitStr) && StringUtils.isBlank(creditStr)) || (StringUtils.isNotBlank(debitStr) && StringUtils.isNotBlank(creditStr))) {
                sb.append("第" + index + "行，借方和贷方请填写一项！");
            }
        }
        if (debitAmount.compareTo(creditAmount) != 0) {
            sb.append("借贷方金额必须相等");
        }
        if (StringUtils.isNotBlank(sb)){
            return new RestResponse(sb.toString(),null,false);
        }else {
            return new RestResponse("检测合符条件",null,true);
        }
    }

    @Transactional(readOnly = true)
    public List<List<String>>  initData(VoucherDto voucherDto) {
        if (voucherDto.getId() != null) {
            List<List<String>> datas = Lists.newArrayList();
            //所有科目
            Map<String, BdAccount> accountNumberMap = voucherDto.getBdAccountList().stream().collect(Collectors.toMap(BdAccount::getFNumber, BdAccount -> BdAccount));
            //所有核算维度
            Map<String, BdFlexItemProperty> flexItemPropertyNameMap = voucherDto.getBdFlexItemPropertyList().stream().collect(Collectors.toMap(BdFlexItemProperty::getFName, BdFlexItemProperty -> BdFlexItemProperty));
            Map<String, BdFlexItemProperty> flexItemPropertyFlexNumberMap = voucherDto.getBdFlexItemPropertyList().stream().collect(Collectors.toMap(BdFlexItemProperty::getFFlexNumber, BdFlexItemProperty -> BdFlexItemProperty));
            //所有使用的核算维度组
            List<String> headers = Lists.newLinkedList();
            for (String header : getFlexItemGroupAllName(voucherDto.getBdFlexItemGroupList())) {
                headers.add("FDetailID__" + flexItemPropertyNameMap.get(header).getFFlexNumber());
            }
            List<VoucherEntry> voucherEntryList = voucherEntryRepository.findByVoucherId(voucherDto.getId());
            for (VoucherEntry voucherEntry : voucherEntryList) {
                List<String> list = Lists.newArrayList();
                list.add(voucherEntry.getFExplanation());//摘要
                BdAccount bdAccount = accountNumberMap.get(voucherEntry.getFAccountid());
                list.add(bdAccount.getFName());//科目名称
                Map<String, VoucherEntryFlow> voucherEntryFlowNameMap = Maps.newHashMap();
                List<VoucherEntryFlow> voucherEntryFlowList = voucherEntryFlowRepository.findByVoucherEntryId(voucherEntry.getId());
                if (CollectionUtil.isNotEmpty(voucherEntryFlowList)) {
                    voucherEntryFlowNameMap = voucherEntryFlowList.stream().collect(Collectors.toMap(VoucherEntryFlow::getName,VoucherEntryFlow->VoucherEntryFlow));
                    for (VoucherEntryFlow voucherEntryFlow : voucherEntryFlowList) {
                        voucherEntryFlow.setFlexName(flexItemPropertyFlexNumberMap.get(voucherEntryFlow.getFlexNumber()).getFName());
                        voucherEntryFlow.setValue(voucherEntryFlow.getValue());
                    }
                }
                for (String header : headers) {
                    if (voucherEntryFlowNameMap.containsKey(header)) {
                        list.add(voucherEntryFlowNameMap.get(header).getValue());
                    } else {
                        list.add("");
                    }
                }
                if (voucherEntry.getFCredit() == null) {
                    list.add(voucherEntry.getFDebit().toString());
                    list.add("");
                } else {
                    list.add("");
                    list.add(voucherEntry.getFCredit().toString());
                }
                datas.add(list);
            }
            return datas;
        }
        return null;
    }

    @Transactional
    public Voucher audit(VoucherForm voucherForm,List<BdFlexItemGroup> bdFlexItemGroupList,List<BdFlexItemProperty> bdFlexItemPropertyList,AccountKingdeeBook accountKingdeeBook, KingdeeBook kingdeeBook){
        Voucher voucher;
        if (StringUtils.isNotBlank(voucherForm.getId())){
            voucher = voucherRepository.findOne(voucherForm.getId());
            voucher.setFDate(voucherForm.getFdate());
            if (VoucherStatusEnum.地区财务审核.name().equals(voucher.getStatus())) {
                voucher.setStatus(VoucherStatusEnum.省公司财务审核.name());
            } else if (accountKingdeeBook != null){
                voucher.setStatus(VoucherStatusEnum.已完成.name());
            }else {
                throw new ServiceException("你的岗位没有权限审核");
            }
        }else {
            voucher = save(voucherForm,bdFlexItemGroupList,bdFlexItemPropertyList);
            voucher.setFDate(voucherForm.getFdate());
            if (VoucherStatusEnum.地区财务审核.name().equals(voucher.getStatus())) {
                voucher.setStatus(VoucherStatusEnum.省公司财务审核.name());
            } else {
                voucher.setStatus(VoucherStatusEnum.已完成.name());
            }
        }
        if (VoucherStatusEnum.已完成.name().equals(voucher.getStatus())) {
            KingdeeSynDto kingdeeSynDto = save(voucher,kingdeeBook, accountKingdeeBook);
            if (kingdeeSynDto.getSuccess()) {
                String outCode = "凭证编号：" + kingdeeSynDto.getBillNo() + "  凭证号：" + glVoucherService.findByBillNo(kingdeeSynDto.getBillNo()).getFVoucherGroupNo();
                voucher.setOutCode(outCode);
                voucher.setCreatedName(accountKingdeeBook.getUsername());
            }else {
                throw new ServiceException(kingdeeSynDto.getResult());
            }
        }
        voucherRepository.save(voucher);
        return voucher;
    }

    //获取所有科目名称及其对应的核算维度
    public Map<String, List<String>> accountNameToFlexGroupNamesMap(List<BdAccount> bdAccountList, List<BdFlexItemGroup> bdFlexItemGroupList) {
        Map<String, List<String>> result = Maps.newHashMap();
        Map<String, BdFlexItemGroup> flexItemgroupIdMap = bdFlexItemGroupList.stream().collect(Collectors.toMap(BdFlexItemGroup::getFId,BdFlexItemGroup->BdFlexItemGroup));
        for (BdAccount bdAccount : bdAccountList) {
            if (!"0".equals(bdAccount.getFItemDetailId())) {
                result.put(bdAccount.getFName(), flexItemgroupIdMap.get(bdAccount.getFItemDetailId()).getFNames());
            }
        }
        return result;
    }

    //同步金蝶
    @Transactional
    public KingdeeSynDto save(Voucher voucher, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        List<GlVoucherFEntityDto> glVoucherFEntityDtoList = Lists.newArrayList();
        List<VoucherEntry> voucherEntryList = voucherEntryRepository.findByVoucherId(voucher.getId());
        for (VoucherEntry voucherEntry : voucherEntryList) {
            GlVoucherFEntityDto entityDto = new GlVoucherFEntityDto();
            entityDto.setExplanation(voucherEntry.getFExplanation());
            entityDto.setAccountNumber(voucherEntry.getFAccountid());
            if (voucherEntry.getFDebit() != null) {
                entityDto.setDebit(voucherEntry.getFDebit());
            } else {
                entityDto.setCredit(voucherEntry.getFCredit());
            }
            List<VoucherEntryFlow> voucherEntryFlowList = voucherEntryFlowRepository.findByVoucherEntryId(voucherEntry.getId());
            //核算维度明细
            List<NameValueDto> nameValueDtoList = Lists.newArrayList();
            for (VoucherEntryFlow voucherEntryFlow : voucherEntryFlowList){
                NameValueDto nameValueDto = new NameValueDto();
                nameValueDto.setName(voucherEntryFlow.getName());
                nameValueDto.setValue(voucherEntryFlow.getCode());
                nameValueDtoList.add(nameValueDto);
            }
            entityDto.setNameValueDtoList(nameValueDtoList);
            glVoucherFEntityDtoList.add(entityDto);
        }
        GlVoucherDto glVoucherDto = new GlVoucherDto();
        glVoucherDto.setCreator(accountKingdeeBook.getUsername());
        glVoucherDto.setDate(voucher.getFDate());
        glVoucherDto.setExtendType(ExtendTypeEnum.凭证_K3.name());
        glVoucherDto.setGlVoucherFEntityDtoList(glVoucherFEntityDtoList);
        return glVoucherService.save(glVoucherDto,kingdeeBook,accountKingdeeBook);
    }

    public List<String> getHeaders(List<BdFlexItemGroup> bdFlexItemGroupList) {
        List<String> list = Lists.newLinkedList();
        list.add("摘要");
        list.add("科目名称");
        list.addAll(getFlexItemGroupAllName(bdFlexItemGroupList));
        list.add("借方金额");
        list.add("贷方金额");
        return list;
    }

    //核算维度组--所有不重复的核算名称
    public List<String> getFlexItemGroupAllName(List<BdFlexItemGroup> bdFlexItemGroupList) {
        List<String> list = Lists.newLinkedList();
        for (BdFlexItemGroup flexItemGroup : bdFlexItemGroupList) {
            for (String name : flexItemGroup.getFNames()) {
                if (!list.contains(name)) {
                    list.add(name);
                }
            }
        }
        return list;
    }

    public SimpleExcelBook export(VoucherDto voucherDto){
        List<String> flexItemNameList = getHeaders(voucherDto.getBdFlexItemGroupList());
        List<List<String>> data = initData(voucherDto);
        Workbook workbook = new SXSSFWorkbook(10000);
        Map<String, CellStyle> cellStyleMap= ExcelUtils.getCellStyleMap(workbook);
        List<List<SimpleExcelColumn>> excelColumnList = Lists.newArrayList();
        List<SimpleExcelColumn> simpleCellList = Lists.newArrayList();
        for (String head : flexItemNameList){
            simpleCellList.add(new SimpleExcelColumn(cellStyleMap.get(ExcelCellStyle.HEADER.name()),head));
        }
        excelColumnList.add(simpleCellList);
        for (List<String> row : data){
            simpleCellList = Lists.newArrayList();
            for (String cell : row){
                simpleCellList.add(new SimpleExcelColumn(cellStyleMap.get(ExcelCellStyle.DATA.name()),cell));
            }
            excelColumnList.add(simpleCellList);
        }
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("凭证",excelColumnList);
        ExcelUtils.doWrite(workbook, simpleExcelSheet);
       return new SimpleExcelBook(workbook,voucherDto.getFDate()+"凭证"+voucherDto.getStatus()+".xlsx",simpleExcelSheet);
    }

    @Transactional
    public void delete(String id) {
        Voucher voucher = voucherRepository.findOne(id);
        if (voucher != null) {
            List<VoucherEntry> voucherEntryList = voucherEntryRepository.findByVoucherId(voucher.getId());
            if (CollectionUtil.isNotEmpty(voucherEntryList)) {
                for (VoucherEntry voucherEntry : voucherEntryList) {
                    List<VoucherEntryFlow> voucherEntryFlowList = voucherEntryFlowRepository.findByVoucherEntryId(voucherEntry.getId());
                    if (CollectionUtil.isNotEmpty(voucherEntryFlowList)) {
                        voucherEntryFlowRepository.deleteInBatch(voucherEntryFlowList);
                    }
                }
                voucherEntryRepository.deleteInBatch(voucherEntryList);
            }
            voucherRepository.delete(voucher.getId());
        }
    }
}
