<template>
  <div>
    <head-tab active="reportScoreOfficeList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="share">{{$t('reportScoreOfficeList.officeRank')}}</el-button>
        <el-button type="primary" @click="formVisible = true" icon="search">{{$t('reportScoreOfficeList.filter')}}</el-button>
        <el-button type="primary" @click="exportData" icon="upload">{{$t('reportScoreOfficeList.export')}}</el-button>
        <el-button type="primary" @click="search()">刷新</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('reportScoreOfficeList.filter')"  v-model="formVisible" size="tiny" class="search-form"  z-index="1500" ref="searchDialog">
        <el-form :model="formData" method="get" >
          <el-row :gutter="4">
            <el-col :span="24">
              <el-form-item :label="$t('reportScoreOfficeList.scoreDate')" :label-width="formLabelWidth">
                <date-range-picker v-model="formData.scoreDateRange"></date-range-picker>
              </el-form-item>
              <el-form-item :label="$t('reportScoreOfficeList.officeName')" :label-width="formLabelWidth">
                 <office-select v-model="formData.officeId" @afterInit="setSearchText"></office-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('expressOrderList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('expressOrderList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column fixed prop="scoreDate" :label="$t('reportScoreOfficeList.scoreDate')" sortable></el-table-column>
        <el-table-column prop="areaName" :label="$t('reportScoreOfficeList.areaName')"  width="180"></el-table-column>
        <el-table-column prop="officeName" :label="$t('reportScoreOfficeList.officeName')"  width="200"></el-table-column>
        <el-table-column prop="score" :label="$t('reportScoreOfficeList.score')"></el-table-column>
        <el-table-column prop="monthScore" :label="$t('reportScoreOfficeList.monthScore')"></el-table-column>
        <el-table-column prop="dateRank" :label="$t('reportScoreOfficeList.dateRank')"></el-table-column>
        <el-table-column prop="monthRank" :label="$t('reportScoreOfficeList.monthRank')"></el-table-column>
        <el-table-column prop="saleQty" :label="$t('reportScoreOfficeList.saleQty')"></el-table-column>
        <el-table-column prop="monthSaleQty" :label="$t('reportScoreOfficeList.monthSaleQty')"></el-table-column>
        <el-table-column prop="recentMonthSaleQty" :label="$t('reportScoreOfficeList.recentMonthSaleQty')"></el-table-column>
        <el-table-column prop="officePoint" :label="$t('reportScoreOfficeList.point')"></el-table-column>
        <el-table-column prop="monthSaleMoney"  :label="$t('reportScoreOfficeList.monthSaleMoney')"></el-table-column>
      </el-table>
      <pageable :page="page" v-on:pageChange="pageChange"></pageable>
    </div>
  </div>
</template>
<script>
  import datePicker from 'components/common/date-picker'
  import officeSelect from 'components/basic/office-select'
  export default {
    components:{
      datePicker,
      officeSelect
      },
    data() {
      return {
        page:{},
        searchText:"",
        formData:{
            extra:{}
        },
        productList:[],
        formLabelWidth: '120px',
        formVisible: false,
        pageLoading: false,
        remoteLoading: false
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
        var submitData = util.deleteExtra(this.formData);
        axios.get('/api/ws/future/crm/reportScoreOffice',{params:submitData}).then((response) => {
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
        this.$router.push({ name: 'reportScoreAreaList'});
      },exportData(){
        util.confirmBeforeAction(this, "最多导出50000条记录，确认导出?").then(() => {
          window.location.href='/api/ws/future/crm/reportScoreOffice/export?'+qs.stringify(util.deleteExtra(this.formData));
        }).catch(()=>{});
      }
    },created () {
      const that = this;
      that.pageHeight = 0.74*window.innerHeight;
      axios.get('/api/ws/future/crm/reportScoreOffice/getQuery').then((response) =>{
        that.formData=response.data;
        console.log(that.formData)
        that.pageRequest();
      });
    }
  };
</script>

