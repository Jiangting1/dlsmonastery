const dictEnumList  = r => require.ensure([], () => r(require('pages/basic/sys/dictEnumList.vue')));
const dictEnumForm  = r => require.ensure([], () => r(require('pages/basic/sys/dictEnumForm.vue')));
const dictMapList  = r => require.ensure([], () => r(require('pages/basic/sys/dictMapList.vue')));
const dictMapForm  = r => require.ensure([], () => r(require('pages/basic/sys/dictMapForm.vue')));
const companyConfigList  = r => require.ensure([], () => r(require('pages/basic/sys/companyConfigList.vue')));
const companyConfigForm  = r => require.ensure([], () => r(require('pages/basic/sys/companyConfigForm.vue')));
const menuCategoryList  = r => require.ensure([], () => r(require('pages/basic/sys/menuCategoryList.vue')));
const menuCategoryForm  = r => require.ensure([], () => r(require('pages/basic/sys/menuCategoryForm.vue')));
const menuList  = r => require.ensure([], () => r(require('pages/basic/sys/menuList.vue')));
const menuForm  = r => require.ensure([], () => r(require('pages/basic/sys/menuForm.vue')));
const folderList  = r => require.ensure([], () => r(require('pages/basic/sys/folderList.vue')));
const folderForm  = r => require.ensure([], () => r(require('pages/basic/sys/folderForm.vue')));
const folderFileList  = r => require.ensure([], () => r(require('pages/basic/sys/folderFileList.vue')));
const processTypeList  = r => require.ensure([], () => r(require('pages/basic/sys/processTypeList.vue')));
const processTypeForm  = r => require.ensure([], () => r(require('pages/basic/sys/processTypeForm.vue')));
const permissionList  = r => require.ensure([], () => r(require('pages/basic/sys/permissionList.vue')));
const permissionForm  = r => require.ensure([], () => r(require('pages/basic/sys/permissionForm.vue')));
const processList= r => require.ensure([], () => r(require('pages/basic/sys/processList.vue')));
const backendList  = r => require.ensure([], () => r(require('pages/basic/sys/backendList.vue')));
const backendForm= r => require.ensure([], () => r(require('pages/basic/sys/backendForm.vue')));
const backendModuleList  = r => require.ensure([], () => r(require('pages/basic/sys/backendModuleList.vue')));
const backendModuleForm= r => require.ensure([], () => r(require('pages/basic/sys/backendModuleForm.vue')));
const officeRuleList  = r => require.ensure([], () => r(require('pages/basic/sys/officeRuleList.vue')));
const officeRuleForm= r => require.ensure([], () => r(require('pages/basic/sys/officeRuleForm.vue')));
const officeList = r => require.ensure([],() => r(require('pages/basic/sys/officeList.vue')));
const officeForm = r => require.ensure([],() => r(require('pages/basic/sys/officeForm.vue')));
const officeBusinessForm = r => require.ensure([],() => r(require('pages/basic/sys/officeBusinessForm.vue')));
const officeChangeForm = r => require.ensure([],() => r(require('pages/basic/sys/officeChangeForm.vue')));
const roleList = r => require.ensure([],() => r(require('pages/basic/sys/roleList.vue')));
const roleForm = r => require.ensure([],() => r(require('pages/basic/sys/roleForm.vue')));
const roleAuthorityForm = r => require.ensure([],() => r(require('pages/basic/sys/roleAuthorityForm.vue')));


let routes = [
  {path:'/basic/sys/processList',component:processList,name:'processList'},
  {path:'/basic/sys/dictEnumList',component:dictEnumList,name:'dictEnumList',meta: {menu:"dictEnumList"}},
  {path:'/basic/sys/dictEnumForm',component:dictEnumForm,name:'dictEnumForm',meta: {menu:"dictEnumList",keepAlive:true}},
  {path:'/basic/sys/dictMapList',component:dictMapList,name:'dictMapList'},
  {path:'/basic/sys/dictMapForm',component:dictMapForm,name:'dictMapForm',meta: {menu:"dictMapList",keepAlive:true}},
  {path:'/basic/sys/companyConfigList',component:companyConfigList,name:'companyConfigList'},
  {path:'/basic/sys/companyConfigForm',component:companyConfigForm,name:'companyConfigForm',meta: {menu:"companyConfigList",keepAlive:true}},
  {path:'/basic/sys/menuCategoryList',component:menuCategoryList,name:'menuCategoryList'},
  {path:'/basic/sys/menuCategoryForm',component:menuCategoryForm,name:'menuCategoryForm',meta: {menu:"menuCategoryList",keepAlive:true}},
  {path:'/basic/sys/menuList',component:menuList,name:'menuList'},
  {path:'/basic/sys/menuForm',component:menuForm,name:'menuForm',meta: {menu:"menuList",keepAlive:true}},
  {path:'/basic/sys/folderList',component:folderList,name:'folderList'},
  {path:'/basic/sys/folderForm',component:folderForm,name:'folderForm',meta: {menu:"folderList",keepAlive:true}},
  {path:'/basic/sys/folderFileList',component:folderFileList,name:'folderFileList'},
  {path:'/basic/sys/processTypeList',component:processTypeList,name:'processTypeList'},
  {path:'/basic/sys/processTypeForm',component:processTypeForm,name:'processTypeForm',meta: {menu:"processTypeList",keepAlive:true}},
  {path:'/basic/sys/permissionList',component:permissionList,name:'permissionList'},
  {path:'/basic/sys/permissionForm',component:permissionForm,name:'permissionForm',meta: {menu:"permissionList",keepAlive:true}},
  {path:'/basic/sys/backendList',component:backendList,name:'backendList'},
  {path:'/basic/sys/backendForm',component:backendForm,name:'backendForm',meta: {menu:"backendList",keepAlive:true}},
  {path:'/basic/sys/backendModuleList',component:backendModuleList,name:'backendModuleList'},
  {path:'/basic/sys/backendModuleForm',component:backendModuleForm,name:'backendModuleForm',meta: {menu:"backendModuleList",keepAlive:true}},
  {path:'/basic/sys/officeRuleList',component:officeRuleList,name:'officeRuleList'},
  {path:'/basic/sys/officeRuleForm',component:officeRuleForm,name:'officeRuleForm',meta: {menu:"officeRuleList",keepAlive:true}},
  {path:'/basic/sys/officeList',component:officeList,name:'officeList'},
  {path:'/basic/sys/officeForm',component:officeForm,name:'officeForm',meta: {menu:"officeList",keepAlive:true}},
  {path:'/basic/sys/officeBusinessForm',component:officeBusinessForm,name:'officeBusinessForm',meta: {menu:"officeList",keepAlive:true}},
  {path:'/basic/sys/officeChangeForm',component:officeChangeForm,name:'officeChangeForm',meta: {menu:"officeList",keepAlive:true}},
  {path:'/basic/sys/roleList',component:roleList,name:'roleList'},
  {path:'/basic/sys/roleForm',component:roleForm,name:'roleForm',meta: {menu:"roleList",keepAlive:true}},
  {path:'/basic/sys/roleAuthorityForm',component:roleAuthorityForm,name:'roleAuthorityForm',meta: {menu:"roleList",keepAlive:true}},
];

export default routes;
