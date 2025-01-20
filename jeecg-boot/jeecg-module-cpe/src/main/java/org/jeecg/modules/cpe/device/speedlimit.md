使用shell脚本，分析网络速率控制脚本/etc/5g/tc，文件内容为：
#!/bin/sh

UP_SPEEDLIMIT="1000mbit"
DOWN_SPEEDLIMIT="1000mbit"

# 通用函数：设置限速
set_speed_limit() {
    local dev=$1
    local speed=$2

    # 删除已有的 qdisc 配置，避免冲突
    tc qdisc del dev $dev root 2>/dev/null

    # 创建新的 qdisc 和 class
    tc qdisc add dev $dev root handle 1: htb default 10
    tc class add dev $dev parent 1: classid 1:1 htb rate $speed ceil $speed
    tc class add dev $dev parent 1: classid 1:10 htb rate $speed ceil $speed

    # 应用到所有源 IP 的流量
    tc filter add dev $dev protocol ip parent 1:0 prio 1 u32 match ip src 0.0.0.0/0 flowid 1:1
}

# 设置上传和下载限速
set_speed_limit usb0 $UP_SPEEDLIMIT  # 限制上传速度
set_speed_limit br-lan $DOWN_SPEEDLIMIT  # 限制下载速度

找到脚本中的UP_SPEEDLIMIT、DOWN_SPEEDLIMIT，并组合为speed_limit参数，修改pushEvent.sh脚本，将speed_limit参数传递至服务器。
请注意，/etc/5g/tc脚本可能不存在，当不存在时不向服务器传递speed_limit。

同时，修改pullOper.sh脚本，当oper参数为speed_limit时：
执行命令tc -s class show dev br-lan，返回结果类似：
class htb 1:10 root prio 0 rate 1Gbit ceil 1Gbit burst 1375b cburst 1375b
 Sent 156859 bytes 537 pkt (dropped 0, overlimits 2 requeues 0)
 backlog 0b 0p requeues 0
 lended: 537 borrowed: 0 giants: 0
 tokens: 173 ctokens: 173

class htb 1:1 root prio 0 rate 1Gbit ceil 1Gbit burst 1375b cburst 1375b
 Sent 131752 bytes 319 pkt (dropped 0, overlimits 2 requeues 0)
 backlog 0b 0p requeues 0
 lended: 319 borrowed: 0 giants: 0
 tokens: 180 ctokens: 180
找到htb 1:10的条目，并获取rate后的“1Gbit”值（请注意，这里需要将Gbit转换为mbit，且转换规则为乘1000）将此值与从服务器获取的值进行对比，如果不同，则使用服务器中的数值替换/etc/5g/tc脚本的值，并调用/etc/init.d/tc_limiter restart命令，从而使新限速值生效。