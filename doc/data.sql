DELETE FROM sys_backend;
INSERT INTO sys_backend SELECT
	t1.*
FROM
	db_oppo_test.sys_backend t1
WHERE
	t1.enabled = 1;
DELETE FROM sys_backend_module;
INSERT INTO sys_backend_module
SELECT
	t1.*
FROM
	db_oppo_test.sys_backend_module t1
WHERE
	t1.enabled = 1;

UPDATE sys_menu_category SET enabled=1;
INSERT INTo sys_menu_category(
id,
name,
sort,
created_by,
created_date,
last_modified_by,
last_modified_date,
remarks,
version,
locked,
enabled,
code,
backend_module_id,
company_id
) SELECT t1.* from db_oppo_test.sys_menu_category t1 where t1.enabled=1;
update sys_menu t1,db_oppo_test.sys_menu t2 set t1.menu_category_id=t2.menu_category_id,t1.code=t2.code where t1.name=t2.name;
DELETE FROM sys_office_rule;
INSERT INTO sys_office_rule SELECT
	t1.*
FROM
	db_oppo_test.sys_office_rule t1
WHERE
	t1.enabled = 1;

update sys_office t1,db_oppo_test.sys_office t2 set t1.office_rule_id=t2.office_rule_id,t1.area_id=t2.area_id where t1.name=t2.name;

INSERT INTO sys_role SELECT
	t1.*
FROM
	db_oppo_test.sys_role t1
WHERE
	t1.enabled = 1;

	update hr_position t1,db_oppo_test.hr_position t2 set t1.role_id=t2.role_id where t1.`name`=t2.`name`;

  delete FROM  sys_company_config where code in ("COMPANY_NAME","DEFAULT_PROVINCE_ID","EXPRESS_PRINT_QTY");
	INSERT into sys_company_config SELECT t1.* FROM db_oppo_test.sys_company_config t1 where t1.code in ("COMPANY_NAME","DEFAULT_PROVINCE_ID","EXPRESS_PRINT_QTY");

	INSERT INTO sys_menu (
	id,
	NAME,
	sort,
	mobile,
	menu_category_id,
	created_by,
	created_date,
	last_modified_by,
	last_modified_date,
	remarks,
	version,
	locked,
	enabled,
	visible,
	mobile_href,
	mobile_icon,
	`code`,
	href,
	company_id
) SELECT
	t1.id,
	t1.`NAME`,
	t1.sort,
	t1.mobile,
	t1.menu_category_id,
	t1.created_by,
	t1.created_date,
	t1.last_modified_by,
	t1.last_modified_date,
	t1.remarks,
	t1.version,
	t1.locked,
	t1.enabled,
	t1.visible,
	t1.mobile_href,
	t1.mobile_icon,
	t1.`code`,
	"" AS href,
	1 AS company_id
FROM
	db_oppo_test.sys_menu t1
WHERE
	t1.`name` not in(
		SELECT
			t2. NAME
		FROM
			sys_menu t2
	)
AND t1.enabled = 1;

update sys_menu set code="clientList" where name="客户管理";

update sys_menu set code="depotStoreList" where name="仓库管理";

