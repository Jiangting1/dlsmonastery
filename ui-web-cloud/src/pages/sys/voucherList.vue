<template>
  <div>
    <head-tab active="voucherList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus" >添加</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search">过滤</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" title="过滤" v-model="formVisible" size="tiny"  class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" :label-width="formLabelWidth">
          <el-row :gutter="4">
            <el-form-item label="编号" >
              <el-input v-model="formData.id"  placeholder="模糊匹配查询"></el-input>
            </el-form-item>
          </el-row>
          <el-row :gutter="4">
            <el-form-item label="凭证日期" >
              <date-picker v-model="formData.fdate"></date-picker>
            </el-form-item>
          </el-row>
          <el-row :gutter="4">
            <el-form-item label="状态" >
              <el-select v-model="formData.status" filterable clearable placeholder="请选择">
                <el-option v-for="status in formData.extra.statusList" :key="status" :label="status" :value="status"></el-option>
              </el-select>
            </el-form-item>
          </el-row>
          <el-row :gutter="4">
            <el-form-item label="创建人">
              <el-select v-model="formData.createdBy" filterable remote placeholder="模糊匹配查询" :remote-method="remoteAccount" :loading="remoteLoading">
                <el-option v-for="item in accountCommonList" :key="item.id" :label="item.loginName" :value="item.id"></el-option>
              </el-select>
            </el-form-item>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">搜索</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" element-loading-text="拼命加载中....." @sort-change="sortChange" stripe border>
        <el-table-column fixed prop="id" label="编号" sortable width="120"></el-table-column>
        <el-table-column prop="fdate" label="凭证日期"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template scope="scope">
            <el-tag :type="scope.row.status === '省公司财务审核' ? 'success' : 'primary'" close-transition>{{scope.row.status}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="outCode" label="外部编码"></el-table-column>
        <el-table-column prop="createdByName" label="创建人"></el-table-column>
        <el-table-column prop="createdDate" label="创建时间"></el-table-column>
        <el-table-column prop="remarks" label="备注"></el-table-column>
        <el-table-column fixed="right" label="操作" width="300">
          <template scope="scope">
            <el-button size="small" @click.native="itemAction(scope.row.id,'detail')">详细</el-button>
            <el-button size="small" @click.native="itemAction(scope.row.id,'export')">导出</el-button>
            <el-button size="small" v-if="scope.row.status !== '已完成' " @click.native="itemAction(scope.row.id,'edit')">修改</el-button>
            <el-button size="small" type="danger" v-if="scope.row.status !== '已完成' " @click.native="itemAction(scope.row.id,'delete')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  export default {
    data() {
      return {
        page:{},
        searchText:"",
        formData:{
          extra:{}
        },
        accountCommonList:{},
        initPromise:{},
        formLabelWidth: '25%',
        formVisible: false,
        pageLoading: false,
        remoteLoading:false
      };
    },
    methods: {
      setSearchText() {
        this.$nextTick(function () {
          this.searchText = util.getSearchText(this.$refs.searchDialog);
        })
      },
      pageRequest() {
        this.pageLoading = true;
        this.setSearchText();
        let submitData = util.deleteExtra(this.formData);
        axios.get('/api/global/cloud/sys/voucher?'+qs.stringify(submitData)).then((response) => {
          this.page = response.data;
          this.pageLoading = false;
        })
      },pageChange(pageNumber,pageSize) {
        this.formData.page = pageNumber;
        this.formData.size = pageSize;
        this.pageRequest();
      },sortChange(column) {
        this.formData.sort=util.getSort(column);
        this.formData.page=0;
        this.pageRequest();
      },search() {
        this.formVisible = false;
        this.pageRequest();
      },itemAdd(){
        this.$router.push({ name: 'voucherForm'})
      },itemAction:function(id,action){
        if(action === "detail") {
          this.$router.push({ name: 'voucherDetail', query: { id: id }})
        }else if(action === "edit") {
          this.$router.push({ name: 'voucherForm', query: { id: id }})
        }else if(action === "delete") {
          util.confirmBeforeDelRecord(this).then(() => {
            axios.get('/api/global/cloud/sys/voucher/delete',{params:{id:id}}).then((response) =>{
              if(response.data.success){
                this.$message(response.data.message);
              }else{
                this.$alert(response.data.message);
              }
              this.pageRequest();
            });
          }).catch(()=>{});
        }else if(action === "export"){
          window.location.href = '/api/global/cloud/sys/voucher/export?id='+id;
        }
      },remoteAccount(query) {
        if (query) {
          this.remoteLoading = true;
          axios.get('/api/basic/hr/account/findByLoginNameLike',{params:{loginName:query}}).then((response)=>{
            this.accountCommonList = response.data;
            this.remoteLoading = false;
          })
        } else {
          this.accountCommonList = {};
        }
      },
    },created () {
      let that = this;
      that.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/global/cloud/sys/voucher/getQuery').then((response) =>{
        that.formData=response.data;
        util.copyValue(that.$route.query,that.formData);
        that.pageRequest();
      });
    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    }
  };
</script>

