<template>
  <div>
    <head-tab active="expressOrderList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="formVisible = true" icon="search">{{ $t('expressOrderList.filter') }}</el-button>
        <el-button type="primary" @click="exportData">{{$t('expressOrderList.export')}}</el-button>
        <el-button type="primary" @click="exportEMSData">{{$t('expressOrderList.EMSExportData')}}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('expressOrderList.filter')" v-model="formVisible" size="medium" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" label-width="120px">
          <el-row :gutter="4">
            <el-col :span="12">
              <el-form-item :label="$t('expressOrderList.fromDepotName')">
                <depot-select v-model="formData.fromDepotIdList" category="store" multiple @afterInit="setSearchText"></depot-select>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.toDepotName')">
                <el-input v-model="formData.toDepotName" :placeholder="$t('expressOrderList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.expressCompanyName')">
                <el-input v-model="formData.expressCompanyName" :placeholder="$t('expressOrderList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.extendBusinessIdStart')">
                <el-input v-model="formData.extendBusinessIdStart" ></el-input>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.printDate')">
              <date-range-picker v-model="formData.printDateRange"></date-range-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('expressOrderList.extendType')">
                <el-select v-model="formData.extendType" clearable :placeholder="$t('expressOrderList.selectExtendType')">
                  <el-option v-for="item in formData.extra.extendTypeList" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.outPrint')">
                <bool-select v-model="formData.outPrint"></bool-select>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.expressPrint')">
                <bool-select v-model="formData.expressPrint"></bool-select>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.extendBusinessId')">
                <el-input v-model="formData.extendBusinessId" :placeholder="$t('expressOrderList.preciseSearch')"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('expressOrderList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('expressOrderList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column type="expand">
          <template scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item :label="$t('expressOrderList.expressCompanyName')">
                <span>{{ props.row.expressCompanyName }}</span>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.shouldGet')">
                <span>{{ props.row.shouldGet }}</span>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.shouldPay')">
                <span>{{ props.row.shouldPay }}</span>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.address')">
                <span>{{ props.row.address }}</span>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.expressPrintDate')">
                <span>{{ props.row.expressPrintDate }}</span>
              </el-form-item>
              <el-form-item :label="$t('expressOrderList.outPrintDate')">
                <span>{{ props.row.outPrintDate }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column prop="extendType" :label="$t('expressOrderList.extendType')"  width="150"></el-table-column>
        <el-table-column prop="extendBusinessId" :label="$t('expressOrderList.extendBusinessId')" sortable></el-table-column>
        <el-table-column prop="fromDepotName" column-key="fromDepotId" :label="$t('expressOrderList.fromDepotName')" sortable></el-table-column>
        <el-table-column prop="toDepotName" column-key="toDepotId" :label="$t('expressOrderList.toDepotName')" sortable></el-table-column>
        <el-table-column prop="realPay" :label="$t('expressOrderList.realPay')" sortable></el-table-column>
        <el-table-column prop="contator" :label="$t('expressOrderList.contact')" sortable></el-table-column>
        <el-table-column prop="mobilePhone" :label="$t('expressOrderList.mobilePhone')" sortable></el-table-column>
        <el-table-column prop="expressPrintQty" :label="$t('expressOrderList.expressPrintQty')" sortable></el-table-column>
        <el-table-column prop="createdDate" :label="$t('expressOrderList.createdDate')" sortable></el-table-column>
        <el-table-column prop="printDate" :label="$t('expressOrderList.printDate')" sortable></el-table-column>
        <el-table-column prop="remarks" :label="$t('expressOrderList.remarks')"></el-table-column>
        <el-table-column  :label="$t('expressOrderList.operation')" >
          <template scope="scope">
            <div class="action" v-permit="'crm:expressOrder:edit'"><el-button size="small" @click.native="itemAction(scope.row.id,'edit')">{{$t('expressOrderList.edit')}}</el-button></div>
            <div class="action" v-permit="'crm:expressOrder:edit'"><el-button size="small" @click.native="itemAction(scope.row.id,'reset')">{{$t('expressOrderList.reset')}}</el-button></div>
          </template>
        </el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<style>
  .demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
</style>
<script>
  import boolSelect from 'components/common/bool-select'
  import depotSelect from 'components/future/depot-select'

  export default{
    components:{
      boolSelect,
      depotSelect,
    },

    data() {
      return {
        pageLoading: false,
        pageHeight:600,
        page:{},
        searchText:"",
        formData:{
            extra:{}
        },
        initPromise:{},
        formVisible: false,
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
        axios.get('/api/ws/future/crm/expressOrder?'+qs.stringify(submitData)).then((response) => {
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
      },itemAction:function(id,action){
        if(action==="edit") {
          this.$router.push({ name: 'expressOrderForm', query: { id: id }})
        } else if(action==="reset") {
          util.confirmBeforeAction(this, this.$t('expressOrderList.confirmBeforeReset')).then(() => {
            axios.get('/api/ws/future/crm/expressOrder/resetPrintStatus',{params:{id:id}}).then((response) =>{
              this.$message(response.data.message);
              this.pageRequest();
            })
          }).catch(()=>{});
        }
      },exportData(){
        util.confirmBeforeExportData(this).then(() => {
          window.location.href='/api/ws/future/crm/expressOrder/export?'+qs.stringify(util.deleteExtra(this.formData));
        }).catch(()=>{});
      },exportEMSData(){
        util.confirmBeforeExportData(this).then(() => {
          window.location.href='/api/ws/future/crm/expressOrder/exportEMS?'+qs.stringify(util.deleteExtra(this.formData));
        }).catch(()=>{});
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.initPromise=axios.get('/api/ws/future/crm/expressOrder/getQuery').then((response) =>{
        this.formData=response.data;
        if(!this.formData.extendType){
          this.formData.extendType = '手机订单';
        }
      });
    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    }
  };
</script>

