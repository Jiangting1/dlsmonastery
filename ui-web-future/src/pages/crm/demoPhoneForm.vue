<template>
  <div>
    <head-tab active="demoPhoneForm"></head-tab>
    <div >
      <el-form :model="inputForm" ref="inputForm" :rules="rules" label-width="120px" class="form input-form">
        <el-form-item :label="$t('demoPhoneForm.productType')" prop="demoPhoneTypeId">
          <el-select v-model="inputForm.demoPhoneTypeId" filterable clearable :placeholder="$t('demoPhoneForm.inputWord')">
            <el-option v-for="item in inputForm.extra.demoPhoneTypes" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('demoPhoneForm.shopName')" prop="shopId">
          <depot-select v-model="inputForm.shopId" category="shop"></depot-select>
        </el-form-item>
        <el-form-item :label="$t('demoPhoneForm.employeeName')" prop="employeeId">
          <employee-select v-model="inputForm.employeeId"></employee-select>
        </el-form-item>
        <el-form-item :label="$t('demoPhoneForm.productIme')" prop="productImeId">
          <el-select v-model="inputForm.productImeId" clearable filterable remote :placeholder="$t('demoPhoneForm.inputImeLast6')" :remote-method="remoteProductIme" :loading="remoteLoading">
            <el-option v-for="productIme in productImes" :key="productIme.id" :label="productIme.ime+' '+productIme.productName" :value="productIme.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('demoPhoneForm.remarks')" prop="remarks">
          <el-input v-model="inputForm.remarks"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :disabled="submitDisabled"  @click="formSubmit()">{{$t('demoPhoneForm.save')}}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
  import depotSelect from 'components/future/depot-select'
  import employeeSelect from 'components/basic/employee-select'
  import demoPhoneType from 'components/future/demo-phone-type-select'
  export default{
    components:{
        depotSelect,
        employeeSelect,
        demoPhoneType
    },
    data(){
      return this.getData()
    },
    methods:{
      getData() {
      return{
        isCreate:this.$route.query.id==null,
        submitDisabled:false,
        inputForm:{
            extra:{}
        },
        rules: {
          demoPhoneTypeId:[{ required: true, message: this.$t('demoPhoneForm.prerequisiteMessage')}],
          shopId: [{ required: true, message: this.$t('demoPhoneForm.prerequisiteMessage')}],
          employeeId: [{ required: true, message: this.$t('demoPhoneForm.prerequisiteMessage')}],
          productImeId: [{ required: true, message: this.$t('demoPhoneForm.prerequisiteMessage')}]
        },
        remoteLoading:false,
        productImes:[],
      }
    },
      formSubmit(){
        var that = this;
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            axios.post('/api/ws/future/crm/demoPhone/save', qs.stringify(util.deleteExtra(this.inputForm))).then((response)=> {
              this.$message(response.data.message);
              if(response.data.success){
                if (!this.isCreate) {
                  util.closeAndBackToPage(this.$router,'demoPhoneList')
                } else {
                  Object.assign(this.$data, this.getData());
                  this.initPage();
                }
              }else{
                this.submitDisabled = false;
              }
            }).catch( ()=> {
              that.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },remoteProductIme(query) {
        if (query !== '' && query.length>=6) {
          this.remoteLoading = true;
          axios.get('/api/ws/future/crm/productIme/search',{params: {productIme:query}}).then((response)  =>{
            this.productImes=response.data;
            this.remoteLoading = false;
          })
        } else {
          this.productImes = [];
        }
      },initPage(){
        axios.get('/api/ws/future/crm/demoPhone/getForm').then((response)=>{
          this.inputForm = response.data;
          if(!this.isCreate){
            axios.get('/api/ws/future/crm/demoPhone/findOne',{params: {id:this.$route.query.id}}).then((response)=> {
              util.copyValue(response.data, this.inputForm);
            })
          }
        })
      }
    },created () {
      this.initPage();
    }
  }
</script>

