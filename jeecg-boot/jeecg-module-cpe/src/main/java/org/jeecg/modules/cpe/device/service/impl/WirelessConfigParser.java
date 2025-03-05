package org.jeecg.modules.cpe.device.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 无线网络配置解析器
 */
@Slf4j
public class WirelessConfigParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 无线设备配置类
     */
    @Data
    @Builder
    public static class RadioConfig {
        private String type;        // 设备类型
        private String channel;     // 信道
        private String htmode;      // 频宽模式
        private String hwmode;      // 硬件模式
        private Integer power;      // 功率
    }

    /**
     * 无线接口配置类
     */
    @Data
    @Builder
    public static class WifiConfig {
        private String device;      // 关联的无线设备
        private String mode;        // 工作模式
        private String ssid;        // SSID
        private String encryption;  // 加密方式
        private String key;         // 密码
        private String disabled;   // 禁用标志
        private Integer maxsta;     // 最大终端数
        private String macfilter;   //MAC过滤
        private String hidden;      //不广播
    }

    /**
     * 完整无线配置类
     */
    @Data
    @Builder
    public static class WirelessConfig {
        private FileDate fileDate;   // 文件修改时间
        private RadioConfig radio0;  // 2.4G无线设备配置
        private RadioConfig radio1;  // 5G无线设备配置
        private WifiConfig wlan0;    // 2.4G无线接口配置
        private WifiConfig wlan1;    // 5G无线接口配置
    }

    /**
     * 完整无线配置类
     */
    @Data
    @Builder
    public static class FileDate {
        private String modify_date;
    }

    /**
     * 解析无线配置JSON内容
     * @param jsonConfig JSON格式的配置内容
     * @return 解析后的WirelessConfig对象
     */
    public static WirelessConfig parse(String jsonConfig) {
        if (jsonConfig == null || jsonConfig.trim().isEmpty()) {
            return null;
        }

        try {
            JsonNode root = objectMapper.readTree(jsonConfig);

            FileDate fileDate = FileDate.builder()
                .modify_date(getStringValue(root, "last_modified"))
                .build();

            // 构建2.4G配置
            RadioConfig radio0 = RadioConfig.builder()
                    .channel(getStringValue(root, "2g_channel"))
                    .power(getIntValue(root, "2g_power", 100))
                    .build();

            WifiConfig wlan0 = WifiConfig.builder()
                    .ssid(getStringValue(root, "2g_ssid"))
                    .encryption(getStringValue(root, "2g_encryption"))
                    .key(getStringValue(root, "2g_key"))
                    .disabled(getStringValue(root, "2g_disabled"))
                    .maxsta(getIntValue(root, "2g_maxsta", 128))
                    .macfilter(getStringValue(root, "2g_macfilter"))
                    .hidden(getStringValue(root, "2g_hidden"))
                    .build();

            // 构建5G配置
            RadioConfig radio1 = RadioConfig.builder()
                    .channel(getStringValue(root, "5g_channel"))
                    .power(getIntValue(root, "5g_power", 100))
                    .build();

            WifiConfig wlan1 = WifiConfig.builder()
                    .ssid(getStringValue(root, "5g_ssid"))
                    .encryption(getStringValue(root, "5g_encryption"))
                    .key(getStringValue(root, "5g_key"))
                    .disabled(getStringValue(root, "5g_disabled"))
                    .maxsta(getIntValue(root, "5g_maxsta", 128))
                    .macfilter(getStringValue(root, "5g_macfilter"))
                    .hidden(getStringValue(root, "5g_hidden"))
                    .build();

            return WirelessConfig.builder()
                    .fileDate(fileDate)
                    .radio0(radio0)
                    .radio1(radio1)
                    .wlan0(wlan0)
                    .wlan1(wlan1)
                    .build();

        } catch (Exception e) {
            log.error("解析无线配置JSON失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取字符串值
     */
    private static String getStringValue(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        return fieldNode != null && !fieldNode.isNull() ? fieldNode.asText() : null;
    }

    /**
     * 获取整数值
     */
    private static Integer getIntValue(JsonNode node, String field, Integer defaultValue) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode != null && !fieldNode.isNull()) {
            try {
                return fieldNode.asInt();
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
} 