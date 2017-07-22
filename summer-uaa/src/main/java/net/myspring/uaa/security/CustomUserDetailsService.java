package net.myspring.uaa.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.uaa.datasource.DbContextHolder;
import net.myspring.uaa.dto.AccountDto;
import net.myspring.uaa.dto.AccountWeixinDto;
import net.myspring.uaa.dto.OfficeDto;
import net.myspring.uaa.dto.WeixinSessionDto;
import net.myspring.uaa.manager.OfficeManager;
import net.myspring.uaa.manager.PermissionManager;
import net.myspring.uaa.manager.WeixinManager;
import net.myspring.uaa.repository.AccountDtoRepository;
import net.myspring.uaa.repository.AccountWeixinDtoRepository;
import net.myspring.uaa.repository.CompanyConfigRepository;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuj on 2017/4/1.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Value("${setting.adminIdList}")
    private String adminIdList;
    @Autowired
    private AccountDtoRepository accountDtoRepository;
    @Autowired
    private AccountWeixinDtoRepository accountWeixinDtoRepository;
    @Autowired
    private WeixinManager weixinManager;
    @Autowired
    private OfficeManager officeManager;
    @Autowired
    private PermissionManager permissionManager;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CompanyConfigRepository companyConfigRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails customUserDetails = null;
        AccountDto accountDto = null;
        HttpServletRequest request  = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String weixinCode = request.getParameter("weixinCode");
        String companyName=request.getParameter("companyName");
        DbContextHolder.get().setCompanyName(companyName);
        if(StringUtils.isNotBlank(weixinCode)) {
            String accountId = ObjectUtils.toString(request.getParameter("accountId"));
            WeixinSessionDto weixinSessionDto = weixinManager.findWeixinSessionDto(weixinCode);
            if(weixinSessionDto != null && StringUtils.isNotBlank(weixinSessionDto.getOpenid())) {
                List<AccountWeixinDto> accountWeixinDtoList = accountWeixinDtoRepository.findByOpenId(weixinSessionDto.getOpenid());
                if(CollectionUtil.isNotEmpty(accountWeixinDtoList)) {
                    Map<String,AccountWeixinDto>  accountWeixinDtoMap = CollectionUtil.extractToMap(accountWeixinDtoList,"accountId");
                    if(!accountWeixinDtoMap.containsKey(accountId)) {
                        accountId = null;
                    }
                    if(StringUtils.isBlank(accountId)) {
                        accountId = accountWeixinDtoList.get(0).getAccountId();
                    }
                }
            }
            if(StringUtils.isNotBlank(accountId)) {
                accountDto = accountDtoRepository.findOne(accountId);
            }
        } else {
            accountDto = accountDtoRepository.findByLoginName(username);
            String password = request.getParameter("password");
            //密码不正确
            if(accountDto==null || !StringUtils.validatePassword(password,accountDto.getPassword())){
                return null;
            }
        }
        if(accountDto != null) {
            accountDto.setCompanyName(companyName);
            LocalDate leaveDate = accountDto.getLeaveDate();
            boolean accountNoExpired = leaveDate == null || leaveDate.isAfter(LocalDate.now());
            Set<SimpleGrantedAuthority> authList = Sets.newHashSet();
            authList.add(new SimpleGrantedAuthority(accountDto.getPositionId()));
            //将用户权限设置到缓存中
            List<String> roleIdList=StringUtils.getSplitList(accountDto.getRoleIds(),CharConstant.COMMA);
            List<String> officeIds=StringUtils.getSplitList(accountDto.getOfficeIds(),CharConstant.COMMA);
            Boolean admin = StringUtils.getSplitList(adminIdList, CharConstant.COMMA).contains(accountDto.getId());
            List<OfficeDto> officeList = officeManager.findByIds(officeIds);
            Boolean allDataScope = CollectionUtil.extractToList(officeList,"allDataScope").contains(true);
            if(admin) {
                allDataScope = true;
            }
            List<String> officeIdList = Lists.newArrayList();
            if(!allDataScope) {
                officeIdList = officeManager.getOfficeIdList(officeIds);
            }
            customUserDetails = new CustomUserDetails(
                    accountDto.getCompanyName() + "_" + accountDto.getLoginName(),
                    accountDto.getPassword(),
                    accountDto.getEnabled(),
                    accountNoExpired,
                    true,
                    !accountDto.getLocked(),
                    authList,
                    accountDto.getId(),
                    accountDto.getPositionId(),
                    accountDto.getOfficeId(),
                    accountDto.getEmployeeId(),
                    accountDto.getCompanyName(),
                    roleIdList,
                    officeIdList,
                    admin
            );
        }
        return customUserDetails;
    }

}
