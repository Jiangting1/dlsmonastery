<template>
  <div>
    <head-tab active="productImeStockReport"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="formVisible = true" icon="search" v-if="!nextIsShop&&'区域'==formData.sumType || '型号'==formData.sumType">过滤</el-button>
        <el-dropdown  @command="exportData">
          <el-button type="primary">导出<i class="el-icon-caret-bottom el-icon--right"></i></el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="按数量">按数量导出</el-dropdown-item>
            <el-dropdown-item command="按合计">按合计导出</el-dropdown-item>
            <el-dropdown-item command="按串码">按串码导出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-button type="primary" @click="preLevel()" v-show="officeId !=formData.officeId&&officeIds.length">返回</el-button>
        <el-button type="primary" @click="search()">刷新</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" title="过滤" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData">
          <el-row :gutter="4">
            <el-col :span="24">
              <el-form-item label="汇总" :label-width="formLabelWidth">
                <el-select v-model="formData.sumType" clearable filterable placeholder="请选择">
                  <el-option v-for="item in formData.extra.sumTypeList" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="查看" :label-width="formLabelWidth">
                <el-select v-model="formData.outType" clearable filterable placeholder="请选择">
                  <el-option v-for="item in formData.extra.outTypeList" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="区域" :label-width="formLabelWidth">
                <el-select v-model="formData.areaType" clearable filterable placeholder="请选择">
                  <el-option v-for="item in formData.extra.areaTypeList" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="乡镇" :label-width="formLabelWidth">
                <el-select v-model="formData.townType" clearable filterable placeholder="请选择">
                  <el-option v-for="item in formData.extra.townTypeList" :key="item" :label="item" :value="item"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="日期" :label-width="formLabelWidth">
                <date-picker v-model="formData.date"></date-picker>
              </el-form-item>
              <el-form-item label="打分型号" :label-width="formLabelWidth">
                <el-select v-model="formData.scoreType" clearable filterable placeholder="请选择">
                  <el-option v-for="(key,value) in formData.extra.boolMap" :key="key" :label="value | bool2str " :value="key"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="货品" :label-width="formLabelWidth">
                <product-type-select v-model="formData.productTypeIdList"  @afterInit="setSearchText" multiple></product-type-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">确定</el-button>
        </div>
      </search-dialog>
      <div>
        <el-table :data="page.list"  style="margin-top:5px;" v-loading="pageLoading" element-loading-text="加载中" @sort-change="sortChange" stripe border>
          <el-table-column prop="depotName" label="门店" sortable width="300" v-if="nextIsShop&&'区域'==formData.sumType"></el-table-column>
          <el-table-column prop="officeName" label="区域" sortable width="300" v-if="!nextIsShop&&'区域'==formData.sumType"></el-table-column>
          <el-table-column  prop="productTypeName" label="型号" sortable width="300" v-if="'型号'==formData.sumType"></el-table-column>
          <el-table-column prop="qty" :label="'数量('+page.sum+')'"  sortable></el-table-column>
          <el-table-column prop="percent" label="占比(%)"></el-table-column>
          <el-table-column  label="操作" width="140">
            <template scope="scope">
              <el-button size="small" @click="nextLevel(scope.row.productTypeId,scope.row.officeId,scope.row.depotId)">详细</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div>
        <el-dialog title="详细" :visible.sync="detailVisible" >
          <div style="width:100%;height:50px;text-align:center;font-size:20px">汇总</div>
          <el-table :data="productTypeDetail">
            <el-table-column property="productName" label="货品" width="400"></el-table-column>
            <el-table-column property="qty" :label="'数量'+sum" ></el-table-column>
          </el-table>
          <div style="width:100%;height:50px;text-align:center;font-size:20px">串码详情</div>
          <el-table :data="depotReportList">
            <el-table-column property="productName" label="货品" width="400"></el-table-column>
            <el-table-column property="ime" :label="'串码'+sum" ></el-table-column>
          </el-table>
        </el-dialog>
      </div>
    </div>
  </div>

</template>
<script>
  import productTypeSelect from 'components/future/product-type-select';
  export default {
    components:{
      productTypeSelect
    },
    data() {
      return {
        page:[],
        searchText:"",
        nextIsShop:false,
        formData:{
          extra:{productTypeIdList:[]},
        },
        sum:'',
        formLabelWidth: '120px',
        formVisible: false,
        detailVisible: false,
        pageLoading: false,
        officeIds:[],
        officeId:null,
        productTypeDetail:[],
        depotReportList:[],
        type:"库存报表",
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
        this.formData.type=this.type;
        var submitData = util.deleteExtra(this.formData);
        if(!this.nextIsShop){
          this.formData.depotId=""
          axios.post('/api/report/crm/productIme/productImeReport',qs.stringify(submitData)).then((response) => {
            this.page = response.data;
            this.pageLoading = false;
          })
        }else {
          axios.post('/api/report/crm/depotShop/depotReportDate',qs.stringify(submitData)).then((response) => {
            this.page = response.data;
            this.pageLoading = false;
          })
        }
      },sortChange(column) {
        this.formData.sort=util.getSort(column);
        this.formData.page=0;
        this.pageRequest();
      },search() {
        this.formVisible = false;
        this.pageRequest();
      },nextLevel(	productTypeId, officeId, depotId){
        if(productTypeId){
          this.formData.productTypeIdList=productTypeId;
          this.formData.sumType="区域";
          this.pageRequest();
        }else {
          if (!this.nextIsShop) {
            axios.get('/api/basic/sys/office/checkLastLevel?officeId=' + officeId).then((response) => {
              this.officeIds.push(officeId);
              this.formData.officeId = this.officeIds[this.officeIds.length - 1];
              this.nextIsShop = response.data;
              this.pageRequest();
            })
          } else {
            this.detailVisible = true;
            this.formData.isDetail = true;
            this.formData.depotId = depotId;
            axios.post('/api/report/crm/depotShop/depotReportDetail', qs.stringify(util.deleteExtra(this.formData))).then((response) => {
              this.depotReportList = response.data.depotReportList;
              this.sum = response.data.sum;
              let productQtyMap = response.data.productQtyMap;
              let productTypeDetail = [];
              if (productQtyMap) {
                for (let key in productQtyMap) {
                  productTypeDetail.push({productName: key, qty: productQtyMap[key]})
                }
                this.productTypeDetail = productTypeDetail;
              }
            })
              this.formData.depotId="";
          }
        }
      },preLevel(){
        this.nextIsShop=false;
        this.formData.isDetail=false;
        this.officeIds.pop();
        this.formData.officeId=this.officeIds[this.officeIds.length-1];
        this.pageRequest();
      },exportData(command) {
        this.formData.exportType=command;
        window.location.href="/api/report/crm/productIme/exportReport?"+qs.stringify(util.deleteExtra(this.formData));
        this.formData.exportType=null;
      }
    },created () {
      this.pageHeight = 0.74*window.innerHeight;
      axios.get('/api/report/crm/productIme/getReportQuery').then((response) => {
        this.formData = response.data;
        this.formData.scoreType=this.formData.scoreType?"1":"0";
        if(response.data.officeId){
          this.officeIds.push(response.data.officeId);
          this.officeId=response.data.officeId;
        }
        this.pageRequest();
      })
    }
  };
</script>

