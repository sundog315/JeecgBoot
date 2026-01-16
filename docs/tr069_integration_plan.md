# TR-069 (CWMP) 集成分析与计划

## 1. 概述
当前系统通过在终端运行 Shell 脚本主动推送数据。为支持不支持脚本化的设备，计划引入 TR-069 (CWMP) 协议支持。TR-069 是一种基于 SOAP/HTTP 的标准管理协议，适用于大规模 CPE（集成商设备）的远程管理。

## 2. TR-069 功能对比分析

### 2.1 可实现功能 (映射关系)
TR-069 通过标准数据模型（如 TR-181）可以完全覆盖当前 Shell 脚本采集的大大部分数据：

| 当前功能项 | TR-069 (TR-181) 对应参数 | 分析 |
| :--- | :--- | :--- |
| 设备基本信息 (IMEI/SN) | `Device.DeviceInfo.IMEI`, `Device.DeviceInfo.SerialNumber` | 标准参数，完全支持 |
| 模块版本 | `Device.DeviceInfo.SoftwareVersion` | 标准参数 |
| 网络状态 (RSRP/RSRQ/SINR) | `Device.Cellular.Interface.{i}.Signal.RSRP` / `RSRQ` / `SINR` | TR-181 标准支持 4G/5G 信号参数 |
| 流量统计 (Up/Down Bytes) | `Device.Cellular.Interface.{i}.Stats.BytesSent` / `Received` | 标准计数器 |
| SIM卡信息 (ICCID) | `Device.Cellular.Interface.{i}.SIM.ICCID` | 标准参数 |
| IP地址 (IPv4/v6) | `Device.IP.Interface.{i}.IPv4Address.{j}.IPAddress` | 标准参数 |
| 远程操作 (重启/固件升级) | `Reboot` 方法 / `Download` 方法 | TR-069 原生支持这些 RPC 方法 |

### 2.2 难以实现或受限的功能
1. **实时细粒度采集**: TR-069 通常基于周期性 `Inform` (如每 5-15 分钟)，虽可触发 `Connection Request` 进行即时下发，但高频率主动上报对服务器压力较大。
2. **非标准私有命令**: 某些 Shell 脚本中调用的私有 AT 指令或特定 `ubus` 输出，如果 TR-069 厂商未定义 `X-Vendor` 扩展参数，则无法获取。
3. **邻区信息 (Neighbor Cells)**: 虽然 TR-181 支持邻区列表，但并非所有入门级 CPE 固件都会完整实现该对象，可能通过脚本 `ubus` 获取更简单。

---

## 3. 集成计划

### 阶段一：ACS (自动配置服务器) 选型与预研
- **选型建议**: 
    - **方案 A (推荐)**: 集成 Java 开源 ACS 核心库（如基于 Spring Boot 的 OpenACS 变体），作为 JeecgBoot 的一个微服务模块。
    - **方案 B**: 采用成熟的 GenieACS (Node.js) 作为前置，通过 Webhook 或数据库共享与 Java 后端交互。
- **目标**: 搭建基础 SOAP 服务，能够接收 CPE 的 `Inform` 消息。

### 阶段二：数据映射层开发
- **开发任务**:
    1. 在 `jeecg-module-cpe` 中增加 CWMP 消息处理器。
    2. 开发 TR-181 参数模板映射，将获取到的 XML 参数解析并转换为 `CpeDeviceStatus` 实体。
    3. 实现设备自动注册逻辑：根据 `Inform` 中的 SN 自动在 `cpe_device` 表创建记录。

### 阶段三：北向接口集成
- **开发任务**:
    1. 在现有的设备列表页面增加“连接方式”标识（Shell vs TR-069）。
    2. 适配 TR-069 的异步操作流程（下发指令 -> 等待 CPE 回应 -> 更新状态）。

### 阶段四：安全与兼容性测试
- **任务**: 
    - 实现 CPE 摘要认证 (Digest Authentication)。
    - 使用不同厂商的 CPE 或模拟器 (如 CWMP-Simulator) 进行测试。

## 4. 后续步骤
1. **环境准备**: 在 `docs` 目录下建立 `tr069_specs` 文件夹，存放相关厂商的 Data Model 定义。
2. **原型开发**: 创建 `jeecg-module-acs` 模块，初步实现 `Inform` 接收与 `InformResponse` 回复。
3. **API 适配**: 调整 `CpeDeviceStatusServiceImpl` 的 `push` 逻辑，使其能兼容来自 ACS 生成的数据包。