update sys_permission set url='/api/ws/future/crm/goodsOrder',method='GET' where permission='crm:goodsOrder:view';
update sys_permission set url='/api/ws/future/crm/goodsOrder/save',method='POST' where permission='crm:goodsOrder:edit';
update sys_permission set url='/api/ws/future/crm/goodsOrder/delete',method='GET' where permission='crm:goodsOrder:delete';
update sys_permission set url='/api/ws/future/crm/goodsOrder/bill',method='POST' where permission='crm:goodsOrder:bill';
update sys_permission set url='/api/ws/future/crm/goodsOrderShip/print',method='GET' where permission='crm:goodsOrder:ship';
update sys_permission set url='/api/ws/future/crm/goodsOrderShip/shipPrint',method='GET' where permission='crm:goodsOrder:print';
update sys_permission set url='/api/ws/future/crm/expressOrder/batchAdd',method='POST' where permission='crm:goodsOrder:batchAdd';
update sys_permission set url='/api/ws/future/crm/expressOrder/shipBack',method='POST' where permission='crm:goodsOrder:shipBack';
update sys_permission set url='/api/ws/future/crm/expressOrder/delete',method='GET' where permission='crm:expressOrder:delete';
update sys_permission set url='/api/ws/future/crm/expressOrder/save,/api/ws/future/crm/expressOrder/resetPrintStatus',method='POST' where permission='crm:expressOrder:edit';
update sys_permission set url='/api/ws/future/crm/expressOrder',method='GET' where permission='crm:expressOrder:view';
update sys_permission set url='/api/ws/future/crm/bankIn/audit',method='POST' where permission='crm:bankIn:audit';
update sys_permission set url='/api/ws/future/crm/bankIn/delete',method='GET' where permission='crm:bankIn:delete';
update sys_permission set url='/api/ws/future/crm/bankIn/save',method='POST' where permission='crm:bankIn:edit';
update sys_permission set url='/api/ws/future/crm/bankIn',method='GET' where permission='crm:bankIn:view';
update sys_permission set url='/api/ws/future/layout/adGoodsOrder/bill',method='POST' where permission='crm:adGoodsOrder:bill';
update sys_permission set url='/api/ws/future/layout/adGoodsOrder/delete',method='GET' where permission='crm:adGoodsOrder:delete';
update sys_permission set url='/api/ws/future/layout/adGoodsOrder/save',method='POST' where permission='crm:adGoodsOrder:edit';
update sys_permission set url='/api/ws/future/layout/adGoodsOrder/print',method='GET' where permission='crm:adGoodsOrder:print';
update sys_permission set url='/api/ws/future/layout/adGoodsOrder/ship',method='POST' where permission='crm:adGoodsOrder:ship';
update sys_permission set url='/api/ws/future/layout/adGoodsOrder/sign',method='POST' where permission='crm:adGoodsOrder:sign';
update sys_permission set url='/api/ws/future/layout/adGoodsOrder',method='GET' where permission='crm:adGoodsOrder:view';
update sys_permission set url='/api/ws/future/layout/adApply/bill',method='POST' where permission='crm:adApply:bill';
update sys_permission set url='/api/ws/future/layout/adApply/delete',method='GET' where permission='crm:adApply:delete';
update sys_permission set url='/api/ws/future/layout/adApply/save,/api/ws/future/layout/adApply/billSave',method='POST' where permission='crm:adApply:edit';
update sys_permission set url='/api/ws/future/layout/adApply/goodsSave',method='POST' where permission='crm:adApply:goods';
update sys_permission set url='/api/ws/future/layout/adApply',method='GET' where permission='crm:adApply:view';
update sys_permission set url='/api/ws/future/crm/storeAllot/delete',method='GET' where permission='crm:storeAllot:delete';
update sys_permission set url='/api/ws/future/crm/storeAllot/save',method='POST' where permission='crm:storeAllot:edit';
update sys_permission set url='/api/ws/future/crm/storeAllot/ship',method='POST' where permission='crm:storeAllot:ship';
update sys_permission set url='/api/ws/future/crm/storeAllot',method='GET' where permission='crm:storeAllot:view';
update sys_permission set url='/api/ws/future/crm/shopAllot/audit',method='POST' where permission='crm:shopAllot:audit';
update sys_permission set url='/api/ws/future/crm/shopAllot/logicDelete',method='GET' where permission='crm:shopAllot:delete';
update sys_permission set url='/api/ws/future/crm/shopAllot/save',method='POST' where permission='crm:shopAllot:edit';
update sys_permission set url='/api/ws/future/crm/shopAllot',method='GET' where permission='crm:shopAllot:view';

update sys_menu set enabled=false where name='国际化设置';
update sys_menu set enabled=false where code='goodsOrderShip';
update sys_menu set enabled=false where name='报表监控';
update sys_menu set enabled=false where name='公司管理';
update sys_menu set enabled=false where name='问答列表';
update sys_menu set enabled=false where name='文章管理';
update sys_menu set enabled=false where name='单元编辑';
update sys_menu set enabled=false where name='机构调整';
update sys_menu set enabled=false where name='职位管理';
update sys_menu set enabled=false where name='公告列表';
update sys_menu set enabled=false where name='私信列表';
update sys_menu set enabled=false where name='招聘信息';
update sys_menu set enabled=false where name='终端统计';
update sys_menu set enabled=false where name='抽奖管理';
update sys_menu set enabled=false where name='上报报表';
update sys_menu set enabled=false where name='串码查询';
update sys_menu set enabled=false where name='应收报表报错';
update sys_menu set enabled=false where name='调价提成';
update sys_menu set enabled=false where name='货品订货报错';
update sys_menu set enabled=false where name='库存盘点';
update sys_menu set enabled=false where name='售后列表';
update sys_menu set enabled=false where name='垫机录入';
update sys_menu set enabled=false where name='售后盘点';
update sys_menu set enabled=false where name='售后调拨';
update sys_menu set enabled=false where name='调拨列表';
update sys_menu set enabled=false where name='保卡统计';
update sys_menu set enabled=false where name='报表中心';
update sys_menu set enabled=false where name='OPPO电子保卡';
update sys_menu set enabled=false where name='OPPO颜色编码';
update sys_menu set enabled=false where name='OPPO发货串码';
update sys_menu set enabled=false where name='考勤查询';
update sys_menu set enabled=false where name='积分查询';
update sys_menu set enabled=false where name='费用报销';
update sys_menu set enabled=false where name='员工报表';
update sys_menu set enabled=false where name='抽奖管理';
update sys_menu set enabled=false where name='仓库调整';
update sys_menu set enabled=false where name='门店报表';
update sys_menu set enabled=false where name='坏机返厂';
update sys_menu set enabled=false where name='好机返库';



