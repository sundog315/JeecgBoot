package org.jeecg.modules.cpe.device.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Data;

/**
 * FRP配置解析器
 */
public class FrpConfigParser implements Cloneable {

    /**
     * FRP配置类
     * 实现Cloneable接口以支持对象克隆
     */
    @Data
    @Builder
    public static class FrpConfig implements Cloneable {
        private String serverAddr;    // 服务器地址
        private Integer serverPort;   // 服务器端口
        private String token;         // 认证令牌
        private List<ServiceConfig> services;  // 服务配置列表

        /**
         * 深度克隆FrpConfig对象
         * @return 克隆后的FrpConfig对象
         * @throws CloneNotSupportedException 如果克隆失败
         */
        @Override
        public Object clone() throws CloneNotSupportedException {
            FrpConfig cloned = (FrpConfig) super.clone();

            // 深度克隆services列表
            if (this.services != null) {
                List<ServiceConfig> clonedServices = new ArrayList<>();
                for (ServiceConfig service : this.services) {
                    clonedServices.add((ServiceConfig) service.clone());
                }
                cloned.setServices(clonedServices);
            }

            return cloned;
        }

        /**
         * 重写equals方法，用于配置比较
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FrpConfig that = (FrpConfig) o;
            return Objects.equals(serverAddr, that.serverAddr) &&
                Objects.equals(serverPort, that.serverPort) &&
                Objects.equals(token, that.token) &&
                Objects.equals(services, that.services);
        }

        /**
         * 重写hashCode方法
         */
        @Override
        public int hashCode() {
            return Objects.hash(serverAddr, serverPort, token, services);
        }
    }

    /**
     * 服务配置类
     * 实现Cloneable接口以支持对象克隆
     */
    @Data
    @Builder
    public static class ServiceConfig implements Cloneable {
        private String name;         // 服务名称
        private String type;         // 服务类型
        private String localIp;      // 本地IP
        private Integer localPort;   // 本地端口
        private Integer remotePort;  // 远程端口
        private Boolean useEncryption;  // 是否加密
        private Boolean useCompression; // 是否压缩

        /**
         * 克隆ServiceConfig对象
         * @return 克隆后的ServiceConfig对象
         * @throws CloneNotSupportedException 如果克隆失败
         */
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();  // 基本类型和String的浅克隆足够
        }

        /**
         * 重写equals方法，用于配置比较
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ServiceConfig that = (ServiceConfig) o;
            return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(localIp, that.localIp) &&
                Objects.equals(localPort, that.localPort) &&
                Objects.equals(remotePort, that.remotePort) &&
                Objects.equals(useEncryption, that.useEncryption) &&
                Objects.equals(useCompression, that.useCompression);
        }

        /**
         * 重写hashCode方法
         */
        @Override
        public int hashCode() {
            return Objects.hash(name, type, localIp, localPort, remotePort, 
                            useEncryption, useCompression);
        }
    }

    /**
     * 解析FRP配置字符串
     * @param content FRP配置内容
     * @return FRP配置对象
     */
    public static FrpConfig parse(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("配置内容不能为空");
        }

        FrpConfig.FrpConfigBuilder configBuilder = FrpConfig.builder();
        List<ServiceConfig> services = new ArrayList<>();

        // 当前配置块名称
        String currentSection = null;
        ServiceConfig.ServiceConfigBuilder serviceBuilder = null;

        // 按行处理配置
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();

            // 跳过空行和注释
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            // 处理配置块开始
            if (line.startsWith("config conf")) {
                // 保存之前的服务配置
                if (serviceBuilder != null) {
                    services.add(serviceBuilder.build());
                }

                // 提取配置块名称
                currentSection = line.substring(12).trim().replace("'", "");

                // 如果不是common块，创建新的服务配置构建器
                if (!"common".equals(currentSection)) {
                    serviceBuilder = ServiceConfig.builder().name(currentSection);
                }
                continue;
            }

            // 处理配置选项
            if (line.startsWith("option")) {
                String[] parts = line.split(" ", 3);
                if (parts.length < 3) continue;

                String key = parts[1];
                String value = parts[2].replace("'", "").trim();

                // 根据当前配置块处理选项
                if ("common".equals(currentSection)) {
                    switch (key) {
                        case "server_addr":
                            configBuilder.serverAddr(value);
                            break;
                        case "server_port":
                            configBuilder.serverPort(Integer.parseInt(value));
                            break;
                        case "token":
                            configBuilder.token(value);
                            break;
                    }
                } else if (serviceBuilder != null) {
                    switch (key) {
                        case "type":
                            serviceBuilder.type(value);
                            break;
                        case "local_ip":
                            serviceBuilder.localIp(value);
                            break;
                        case "local_port":
                            serviceBuilder.localPort(Integer.parseInt(value));
                            break;
                        case "remote_port":
                            serviceBuilder.remotePort(Integer.parseInt(value));
                            break;
                        case "use_encryption":
                            serviceBuilder.useEncryption(Boolean.parseBoolean(value));
                            break;
                        case "use_compression":
                            serviceBuilder.useCompression(Boolean.parseBoolean(value));
                            break;
                    }
                }
            }
        }

        // 添加最后一个服务配置
        if (serviceBuilder != null) {
            services.add(serviceBuilder.build());
        }

        // 构建并返回配置对象
        return configBuilder.services(services).build();
    }

    /**
     * 克隆FrpConfigParser对象
     * @return 克隆后的FrpConfigParser对象
     * @throws CloneNotSupportedException 如果克隆失败
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        FrpConfigParser cloned = (FrpConfigParser) super.clone();
        // 这里可以添加其他需要克隆的字段
        return cloned;
    }
}