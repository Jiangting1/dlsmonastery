<template>
  <div>
    <head-tab active="dutyTaskList"></head-tab>
    <div>
      <el-row>
        <el-button type="primary" @click="batchPass" icon="check">{{$t('dutyTaskList.batchPass')}}</el-button>
      </el-row>
      <el-table :data="page" :height="pageHeight" style="margin-top:5px;" v-loading="pageLoading" :element-loading-text="$t('dutyTaskList.loading')" @selection-change="handleSelectionChange" stripe border >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column fixed prop="formatId" :label="$t('dutyTaskList.formatId')" sortable></el-table-column>
        <el-table-column prop="dutyType"  :label="$t('dutyTaskList.dutyType')" ></el-table-column>
        <el-table-column prop="dutyDate" :label="$t('dutyTaskList.dutyDate')"></el-table-column>
        <el-table-column prop="dateType" :label="$t('dutyTaskList.dateType')"></el-table-column>
        <el-table-column prop="hour" :label="$t('dutyTaskList.hour')"></el-table-column>
        <el-table-column prop="accountName" :label="$t('dutyTaskList.applyAccount')"></el-table-column>
        <el-table-column prop="remarks" :label="$t('dutyTaskList.remarks')"></el-table-column>
        <el-table-column fixed="right" :label="$t('dutyTaskList.operation')" width="140">
          <template scope="scope">
            <div class="action"><el-button size="small" @click.native="itemAction(scope.row.id,'audit',scope.row.dutyType)" v-permit="'hr:duty:edit'">{{$t('dutyTaskList.audit')}}</el-button></div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
  export default {
    data() {
      return {
        page:[],
        multipleSelection:[],
        pageLoading: false
      };
    },
    methods: {
      pageRequest() {
        this.pageLoading = true;
        axios.get('/api/basic/hr/duty').then((response) => {
          this.page = response.data;
          this.pageLoading = false;
        })
      },batchPass(){
        if(!this.multipleSelection || this.multipleSelection.length < 1){
          this.$message({message:this.$t('dutyTaskList.noSelectionFound'), type: 'warning'});
          return ;
        }
        util.confirmBeforeBatchPass(this).then(() => {
          this.pageLoading = true;

          axios.get('/api/basic/hr/duty/batchPass',{params:{dutyAuditMap : this.multipleSelection}}).then((response) =>{
            if(response.data.success){
              this.$message(response.data.message);
            }else {
              this.$message.error(response.data.message);
            }
            this.pageRequest();
          })
        }).catch(()=>{});

      },itemAction:function(id,action,dutyType){
        if(action=="audit") {
          this.$router.push({ name: 'dutyTaskForm', query: { id: id, dutyType: dutyType}})
        }
      },handleSelectionChange(selection) {
         this.multipleSelection={};
         for(var key in selection){
           this.multipleSelection[selection[key].id]=selection[key].dutyType
        }
      }
    },created () {
       this.pageHeight = 0.74*window.innerHeight;
      this.pageRequest();
    }
  };
</script>

