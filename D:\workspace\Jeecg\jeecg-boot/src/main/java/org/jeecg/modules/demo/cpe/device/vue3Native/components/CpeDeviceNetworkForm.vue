<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form class="antd-modal-form" v-bind="formItemLayout" ref="formRef" name="CpeDeviceNetworkForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="内网地址" v-bind="validateInfos.ipaddr" id="CpeDeviceNetwork-ipaddr" name="ipaddr">
								<a-input v-model:value="formData.ipaddr" placeholder="请输入内网地址"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="子网掩码" v-bind="validateInfos.netmask" id="CpeDeviceNetwork-netmask" name="netmask">
								<a-input v-model:value="formData.netmask" placeholder="请输入子网掩码"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="DHCP起始地址" v-bind="validateInfos.dhcpStart" id="CpeDeviceNetwork-dhcpStart" name="dhcpStart">
								<a-input v-model:value="formData.dhcpStart" placeholder="请输入DHCP起始地址"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="DHCP截至地址" v-bind="validateInfos.dhcpEnd" id="CpeDeviceNetwork-dhcpEnd" name="dhcpEnd">
								<a-input v-model:value="formData.dhcpEnd" placeholder="请输入DHCP截至地址"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="DHCP租期" v-bind="validateInfos.dhcpLeasetime" id="CpeDeviceNetwork-dhcpLeasetime" name="dhcpLeasetime">
								<a-input v-model:value="formData.dhcpLeasetime" placeholder="请输入DHCP租期"  allow-clear ></a-input>
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
  import { cpeDeviceNetworkSaveOrUpdate } from '../CpeDeviceInfo.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';

  //接收主表id
  const mainId = inject('mainId');
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
        ipaddr: '',   
        netmask: '',   
        dhcpStart: '',   
        dhcpEnd: '',   
        dhcpLeasetime: '',   
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    ipaddr: [{ required: true, message: '请输入内网地址!'}, { pattern: '^((25[0-5]|2[0-4][0-9]|?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|?[0-9][0-9]?)$', message: '不符合校验规则!'},],
    netmask: [{ required: true, message: '请输入子网掩码!'}, { pattern: '^((25[0-5]|2[0-4][0-9]|?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|?[0-9][0-9]?)$', message: '不符合校验规则!'},],
    dhcpStart: [{ required: true, message: '请输入DHCP起始地址!'}, { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},],
    dhcpEnd: [{ required: true, message: '请输入DHCP截至地址!'}, { pattern: /^-?\d+\.?\d*$/, message: '请输入数字!'},],
    dhcpLeasetime: [{ required: true, message: '请输入DHCP租期!'},],
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
    await cpeDeviceNetworkSaveOrUpdate(model, isUpdate.value)
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
