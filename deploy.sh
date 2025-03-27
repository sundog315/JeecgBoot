#!/bin/bash
###
 # @Author: Janelle.Liu sundog315@foxmail.com
 # @Date: 2025-02-28 09:15:07
 # @LastEditors: Janelle.Liu sundog315@foxmail.com
 # @LastEditTime: 2025-05-10 16:30:40
 # @FilePath: /JeecgBoot/deploy.sh
 # @Description: 部署和重启脚本
### 

# 配置项
ROOT_PATH="/Users/sd/Documents/JeecgBoot"
REMOTE_HOST="192.168.88.10"
REMOTE_USER="root"
JAR_VERSION="3.7.2"
JAR_NAME="jeecg-system-start-${JAR_VERSION}.jar"
DEPLOY_PATH="/var/www/html"
SSH_OPTS="-o ConnectTimeout=10 -o StrictHostKeyChecking=no"

# 错误处理
set -e
trap 'echo "Error occurred at line $LINENO. Exit code: $?" >&2' ERR

# 显示使用说明
show_usage() {
    echo "使用方法: $0 [选项]"
    echo "选项:"
    echo "  -r, --restart    仅重启服务，不重新编译和部署"
    echo "  -h, --help       显示此帮助信息"
    echo "示例:"
    echo "  $0               完整部署（编译、部署和启动）"
    echo "  $0 -r            仅重启服务"
}

# 解析命令行参数
RESTART_ONLY=false
for arg in "$@"; do
    case $arg in
        -r|--restart)
            RESTART_ONLY=true
            shift
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            # 未知参数
            echo "错误: 未知参数 '$arg'"
            show_usage
            exit 1
            ;;
    esac
done

# 日志函数
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

# 检查SSH连接
check_ssh_connection() {
    if ! ssh ${SSH_OPTS} -q $REMOTE_USER@$REMOTE_HOST "exit"; then
        log "ERROR: Cannot connect to remote host $REMOTE_HOST"
        exit 1
    fi
}

# 重启服务
restart_service() {
    log "Checking SSH connection..."
    check_ssh_connection

    # 停止现有服务
    log "Stopping existing service..."
    if ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "pgrep -f '$JAR_NAME'" > /dev/null 2>&1; then
        ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "pkill -f '$JAR_NAME'" || true
        log "Waiting for service to stop..."
        sleep 5
    fi

    # 启动服务
    log "Starting service..."
    # 使用 ssh -f 在后台运行命令，并使用 () 创建子shell
    ssh ${SSH_OPTS} -f $REMOTE_USER@$REMOTE_HOST "( cd ~ && \
        java \
        -Xms256m \
        -Xmx1024m \
        -XX:+HeapDumpOnOutOfMemoryError \
        -Duser.timezone=GMT+08 \
        -jar $JAR_NAME \
        > app.log 2>&1 & \
        echo \$! > app.pid )"

    # 短暂等待确保进程已启动
    sleep 2

    # 检查进程是否成功启动
    log "Verifying process started..."
    if ! ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "ps -p \$(cat app.pid) > /dev/null 2>&1"; then
        log "ERROR: Failed to start process"
        ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "tail -n 50 app.log"
        exit 1
    fi
    
    log "Service restarted successfully"
}

# 根据参数选择执行模式
if [ "$RESTART_ONLY" = true ]; then
    log "运行模式: 仅重启服务"
    restart_service
    exit 0
fi

log "运行模式: 完整部署"

# 检查必要条件
if [ ! -d "$ROOT_PATH" ]; then
    log "ERROR: Root path $ROOT_PATH does not exist"
    exit 1
fi

# 切换Java环境
source ~/java8.sh
log "Switched to Java 8 environment"

# 后端构建
log "Building backend..."
cd "$ROOT_PATH/jeecg-boot" || exit 1
mvn clean compile package -DskipTests

# 前端构建
log "Building frontend..."
cd "$ROOT_PATH/jeecgboot-vue3" || exit 1
pnpm run build

# 部署后端
log "Deploying backend..."
check_ssh_connection

# 停止现有服务
log "Stopping existing service..."
if ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "pgrep -f '$JAR_NAME'" > /dev/null 2>&1; then
    ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "pkill -f '$JAR_NAME'" || true
    log "Waiting for service to stop..."
    sleep 5
fi

# 上传新版本
log "Uploading new version..."
scp ${SSH_OPTS} "$ROOT_PATH/jeecg-boot/jeecg-module-system/jeecg-system-start/target/$JAR_NAME" "$REMOTE_USER@$REMOTE_HOST:~/"

# 启动服务
log "Starting service..."
# 使用 ssh -f 在后台运行命令，并使用 () 创建子shell
ssh ${SSH_OPTS} -f $REMOTE_USER@$REMOTE_HOST "( cd ~ && \
    java \
    -Xms256m \
    -Xmx1024m \
    -XX:+HeapDumpOnOutOfMemoryError \
    -Duser.timezone=GMT+08 \
    -jar $JAR_NAME \
    > app.log 2>&1 & \
    echo \$! > app.pid )"

# 短暂等待确保进程已启动
sleep 2

# 检查进程是否成功启动
log "Verifying process started..."
if ! ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "ps -p \$(cat app.pid) > /dev/null 2>&1"; then
    log "ERROR: Failed to start process"
    ssh ${SSH_OPTS} $REMOTE_USER@$REMOTE_HOST "tail -n 50 app.log"
    exit 1
fi

# 部署前端
log "Deploying frontend..."
ssh -n $REMOTE_USER@$REMOTE_HOST "rm -rf $DEPLOY_PATH/*"
scp -rp "$ROOT_PATH/jeecgboot-vue3/dist/"* "$REMOTE_USER@$REMOTE_HOST:$DEPLOY_PATH/"

# 检查部署状态
log "Checking deployment status..."
sleep 5
if ssh -n $REMOTE_USER@$REMOTE_HOST "pgrep -f '$JAR_NAME' > /dev/null"; then
    log "Backend service is running"
else
    log "ERROR: Backend service failed to start"
    exit 1
fi

if ssh -n $REMOTE_USER@$REMOTE_HOST "[ -f $DEPLOY_PATH/index.html ]"; then
    log "Frontend deployed successfully"
else
    log "ERROR: Frontend deployment failed"
    exit 1
fi

log "Deployment completed successfully"
