<template>
  <div>
    <head-tab active="productTypeList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="itemAdd" icon="plus" v-permit="'crm:productType:edit'">{{$t('productTypeList.add')}}</el-button>
        <el-button type="primary"@click="formVisible = true" icon="search" v-permit="'crm:productType:view'">{{$t('productTypeList.filter')}}</el-button>
        <el-button type="primary" @click="exportData"  v-permit="'crm:productType:view'">{{$t('productTypeList.export')}}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('productTypeList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" :label-width="fromLabelWidth">
          <el-row :gutter="4">
            <el-col :span="24">
              <el-form-item :label="$t('productTypeList.name')">
                <el-input v-model="formData.name" :placeholder="$t('productTypeList.likeSearch')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('productTypeList.code')">
                <el-input v-model="formData.code" :placeholder="$t('productTypeList.likeSearch')"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('productTypeList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('productTypeList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column fixed prop="name" :label="$t('productTypeList.name')"  width="150" sortable></el-table-column>
        <el-table-column prop="reportName" :label="$t('productTypeList.reportName')" sortable></el-table-column>
        <el-table-column prop="code" :label="$t('productTypeList.code')" sortable></el-table-column>
        <el-table-column prop="productNames" :label="$t('productTypeList.productNames')" width="300"></el-table-column>
        <el-table-column prop="price1" :label="$t('productTypeList.price1')" sortable></el-table-column>
        <el-table-column prop="scoreType" :label="$t('productTypeList.scoreType')" sortable>
          <template scope="scope">
            <el-tag :type="scope.row.scoreType? 'primary' : 'danger'">{{scope.row.scoreType | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" :label="$t('productTypeList.remarks')" sortable></el-table-column>
        <el-table-column prop="createdByName" column-key="createdBy"   :label="$t('productTypeList.createdBy')" sortable></el-table-column>
        <el-table-column prop="createdDate" :label="$t('productTypeList.createdDate')" sortable></el-table-column>
        <el-table-column prop="locked" :label="$t('productTypeList.locked')">
          <template scope="scope">
            <el-tag :type="scope.row.locked ? 'primary' : 'danger'">{{scope.row.locked | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" :label="$t('productTypeList.enabled')">
          <template scope="scope">
            <el-tag :type="scope.row.enabled ? 'primary' : 'danger'">{{scope.row.enabled | bool2str}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" :label="$t('productTypeList.operation')" >
          <template scope="scope">
            <div class="action" v-permit="'crm:productType:edit'"><el-button size="small" @click.native="itemAction(scope.row.id,'edit')">{{$t('demoPhoneTypeList.edit')}}</el-button></div>
            <div class="action" v-permit="'crm:productType:delete'"><el-button size="small" @click.native="itemAction(scope.row.id,'delete')">{{$t('demoPhoneTypeList.delete')}}</el-button></div>
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
        formData:{
          extra:{}
          },
        initPromise:{},
        searchText:'',
        formVisible: false,
        pageHeight: 600,
        pageLoading: false,
        fromLabelWidth:"28%"
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
        axios.get('/api/ws/future/basic/productType?'+qs.stringify(submitData)).then((response) => {
          this.page = response.data;
          this.pageLoading = false;
        });

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
        this.$router.push({ name: 'productTypeForm'})
      },itemAction:function(id,action){
        if(action==="edit") {
          this.$router.push({ name: 'productTypeForm', query: { id: id }})
        } else if(action==="delete") {
          util.confirmBeforeDelRecord(this).then(() => {
            axios.get('/api/ws/future/basic/productType/delete',{params:{id:id}}).then((response) =>{
              this.$message(response.data.message);
              this.pageRequest();
            });
          }).catch(()=>{});
        }
      },exportData(){
        util.confirmBeforeExportData(this).then(() => {
          window.location.href='/api/ws/future/basic/productType/export?'+qs.stringify(util.deleteExtra(this.formData));
        }).catch(()=>{});
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/ws/future/basic/productType/getQuery').then((response) =>{
        this.formData=response.data;
      });
    },activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      });
    }
  };
</script>
