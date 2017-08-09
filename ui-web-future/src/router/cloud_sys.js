const kingdeeBookList  = r => require.ensure([], () => r(require('pages/cloud/sys/kingdeeBookList.vue')));
const kingdeeBookForm  = r => require.ensure([], () => r(require('pages/cloud/sys/kingdeeBookForm.vue')));
const accountKingdeeBookList  = r => require.ensure([], () => r(require('pages/cloud/sys/accountKingdeeBookList.vue')));
const accountKingdeeBookForm  = r => require.ensure([], () => r(require('pages/cloud/sys/accountKingdeeBookForm.vue')));
const currentKingdeeBookForm  = r => require.ensure([], () => r(require('pages/cloud/sys/currentKingdeeBookForm.vue')));
const materialPriceManager  = r => require.ensure([], () => r(require('pages/cloud/sys/materialPriceManager.vue')));
const voucherList  = r => require.ensure([], () => r(require('pages/cloud/sys/voucherList.vue')));
const voucherForm  = r => require.ensure([], () => r(require('pages/cloud/sys/voucherForm.vue')));
const voucherDetail  = r => require.ensure([], () => r(require('pages/cloud/sys/voucherDetail.vue')));
const kingdeeSynList  = r => require.ensure([], () => r(require('pages/cloud/sys/kingdeeSynList.vue')));
const kingdeeSynDetail  = r => require.ensure([], () => r(require('pages/cloud/sys/kingdeeSynDetail.vue')));
const kingdeeSynForNoPushList  = r => require.ensure([], () => r(require('pages/cloud/sys/kingdeeSynForNoPushList.vue')));

let routes = [
  {path: '/cloud/sys/kingdeeBookList',component: kingdeeBookList,name: 'kingdeeBookList'},
  {path: '/cloud/sys/kingdeeBookForm',component: kingdeeBookForm,name: 'kingdeeBookForm',meta:{menu:"kingdeeBookList",keepAlive:true}},
  {path: '/cloud/sys/accountKingdeeBookList',component: accountKingdeeBookList,name: 'accountKingdeeBookList'},
  {path: '/cloud/sys/accountKingdeeBookForm',component: accountKingdeeBookForm,name: 'accountKingdeeBookForm',meta:{menu:"accountKingdeeBookList"}},
  {path: '/cloud/sys/currentKingdeeBookForm',component: currentKingdeeBookForm,name: 'currentKingdeeBookForm'},
  {path: '/cloud/sys/materialPriceManager',component: materialPriceManager,name: 'materialPriceManager'},
  {path: '/cloud/sys/voucherList',component: voucherList,name: 'voucherList'},
  {path: '/cloud/sys/voucherForm',component: voucherForm,name: 'voucherForm',meta:{menu:"voucherList"}},
  {path: '/cloud/sys/voucherDetail',component: voucherDetail,name: 'voucherDetail',meta:{menu:"voucherList"}},
  {path: '/cloud/sys/kingdeeSynList',component: kingdeeSynList,name: 'kingdeeSynList'},
  {path: '/cloud/sys/kingdeeSynDetail',component: kingdeeSynDetail,name: 'kingdeeSynDetail',meta:{menu:"kingdeeSynList"}},
  {path: '/cloud/sys/kingdeeSynForNoPushList',component: kingdeeSynForNoPushList,name: 'kingdeeSynForNoPushList'},
];

export default routes;
