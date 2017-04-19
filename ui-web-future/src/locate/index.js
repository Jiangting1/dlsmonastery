import elementId from 'element-ui/lib/locale/lang/id'
import basicHrId from"./basic_hr_id";
import basicSysId from "./basic_sys_id"
import businessBasicId from "./business_basic_id"
import businessCrmId from "./business_crm_id"
import businessLayoutId from "./business_layout_id"
import commonId from "./common_id"

import elementZhCn from 'element-ui/lib/locale/lang/zh-CN'
import basicHrZhCn from"./basic_hr_zh-CN";
import basicSysZhCn from "./basic_sys_zh-CN"
import businessBasicZhCn from "./business_basic_zh-CN"
import businessCrmZhCn from "./business_crm_zh-CN"
import businessLayoutZhCn from "./business_layout_zh-CN"
import commonZhCn from "./common_zh-CN"

var idArray = [elementId,basicHrId,basicSysId,businessBasicId,businessCrmId,businessLayoutId,commonId];
var zhCnArray = [elementZhCn,basicHrZhCn,basicSysZhCn,businessBasicZhCn,businessCrmZhCn,businessLayoutZhCn,commonZhCn];

var id = merge(idArray);
var zhCn = merge(zhCnArray);

function  merge(jsonArray) {
  var resultJsonObject = {};
  for (var index in jsonArray) {
    for (var attr in jsonArray[index]) {
      resultJsonObject[attr] = jsonArray[index][attr];
    }
  }
  return resultJsonObject;
}

export default {
  id:id,
  zhCn:zhCn
}