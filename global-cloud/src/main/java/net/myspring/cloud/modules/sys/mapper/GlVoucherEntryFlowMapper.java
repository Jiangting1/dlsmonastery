package net.myspring.cloud.modules.sys.mapper;

import net.myspring.cloud.modules.sys.domain.GlVoucherEntryFlow;
import net.myspring.mybatis.mapper.CrudMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by lihx on 2017/4/5.
 */
@Mapper
public interface GlVoucherEntryFlowMapper extends CrudMapper<GlVoucherEntryFlow,String> {
}