update sys_menu_category set enabled=false where name='客服中心';
update sys_menu_category set enabled=false where name='数据管理';
update sys_menu_category set enabled=false where name='工资管理';
update sys_menu_category set enabled=false where name='移动商城';
update sys_menu_category set enabled=false where name='任务政策';
update sys_menu_category set enabled=false where name='提成基准';
update sys_menu_category set enabled=false where name='K3Cloud';
update sys_menu_category set enabled=false where name='报表中心';

update sys_menu set mobile=FALSE;
update sys_menu set mobile=TRUE where name in ("每日排名","仓库管理","广告申请","串码核销","请假申请","签到列表","加班申请","调休申请","免打考勤","出差申请");

INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('内销部文员',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'内销部文员');
INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('内销部主管',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'内销部主管');
INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('仓储部文员',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'仓储部文员');
INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('仓储部主管',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'仓储部主管');
INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('导购',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'导购');
INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('业务',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'业务');
INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('财务部文员',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'财务部文员');
INSERT into sys_role(name,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,permission)VALUES('财务部主管',1,'2017-06-30 12:22:34',1,'2017-06-30 12:22:34','',0,0,1,'财务部主管');


update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='市县导购';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='流动导购';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='乡镇导购';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='返聘导购';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='体专店员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='体专店长';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='代理店长';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='体专专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='体专主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='导购') where t1.name='体专终端';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='产品经理';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='阿米巴巴长';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='联电经理';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='市县业务主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='乡镇业务主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='培训主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='终端主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='推广主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='市县督导';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='培训专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='终端专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='助理培训师';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='推广专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='乡镇业务';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='乡镇组长';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='乡镇培训专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='乡镇督导';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='别动队员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='O2O专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='运营商专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where t1.name='办事处司机';

update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='内销部文员') where t1.name='内销部专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='内销部主管') where t1.name='内销部主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='内销部主管') where t1.name='内销部部长';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='仓储部文员') where t1.name='仓储部专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='仓储部主管') where t1.name='仓储部主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='财务部文员') where t1.name='办事处财务';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='财务部文员') where t1.name='财务部专员';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='财务部文员') where t1.name='省公司财务出纳';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='财务部主管') where t1.name='财务部主管';
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='财务部主管') where t1.name='财务部部长';

update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='业务') where (t1.role_id is NULL OR t1.role_id=1);
DELETE FROM sys_role where id=7;
update hr_position t1 set t1.role_id=(select t2.id from sys_role t2 where t2.name='管理员') where (t1.name="信息部专员");

INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:depotShop:view','crm:depotShop:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'208','/api/ws/future/basic/depotShop','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:depotShop:businessEdit','crm:depotShop:businessEdit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'208','/api/ws/future/basic/depotShop/saveDepot','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:depotShop:delete','crm:depotShop:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'208','/api/ws/future/basic/depotShop/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:depotShop:basicEdit','crm:depotShop:basicEdit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'208','/api/ws/future/basic/depotShop/save','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:goodsOrderShip:view','crm:goodsOrderShip:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'169','/api/ws/future/crm/goodsOrderShip','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:goodsOrderShip:edit','crm:goodsOrderShip:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'169','/api/ws/future/crm/goodsOrderShip/save','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:goodsOrderShip:delete','crm:goodsOrderShip:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'169','/api/ws/future/crm/goodsOrderShip/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleArea:view','crm:afterSaleArea:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'206','/api/ws/future/crm/afterSaleArea','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleArea:edit','crm:afterSaleArea:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'206','/api/ws/future/crm/afterSaleArea/save','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleArea:delete','crm:afterSaleArea:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'206','/api/ws/future/crm/afterSaleArea/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleHead:view','crm:afterSaleHead:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'207','/api/ws/future/crm/afterSaleHead','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleHead:edit','crm:afterSaleHead:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'207','/api/ws/future/crm/afterSaleHead/save','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleHead:delete','crm:afterSaleHead:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'207','/api/ws/future/crm/afterSaleHead/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleCompany:view','crm:afterSaleCompany:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'218','/api/ws/future/crm/afterSaleCompany','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleCompany:edit','crm:afterSaleCompany:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'218','/api/ws/future/crm/afterSaleCompany/save','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:afterSaleCompany:delete','crm:afterSaleCompany:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'218','/api/ws/future/crm/afterSaleCompany/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('crm:storeInventoryReport:view','crm:storeInventoryReport:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'219','/api/ws/future/basic/depotStore/storeReport','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:backendModule:view','sys:backendModule:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'176','/api/basic/sys/backendModule','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:backendModule:edit','sys:backendModule:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'176','/api/basic/sys/backendModule/save','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:backendModule:delete','sys:backendModule:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'176','/api/basic/sys/backendModule/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:role:view','sys:role:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'183','/api/basic/sys/role','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:role:edit','sys:role:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'183','/api/basic/sys/role/save','PSOT');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:rolw:delete','sys:rolw:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'183','/api/basic/sys/role/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:backend:view','sys:backend:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'177','/api/basic/sys/backend','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:backend:edit','sys:backend:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'177','/api/basic/sys/backend/save','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:backend:delete','sys:backend:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'177','/api/basic/sys/backend/delete','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:officeRule:view','sys:officeRule:view',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'178','/api/basic/sys/officeRule','GET');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:officeRule:edit','sys:officeRule:edit',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'178','/api/basic/sys/officeRule/delete','POST');
INSERT into sys_permission(name,permission,created_by,created_date,last_modified_by,last_modified_date,remarks,version,locked,enabled,menu_id,url,method)VALUES('sys:officeRule:delete','sys:officeRule:delete',1,'2017-07-01 12:21:34',1,'2017-07-01 12:21:34','',0,0,1,'178','/api/basic/sys/officeRule/save','GET');

INSERT INTO `sys_role_module` VALUES ('1', '25', '3', '1', '2017-06-29 10:56:31', '1', '2017-06-29 10:56:31', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('2', '25', '4', '1', '2017-06-29 10:56:31', '1', '2017-06-29 10:56:31', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('3', '24', '3', '1', '2017-06-29 10:56:52', '1', '2017-06-29 10:56:52', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('4', '24', '4', '1', '2017-06-29 10:56:52', '1', '2017-06-29 10:56:52', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('5', '21', '4', '1', '2017-06-29 10:57:00', '1', '2017-06-29 10:57:00', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('6', '20', '4', '1', '2017-06-29 10:57:07', '1', '2017-06-29 10:57:07', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('7', '19', '3', '1', '2017-06-29 10:57:18', '1', '2017-06-29 10:57:18', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('8', '19', '4', '1', '2017-06-29 10:57:18', '1', '2017-06-29 10:57:18', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('9', '18', '3', '1', '2017-06-29 10:57:29', '1', '2017-06-29 10:57:29', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('10', '18', '4', '1', '2017-06-29 10:57:29', '1', '2017-06-29 10:57:29', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('11', '1', '1', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('12', '1', '2', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('13', '1', '3', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('14', '1', '4', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '0');
INSERT INTO `sys_role_module` VALUES ('15', '1', '5', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('16', '1', '6', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('17', '1', '7', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('18', '1', '8', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('19', '1', '9', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('20', '1', '10', '1', '2017-06-29 10:57:34', '1', '2017-06-29 10:57:34', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('21', '25', '7', '1', '2017-06-29 10:57:44', '1', '2017-06-29 10:57:44', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('22', '25', '9', '1', '2017-06-29 10:57:44', '1', '2017-06-29 10:57:44', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('23', '25', '10', '1', '2017-06-29 10:57:44', '1', '2017-06-29 10:57:44', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('24', '24', '7', '1', '2017-06-29 10:57:52', '1', '2017-06-29 10:57:52', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('25', '24', '9', '1', '2017-06-29 10:57:52', '1', '2017-06-29 10:57:52', '', '0', '0', '1');
INSERT INTO `sys_role_module` VALUES ('26', '24', '10', '1', '2017-06-29 10:57:52', '1', '2017-06-29 10:57:52', '', '0', '0', '1');





