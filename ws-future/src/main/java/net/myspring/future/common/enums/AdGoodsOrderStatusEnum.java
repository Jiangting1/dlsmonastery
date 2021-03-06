package net.myspring.future.common.enums;

import com.google.common.collect.Lists;
import net.myspring.util.collection.CollectionUtil;

import java.util.List;

public enum AdGoodsOrderStatusEnum {
	待开单, 待发货, 待签收, 已完成;

	private static List<String> list= Lists.newArrayList();

	public static List<String> getList(){
		if(CollectionUtil.isEmpty(list)){
			for(AdGoodsOrderStatusEnum each : AdGoodsOrderStatusEnum.values()){
				list.add(each.name());
			}
		}
		return list;
	}
}