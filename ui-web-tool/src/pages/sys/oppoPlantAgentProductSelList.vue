<template>
  <div>
    <head-tab active="oppoPlantAgentProductSelList"></head-tab>
    <div  v-loading="loading" element-loading-text="正在同步工厂数据..." >
      <el-row>
        <div style="float:left">
          <el-button type="primary" @click="formSubmit" icon="check">保存</el-button>
          <el-button type="primary"@click="formVisible = true" icon="search">过滤</el-button>
        </div>
        <div style="float: left;margin-left: 10px">
          <date-picker v-model="date"></date-picker>
        </div>
        <div style="float: left;margin-left: 10px">
          <el-button type="primary" @click="pullFactoryData">下拉工厂数据</el-button>
          <el-button type="primary" @click="synData">数据同步</el-button>
          <el-button type="primary" @click="exportData" >发货串码导出</el-button>
        </div>
        <span v-html="searchText"></span>
      </el-row>
      <search-dialog @enter="search()" :show="formVisible" @hide="formVisible=false" :title="$t('过滤')" v-model="formVisible"  size="medium" class="search-form"  z-index="1500" ref="searchDialog">
        <el-form :model="formData"  ref="inputForm" >
          <el-row :gutter="8">
            <el-col :span="12">
              <el-form-item :label="$t('物料描述')" :label-width="formLabelWidth">
                <el-input v-model="formData.itemDesc" auto-complete="off" :placeholder="$t('模糊查询')"></el-input>
              </el-form-item>
              <el-form-item :label="$t('物料编号')" :label-width="formLabelWidth">
                <el-input type="textarea" v-model="formData.itemNumberStr" auto-complete="off" :placeholder="$t('模糊查询')"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="$t('型号')" :label-width="formLabelWidth">
                <el-input v-model="formData.productName" auto-complete="off" :placeholder="$t('模糊查询')"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="search()">{{$t('确定')}}</el-button>
        </div>
      </search-dialog>
      <div ref="handsontable" style="height:1200px;overflow:hidden;margin-top:20px"></div>
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
    data(){
      return this.getData()
    },mounted() {
      console.log("companyName:"+this.companyName)
      if (this.companyName == "JXOPPO"){
        this.settings.colHeaders.push("LX对应货品");
        this.settings.columns.push(
          {
            data:'lxProductName',
            type: "autocomplete",
            allowEmpty: true,
            strict: true,
            width:200,
            tempLxProductNames:[],
            source: function (query, process) {
              var that = this;
              if (that.tempLxProductNames.indexOf(query) >= 0) {
                process(that.tempLxProductNames);
              } else {
                var productNames = new Array();
                if (query.length >= 2) {
                  axios.get('/api/global/tool/factory/oppo/oppoPlantAgentProductSel/findByProductName?name=' + query).then((response) => {
                    if (response.data.length > 0) {
                      for (var index in response.data) {
                        var productName = response.data[index].name;
                        productNames.push(productName);
                        if (that.tempLxProductNames.indexOf(productName) < 0) {
                          that.tempLxProductNames.push(productName);
                        }
                      }
                    }
                    process(productNames);
                  });
                } else {
                  process(productNames);
                }
              }
            }
          })
        }
        this.settings.colHeaders.push("货品型号");
        this.settings.columns.push({data:'typeName',strict: true,readOnly: true,width:100});
        //Handsontable初始化操作
        this.search();
        table = new Handsontable(this.$refs["handsontable"], this.settings);
    },methods: {
      setSearchText() {
        this.$nextTick(function () {
          this.searchText = util.getSearchText(this.$refs.searchDialog);
        })
      },getData() {
        return {
          companyName:JSON.parse(window.localStorage.getItem("account")).companyName,
          formData:{
          },
          inputForm:{
            extra:{}
          },
          loading:false,
          searchText:'',
          productNames:[],
          settings: {
            rowHeaders: true,
            autoColumnSize: true,
            stretchH: 'all',
            height: 650,
            startCols: 5,
            colHeaders: ['ID', '颜色Id', '颜色', '类型', '物料描述', '物料编码', 'TD对应货品'],
            columns: [
              {data:'id',strict: true,readOnly: true,width:50},
              {data:'colorId',strict: true,readOnly: true,width:50},
              {data:'colorName',strict: true,readOnly: true,width:50},
              {data:'brandType',strict: true,readOnly: true,width:50},
              {data:'itemDesc',strict: true,readOnly: true,width:200},
              {data:'itemNumber',strict: true,readOnly: true,width:50},
              {
                data:'productName',
                type: "autocomplete",
                allowEmpty: true,
                width:200,
                tempProductNames:[],
                source: function (query, process) {
                  var that = this;
                  if (that.tempProductNames.indexOf(query) >= 0) {
                    process(that.tempProductNames);
                  } else {
                    var productNames = new Array();
                    if (query.length >= 2) {
                      axios.get('/api/global/tool/factory/oppo/oppoPlantAgentProductSel/findByProductName?name=' + query).then((response) => {
                        if (response.data.length > 0) {
                          for (var index in response.data) {
                            var productName = response.data[index].name;
                            productNames.push(productName);
                            if (that.tempProductNames.indexOf(productName) < 0) {
                              that.tempProductNames.push(productName);
                            }
                          }
                        }
                        process(productNames);
                      });
                    } else {
                      process(productNames);
                    }
                  }
                }
              }
            ],
           contextMenu: util.contextMenu(this.$store.state.global.lang),
          },
          date:util.currentDate(),
          rules: {},
          submitDisabled: false,
          formLabelWidth: '120px',
          formVisible: false,
        };
      },formSubmit(){
        this.submitDisabled = true;
        let form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            this.inputForm.dataList = new Array();
            let tableData = [];
            let list = table.getData();
            for (let item in list) {
              if (!table.isEmptyRow(item)) {
                let row = list[item];
                let createForm = {};
                if(this.companyName == "JXOPPO"){
                  createForm.id = row[0];
                  createForm.productName = row[6];
                  createForm.lxProductName = row[7];
                }else{
                  createForm.id = row[0];
                  createForm.productName = row[6];
                }
                tableData.push(createForm);
              }
            }
            this.inputForm.dataList = JSON.stringify(tableData);
            axios.post('/api/global/tool/factory/oppo/oppoPlantAgentProductSel/save', qs.stringify({data: this.inputForm.dataList}, {allowDots: true})).then((response) => {
              this.$message(response.data.message);
              this.submitDisabled = false;
              if(response.data.success){
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
      },getTableData(){
        let submitData = util.deleteExtra(this.formData);
        axios.get('/api/global/tool/factory/oppo/oppoPlantAgentProductSel/findAll', {params: submitData}).then((response) => {
          this.settings.data = response.data;
          console.log(response.data.length);
          table.loadData(this.settings.data);
        });
      },search() {
        this.formVisible = false;
        this.setSearchText();
        this.getTableData();
      },synData(){
        if(this.date){
          this.loading = true;
          let companyName = JSON.parse(window.localStorage.getItem("account")).companyName;
          axios.get('/api/ws/future/third/factory/oppo/pullFactoryData?companyName='+companyName+'&date='+this.date).then((response)=>{
            this.loading = false;
            this.$message(response.data);
          }).catch(function () {
            this.loading = false;
            this.$message({message:"同步失败",type:'error'});
          });
        }else{
          this.$message({message:"请选择同步日期",type:'warning'});
        }
      },pullFactoryData() {
        this.loading = true;
        let companyName = JSON.parse(window.localStorage.getItem("account")).companyName;
        axios.get('/api/global/tool/factory/oppo/pullFactoryData?companyName='+companyName+'&date='+this.date).then((response)=>{
          this.loading = false;
          this.$message(response.data);
        }).catch(function () {
          this.loading = false;
          this.$message({message:"数据下拉失败",type:'error'});
        });
      },exportData(){
        if(this.date){
          window.location.href='/api/global/tool/factory/oppo/oppoPlantSendImeiPpsel/export?date='+this.date;
        }else{
          this.$message({message:"请选择发货串码导出日期",type:'warning'})
        }
      },initPage(){
        axios.get('/api/global/tool/factory/oppo/oppoPlantAgentProductSel/getQuery').then((response)=>{
          this.formData = response.data;
        })
      }
    },created(){
      this.initPage();
    }
  }
</script>
