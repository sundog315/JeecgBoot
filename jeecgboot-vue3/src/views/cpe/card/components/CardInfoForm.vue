<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="CardInfoForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="卡号" v-bind="validateInfos.cardNo" id="CardInfoForm-cardNo" name="cardNo">
								<a-input v-model:value="formData.cardNo" placeholder="请输入卡号"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="短号" v-bind="validateInfos.shortNo" id="CardInfoForm-shortNo" name="shortNo">
								<a-input v-model:value="formData.shortNo" placeholder="请输入短号"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="接入号" v-bind="validateInfos.joinNo" id="CardInfoForm-joinNo" name="joinNo">
								<a-input v-model:value="formData.joinNo" placeholder="请输入接入号"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="卡片运营商" v-bind="validateInfos.netCorps" id="CardInfoForm-netCorps" name="netCorps">
								<j-dict-select-tag v-model:value="formData.netCorps" dictCode="cpe_network" placeholder="请选择卡片运营商"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="是否实名" v-bind="validateInfos.named" id="CardInfoForm-named" name="named">
								<j-dict-select-tag v-model:value="formData.named" dictCode="card_isnamed" placeholder="请选择是否实名"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="本周期上传量" v-bind="validateInfos.upBytes" id="CardInfoForm-upBytes" name="upBytes">
								<a-input-number v-model:value="formData.upBytes" placeholder="请输入本周期上传量" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="本周期下载量" v-bind="validateInfos.downBytes" id="CardInfoForm-downBytes" name="downBytes">
								<a-input-number v-model:value="formData.downBytes" placeholder="请输入本周期下载量" style="width: 100%" />
							</a-form-item>
						</a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../CardInfo.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  import { duplicateValidate } from '/@/utils/helper/validator'
  
  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({}) },
    formBpm: { type: Boolean, default: true }
  });
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
        cardNo: '',   
        shortNo: '',   
        joinNo: '',   
        netCorps: '',   
        named: undefined,
        upBytes: undefined,
        downBytes: undefined,
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = reactive({
    cardNo: [{ required: true, message: '请输入卡号!'}, { validator: cardNoDuplicatevalidate }],
    named: [{ required: true, message: '请输入是否实名!'},],
    upBytes: [{ required: true, message: '请输入本周期上传量!'},],
    downBytes: [{ required: true, message: '请输入本周期下载量!'},],
  });
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });
  const formRef = ref();
  // 表单禁用
  const disabled = computed(()=>{
    if(props.formBpm === true){
      if(props.formData.disabled === false){
        return false;
      }else{
        return true;
      }
    }
    return props.formDisabled;
  });

  
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
    await saveOrUpdate(model, isUpdate.value)
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


  async function cardNoDuplicatevalidate(_r, value) {
    return duplicateValidate('card_info', 'card_no', value, formData.id || '')
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
