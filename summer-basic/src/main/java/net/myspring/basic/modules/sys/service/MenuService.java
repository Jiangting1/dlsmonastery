package net.myspring.basic.modules.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.repository.AccountPermissionRepository;
import net.myspring.basic.modules.sys.domain.Menu;
import net.myspring.basic.modules.sys.domain.MenuCategory;
import net.myspring.basic.modules.sys.domain.Permission;
import net.myspring.basic.modules.sys.dto.BackendMenuDto;
import net.myspring.basic.modules.sys.dto.MenuDto;
import net.myspring.basic.modules.sys.manager.RoleManager;
import net.myspring.basic.modules.sys.repository.BackendRepository;
import net.myspring.basic.modules.sys.repository.MenuCategoryRepository;
import net.myspring.basic.modules.sys.repository.MenuRepository;
import net.myspring.basic.modules.sys.repository.PermissionRepository;
import net.myspring.basic.modules.sys.web.form.MenuForm;
import net.myspring.basic.modules.sys.web.query.MenuQuery;
import net.myspring.common.constant.CharConstant;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private BackendRepository backendRepository;
    @Autowired
    private AccountPermissionRepository accountPermissionRepository;
    @Autowired
    private MenuCategoryRepository menuCategoryRepository;

    public List<MenuDto> findAll() {
        List<Menu> menuList = menuRepository.findAll();
        List<MenuDto> menuDtoList = BeanUtil.map(menuList, MenuDto.class);
        cacheUtils.initCacheInput(menuDtoList);
        return menuDtoList;
    }

    public Menu findOne(String id) {
        Menu menu = menuRepository.findOne(id);
        return menu;
    }

    public MenuDto findOne(MenuDto menuDto) {
        if (!menuDto.isCreate()) {
            Menu menu = findOne(menuDto.getId());
            menuDto = BeanUtil.map(menu, MenuDto.class);
        }
        return menuDto;
    }

    public Page<MenuDto> findPage(Pageable pageable, MenuQuery menuQuery) {
        Page<MenuDto> menuDtoPage = menuRepository.findPage(pageable, menuQuery);
        cacheUtils.initCacheInput(menuDtoPage.getContent());
        return menuDtoPage;
    }

    @Transactional
    public void delete(String id) {
        menuRepository.logicDelete(id);
    }

    @Transactional
    public Menu save(MenuForm menuForm) {
        Menu menu;
        if (menuForm.isCreate()) {
            menu = BeanUtil.map(menuForm, Menu.class);
            menuRepository.save(menu);
        } else {
            menu = menuRepository.findOne(menuForm.getId());
            ReflectionUtil.copyProperties(menuForm, menu);
            menuRepository.save(menu);
        }
        return menu;
    }

    public List<Map<String, Object>> findMobileMenus(String accountId,List<String> roleIdList) {
        List<Map<String, Object>> list = Lists.newArrayList();
        List<String> menuIdList = getMenuIdList(accountId,roleIdList);
        List<Menu> menuList = menuRepository.findByMenuIdsAndMobile(menuIdList,true,roleIdList);
        if (CollectionUtil.isNotEmpty(menuList)) {
            Map<String, List<Menu>> menuMap = CollectionUtil.extractToMapList(menuList, "menuCategoryId");
            List<MenuCategory> menuCategoryList = menuCategoryRepository.findAll(CollectionUtil.extractToList(menuList, "menuCategoryId"));
            for (MenuCategory menuCategory : menuCategoryList) {
                Map<String, Object> item = Maps.newHashMap();
                item.put("category", menuCategory);
                item.put("menus", menuMap.get(menuCategory.getId()));
                list.add(item);
            }
        }
        return list;
    }

    public List<BackendMenuDto> getMenusMap(String accountId,List<String> roleIdList) {
        List<BackendMenuDto> backendList = Lists.newLinkedList();
        List<String> menuIdList = getMenuIdList(accountId,roleIdList);
        if (CollectionUtil.isNotEmpty(menuIdList)) {
            backendList = backendRepository.findByMenuList(menuIdList);
        }
        return backendList;
    }

    private List<String> getMenuIdList(String accountId,List<String> roleIdList) {
        List<String> menuIdList = Lists.newArrayList();
        if (RequestUtils.getAdmin()) {
            List<Menu> menuList = menuRepository.findAllEnabled();
            menuIdList = CollectionUtil.extractToList(menuList, "id");
        } else {
            List<Permission> permissionList;
            List<String> accountPermissions = accountPermissionRepository.findPermissionIdByAccountId(accountId);
            if (CollectionUtil.isNotEmpty(accountPermissions)) {
                permissionList = permissionRepository.findByAccountId( accountId);
            } else {
                permissionList = permissionRepository.findByRoleIdList(roleIdList);
            }
            if(CollectionUtil.isNotEmpty(permissionList)){
                menuIdList = CollectionUtil.extractToList(permissionList, "menuId");
                List<Menu> menuList=menuRepository.findByMenuIdsAndMobile(menuIdList,false,roleIdList);
                if(CollectionUtil.isNotEmpty(menuList)){
                    menuIdList=CollectionUtil.extractToList(menuList,"id");
                }else {
                    menuIdList=Lists.newArrayList();
                }
            }
            List<Menu> permissionIsEmptyMenus = menuRepository.findByPermissionIsEmpty();
            menuIdList = CollectionUtil.union(menuIdList, CollectionUtil.extractToList(permissionIsEmptyMenus, "id"));
            menuIdList = Lists.newArrayList(Sets.newHashSet(menuIdList));
        }
        return menuIdList;
    }
}
