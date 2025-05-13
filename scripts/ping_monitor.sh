#!/bin/sh

VERSION="1.0.2"
# 目标IP地址
TARGET_IP1="8.8.8.8"
TARGET_IP2="1.1.1.1"

# Ping的次数
PING_COUNT=3

# GPIO操作路径
GPIO_PATH="/sys/class/gpio/5gpower/value"

# 日志文件路径
LOG_DIR="/etc/5g"
LOG_FILE="$LOG_DIR/ping_monitor.log"
STATUS_FILE="$LOG_DIR/ping_monitor_status.dat"
LOCK_FILE="$LOG_DIR/ping_monitor.lock"

# 初始间隔时间（分钟）
INITIAL_INTERVAL=2
MAX_INTERVAL=20
SILENT_PERIOD=10  # 系统启动后的静默期（分钟）

# 检查系统启动时间
check_uptime_silent_period() {
    # 获取uptime输出并提取分钟数
    local uptime_str=$(uptime)
    local uptime_min

    # 解析uptime字符串，提取分钟数
    if echo "$uptime_str" | grep -q "min"; then
        # 如果包含"min"，说明运行时间小于1小时
        uptime_min=$(echo "$uptime_str" | sed 's/.*up \([0-9]*\) min.*/\1/')
    else
        # 如果运行时间超过1小时，则已经超过静默期，直接返回
        return 0
    fi

    # 检查是否处于静默期
    if [ -n "$uptime_min" ] && [ "$uptime_min" -lt "$SILENT_PERIOD" ]; then
        log_info "系统启动时间（${uptime_min}分钟）小于静默期（${SILENT_PERIOD}分钟），暂不执行检查"
        return 1
    fi

    return 0
}

# 确保日志目录存在
ensure_log_dir() {
    if [ ! -d "$LOG_DIR" ]; then
        mkdir -p "$LOG_DIR"
    fi
}

# 锁定机制，防止同时执行多个实例
acquire_lock() {
    if [ -e "$LOCK_FILE" ]; then
        PID=$(cat "$LOCK_FILE")
        if kill -0 "$PID" 2>/dev/null; then
            log_error "另一个脚本实例正在运行 (PID: $PID)，退出。"
            exit 1
        else
            log_info "发现过期锁文件，清除。"
        fi
    fi
    echo $$ > "$LOCK_FILE"
}

release_lock() {
    rm -f "$LOCK_FILE"
}

# 日志记录函数
log_info() {
    logger -t "ping_monitor" -p user.info "$1"
    echo "[INFO] $(date '+%Y-%m-%d %H:%M:%S') - $1"
}

log_error() {
    logger -t "ping_monitor" -p user.error "$1"
    echo "[ERROR] $(date '+%Y-%m-%d %H:%M:%S') - $1" >&2
}

# 记录到文件日志
log_to_file() {
    ensure_log_dir
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" >> "$LOG_FILE"
}

# 获取当前时间戳（秒）
get_current_time() {
    date +%s
}

# 初始化或读取状态文件
init_or_read_status() {
    ensure_log_dir
    if [ ! -f "$STATUS_FILE" ]; then
        # 初始化状态：上次重启时间 当前等待间隔 网络状态(0=正常,1=异常)
        echo "0 $INITIAL_INTERVAL 0" > "$STATUS_FILE"
        log_info "初始化状态文件：初始间隔为 $INITIAL_INTERVAL 分钟"
    fi

    # 读取状态
    STATUS=$(cat "$STATUS_FILE")
    LAST_RESTART=$(echo "$STATUS" | cut -d' ' -f1)
    CURRENT_INTERVAL=$(echo "$STATUS" | cut -d' ' -f2)
    NETWORK_STATUS=$(echo "$STATUS" | cut -d' ' -f3)

    log_info "读取状态：上次重启时间=$(date -d @$LAST_RESTART '+%Y-%m-%d %H:%M:%S' 2>/dev/null || echo 'never')，当前等待间隔=$CURRENT_INTERVAL分钟，网络状态=$NETWORK_STATUS"

    # 导出为全局变量
    export LAST_RESTART
    export CURRENT_INTERVAL
    export NETWORK_STATUS
}

# 更新状态文件
update_status() {
    local last_restart=$1
    local interval=$2
    local network_status=$3

    echo "$last_restart $interval $network_status" > "$STATUS_FILE"
    log_info "更新状态：上次重启时间=$(date -d @$last_restart '+%Y-%m-%d %H:%M:%S' 2>/dev/null || echo 'never')，当前等待间隔=$interval分钟，网络状态=$network_status"
}

