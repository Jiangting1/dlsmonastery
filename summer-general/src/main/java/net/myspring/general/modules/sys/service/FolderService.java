package net.myspring.general.modules.sys.service;

import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.constant.TreeConstant;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.general.common.utils.RequestUtils;
import net.myspring.general.modules.sys.domain.Folder;
import net.myspring.general.modules.sys.dto.FolderDto;
import net.myspring.general.modules.sys.repository.FolderRepository;
import net.myspring.general.modules.sys.web.form.FolderForm;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class FolderService {
    @Autowired
    private FolderRepository folderRepository;

    public FolderDto findOne(FolderDto folderDto) {
        if (!folderDto.isCreate()) {
            Folder folder = folderRepository.findOne(folderDto.getId());
            folderDto = BeanUtil.map(folder, FolderDto.class);
        }
        return folderDto;
    }

    public void logicDelete(String id) {
        folderRepository.logicDelete(id);
    }

    public Folder getRoot(String accountId) {
        Folder folder = folderRepository.findByCreatedByAndParentIds(accountId, TreeConstant.ROOT_PARENT_IDS);
        if (folder == null) {
            folder = new Folder();
            folder.setName(accountId);
            folder.setParentIds(TreeConstant.ROOT_PARENT_IDS);
            folderRepository.save(folder);
        }
        return folder;
    }

    public Folder getAccountFolder(String accountId, String path) {
        Folder parent = getRoot(accountId);
        Folder folder = null;
        String[] paths = path.split("/");
        for (String p : paths) {
            p = p.trim();
            if (StringUtils.isNotBlank(p)) {
                folder = folderRepository.findByParentIdAndName(parent.getId(), p);
                if (folder == null) {
                    FolderForm folderForm = new FolderForm();
                    folderForm.setParentId(parent.getId());
                    folderForm.setName(p);
                    folderForm.setParentIds(parent.getParentIds() + parent.getId() + ",");
                    folderRepository.save(BeanUtil.map(folderForm, Folder.class));
                }
                parent = folder;
            }
        }
        return folder;
    }

    public List<FolderDto> findAll(String accountId) {
        List<FolderDto> folderDtoList = Lists.newArrayList();
        Folder parent = getRoot(accountId);
        List<Folder> folders = Lists.newArrayList();
        List<Folder> sourceList = folderRepository.findByCreatedBy(accountId);
        sortList(folders, sourceList, parent.getId());
        if (!CollectionUtil.isEmpty(folders)) {
            folderDtoList = BeanUtil.map(folders, FolderDto.class);
            for (FolderDto folderDto : folderDtoList) {
                if (folderDto.getParentId() == null) {
                    folderDto.setLevelName(folderDto.getName().trim());
                } else {
                    String[] space = new String[folderDto.getParentIds().split(CharConstant.COMMA).length - parent.getParentIds().split(CharConstant.COMMA).length];
                    Arrays.fill(space, CharConstant.SPACE_FULL);
                    folderDto.setLevelName(StringUtils.join(space) + folderDto.getName().trim());
                }
            }
        }
        return folderDtoList;
    }

    public RestResponse save(FolderForm folderForm) {
        String oldParentIds = folderForm.getParentIds();
        Folder parent = folderRepository.findOne(folderForm.getParentId());
        // 无法将上级部门设置为自己或者自己的下级部门
        folderForm.setParentIds(parent.getParentIds() + folderForm.getParentId() + ",");
        if (!folderForm.isCreate() && folderForm.getParentIds().contains("," + folderForm.getId() + ",")) {
            return new RestResponse("无法将上级目录设置为自己或者自己的下级目录", ResponseCodeEnum.invalid.name());
        }
        Folder folder;
        if(folderForm.isCreate()){
            folder = BeanUtil.map(folderForm, Folder.class);
            folderRepository.save(folder);
        }else {
            folder = folderRepository.findOne(folderForm.getId());
            ReflectionUtil.copyProperties(folderForm,folder);
            folderRepository.save(folder);
            List<Folder> list = folderRepository.findByParentIdsLike("%," + folderForm.getId() + ",%");
            for (Folder e : list) {
                e.setParentIds(e.getParentIds().replace(oldParentIds, folderForm.getParentIds()));
            }
        }
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    private void sortList(List<Folder> list, List<Folder> sourceList, String parentId) {
        for (int i = 0; i < sourceList.size(); i++) {
            Folder e = sourceList.get(i);
            if (e.getParentId() == null) {
                if (e.getId().equals(parentId)) {
                    list.add(e);
                }
            } else {
                if (e.getParentId().equals(parentId)) {
                    list.add(e);
                    // 判断是否还有子节点, 有则继续获取子节点
                    for (int j = 0; j < sourceList.size(); j++) {
                        Folder child = sourceList.get(j);
                        if (child.getParentId() != null && child.getParentId() != null && child.getParentId().equals(e.getId())) {
                            sortList(list, sourceList, e.getId());
                            break;
                        }
                    }
                }
            }
        }
    }
}
