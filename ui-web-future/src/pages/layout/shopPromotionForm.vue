<template>
  <div>
    <head-tab active="shopPromotionForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="130px"  class="form input-form">
        <el-row>
          <el-col :span="12">
           <el-form-item :label="$t('shopPromotionForm.shopId')" prop="shopId">
             <depot-select v-model="inputForm.shopId" category="adShop" :disabled="shopDisabled"></depot-select>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.activityDate')" prop="activityDate">
              <date-picker v-model="inputForm.activityDate" type="date" :placeholder="$t('shopPromotionForm.inputDate')"></date-picker>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.activityType')" prop="activityType">
              <el-select v-model="inputForm.activityType" filterable clearable :placeholder="$t('shopPromotionForm.inputType')">
                <el-option v-for="activityType in inputForm.extra.activityTypeList" :key="activityType" :label="activityType" :value="activityType"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.dayAmount')" prop="dayAmount">
              <el-input v-model.number="inputForm.dayAmount" ></el-input>
            </el-form-item>
            <el-form-item  :label="$t('shopPromotionForm.amount')" prop="amount">
              <el-input v-model.number="inputForm.amount" ></el-input>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.salerComment')"prop="salerComment">
              <el-input v-model="inputForm.salerComment" type="textarea" autosize></el-input>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.materialComment')" prop="materialComment">
              <el-input v-model="inputForm.materialComment" type="textarea"></el-input>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.comment')" prop="comment">
              <el-input v-model="inputForm.comment" type="textarea"></el-input>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.phone')" prop="phone">
              <el-input v-model="inputForm.phone" ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('shopPromotionForm.activityImage1')" prop="activityImage1">
                  <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/活动拉销" :on-change="handleChange1" :on-remove="handleRemove1"  :on-preview="handlePreview1" :file-list="fileList1" list-type="picture">
                    <el-button size="small" type="primary">{{$t('shopPromotionForm.clickUpload')}}</el-button>
                    <div slot="tip" class="el-upload__tip">{{$t('shopPromotionForm.uploadImageSizeFor5000KB')}}</div>
                  </el-upload>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.activityImage2')" prop="activityImage2">
                  <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/活动拉销" :on-change="handleChange2" :on-remove="handleRemove2"  :on-preview="handlePreview2" :file-list="fileList2" list-type="picture">
                    <el-button size="small" type="primary">{{$t('shopPromotionForm.clickUpload')}}</el-button>
                    <div slot="tip" class="el-upload__tip">{{$t('shopPromotionForm.uploadImageSizeFor5000KB')}}</div>
                  </el-upload>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.activityImage3')" prop="activityImage3">
                  <el-upload action="/api/general/sys/folderFile/upload?uploadPath=/活动拉销" :on-change="handleChange3" :on-remove="handleRemove3"  :on-preview="handlePreview3" :file-list="fileList3" list-type="picture">
                    <el-button size="small" type="primary">{{$t('shopPromotionForm.clickUpload')}}</el-button>
                    <div slot="tip" class="el-upload__tip">{{$t('shopPromotionForm.uploadImageSizeFor5000KB')}}</div>
                  </el-upload>
            </el-form-item>
            <el-form-item :label="$t('shopPromotionForm.remarks')" prop="remarks">
              <el-input v-model="inputForm.remarks" type="textarea"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :disabled="submitDisabled"  @click="formSubmit()">{{$t('shopPromotionForm.save')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script>
  import depotSelect from 'components/future/depot-select'
  export default{
    components:{depotSelect},
    data(){
      return this.getData();
    },
    methods: {
      getData(){
        return{
          isInit:false,
          isCreate:this.$route.query.id==null,
          submitDisabled:false,
          shopDisabled:false,
          fileList1:[],
          fileList2:[],
          fileList3:[],
          inputForm:{
            extra:{}
          },
          rules: {
            shopId: [{ required: true, message: this.$t('shopPromotionForm.prerequisiteMessage')}],
            activityDate: [{ required: true, message: this.$t('shopPromotionForm.prerequisiteMessage')}],
            activityType: [{ required: true, message: this.$t('shopPromotionForm.prerequisiteMessage')}],
            amount: [{ required: true, message: this.$t('shopPromotionForm.prerequisiteMessage')},{type:"number",message:this.$t('shopPromotionForm.inputLegalValue')}],
            dayAmount: [{ required: true, message: this.$t('shopPromotionForm.prerequisiteMessage')},{type:"number",message:this.$t('shopPromotionForm.inputLegalValue')}],
            phone: [{ required: true, message: this.$t('shopPromotionForm.prerequisiteMessage')}],
          },
          remoteLoading:false,
        }
      },
      formSubmit(){
        let form = this.$refs["inputForm"];
        this.submitDisabled = true;
        this.inputForm.activityDate = util.formatLocalDate(this.inputForm.activityDate)
        form.validate((valid) => {
          this.inputForm.activityImage1 = util.getFolderFileIdStr(this.fileList1);
          this.inputForm.activityImage2 = util.getFolderFileIdStr(this.fileList2);
          this.inputForm.activityImage3 = util.getFolderFileIdStr(this.fileList3);
          if (valid) {
            axios.post('/api/ws/future/layout/shopPromotion/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response) => {
              this.$message(response.data.message);
              if(response.data.success) {
                if (!this.isCreate) {
                  util.closeAndBackToPage(this.$router,'shopPromotionList')
                }else{
                  Object.assign(this.$data, this.getData());
                  this.initPage();
                }
              }
            }).catch(() => {
              this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      }, handlePreview1(file) {
        window.open(file.url);
      }, handleChange1(file, fileList) {
        this.fileList1 = fileList;
      }, handleRemove1(file, fileList) {
        this.fileList1 = fileList;
      }, handlePreview2(file) {
        window.open(file.url);
      }, handleChange2(file, fileList) {
        this.fileList2 = fileList;
      }, handleRemove2(file, fileList) {
        this.fileList2 = fileList;
      }, handlePreview3(file) {
        window.open(file.url);
      }, handleChange3(file, fileList) {
        this.fileList3 = fileList;
      }, handleRemove3(file, fileList) {
        this.fileList3 = fileList;
      }, initPage(){
        axios.get('/api/ws/future/layout/shopPromotion/getForm').then((response)=>{
          this.inputForm = response.data;
          if(!this.isCreate) {
            axios.get('/api/ws/future/layout/shopPromotion/findOne', {params: {id: this.$route.query.id}}).then((response) => {
              util.copyValue(response.data, this.inputForm);
              if (response.data.shopId != null) {
                this.shopDisabled = true;
              } else {
                this.shopDisabled = false;
              }
              if (this.inputForm.activityImage1 != null) {
                axios.get('/api/general/sys/folderFile/findByIds', {params: {ids: this.inputForm.activityImage1}}).then((response) => {
                  this.fileList1 = response.data;
                });
              } else {
                this.fileList1 = [];
              }
              if (this.inputForm.activityImage2 != null) {
                axios.get('/api/general/sys/folderFile/findByIds', {params: {ids: this.inputForm.activityImage2}}).then((response) => {
                  this.fileList2 = response.data;
                });
              } else {
                this.fileList2 = [];
              }
              if (this.inputForm.activityImage3 != null) {
                axios.get('/api/general/sys/folderFile/findByIds', {params: {ids: this.inputForm.activityImage3}}).then((response) => {
                  this.fileList3 = response.data;
                });
              } else {
                this.fileList3 = [];
              }
            });
          }
        });
      }
    },created () {
        this.initPage();
    }
  }
</script>
