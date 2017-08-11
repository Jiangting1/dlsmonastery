const employeeList  = r => require.ensure([], () => r(require('pages/basic/hr/employeeList.vue')));
const employeeForm  = r => require.ensure([], () => r(require('pages/basic/hr/employeeForm.vue')));
const employeeEditForm  = r => require.ensure([], () => r(require('pages/basic/hr/employeeEditForm.vue')));
const accountList  = r => require.ensure([], () => r(require('pages/basic/hr/accountList.vue')));
const accountForm  = r => require.ensure([], () => r(require('pages/basic/hr/accountForm.vue')))
const accountPositionForm  = r => require.ensure([], () => r(require('pages/basic/hr/accountPositionForm.vue')));

const accountChangeList  = r => require.ensure([], () => r(require('pages/basic/hr/accountChangeList.vue')));
const accountChangeForm  = r => require.ensure([], () => r(require('pages/basic/hr/accountChangeForm.vue')));
const accountChangeBatchForm  = r => require.ensure([], () => r(require('pages/basic/hr/accountChangeBatchForm.vue')));
const positionList = r => require.ensure([],() => r(require('pages/basic/hr/positionList.vue')));
const positionForm = r => require.ensure([],() => r(require('pages/basic/hr/positionForm.vue')));
const dutySignList = r => require.ensure([],() => r(require('pages/basic/hr/dutySignList.vue')));
const dutyAnnualList = r => require.ensure([],() => r(require('pages/basic/hr/dutyAnnualList.vue')));
const dutyAnnualForm = r => require.ensure([],() => r(require('pages/basic/hr/dutyAnnualForm.vue')));
const dutyWorktimeList = r => require.ensure([],() => r(require('pages/basic/hr/dutyWorktimeList.vue')));
const dutyWorktimeForm = r => require.ensure([],() => r(require('pages/basic/hr/dutyWorktimeForm.vue')));
const dutyTaskList = r => require.ensure([],() => r(require('pages/basic/hr/dutyTaskList.vue')));
const dutyTaskForm = r => require.ensure([],() => r(require('pages/basic/hr/dutyTaskForm.vue')));


const recruitList = r => require.ensure([],() => r(require('pages/basic/hr/recruitList.vue')));
const recruitForm = r => require.ensure([],() => r(require('pages/basic/hr/recruitForm.vue')));
const recruitEnumList = r => require.ensure([],() => r(require('pages/basic/hr/recruitEnumList.vue')));
const recruitEnumForm = r => require.ensure([],() => r(require('pages/basic/hr/recruitEnumForm.vue')));
const recruitBatchForm = r => require.ensure([],() => r(require('pages/basic/hr/recruitBatchForm.vue')));
const dutyLeaveList= r => require.ensure([], () => r(require('pages/basic/hr/dutyLeaveList.vue')));
const dutyFreeList= r => require.ensure([], () => r(require('pages/basic/hr/dutyFreeList.vue')));
const dutyTripList= r => require.ensure([], () => r(require('pages/basic/hr/dutyTripList.vue')));
const dutyRestList= r => require.ensure([], () => r(require('pages/basic/hr/dutyRestList.vue')));
const dutyPublicFreeList= r => require.ensure([], () => r(require('pages/basic/hr/dutyPublicFreeList.vue')));
const dutyOvertimeList= r => require.ensure([], () => r(require('pages/basic/hr/dutyOvertimeList.vue')));
const auditFileList= r => require.ensure([], () => r(require('pages/basic/hr/auditFileList.vue')));
const auditFileForm= r => require.ensure([], () => r(require('pages/basic/hr/auditFileForm.vue')));
const auditFileDetail= r => require.ensure([], () => r(require('pages/basic/hr/auditFileDetail.vue')));
const auditFilePrint= r => require.ensure([], () => r(require('pages/basic/hr/auditFilePrint.vue')));
const accountTaskList= r => require.ensure([], () => r(require('pages/basic/hr/accountTaskList.vue')));
const accountAuthorityForm= r => require.ensure([], () => r(require('pages/basic/hr/accountAuthorityForm.vue')));

const unitsOfficeForm= r => require.ensure([], () => r(require('pages/basic/hr/unitsOfficeForm.vue')));
const batchUnitsForm= r => require.ensure([], () => r(require('pages/basic/hr/batchUnitsForm.vue')));
const accountWeixinList= r => require.ensure([], () => r(require('pages/basic/hr/accountWeixinList.vue')));




