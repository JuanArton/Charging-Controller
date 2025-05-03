#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <limits.h>
#include <errno.h>
#include <time.h>

#define CONFIG_PATH "/data/adb/modules/3C/3C.conf"
#define PID_PATH "/data/adb/modules/3C/PID"
#define LOG_PATH "/data/adb/modules/3C/log.txt"

void log_message(const char *message) {
    FILE *log_file;
    struct stat st;
    time_t now;
    char *time_str;

    if (stat(LOG_PATH, &st) == 0 && st.st_size >= 1048576) {
        log_file = fopen(LOG_PATH, "w");
        if (log_file) {
            fprintf(log_file, "[LOG ROTATED]\n");
            fclose(log_file);
        }
    }

    log_file = fopen(LOG_PATH, "a");
    if (log_file) {
        now = time(NULL);
        time_str = ctime(&now);
        time_str[strlen(time_str)-1] = '\0';
        fprintf(log_file, "[%s] %s\n", time_str, message);
        fclose(log_file);
    }
}

void Set_value(const char *value, const char *path) {
    char log_buf[512];
    int fd = open(path, O_WRONLY);
    if (fd < 0) {
        snprintf(log_buf, sizeof(log_buf), "Failed to open %s: %s", path, strerror(errno));
        log_message(log_buf);
        return;
    }
    write(fd, value, strlen(value));
    close(fd);
    snprintf(log_buf, sizeof(log_buf), "Wrote '%s' to %s", value, path);
    log_message(log_buf);
}

void Fast_charge(const char *name, const char *fc_value) {
    char command[256];
    FILE *fp;
    char path[PATH_MAX];

    snprintf(command, sizeof(command), "ls /sys/class/power_supply/*/%s 2>/dev/null", name);
    fp = popen(command, "r");
    if (fp == NULL) {
        log_message("Failed to run ls command for Fast_charge");
        return;
    }

    while (fgets(path, sizeof(path), fp) != NULL) {
        path[strcspn(path, "\n")] = 0;
        Set_value(fc_value, path);
    }

    pclose(fp);
}

char *get_config_value(const char *key) {
    static char value[128];
    FILE *file = fopen(CONFIG_PATH, "r");
    char line[256];

    if (!file) {
        log_message("Failed to open config file");
        return NULL;
    }

    while (fgets(line, sizeof(line), file)) {
        if (strncmp(line, key, strlen(key)) == 0) {
            char *eq = strchr(line, '=');
            if (eq) {
                strncpy(value, eq + 1, sizeof(value)-1);
                value[strcspn(value, "\n")] = 0;

                char *src = value, *dst = value;
                while (*src) {
                    if (*src != ' ') *dst++ = *src;
                    src++;
                }
                *dst = 0;
                fclose(file);
                return value;
            }
        }
    }

    fclose(file);
    return NULL;
}

int main() {
    pid_t pid = getpid();
    char log_buf[256];

    snprintf(log_buf, sizeof(log_buf), "PID: %d", pid);
    log_message(log_buf);

    FILE *pid_file = fopen(PID_PATH, "w");
    if (pid_file) {
        fprintf(pid_file, "%d\n", pid);
        fclose(pid_file);
    }

    char *fast_charge_str = get_config_value("chargingCurrent");
    if (!fast_charge_str) return 1;
    int FAST_CHARGE = atoi(fast_charge_str);
    int FAST_CHARGE1 = FAST_CHARGE + 1000;
    int FC = FAST_CHARGE * 1000;
    int FCC = FAST_CHARGE1 * 1000;

    char *version_str = get_config_value("version");
    if (!version_str) return 1;
    int VERSION = atoi(version_str);

    while (1) {
        if (VERSION == 1) {
            Set_value("1", "/sys/kernel/fast_charge/force_fast_charge");
            Set_value("1", "/sys/class/power_supply/battery/system_temp_level");
            Set_value("1", "/sys/kernel/fast_charge/failsafe");
            Set_value("1", "/sys/class/power_supply/battery/allow_hvdcp3");
            Set_value("1", "/sys/class/power_supply/usb/pd_allowed");
            Set_value("1", "/sys/class/power_supply/battery/subsystem/usb/pd_allowed");
            Set_value("0", "/sys/class/power_supply/battery/input_current_limited");
            Set_value("1", "/sys/class/power_supply/battery/input_current_settled");
            Set_value("0", "/sys/class/qcom-battery/restricted_charging");
            Set_value("0", "/sys/class/qcom-battery/restrict_chg");

            char fc_buf[16];
            snprintf(fc_buf, sizeof(fc_buf), "%d", FCC);
            Set_value(fc_buf, "/sys/class/qcom-battery/restricted_current");
            Set_value(fc_buf, "/sys/class/qcom-battery/restrict_cur");

            char fc_buf2[16];
            snprintf(fc_buf2, sizeof(fc_buf2), "%d", FC);
            Fast_charge("current_max", fc_buf2);
            Fast_charge("hw_current_max", fc_buf2);
            Fast_charge("pd_current_max", fc_buf2);
            Fast_charge("ctm_current_max", fc_buf2);
            Fast_charge("constant_charge_current_max", fc_buf2);
        } else {
            char *enable_charging_str = get_config_value("enableCharging");
            if (!enable_charging_str) continue;
            int ENABLE_CHARGING = atoi(enable_charging_str);

            char *limit_triggered_str = get_config_value("chargingLimitTriggered");
            if (!limit_triggered_str) continue;
            int LIMIT_TRIGGERED = atoi(limit_triggered_str);

            char controller_real[PATH_MAX];
            if (realpath("/sys/class/power_supply/battery/constant_charge_current", controller_real)) {
                char fc_buf3[16];
                snprintf(fc_buf3, sizeof(fc_buf3), "%d", FC);

                if (ENABLE_CHARGING == 1) {
                    if (LIMIT_TRIGGERED == 1) {
                        Set_value("0", controller_real);
                    } else {
                        Set_value(fc_buf3, controller_real);
                    }
                } else {
                    Set_value("0", controller_real);
                }
            } else {
                snprintf(log_buf, sizeof(log_buf), "Failed to resolve constant_charge_current: %s", strerror(errno));
                log_message(log_buf);
            }
        }

        sleep(1);
    }

    return 0;
}
