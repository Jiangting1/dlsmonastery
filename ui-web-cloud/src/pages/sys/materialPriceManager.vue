<template>
  <div>
    <head-tab active="materialPriceManager"></head-tab>
    <div>
      <el-form :model="formData" method="get" ref="inputForm" :inline="true">
        <el-button type="primary" :disabled="synDisabled" @click="syn" icon="check">同步</el-button>
        <el-button type="primary" :disabled="submitDisabled" @click="formSubmit" icon="check">保存</el-button>
        <div id="grid" ref="handsontable" style="width:100%;height:600px;overflow:hidden;margin-top: 20px"></div>
      </el-form>
    </div>
  </div>
</template>
<style>
  @import "~handsontable/dist/handsontable.full.css";
</style>
<script>
  import Handsontable from 'handsontable/dist/handsontable.full.js';
  var table = null;
  export default {
    data() {
      return {
        table:null,
        storeList:{},
        settings: {
          rowHeaders:true,
          autoColumnSize:true,
          stretchH: 'all',
          minRows: 10,
          minSpareRows:1,
          height: 650,
          colHeaders: ["货品","货品编码","价格"],
          columns: [
            {type: "autocomplete", allowEmpty: true, strict: true,productNames:[],source:this.productNames},
            {type:"text", allowEmpty: false, strict: true},
            {type: 'numeric',allowEmpty: false,format:"0,0.00"},
          ],
          afterChange: function (changes, source) {
            if (source === 'edit') {
              for (let i = changes.length - 1; i >= 0; i--) {
                let row = changes[i][0];
                let column = changes[i][1]==0;
                if(column){
                  let name = changes[i][3];
                  axios.get('/api/global/cloud/sys/product/findByName',{params:{name:name}}).then((response) =>{
                    let  material = response.data;
                    table.setDataAtCell(row,1,material.code);
                  });
                }
              }
            }
          }
        },
        formData:{
          json:[],
        },
        submitDisabled:false,
        synDisabled:false,
      };
    },
    methods: {
      formSubmit(){
        this.submitDisabled = true;
        var form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            this.formData.json =new Array();
            let list = table.getData();
            for(let item in list){
              if(!table.isEmptyRow(item)){
                this.formData.json.push(list[item]);
              }
            }
            this.formData.json = JSON.stringify(this.formData.json);
            axios.post('/api/global/cloud/sys/product/save', qs.stringify(this.formData,{allowDots:true})).then((response)=> {
              if(response.data.success){
                this.$message(response.data.message);
                this.submitDisabled = false;
                this.initPage();
              }else{
                this.$alert(response.data.message);
                this.submitDisabled = false;
              }
            }).catch(function () {
              this.submitDisabled = false;
            });
          }else{
            this.submitDisabled = false;
          }
        })
      },
      syn(){
        this.synDisabled = true;
        axios.get('/api/global/cloud/sys/product/syn').then((response)=> {
          if(response.data.success){
            this.$message(response.data.message);
            this.synDisabled = false;
          }else{
            this.$alert(response.data.message);
            this.synDisabled = false;
          }
        })
      },
      initPage() {
        axios.get('/api/global/cloud/sys/product/form').then((response)=>{
          this.settings.columns[0].source = response.data.productNameList;
          table = new Handsontable(this.$refs["handsontable"], this.settings);
        });
      },
    },
    created() {
      this.initPage();
    },
  }
</script>
