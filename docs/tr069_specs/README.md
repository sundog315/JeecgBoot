# TR-181 Cellular Data Model Reference Guide

This document defines the mapping between the CPE Management Platform's internal data fields and the standard Broadband Forum **TR-181 Device:2** Data Model.

## 1. Core Parameter Mapping

| Platform Field | TR-181 Parameter Path | Type | Description |
| :--- | :--- | :--- | :--- |
| **IMEI** | `Device.DeviceInfo.IMEI` | string | International Mobile Equipment Identity |
| **Serial Number** | `Device.DeviceInfo.SerialNumber` | string | Device Hardware Serial Number |
| **Software Version**| `Device.DeviceInfo.SoftwareVersion` | string | Router/Gateway Firmware version |
| **ICCID** | `Device.Cellular.Interface.{i}.SIM.ICCID` | string | Integrated Circuit Card Identifier |
| **RSRP** | `Device.Cellular.Interface.{i}.Signal.RSRP` | int | Reference Signal Received Power |
| **RSRQ** | `Device.Cellular.Interface.{i}.Signal.RSRQ` | int | Reference Signal Received Quality |
| **SINR** | `Device.Cellular.Interface.{i}.Signal.SINR` | int | Signal to Interference plus Noise Ratio |
| **Up Bytes** | `Device.Cellular.Interface.{i}.Stats.BytesSent` | unsignedLong | Total bytes sent on the interface |
| **Down Bytes** | `Device.Cellular.Interface.{i}.Stats.BytesReceived` | unsignedLong| Total bytes received on the interface |
| **IPv4 Address** | `Device.IP.Interface.{i}.IPv4Address.{j}.IPAddress` | string | Current IPv4 Address |

## 2. Advanced / Vendor Extensions (Examples)

For parameters not covered by TR-181, vendors typically use the `X_` prefix.

| Field | Example Vendor Path | Source |
| :--- | :--- | :--- |
| **Module Version**| `Device.DeviceInfo.X_VENDOR_ModemVersion` | Quectel/Fibocom Extension |
| **CPU Temp** | `Device.DeviceInfo.X_VENDOR_CPUTemperature` | Hardware Sensor |
| **Neighbor Cells**| `Device.Cellular.Interface.{i}.X_VENDOR_NeighborCells` | Customized Object |

## 3. Reference Files
- [standard-tr181-cellular.xml](./standard-tr181-cellular.xml): Template for standard TR-181 cellular parameters.
- [vendor-extension-template.xml](./vendor-extension-template.xml): Structure for adding vendor-specific parameters.
