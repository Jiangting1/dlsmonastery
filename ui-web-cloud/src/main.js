import Vue from 'vue';
import VueI18n from 'vue-i18n';
import VueRouter from 'vue-router';
import axios from 'axios'
import qs from 'qs'
import _ from 'lodash'
import store from './store/'

import ElementUI from 'element-ui';
import zhElement from 'element-ui/lib/locale/lang/zh-CN'
import idElement from 'element-ui/lib/locale/lang/id'
import 'element-ui/lib/theme-default/index.css';
import VueQuillEditor from 'vue-quill-editor'
import VueProgressBar from 'vue-progressbar'

import pageable from './components/pageable';
import headTab from './components/head-tab';
import searchTag from './components/search-tag';

import App from './app.vue';
import fullCalendar from 'vue-fullcalendar'
import routes from './routes';
import './style.scss';
import './filters'
import util from "./utils/util"
import zhLocale from "./utils/locales/zh-CN"
import idLocale from "./utils/locales/id"
window.qs = qs;
window._=_;
window.util=util;
window.enumMap = null;

Vue.use(VueI18n);
Vue.use(VueRouter);
Vue.use(ElementUI);
Vue.use(VueQuillEditor);

// register dashboard components
Vue.component('pageable', pageable);
Vue.component('full-calendar', fullCalendar);
Vue.component('head-tab', headTab);
Vue.component('search-tag', searchTag);

// progressBar
const options = {
  color: 'rgb(143, 255, 199)',
  failedColor: 'red',
  thickness: '3px'
}

Vue.use(VueProgressBar, options)

// set locales
Vue.locale('zh-cn', util.mergeJsonObject(zhElement,zhLocale));
Vue.locale("id",util.mergeJsonObject(idElement,idLocale));

Vue.directive('permit', function (el, binding) {
  var  hasPermit=false;
  if(binding.value){
    hasPermit= util.isPermit(binding.value);
  }
  if(!hasPermit){
    el.style.display="none"
  }else {
    el.style.display=""
  }
})
Vue.config.errorHandler = function (err, vm) {
  vm.$notify.error({
    title: '系统异常',
    message: err+""
  });
  console.error(err);
}

export const router = new VueRouter({
  routes,
  mode: 'hash',
  linkActiveClass: 'active'
});

router.beforeEach((to, from, next) => {
  router.app.$Progress.start()
  //检查是否已经登陆
  if (to.matched.some(record => record.meta.requiresAuth==false)) {
    next();
  } else {
    if(checkLogin()) {
      util.setQuery(to.name, to.query);
      next();
    } else {
      router.push("login");
    }
  }
});

router.afterEach(route => {
  window.scrollTo(0, 0)
  router.app.$Progress.finish()
})


axios.interceptors.request.use(function (config) {
  if(config.url !='/api/uaa/oauth/token') {
    if(checkLogin()) {
      config.headers['Authorization'] = 'Bearer ' + store.state.global.token.access_token;
    } else {
      router.push("login");
    }
  }
  return config;
}, function (error) {
  // Do something with request error
  return Promise.reject(error);
});


window.axios = axios;

window.checkLogin = function () {
  var token = store.state.global.token;
  if(token==null || util.isBlank(token.access_token)) {
    return false;
  } else {
    var expDate = new Date(token.exp + token.expires_in*1000);
    var distance = expDate.getTime() - new Date().getTime();
    if(distance<60*1000) {
      return false;
    }
    //当前需要刷新token
    if(distance < token.expires_in*500 && !store.state.isRefreshToken) {
      store.dispatch('setIsRefreshToken',true);
      let data = {
        grant_type:'refresh_token',
        refresh_token:token.refresh_token
      };
      axios.post('/api/uaa/oauth/token',qs.stringify(data)).then((response)=>{
        store.dispatch('setToken',response.data);
        store.dispatch('setIsRefreshToken',false);
      });
    }
    return true;
  }
}


new Vue({
  render: h => h(App),
  store,
  router
}).$mount('#app');
