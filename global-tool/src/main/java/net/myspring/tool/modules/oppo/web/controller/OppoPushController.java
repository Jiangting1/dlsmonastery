package net.myspring.tool.modules.oppo.web.controller;

import com.google.common.collect.Maps;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.tool.common.client.CompanyConfigClient;
import net.myspring.tool.common.dataSource.DbContextHolder;
import net.myspring.tool.modules.oppo.domain.*;
import net.myspring.tool.modules.oppo.dto.OppoPlantSendImeiPpselDto;
import net.myspring.tool.modules.oppo.dto.OppoResponseMessage;
import net.myspring.tool.modules.oppo.service.OppoPushSerivce;
import net.myspring.tool.modules.oppo.service.OppoPullService;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.MD5Utils;
import net.myspring.util.time.LocalDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "factory/oppo")
public class OppoPushController {
    @Autowired
    private OppoPushSerivce oppoPushSerivce;
    @Autowired
    private CompanyConfigClient companyConfigClient;


    protected Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "pushFactoryData")
    public String pushFactoryData(String date) {
        String companyName=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.COMPANY_NAME.name()).replace("\"","");
        DbContextHolder.get().setCompanyName(companyName);
        //上抛oppo门店数据,只上抛二代和渠道门店
        oppoPushSerivce.getOppoCustomers(date);
        //上抛运营商属性
        oppoPushSerivce.getOppoCustomerOperatortype(date);
        //发货退货调拨数据上抛
        List<OppoCustomerAllot> oppoCustomerAllots=oppoPushSerivce.getFutureOppoCustomerAllot(date);
        oppoPushSerivce.getOppoCustomerAllot(oppoCustomerAllots,date);
        //上抛一代二代库存数据,不包括门店数据
        List<OppoCustomerStock> oppoCustomerStocks=oppoPushSerivce.getFutureOppoCustomerStock(date);
        oppoPushSerivce.getOppoCustomerStock(oppoCustomerStocks,date);
        //获取渠道串码收货数据
        List<OppoCustomerImeiStock> oppoCustomerImeiStocks=oppoPushSerivce.getFutureOppoCustomerImeiStock(date);
        oppoPushSerivce.getOppoCustomerImeiStock(oppoCustomerImeiStocks,date);
        //获取店核销总数据
        List<OppoCustomerSale> oppoCustomerSales=oppoPushSerivce.getFutureOppoCustomerSale(date);
        oppoPushSerivce.getOppoCustomerSales(oppoCustomerSales,date);
        //门店销售明细数据
        List<OppoCustomerSaleImei> oppoCustomerSaleImeis=oppoPushSerivce.getFutureOppoCustomerSaleImeis(date);
        oppoPushSerivce.getOppoCustomerSaleImes(oppoCustomerSaleImeis,date);
        //门店销售数据汇总
        List<OppoCustomerSaleCount> oppoCustomerSaleCounts=oppoPushSerivce.getFutureOppoCustomerSaleCounts(date);
        oppoPushSerivce.getOppoCustomerSaleCounts(oppoCustomerSaleCounts,date);
        //门店售后退货汇总
        List<OppoCustomerAfterSaleImei>  oppoCustomerAfterSaleImeis=oppoPushSerivce.getFutureOppoCustomerAfterSaleImeis(date);
        oppoPushSerivce.getOppoCustomerAfterSaleImeis(oppoCustomerAfterSaleImeis,date);
        //门店演示机汇总数据
        List<OppoCustomerDemoPhone> oppoCustomerDemoPhones=oppoPushSerivce.getFutureOppoCustomerDemoPhone(date);
        oppoPushSerivce.getOppoCustomerDemoPhone(oppoCustomerDemoPhones,date);
        return "OPPO同步成功";
    }

    //代理商经销商基础数据上抛
    @RequestMapping(value = "pullCustomers", method = RequestMethod.GET)
    public OppoResponseMessage pullOppoCustomers(String key, String createdDate,HttpServletResponse response) throws IOException {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey = MD5Utils.encode(factoryAgentName + createdDate);
        OppoResponseMessage responseMessage = new OppoResponseMessage();
        if (!localKey.equals(key)) {
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        } else {
            List<OppoCustomer> oppoCustomers = oppoPushSerivce.getOppoCustomersByDate(createdDate);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomers));
            responseMessage.setResult("success");
        }
        return responseMessage;
    }

    //运营商属性上抛
    @RequestMapping(value = "pullCustomerOperatortype", method = RequestMethod.GET)
    public OppoResponseMessage pullOppoCustomerOperatortype(String key, String createdDate) {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey = MD5Utils.encode(factoryAgentName + createdDate);
        OppoResponseMessage responseMessage = new OppoResponseMessage();
        if (!localKey.equals(key)) {
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        } else {
            List<OppoCustomerOperatortype> oppoCustomerOperatortypes = oppoPushSerivce.getOppoCustomerOperatortypesByDate(createdDate);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerOperatortypes));
            responseMessage.setResult("success");
        }
        logger.info("responseMessage=="+responseMessage.toString());
        return responseMessage;
    }

    //发货退货调拨数据上抛
    @RequestMapping(value ="pullCustomerAllot", method = RequestMethod.GET)
    public OppoResponseMessage pullOppoCustomersAllot(String key, String dateStart, String dateEnd) {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey=MD5Utils.encode(factoryAgentName+dateStart+dateEnd);
        OppoResponseMessage responseMessage=new OppoResponseMessage();
        if(!localKey.equals(key)){
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        }else{
            String dateStartTime= LocalDateUtils.format(LocalDateUtils.parse(dateStart));
            String dateEndTime=LocalDateUtils.format(LocalDateUtils.parse(dateEnd).plusDays(1));
            List<OppoCustomerAllot> oppoCustomerAllots=oppoPushSerivce.getOppoCustomerAllotsByDate(dateStartTime,dateEndTime);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerAllots));
            responseMessage.setResult("success");
        }
        return responseMessage;
    }

    //库存数据上抛
    @RequestMapping(value ="pullCustomerStock", method = RequestMethod.GET)
    public OppoResponseMessage pullCustomerStock(String key,String createdDate)  {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey=MD5Utils.encode(factoryAgentName+createdDate);
        OppoResponseMessage responseMessage=new OppoResponseMessage();
        if(!localKey.equals(key)){
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        }else{
            logger.info("库存上抛开始："+new Date());
            String dateStart= LocalDateUtils.format(LocalDateUtils.parse(createdDate).plusMonths(-12));
            String dateEnd=LocalDateUtils.format(LocalDateUtils.parse(createdDate).plusDays(1));
            List<OppoCustomerStock> oppoCustomerStocks=oppoPushSerivce.getOppoCustomerStocksByDate(dateStart,dateEnd);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerStocks));
            responseMessage.setResult("success");
        }
        logger.info("库存上抛结束："+new Date());
        return responseMessage;
    }

    //门店条码调拨明细上抛
    @RequestMapping(value ="pullCustomerImeStock", method = RequestMethod.GET)
    public OppoResponseMessage pullCustomerImeStock(String key,String dateStart,String dateEnd)  {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey=MD5Utils.encode(factoryAgentName+dateStart+dateEnd);
        OppoResponseMessage responseMessage=new OppoResponseMessage();
        if(!localKey.equals(key)){
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        }else{
            String dateStartTime= LocalDateUtils.format(LocalDateUtils.parse(dateStart));
            String dateEndTime=LocalDateUtils.format(LocalDateUtils.parse(dateEnd).plusDays(1));
            List<OppoCustomerImeiStock> oppoCustomerImeiStocks=oppoPushSerivce.getOppoCustomerImeiStocksByDate(dateStartTime,dateEndTime);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerImeiStocks));
            responseMessage.setResult("success");
        }
        return responseMessage;
    }

    //店总数据上抛
    @RequestMapping(value ="pullCustomerSale", method = RequestMethod.GET)
    public OppoResponseMessage pullCustomerSale(String key,String dateStart,String dateEnd) {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey = MD5Utils.encode(factoryAgentName + dateStart + dateEnd);
        OppoResponseMessage responseMessage = new OppoResponseMessage();
        if (!localKey.equals(key)) {
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        } else {
            String dateStartTime= LocalDateUtils.format(LocalDateUtils.parse(dateStart));
            String dateEndTime=LocalDateUtils.format(LocalDateUtils.parse(dateEnd).plusDays(1));
            List<OppoCustomerSale> oppoCustomerSales = oppoPushSerivce.getOppoCustomerSalesByDate(dateStartTime, dateEndTime);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerSales));
            responseMessage.setResult("success");
        }
        return responseMessage;
    }

    //门店销售明细
    @RequestMapping(value ="pullCustomerSaleIme", method = RequestMethod.GET)
    public OppoResponseMessage pullCustomerSaleIme(String key,String dateStart,String dateEnd)  {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey = MD5Utils.encode(factoryAgentName + dateStart + dateEnd);
        OppoResponseMessage responseMessage=new OppoResponseMessage();
        if(!localKey.equals(key)){
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        }else{
            String dateStartTime= LocalDateUtils.format(LocalDateUtils.parse(dateStart));
            String dateEndTime=LocalDateUtils.format(LocalDateUtils.parse(dateEnd).plusDays(1));
            List<OppoCustomerSaleImei> oppoCustomerSaleImes=oppoPushSerivce.getOppoCustomerSaleImeisByDate(dateStartTime,dateEndTime);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerSaleImes));
            responseMessage.setResult("success");
        }
        return responseMessage;
    }

    //门店销售数据汇总
    @RequestMapping(value ="pullCustomerSaleCount", method = RequestMethod.GET)
    public OppoResponseMessage pullCustomerSaleCount(String key,String dateStart,String dateEnd)  {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey = MD5Utils.encode(factoryAgentName + dateStart + dateEnd);
        OppoResponseMessage responseMessage=new OppoResponseMessage();
        if(!localKey.equals(key)){
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        }else{
            logger.info("门店销售数据汇总上抛开始");
            String dateStartTime= LocalDateUtils.format(LocalDateUtils.parse(dateStart));
            String dateEndTime=LocalDateUtils.format(LocalDateUtils.parse(dateEnd).plusDays(1));
            List<OppoCustomerSaleCount> oppoCustomerSaleCounts=oppoPushSerivce.getOppoCustomerSaleCountsByDate(dateStartTime,dateEndTime);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerSaleCounts));
            responseMessage.setResult("success");
            logger.info("门店销售数据汇总上抛结束");
        }
        return responseMessage;
    }

    //门店售后零售退货条码数据
    @RequestMapping(value ="pullCustomerAfterSaleIme", method = RequestMethod.GET)
    public OppoResponseMessage pullCustomerAfterSaleIme(String key,String dateStart,String dateEnd)  {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey = MD5Utils.encode(factoryAgentName + dateStart + dateEnd);
        OppoResponseMessage responseMessage=new OppoResponseMessage();
        if(!localKey.equals(key)){
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        }else{
            logger.info("门店店售后退货条码数据上抛开始");
            String dateStartTime= LocalDateUtils.format(LocalDateUtils.parse(dateStart));
            String dateEndTime=LocalDateUtils.format(LocalDateUtils.parse(dateEnd).plusDays(1));
            List<OppoCustomerAfterSaleImei> oppoCustomerAfterSaleImeis=oppoPushSerivce.getOppoCustomerAfterSaleImeisByDate(dateStartTime,dateEndTime);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerAfterSaleImeis));
            responseMessage.setResult("success");
            logger.info("门店店售后退货条码数据上抛结束");
        }
        return responseMessage;
    }

    //演示机条码数据
    @RequestMapping(value ="pullCustomerDemoPhone", method = RequestMethod.GET)
    public OppoResponseMessage pullCustomerDemoPhone(String key,String dateStart,String dateEnd)  {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String factoryAgentName =agentCode.split(CharConstant.COMMA)[0];
        String localKey = MD5Utils.encode(factoryAgentName + dateStart + dateEnd);
        OppoResponseMessage responseMessage=new OppoResponseMessage();
        if(!localKey.equals(key)){
            responseMessage.setMessage("密钥不正确");
            responseMessage.setResult("false");
        }else{
            String dateStartTime= LocalDateUtils.format(LocalDateUtils.parse(dateStart));
            String dateEndTime=LocalDateUtils.format(LocalDateUtils.parse(dateEnd).plusDays(1));
            List<OppoCustomerDemoPhone> oppoCustomerDemoPhones=oppoPushSerivce.getOppoCustomerDemoPhonesByDate(dateStartTime,dateEndTime);
            responseMessage.setMessage(ObjectMapperUtils.writeValueAsString(oppoCustomerDemoPhones));
            responseMessage.setResult("success");
        }
        return responseMessage;
    }
}