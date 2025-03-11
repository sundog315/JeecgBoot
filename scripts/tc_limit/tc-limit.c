#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>

#define VERSION "1.0.0"
#define DEFAULT_UP_SPEED "1000mbit"
#define DEFAULT_DOWN_SPEED "1000mbit"
#define MAX_CMD_LEN 256

// 执行系统命令并检查返回值
static int execute_cmd(const char *cmd) {
    int ret = system(cmd);
    if (ret == -1) {
        fprintf(stderr, "Failed to execute command: %s\n", cmd);
        return -1;
    }
    return WEXITSTATUS(ret);
}

// 设置速度限制
static int set_speed_limit(const char *dev, const char *speed) {
    char cmd[MAX_CMD_LEN];

    // 删除已有的 qdisc 配置
    snprintf(cmd, sizeof(cmd), "tc qdisc del dev %s root 2>/dev/null", dev);
    execute_cmd(cmd);

    // 创建新的 qdisc 和 class
    snprintf(cmd, sizeof(cmd), "tc qdisc add dev %s root handle 1: htb default 10", dev);
    if (execute_cmd(cmd) != 0) {
        fprintf(stderr, "Failed to add root qdisc on %s\n", dev);
        return -1;
    }

    snprintf(cmd, sizeof(cmd), "tc class add dev %s parent 1: classid 1:1 htb rate %s ceil %s",
            dev, speed, speed);
    if (execute_cmd(cmd) != 0) {
        fprintf(stderr, "Failed to add class 1:1 on %s\n", dev);
        return -1;
    }

    snprintf(cmd, sizeof(cmd), "tc class add dev %s parent 1: classid 1:10 htb rate %s ceil %s",
            dev, speed, speed);
    if (execute_cmd(cmd) != 0) {
        fprintf(stderr, "Failed to add class 1:10 on %s\n", dev);
        return -1;
    }

    // 应用到所有源 IP 的流量
    snprintf(cmd, sizeof(cmd), "tc filter add dev %s protocol ip parent 1:0 prio 1 u32 match ip src 0.0.0.0/0 flowid 1:1",
            dev);
    if (execute_cmd(cmd) != 0) {
        fprintf(stderr, "Failed to add filter on %s\n", dev);
        return -1;
    }

    return 0;
}

void print_usage(const char *prog_name) {
    printf("Usage: %s [OPTIONS]\n", prog_name);
    printf("Options:\n");
    printf("  -u SPEED  Set upload speed limit (default: %s)\n", DEFAULT_UP_SPEED);
    printf("  -d SPEED  Set download speed limit (default: %s)\n", DEFAULT_DOWN_SPEED);
    printf("  -v        Show version\n");
    printf("  -h        Show this help message\n");
}

int main(int argc, char *argv[]) {
    const char *up_speed = DEFAULT_UP_SPEED;
    const char *down_speed = DEFAULT_DOWN_SPEED;
    int opt;

    while ((opt = getopt(argc, argv, "u:d:vh")) != -1) {
        switch (opt) {
            case 'u':
                up_speed = optarg;
                break;
            case 'd':
                down_speed = optarg;
                break;
            case 'v':
                printf("tc-limit version %s\n", VERSION);
                return 0;
            case 'h':
                print_usage(argv[0]);
                return 0;
            default:
                print_usage(argv[0]);
                return 1;
        }
    }

    // 设置上传和下载限速
    if (set_speed_limit("usb0", up_speed) != 0) {
        fprintf(stderr, "Failed to set upload speed limit\n");
        return 1;
    }

    if (set_speed_limit("br-lan", down_speed) != 0) {
        fprintf(stderr, "Failed to set download speed limit\n");
        return 1;
    }

    printf("Speed limits set successfully:\n");
    printf("Upload (usb0): %s\n", up_speed);
    printf("Download (br-lan): %s\n", down_speed);

    return 0;
}