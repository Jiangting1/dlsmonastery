<template>
  <div>
    <head-tab active="imeAllotBatchForm"></head-tab>
    <search-dialog :show="searchFormVisible" @hide="searchFormVisible=false" :title="$t('imeAllotBatchForm.filter')" v-model="searchFormVisible" size="tiny" class="search-form" z-index="1500">
      <el-form>
        <el-row :gutter="4">
          <el-col :span="24">
            <el-form-item :label="$t('imeAllotBatchForm.imeStr')">
              <el-input type="textarea" :rows="6" v-model="imeStr"  ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="search" icon="search">{{$t('imeAllotBatchForm.sure')}}</el-button>
      </div>
    </search-dialog>
    <div>
      <el-form  :model="inputForm"  ref="inputForm">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-button type="primary" @click="formSubmit" icon="check">保存</el-button>
            <el-button type="primary" @click="searchFormVisible = true">过滤</el-button>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="21">
            <el-form-item>
              <su-alert  type="danger" :text="errMsg"> </su-alert>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <div id="grid" ref="handsontable" style="width:100%;height:600px;overflow:hidden;margin-top: 20px;"></div>
          </el-col>
        </el-row>

      </el-form>
    </div>
  </div>
</template>
<style>
  @import "~handsontable/dist/handsontable.full.css";
</style>
<script>
  import Handsontable from 'handsontable/dist/handsontable.full.js'
  import suAlert from 'components/common/su-alert'
  var table = null;
  export default{
    components:{
      suAlert,
    },

    data(){
      return this.getData()
    },

    methods: {
      getData() {
        return {
          errMsg:'',
          searchFormVisible:false,
          imeStr:'',
          inputForm: {
              extra:{},
          },
          submitDisabled: false,
          settings: {
            rowHeaders: true,
            minSpareRows: 100,
            startRows: 100,
            startCols: 4,
            colHeaders: [ '串码', '调拨前', '调拨后',  '备注'],
            columns: [{
              data: "ime",
              strict: true,
              allowEmpty: true,
              width: 200
            }, {
              data: "depotName",
              readOnly: true,
              width: 300
            }, {
              type: "autocomplete",
              allowEmpty: false,
              strict: true,
              source: [],
              width: 300
            }, {
              strict: true,
              width: 200
            }],
           contextMenu: util.contextMenu(this.$store.state.global.lang),
          },

        };
      },
      search() {
        this.searchFormVisible = false;
        axios.post('/api/ws/future/crm/productIme/findDtoListByImes', qs.stringify({imeStr: this.imeStr})).then((response) => {
          table.loadData(response.data);
        });
        axios.post('/api/ws/future/crm/imeAllot/checkForImeAllot', qs.stringify({imeStr: this.imeStr})).then((response)=>{
          this.errMsg=response.data;
        });
      },
      formSubmit(){

        if (this.errMsg) {
          this.$alert( this.$t('imeAllotBatchForm.formInvalid'), this.$t('imeAllotBatchForm.notify'));
          return;
        }

        let form = this.$refs["inputForm"];
        form.validate((valid) => {
          if (valid) {
            this.submitDisabled = true;
            let tableData = [];

            let list = table.getData();
            for (let item in list) {
              if (!table.isEmptyRow(item)) {
                let row = list[item];
                let imeAllotSimpleForm = {};
                imeAllotSimpleForm.ime = row[0];
                imeAllotSimpleForm.toDepotName = row[2];
                imeAllotSimpleForm.remarks = row[3];
                tableData.push(imeAllotSimpleForm);
              }
            }
            this.inputForm.imeAllotSimpleFormList = tableData;

            axios.post('/api/ws/future/crm/imeAllot/batchAllot', qs.stringify(util.deleteExtra(this.inputForm), {allowDots: true})).then((response) => {
              this.$message(response.data.message);
              Object.assign(this.$data, this.getData());
              this.initPage();

            }).catch( () => {
              this.submitDisabled = false;
            });

          }
        })
      },initPage(){
        axios.get('/api/ws/future/crm/imeAllot/getImeAllotBatchForm').then((response)=>{
          this.inputForm = response.data;
          this.settings.columns[2].source = response.data.extra.toDepotNameList;
          table = new Handsontable(this.$refs["handsontable"], this.settings);
        });
      }
    },
    created() {
        this.initPage();
    },
  }
</script>
