<template>
  <div>
    <head-tab active="dutyPublicFreeList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary"@click="formVisible = true" icon="search">{{$t('dutyPublicFreeList.filter')}}</el-button>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('dutyPublicFreeList.filter')" v-model="formVisible" size="tiny" class="search-form" z-index="1500" ref="searchDialog">
        <el-form :model="formData" :label-width="formLabelWidth">
              <el-form-item :label="$t('dutyPublicFreeList.freeDate')" >
                <date-range-picker v-model="formData.dutyDate"></date-range-picker>
              </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('dutyPublicFreeList.sure')}}</el-button>
        </div>
      </search-dialog>
      <el-table :data="page.content" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('dutyPublicFreeList.loading')" @sort-change="sortChange" stripe border>
        <el-table-column prop="freeDate":label="$t('dutyPublicFreeList.freeDate')"></el-table-column>
        <el-table-column prop="dateType" :label="$t('dutyPublicFreeList.dateType')"></el-table-column>
        <el-table-column prop="status" :label="$t('dutyPublicFreeList.status')">
          <template scope="scope">
            <el-tag :type="scope.row.status == '申请中' ? 'primary' : 'danger'">{{scope.row.status}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remarks" :label="$t('dutyPublicFreeList.remarks')"></el-table-column>
        <el-table-column prop="created.loginName" :label="$t('dutyPublicFreeList.createdBy')"></el-table-column>
        <el-table-column prop="createdDate" :label="$t('dutyPublicFreeList.createdDate')"></el-table-column>
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
          extra:{},
        },
        initPromise:{},
        searchText:"",
        formLabelWidth: '25%',
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
        var submitData = util.deleteExtra(this.formData);
        axios.get('/api/basic/hr/dutyPublicFree?'+qs.stringify(submitData)).then((response) => {
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
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.initPromise = axios.get('/api/basic/hr/dutyPublicFree/getQuery').then((response) =>{
        this.formData=response.data;
      });
    },
    activated(){
      this.initPromise.then(()=>{
        this.pageRequest();
      })
    }
  };
</script>

