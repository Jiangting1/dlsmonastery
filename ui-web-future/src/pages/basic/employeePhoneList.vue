<template>
  <div>
    <head-tab active="employeePhoneList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus" v-permit="'crm:employeePhoneDeposit:edit'">{{$t('employeePhoneList.employeePhoneDepositList')}}</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search" v-permit="'crm:employeePhone:view'">{{$t('employeePhoneList.filter')}}</el-button>
        <el-button type="primary" @click="exportData" icon="upload" v-permit="'crm:employeePhone:view'">{{$t('employeePhoneList.export')}}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('employeePhoneList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" :label-width="formLabelWidth">
          <el-row :gutter="4">
            <el-col :span="24">
              <el-form-item :label="$t('employeePhoneList.depotName')" >
                <el-input v-model="formData.depotName" auto-complete="off" :placeholder="$t('employeePhoneList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('employeePhoneList.employeeName')">
                <employee-select v-model="formData.employeeId"  @afterInit="setSearchText"></employee-select>
              </el-form-item>
              <el-form-item :label="$t('employeePhoneList.status')">
                <el-select v-model="formData.status" filterable clearable :placeholder="$t('employeePhoneList.selectStatus')">
                  <el-option  v-for="item in formData.extra.statusList" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('employeePhoneList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('employeePhoneList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column fixed prop="areaName" :label="$t('employeePhoneList.areaName')"  width="150"></el-table-column>
        <el-table-column prop="depotName" :label="$t('employeePhoneList.depotName')"></el-table-column>
        <el-table-column prop="employeeName" :label="$t('employeePhoneList.employeeName')" width="100"></el-table-column>
        <el-table-column prop="depositAmount" :label="$t('employeePhoneList.depositAmount')"  width="100"></el-table-column>
        <el-table-column prop="status" :label="$t('employeePhoneList.status')"  width="100"></el-table-column>
        <el-table-column prop="uploadTime" :label="$t('employeePhoneList.uploadTime')"></el-table-column>
        <el-table-column prop="productName" :label="$t('employeePhoneList.productName')" width="120"></el-table-column>
        <el-table-column prop="jobPrice" :label="$t('employeePhoneList.jobPrice')" ></el-table-column>
        <el-table-column prop="imeStr" :label="$t('employeePhoneList.imeStr')" width="120"></el-table-column>
        <el-table-column prop="retailPrice" :label="$t('employeePhoneList.retailPrice')"></el-table-column>
        <el-table-column prop="createdDate" :label="$t('employeePhoneList.createdDate')" width="120"></el-table-column>
        <el-table-column prop="createdByName" :label="$t('employeePhoneList.createdBy')"></el-table-column>
        <el-table-column prop="remarks" label="备注"></el-table-column>
        <el-table-column fixed="right" :label="$t('employeePhoneList.operation')" width="140">
          <template scope="scope">
            <el-button size="small" @click.native="itemAction(scope.row.id,'edit')" v-permit="'crm:employeePhone:edit'">修改</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  import employeeSelect from 'components/basic/employee-select'
  export default {
    components:{employeeSelect},
    data() {
      return {
        page:{},
        formData:{
          extra:{}
        },
        initPromise:{},
        searchText:"",
        formLabelWidth: '28%',
        formVisible: false,
        pageLoading: false
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
        axios.get('/api/ws/future/basic/employeePhone?'+qs.stringify(submitData)).then((response) => {
          this.page = response.data;
          this.pageLoading = false;
        })
      },pageChange(pageNumber,pageSize) {
        this.formData.page = pageNumber;
        this.formData.size = pageSize;
        this.pageRequest();
      },sortChange(column) {
        this.formData.order=util.getSort(column);
        this.formData.page=0;
        this.pageRequest();
      },search() {
        this.formVisible = false;
        this.pageRequest();
      },itemAdd(){
        this.$router.push({ name: 'employeePhoneDepositList'})
      },itemAction:function(id,action){
        if(action=="edit") {
          this.$router.push({ name: 'employeePhoneForm', query: { id: id }})
        }
      },exportData(){
        util.confirmBeforeExportData(this).then(() => {
          window.location.href="/api/ws/future/basic/employeePhone/export?"+qs.stringify(util.deleteExtra(this.formData));
        }).catch(()=>{});
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.initPromise=axios.get('/api/ws/future/basic/employeePhone/getQuery').then((response) =>{
        this.formData=response.data;
      });
    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    }
  };
</script>

