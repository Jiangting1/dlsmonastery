package net.myspring.basic.modules.sys.mapper;

import net.myspring.basic.common.mybatis.MyMapper;
import net.myspring.basic.modules.sys.domain.DictEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DictEnumMapper extends MyMapper<DictEnum,String> {

}
