<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form class="antd-modal-form" v-bind="formItemLayout" ref="formRef" name="CpeDeviceStatusForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="时间戳" v-bind="validateInfos.ts" id="CpeDeviceStatus-ts" name="ts">
								<a-date-picker placeholder="请选择时间戳"  v-model:value="formData.ts" showTime value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="RAT" v-bind="validateInfos.rat" id="CpeDeviceStatus-rat" name="rat">
								<a-input v-model:value="formData.rat" placeholder="请输入RAT"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="频段" v-bind="validateInfos.onlineBand" id="CpeDeviceStatus-onlineBand" name="onlineBand">
								<a-input v-model:value="formData.onlineBand" placeholder="请输入频段"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="运营商名称" v-bind="validateInfos.cops" id="CpeDeviceStatus-cops" name="cops">
								<a-input v-model:value="formData.cops" placeholder="请输入运营商名称"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="SINR" v-bind="validateInfos.sinr" id="CpeDeviceStatus-sinr" name="sinr">
								<a-input v-model:value="formData.sinr" placeholder="请输入SINR"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="RSRP" v-bind="validateInfos.rsrp" id="CpeDeviceStatus-rsrp" name="rsrp">
								<a-input v-model:value="formData.rsrp" placeholder="请输入RSRP"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="RSRQ" v-bind="validateInfos.rsrq" id="CpeDeviceStatus-rsrq" name="rsrq">
								<a-input v-model:value="formData.rsrq" placeholder="请输入RSRQ"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="CELLID" v-bind="validateInfos.cellId" id="CpeDeviceStatus-cellId" name="cellId">
								<a-input v-model:value="formData.cellId" placeholder="请输入CELLID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="PCID" v-bind="validateInfos.pcid" id="CpeDeviceStatus-pcid" name="pcid">
								<a-input v-model:value="formData.pcid" placeholder="请输入PCID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="ARFCN" v-bind="validateInfos.arfcn" id="CpeDeviceStatus-arfcn" name="arfcn">
								<a-input v-model:value="formData.arfcn" placeholder="请输入ARFCN"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="信号带宽" v-bind="validateInfos.bandwidth" id="CpeDeviceStatus-bandwidth" name="bandwidth">
								<a-input v-model:value="formData.bandwidth" placeholder="请输入信号带宽"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="连接状态" v-bind="validateInfos.linkStatus" id="CpeDeviceStatus-linkStatus" name="linkStatus">
								<a-input v-model:value="formData.linkStatus" placeholder="请输入连接状态"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="连接类型" v-bind="validateInfos.linkType" id="CpeDeviceStatus-linkType" name="linkType">
								<a-input v-model:value="formData.linkType" placeholder="请输入连接类型"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="MCC" v-bind="validateInfos.mcc" id="CpeDeviceStatus-mcc" name="mcc">
								<a-input v-model:value="formData.mcc" placeholder="请输入MCC"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="MNC" v-bind="validateInfos.mnc" id="CpeDeviceStatus-mnc" name="mnc">
								<a-input v-model:value="formData.mnc" placeholder="请输入MNC"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="IPV4" v-bind="validateInfos.ipv4" id="CpeDeviceStatus-ipv4" name="ipv4">
								<a-input v-model:value="formData.ipv4" placeholder="请输入IPV4"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="IPV6" v-bind="validateInfos.ipv6" id="CpeDeviceStatus-ipv6" name="ipv6">
								<a-input v-model:value="formData.ipv6" placeholder="请输入IPV6"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="DNS1" v-bind="validateInfos.dns1" id="CpeDeviceStatus-dns1" name="dns1">
								<a-input v-model:value="formData.dns1" placeholder="请输入DNS1"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="DNS2" v-bind="validateInfos.dns2" id="CpeDeviceStatus-dns2" name="dns2">
								<a-input v-model:value="formData.dns2" placeholder="请输入DNS2"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="上行流量" v-bind="validateInfos.upBytes" id="CpeDeviceStatus-upBytes" name="upBytes">
								<a-input-number v-model:value="formData.upBytes" placeholder="请输入上行流量" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="下行流量" v-bind="validateInfos.downBytes" id="CpeDeviceStatus-downBytes" name="downBytes">
								<a-input-number v-model:value="formData.downBytes" placeholder="请输入下行流量" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="连接时长" v-bind="validateInfos.uptime" id="CpeDeviceStatus-uptime" name="uptime">
								<a-input v-model:value="formData.uptime" placeholder="请输入连接时长"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, onMounted, inject, defineProps, unref } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getValueType } from '/@/utils';
  import { cpeDeviceStatusSaveOrUpdate } from '../CpeDeviceInfo.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';

  //接收主表id
  const mainId = inject('mainId');
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
        ts: '',   
        rat: '',   
        onlineBand: '',   
        cops: '',   
        sinr: '',   
        rsrp: '',   
        rsrq: '',   
        cellId: '',   
        pcid: '',   
        arfcn: '',   
        bandwidth: '',   
        linkStatus: '',   
        linkType: '',   
        mcc: '',   
        mnc: '',   
        ipv4: '',   
        ipv6: '',   
        dns1: '',   
        dns2: '',   
        upBytes: undefined,
        downBytes: undefined,
        uptime: '',   
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    ts: [{ required: true, message: '请输入时间戳!'},],
    upBytes: [{ required: true, message: '请输入上行流量!'},],
    downBytes: [{ required: true, message: '请输入下行流量!'},],
  };
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });
  const props = defineProps({
    disabled: { type: Boolean, default: false },
  });
  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 5 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };
  
  /**
   * 新增
   */
  function add() {
    edit({});
  }

  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      const tmpData = {};
      Object.keys(formData).forEach((key) => {
        if(record.hasOwnProperty(key)){
          tmpData[key] = record[key]
        }
      })
      //赋值
      Object.assign(formData,tmpData);
    });
  }

  /**
   * 提交数据
   */
  async function submitForm() {
    // 触发表单验证
    try {
      // 触发表单验证
      await validate();
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          formRef.value.scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    }
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.id) {
      isUpdate.value = true;
    }
   
    //循环数据
    for (let data in model) {
      //如果该数据是数组并且是字符串类型
      if (model[data] instanceof Array) {
        let valueType = getValueType(formRef.value.getProps, data);
        //如果是字符串类型的需要变成以逗号分割的字符串
        if (valueType === 'string') {
          model[data] = model[data].join(',');
        }
      }
    }
    if (unref(mainId)) {
      model['cpeId'] = unref(mainId);
    }
    await cpeDeviceStatusSaveOrUpdate(model, isUpdate.value)
      .then((res) => {
        if (res.success) {
          createMessage.success(res.message);
          emit('ok');
        } else {
          createMessage.warning(res.message);
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }


  defineExpose({
    add,
    edit,
    submitForm,
  });
</script>

<style lang="less" scoped>
  .antd-modal-form {
    padding: 14px;
  }
</style>