let routes = [
  {path:'/basic/hr/dutyLeaveList',component:dutyLeaveList,name:'dutyLeaveList'},
  {path:'/basic/hr/dutyTripList',component:dutyTripList,name:'dutyTripList'},
  {path:'/basic/hr/dutyOvertimeList',component:dutyOvertimeList,name:'dutyOvertimeList'},
  {path:'/basic/hr/dutyRestList',component:dutyRestList,name:'dutyRestList'},
  {path:'/basic/hr/dutyFreeList',component:dutyFreeList,name:'dutyFreeList'},
  {path:'/basic/hr/dutyPublicFreeList',component:dutyPublicFreeList,name:'dutyPublicFreeList'},
  {path:'/basic/hr/accountList',component:accountList,name:'accountList'},

  {path:'/basic/hr/accountPositionForm',component:accountPositionForm,name:'accountPositionForm',meta:{menu:"accountList",keepAlive:true}},
  {path:'/basic/hr/accountForm',component:accountForm,name:'accountForm',meta:{menu:"accountList",keepAlive:true}},
  {path:'/basic/hr/accountChangeList',component:accountChangeList,name:'accountChangeList'},
  {path:'/basic/hr/accountChangeForm',component:accountChangeForm,name:'accountChangeForm',meta:{menu:"accountChangeList",keepAlive:false}},
  {path:'/basic/hr/accountChangeBatchForm',component:accountChangeBatchForm,name:'accountChangeBatchForm',meta:{menu:"accountChangeList",keepAlive:false}},
  {path:'/basic/hr/employeeList',component:employeeList,name:'employeeList'},
  {path:'/basic/hr/employeeForm',component:employeeForm,name:'employeeForm',meta: {menu:"employeeList",keepAlive:true}},
  {path:'/basic/hr/employeeEditForm',component:employeeEditForm,name:'employeeEditForm',meta: {menu:"employeeList",keepAlive:true}},
  {path:'/basic/hr/positionList',component:positionList,name:'positionList'},
  {path:'/basic/hr/positionForm',component:positionForm,name:'positionForm',meta: {menu:"positionList",keepAlive:true}},
  {path:'/basic/hr/dutySignList',component:dutySignList,name:'dutySignList'},
  {path:'/basic/hr/dutyAnnualList',component:dutyAnnualList,name:'dutyAnnualList'},
  {path:'/basic/hr/dutyAnnualForm',component:dutyAnnualForm,name:'dutyAnnualForm',meta:{menu:"dutyAnnualList",keepAlive:true}},
  {path:'/basic/hr/dutyWorktimeList',component:dutyWorktimeList,name:'dutyWorktimeList'},
  {path:'/basic/hr/dutyWorktimeForm',component:dutyWorktimeForm,name:'dutyWorktimeForm',meta: {menu:"dutyWorktimeList",keepAlive:true}},
  {path:'/basic/hr/dutyTaskList',component:dutyTaskList,name:'dutyTaskList'},
  {path:'/basic/hr/dutyTaskForm',component:dutyTaskForm,name:'dutyTaskForm',meta: {menu:"dutyTaskList",keepAlive:true}},
  {path:'/basic/hr/auditFileList',component:auditFileList,name:'auditFileList'},
  {path:'/basic/hr/auditFileForm',component:auditFileForm,name:'auditFileForm',meta: {menu:"auditFileList",keepAlive:true}},
  {path:'/basic/hr/auditFileDetail',component:auditFileDetail,name:'auditFileDetail',meta: {menu:"auditFileList"}},
  {path:'/basic/hr/auditFilePrint',component:auditFilePrint,name:'auditFilePrint',meta:{hidden:true}},


  {path:'/basic/hr/accountTaskList',component:accountTaskList,name:'accountTaskList'},
  {path:'/basic/hr/recruitList',component:recruitList,name:'recruitList'},
  {path:'/basic/hr/recruitForm',component:recruitForm,name:'recruitForm',meta: {menu:"recruitList",keepAlive:true}},
  {path:'/basic/hr/recruitEnumList',component:recruitEnumList,name:'recruitEnumList'},
  {path:'/basic/hr/recruitEnumForm',component:recruitEnumForm,name:'recruitEnumForm',meta: {menu:"recruitEnumList",keepAlive:true}},
  {path:'/basic/hr/recruitBatchForm',component:recruitBatchForm,name:'recruitBatchForm',meta: {menu:"recruitList",keepAlive:true}},
  {path:'/basic/hr/accountAuthorityForm',component:accountAuthorityForm,name:'accountAuthorityForm',meta: {menu:"accountList",keepAlive:true}},

  {path:'/basic/hr/unitsOfficeForm',component:unitsOfficeForm,name:'unitsOfficeForm'},
  {path:'/basic/hr/batchUnitsForm',component:batchUnitsForm,name:'batchUnitsForm'},
  {path:'/basic/hr/accountWeixinList',component:accountWeixinList,name:'accountWeixinList'},



];

export default routes;
