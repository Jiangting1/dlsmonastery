<template>
  <div>
    <head-tab active="shopPrintDetail"></head-tab>
    <div>
      <el-form :model="shopPrint" :rules="rules" ref="shopPrint" label-width="120px" class="form input-form">
        <el-row :gutter="20">
          <el-col :span="10">
            <el-form-item :label="$t('shopPrintDetail.code')">
              {{shopPrint.id}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.officeName')">
              {{shopPrint.officeName}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.printType')">
              {{shopPrint.printType}}
            </el-form-item>
            <el-form-item  :label="$t('shopPrintDetail.content')">
              {{shopPrint.content}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.attachment')" prop="attachment">
              <el-upload  action="/api/basic/sys/folderFile/upload?uploadPath=/广告印刷" :on-preview="handlePreview" :file-list="fileList" list-type="picture" multiple >
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item :label="$t('shopPrintDetail.address')">
              {{shopPrint.address}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.contact')">
              {{shopPrint.contator}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.mobilePhone')">
              {{shopPrint.mobilePhone}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.createdBy')" >
              {{shopPrint.createdByName}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.createdDate')"  >
              {{shopPrint.createdDate}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.remarks')"  >
              {{shopPrint.remarks}}
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.pass')"  v-if="action=='audit'">
              <bool-radio-group v-model="formData.pass"></bool-radio-group>
            </el-form-item>
            <el-form-item :label="$t('shopPrintDetail.passRemarks')"  v-if="action=='audit'">
              <el-input v-model="formData.passRemarks" :placeholder="$t('shopPrintDetail.inputRemarks')" type="textarea"></el-input>
            </el-form-item>
            <el-form-item v-if="action=='audit'">
              <el-button type="primary" :disabled="submitDisabled"  @click="passSubmit()">{{$t('shopPrintDetail.save')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <process-details v-model="shopPrint.processInstanceId"></process-details>
  </div>
</template>

<script>
  import processDetails from 'components/general/process-details';
  import boolRadioGroup from 'components/common/bool-radio-group';
  export default{
    components:{processDetails,boolRadioGroup},
    data(){
      return this.getData();
    },
    methods:{
        getData(){
          return{
            isCreate:this.$route.query.id==null,
            action:this.$route.query.action,
            shopPrint:{},
            formData:{
              extra:{},
            },
            submitDisabled:false,
            fileList:[],
            rules:{

            }
          }
        },
      passSubmit(){
        this.submitDisabled = true;
        let form = this.$refs["shopPrint"];
        form.validate((valid) => {
          if (valid) {
             let submitData =  util.deleteExtra(this.formData);
             axios.post('/api/ws/future/layout/shopPrint/audit', qs.stringify(submitData)).then((response)=> {
               this.$message(response.data.message);
               this.submitDisabled = false;
                if(response.data.success){
                  Object.assign(this.$data,this.getData());
                  this.initPage();
                  this.submitDisabled = true;
                  this.$router.push({name:'shopPrintList',query:util.getQuery("shopPrintList")})
                }
             }).catch(() => {
                this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },
      handlePreview(file) {
        window.open(file.url);
      },initPage(){
        axios.get('/api/ws/future/layout/shopPrint/findOne',{params: {id:this.$route.query.id}}).then((response)=>{
          this.shopPrint = response.data;
          if(this.shopPrint.attachment != null) {
            axios.get('/api/general/sys/folderFile/findByIds',{params: {ids:this.shopPrint.attachment}}).then((response)=>{
              this.fileList = response.data;
            });
          }
        })
        axios.get('/api/ws/future/layout/shopPrint/getAuditForm',{params: {id:this.$route.query.id}}).then((response)=>{
          this.formData = response.data;
        })
      }
    },created(){
        this.initPage();
    }
  }
</script>
