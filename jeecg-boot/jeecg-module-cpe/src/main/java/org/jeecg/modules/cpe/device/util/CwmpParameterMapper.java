package org.jeecg.modules.cpe.device.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeDeviceStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * TR-069 Parameter Mapper util
 * Maps CWMP parameter paths to Entity fields.
 */
@Slf4j
public class CwmpParameterMapper {

    private static final Map<String, BiConsumer<String, Object[]>> MAPPING = new HashMap<>();

    static {
        // Device Info
        MAPPING.put("Device.DeviceInfo.Manufacturer",
                (val, objs) -> ((CpeDevice) objs[0]).setMemo("Manufacturer: " + val));
        MAPPING.put("Device.DeviceInfo.ModelName", (val, objs) -> ((CpeDevice) objs[0]).setDeviceModuleNo(val));
        MAPPING.put("Device.DeviceInfo.ModelNumber", (val, objs) -> {
            ((CpeDevice) objs[0]).setDeviceModuleNo(val);
            ((CpeDevice) objs[0]).setFiveGModule(val);
        });
        MAPPING.put("Device.DeviceInfo.SoftwareVersion", (val, objs) -> {
            ((CpeDeviceStatus) objs[1]).setModemVersion(val);
            ((CpeDeviceStatus) objs[1]).setOpenwrtVersion(val);
        });
        MAPPING.put("Device.DeviceInfo.HardwareVersion",
                (val, objs) -> ((CpeDeviceStatus) objs[1]).setModemVersion(val));
        MAPPING.put("Device.DeviceInfo.UpTime", (val, objs) -> ((CpeDeviceStatus) objs[1]).setSysUptime(val));
        MAPPING.put("Device.DeviceInfo.IMEI", (val, objs) -> ((CpeDeviceStatus) objs[1]).setImei(val));

        // Cellular Signal (Assuming Interface 1)
        MAPPING.put("Device.Cellular.Interface.1.Signal.RSRP",
                (val, objs) -> ((CpeDeviceStatus) objs[1]).setRsrp(ssRsrp(val)));
        MAPPING.put("Device.Cellular.Interface.1.Signal.RSRQ",
                (val, objs) -> ((CpeDeviceStatus) objs[1]).setRsrq(ssRsrq(val)));
        MAPPING.put("Device.Cellular.Interface.1.Signal.SINR",
                (val, objs) -> ((CpeDeviceStatus) objs[1]).setSinr(ssSinr(val)));
        MAPPING.put("Device.Cellular.Interface.1.SIM.ICCID", (val, objs) -> ((CpeDeviceStatus) objs[1]).setIccid(val));

        // Network Information
        MAPPING.put("Device.Cellular.Interface.1.CurrentBand", (val, objs) -> {
            String band = parseBand(val);
            ((CpeDevice) objs[0]).setOnlineBand(band);
            ((CpeDeviceStatus) objs[1]).setOnlineBand(band);
        });

        // Network Stats
        MAPPING.put("Device.Cellular.Interface.1.Stats.BytesSent", (val, objs) -> {
            try {
                if (val != null && !val.isEmpty()) {
                    ((CpeDeviceStatus) objs[1]).setUpBytes(Double.parseDouble(val));
                }
            } catch (Exception e) {
            }
        });
        MAPPING.put("Device.Cellular.Interface.1.Stats.BytesReceived", (val, objs) -> {
            try {
                if (val != null && !val.isEmpty()) {
                    ((CpeDeviceStatus) objs[1]).setDownBytes(Double.parseDouble(val));
                }
            } catch (Exception e) {
            }
        });

        // IP Address
        MAPPING.put("Device.IP.Interface.1.IPv4Address.1.IPAddress",
                (val, objs) -> ((CpeDeviceStatus) objs[1]).setIpv4(val));

        // Remote Management (TR-181)
        MAPPING.put("Device.ManagementServer.ConnectionRequestURL", (val, objs) -> {
            ((CpeDevice) objs[0]).setMemo("ConnectionRequestURL: " + val);
        });
    }

    /**
     * Map a TR-069 parameter map to entity objects.
     * Supports both Device (TR-181) and InternetGatewayDevice (TR-098) prefixes.
     * 
     * @param params Key-Value pairs from CWMP Inform/GetParameterValues
     * @param device Target CpeDevice entity
     * @param status Target CpeDeviceStatus entity
     */
    public static void mapParameters(Map<String, String> params, CpeDevice device, CpeDeviceStatus status) {
        Object[] context = new Object[] { device, status };
        params.forEach((path, value) -> {
            // Normalize path for compatibility: TR-098 (InternetGatewayDevice) -> TR-181
            // (Device)
            String normalizedPath = path.replace("InternetGatewayDevice.", "Device.");
            if (MAPPING.containsKey(normalizedPath)) {
                try {
                    MAPPING.get(normalizedPath).accept(value, context);
                } catch (Exception e) {
                    log.warn("Error mapping CWMP parameter {}: {}", path, e.getMessage());
                }
            } else {
                log.debug("Unmapped CWMP parameter ignored: {}", path);
            }
        });
    }

    private static String ssSinr(String str) {
        if (str == null || str.isEmpty() || "2147483647".equals(str))
            return "";
        try {
            int val = Integer.parseInt(str);
            return String.valueOf(val * 0.5 - 23) + "dB";
        } catch (Exception e) {
            return str;
        }
    }

    private static String ssRsrp(String str) {
        if (str == null || str.isEmpty() || "2147483647".equals(str))
            return "";
        try {
            int val = Integer.parseInt(str);
            return String.valueOf(val - 156) + "dBm";
        } catch (Exception e) {
            return str;
        }
    }

    private static String ssRsrq(String str) {
        if (str == null || str.isEmpty() || "2147483647".equals(str))
            return "";
        try {
            int val = Integer.parseInt(str);
            return String.valueOf(val * 0.5 - 43) + "dB";
        } catch (Exception e) {
            return str;
        }
    }

    private static String parseBand(String val) {
        if (val == null || val.isEmpty())
            return "";
        if (val.contains("HFREQINFO")) {
            // Parse ^HFREQINFO: 0,7,41,...
            String[] parts = val.split(",");
            if (parts.length > 2) {
                return "Band " + parts[2];
            }
        }
        return val;
    }
}