# 检查网络连接的函数
check_connection() {
    ALL_FAIL=1  # 假设所有目标都不可达

    for TARGET_IP in $TARGET_IP1 $TARGET_IP2; do
        FAIL_COUNT=0

        # 对目标IP进行ping测试
        for i in $(seq 1 $PING_COUNT); do
            ping -c 1 -w 1 $TARGET_IP > /dev/null 2>&1
            if [ $? -ne 0 ]; then
                FAIL_COUNT=$((FAIL_COUNT+1))
            fi
        done

        # 如果有一次ping成功，则认为网络正常
        if [ $FAIL_COUNT -lt $PING_COUNT ]; then
            ALL_FAIL=0
            break  # 如果某个目标可达，退出循环
        fi
    done

    return $ALL_FAIL  # 如果所有目标都失败，返回 1，否则返回 0
}

# 重启模块的函数
restart_module() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    local restart_msg="重启 5G 模块... (时间戳: $timestamp)"

    # 记录到系统日志
    log_info "$restart_msg"

    # 记录到文件日志
    log_to_file "5G模块重启触发 - 网络连接失败"

    # 执行重启操作
    echo 1 > $GPIO_PATH
    sleep 20
    echo 0 > $GPIO_PATH

    # 记录重启完成
    local complete_msg="5G 模块已重启。"
    log_info "$complete_msg"
    log_to_file "5G模块重启完成"

    # 更新最后重启时间
    local current_time=$(get_current_time)
    update_status "$current_time" "$CURRENT_INTERVAL" 1
}

# 计算下一个等待间隔
calculate_next_interval() {
    local current=$1
    local next_interval=$((current * 3))

    # 确保不超过最大值
    if [ "$next_interval" -gt "$MAX_INTERVAL" ]; then
        next_interval=$MAX_INTERVAL
    fi

    echo "$next_interval"
}

# 主流程
perform_check() {
    # 检查是否在静默期
    check_uptime_silent_period || exit 0

    # 初始化或读取状态
    init_or_read_status

    # 第一次检查
    check_connection
    if [ $? -ne 0 ]; then
        log_info "第一次网络检查失败，等待 5 秒..."
        sleep 5

        # 第二次检查
        check_connection
        if [ $? -ne 0 ]; then
            log_error "第二次网络检查失败，准备评估是否重启模块。"

            # 检查是否已经到达可以重启的时间
            local current_time=$(get_current_time)
            local time_since_last_restart=$((current_time - LAST_RESTART))
            local required_wait_seconds=$((CURRENT_INTERVAL * 60))

            if [ "$NETWORK_STATUS" -eq 0 ] || [ "$LAST_RESTART" -eq 0 ]; then
                # 如果网络之前是正常的，或者这是第一次重启，立即重启
                log_info "网络新故障或初始状态，立即重启模块。"
                restart_module

                # 设置下一次等待间隔
                CURRENT_INTERVAL=$(calculate_next_interval "$INITIAL_INTERVAL")
                update_status "$(get_current_time)" "$CURRENT_INTERVAL" 1
            elif [ "$time_since_last_restart" -ge "$required_wait_seconds" ]; then
                # 如果已经等待了足够长的时间，进行重启
                log_info "已经等待了 $time_since_last_restart 秒 (要求: $required_wait_seconds 秒)，重启模块。"
                restart_module

                # 计算下一次等待间隔
                CURRENT_INTERVAL=$(calculate_next_interval "$CURRENT_INTERVAL")
                update_status "$(get_current_time)" "$CURRENT_INTERVAL" 1
            else
                # 尚未到达重启时间
                local remaining_seconds=$((required_wait_seconds - time_since_last_restart))
                local remaining_minutes=$((remaining_seconds / 60))
                log_error "网络仍然故障，但需要等待 $remaining_minutes 分钟 ($remaining_seconds 秒) 才能再次重启。"
                update_status "$LAST_RESTART" "$CURRENT_INTERVAL" 1
            fi
        else
            log_info "第二次网络检查成功，网络正常。"

            # 如果网络状态之前是异常的，现在恢复了，重置间隔
            if [ "$NETWORK_STATUS" -eq 1 ]; then
                log_info "网络已恢复正常，重置等待间隔为 $INITIAL_INTERVAL 分钟。"
                update_status "$LAST_RESTART" "$INITIAL_INTERVAL" 0
            else
                update_status "$LAST_RESTART" "$CURRENT_INTERVAL" 0
            fi
        fi
    else
        log_info "第一次网络检查成功，网络正常。"

        # 如果网络状态之前是异常的，现在恢复了，重置间隔
        if [ "$NETWORK_STATUS" -eq 1 ]; then
            log_info "网络已恢复正常，重置等待间隔为 $INITIAL_INTERVAL 分钟。"
            update_status "$LAST_RESTART" "$INITIAL_INTERVAL" 0
        else
            update_status "$LAST_RESTART" "$CURRENT_INTERVAL" 0
        fi
    fi
}

# 添加脚本启动日志
log_info "网络连接监控脚本启动 (版本 $VERSION)"

# 获取锁
acquire_lock

# 执行检查
perform_check

# 释放锁
release_lock

# 添加脚本结束日志
log_info "网络连接监控脚本执行完毕"