<template>
  <div>
    <head-tab active="productImeSaleBackForm"></head-tab>
    <div>
      <el-form :model="inputForm" ref="inputForm"   label-width="120px" class="form input-form">
        <el-row >
          <el-col :span="21">
            <el-form-item >
              <su-alert :text="errMsg" type="danger"> </su-alert>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item :label="$t('productImeSaleBackForm.ime')" prop="imeStr">
              <el-input type="textarea" :rows="6" v-model="inputForm.imeStr" :placeholder="$t('productImeSaleBackForm.inputIme')"></el-input>
            </el-form-item>
            <el-form-item >
              <el-button  type="primary" @click.native="searchImeStr">{{$t('productImeSaleBackForm.search')}}</el-button>
              <el-button  type="primary" @click.native="reset">{{$t('productImeSaleBackForm.reset')}}</el-button>
            </el-form-item>
          </el-col>
          <div v-if="searched">
           <el-col :span="8">
             <el-form-item :label="$t('productImeSaleBackForm.remarks')" prop="remarks" >
               <el-input type="textarea" :rows="3" v-model="inputForm.remarks"></el-input>
             </el-form-item>
             <el-form-item>
               <el-button type="primary" :disabled="submitDisabled" @click="formSubmit()" >{{$t('productImeSaleBackForm.saleBack')}}</el-button>
             </el-form-item>
           </el-col>
          </div>
        </el-row>
        <el-row :gutter="24" v-if="searched">
          <el-col :span="6">
            <template>
              <el-table :data="productQtyList" style="width: 100%" border>
                <el-table-column prop="productName" :label="$t('productImeSaleBackForm.name')"></el-table-column>
                <el-table-column prop="qty" :label="$t('productImeSaleBackForm.qty')" :render-header="count"></el-table-column>
              </el-table>
            </template>
          </el-col>
          <el-col :span="18">
            <template>
              <el-table :data="productImeList" style="width: 100%" border>
                <el-table-column prop="ime" :label="$t('productImeSaleBackForm.ime')"></el-table-column>
                <el-table-column prop="depotName" :label="$t('productImeSaleBackForm.depotName')"></el-table-column>
                <el-table-column prop="productName"  :label="$t('productImeSaleBackForm.productType')"></el-table-column>
                <el-table-column prop="productImeSaleShopName" :label="$t('productImeSaleBackForm.shopName')"></el-table-column>
                <el-table-column prop="productImeSaleEmployeeName" :label="$t('productImeSaleBackForm.productImeSaleEmployeeName')"></el-table-column>
                <el-table-column prop="productImeSaleCreatedDate" :label="$t('productImeSaleBackForm.productImeSaleCreatedDate')"></el-table-column>
                <el-table-column prop="productImeUploadCreatedDate" :label="$t('productImeSaleBackForm.productImeUploadCreatedDate')"></el-table-column>
              </el-table>
            </template>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>
<style>
  .el-table { margin-bottom: 50px;}
</style>
<script>

  import depotSelect from 'components/future/depot-select'

  export default{
    components: {
      depotSelect,
    },
    data(){
      return this.getData()
    },
    methods: {
      getData() {
        return {
          isCreate: this.$route.query.id == null,
          submitDisabled: false,
          searched: false,
          inputForm: {
              extra:{},
          },
          errMsg: '',
          productImeList: [],
          productQtyList: [],
          rules: {
            imeStr: [{required: true, message: this.$t('productImeSaleBackForm.prerequisiteMessage')}],
          },
        }
      },
      formSubmit(){

        if (this.errMsg) {
          this.$alert( this.$t('productImeSaleBackForm.formInvalid'), this.$t('productImeSaleBackForm.notify'));
          return;
        }
        this.submitDisabled = true;
        let form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {

            axios.post('/api/ws/future/crm/productImeSale/saleBack', qs.stringify(util.deleteExtra(this.inputForm))).then((response) => {
              this.$message(response.data.message);
              if (response.data.success) {
                Object.assign(this.$data, this.getData());
                this.initPage();
              }
            }).catch( () => {
              this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      }, searchImeStr(){
        this.searched = true;
        axios.post('/api/ws/future/crm/productImeSale/checkForSaleBack', qs.stringify({imeStr: this.inputForm.imeStr})).then((response) => {
          this.errMsg = response.data;
        });
        axios.post('/api/ws/future/crm/productIme/findDtoListByImes', qs.stringify({imeStr: this.inputForm.imeStr})).then((response) => {
          this.productImeList = response.data;

          let tmpMap = new Map();
          if (this.productImeList) {
            for (let productIme of this.productImeList) {
              if (!tmpMap.has(productIme.productId)) {
                tmpMap.set(productIme.productId, {productName: productIme.productName, qty: 0});
              }
              tmpMap.get(productIme.productId).qty += 1;
            }
          }
          let tmpList = [];
          for (let key of tmpMap.keys()) {
            tmpList.push(tmpMap.get(key));
          }
          this.productQtyList = tmpList;
        });
      }, reset(){
        this.searched = false;
        this.errMsg = '';
        this.productImeList = [];
        this.productQtyList = [];
        this.$refs["inputForm"].resetFields();
      },initPage(){
        axios.get('/api/ws/future/crm/productImeSale/getSaleBackForm').then((response) => {
          this.inputForm = response.data;
        });
      },
      count(createElement,cols){
        return util.countTotal(createElement,cols)
      }
    }, created () {
        this.initPage();
    }
  }
</script>
