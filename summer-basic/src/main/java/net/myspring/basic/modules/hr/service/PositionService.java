package net.myspring.basic.modules.hr.service;

import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.modules.hr.domain.Position;
import net.myspring.basic.modules.hr.dto.PositionDto;
import net.myspring.basic.modules.hr.mapper.PositionMapper;
import net.myspring.basic.modules.hr.web.form.PositionForm;
import net.myspring.basic.modules.hr.web.query.PositionQuery;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PositionService {
    
    @Autowired
    private PositionMapper positionMapper;
    @Autowired
    private CacheUtils cacheUtils;


    public List<PositionDto> findAll(){
        List<Position> positionList=positionMapper.findAll();
        List<PositionDto> positionDtoList= BeanUtil.map(positionList,PositionDto.class);
        cacheUtils.initCacheInput(positionDtoList);
        return positionDtoList;
    }

    public Page<PositionDto> findPage(Pageable pageable, PositionQuery positionQuery) {
        Page<PositionDto> page = positionMapper.findPage(pageable, positionQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public List<PositionDto> findByNameLike(String name){
        List<Position> positionList=positionMapper.findByNameLike(name);
        List<PositionDto> positionDtoList= BeanUtil.map(positionList,PositionDto.class);
        cacheUtils.initCacheInput(positionDtoList);
        return  positionDtoList;
    }

    public Position findOne(String id){
        Position position = positionMapper.findOne(id);
        return position;
    }

    public PositionForm findForm(PositionForm positionForm){
        if(!positionForm.isCreate()){
            Position position = positionMapper.findOne(positionForm.getId());
            positionForm= BeanUtil.map(position,PositionForm.class);
            cacheUtils.initCacheInput(positionForm);
        }
        return positionForm;
    }

    public Position save(PositionForm positionForm){
        Position position;
        if(positionForm.isCreate()){
            position=BeanUtil.map(positionForm,Position.class);
            positionMapper.save(position);
        }else{
            position = positionMapper.findOne(positionForm.getId());
            ReflectionUtil.copyProperties(positionForm,position);
            positionMapper.update(position);
        }
        return position;
    }

    public void delete(String id){
        positionMapper.logicDeleteOne(id);
    }

}
