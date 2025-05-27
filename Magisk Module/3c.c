#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define FILEPATH "/data/adb/modules/3C"
#define FASTCHARGE_PATH FILEPATH "/fastcharge"
#define BM_SCRIPT_PATH FILEPATH "/batteryMonitor.sh"
#define PID_PATH FILEPATH "/PID"
#define BM_PID_PATH FILEPATH "/bmPID"
#define CONF_PATH FILEPATH "/3C.conf"
#define CHARGING_PATH "/sys/class/power_supply/battery/charging_enabled"

void restartCurrentController() {
    FILE *pidFile = fopen(PID_PATH, "r");
    if (pidFile) {
        int pid;
        if (fscanf(pidFile, "%d", &pid) == 1) {
            char killCmd[64];
            snprintf(killCmd, sizeof(killCmd), "kill %d", pid);
            system(killCmd);
        }
        fclose(pidFile);
    }
    system("nohup " FASTCHARGE_PATH " >/dev/null 2>&1 &");
}

void killBatteryMonitor() {
    FILE *pidFile = fopen(BM_PID_PATH, "r");
    if (pidFile) {
        int pid;
        if (fscanf(pidFile, "%d", &pid) == 1) {
            char killCmd[64];
            snprintf(killCmd, sizeof(killCmd), "kill %d", pid);
            system(killCmd);
        }
        fclose(pidFile);
    }
}

void restartBatteryMonitor() {
    killBatteryMonitor();
    system("nohup " BM_SCRIPT_PATH " >/dev/null 2>&1 &");
}

void chargingSwitch() {
    FILE *conf = fopen(CONF_PATH, "r");
    if (!conf) {
        perror("Failed to open config");
        return;
    }

    char line[256];
    char value[16] = "0";

    while (fgets(line, sizeof(line), conf)) {
        if (strstr(line, "enableCharging") == line) {
            char *equal = strchr(line, '=');
            if (equal) {
                sscanf(equal + 1, "%s", value);
            }
            break;
        }
    }
    fclose(conf);

    char chmodCmd[128];
    snprintf(chmodCmd, sizeof(chmodCmd), "chmod 644 %s", CHARGING_PATH);
    system(chmodCmd);

    FILE *chargeFile = fopen(CHARGING_PATH, "w");
    if (chargeFile) {
        fprintf(chargeFile, "%s", value);
        fclose(chargeFile);
    }

    snprintf(chmodCmd, sizeof(chmodCmd), "chmod 444 %s", CHARGING_PATH);
    system(chmodCmd);
}

void setValue(const char *config, const char *val) {
    char sedCmd[512];
    snprintf(sedCmd, sizeof(sedCmd),
             "sed -i 's/^%s = .*/%s = %s/' %s",
             config, config, val, CONF_PATH);
    system(sedCmd);
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        printf("No argument given.\n");
        return 1;
    }

    if (strcmp(argv[1], "restartCurrentController") == 0) {
        restartCurrentController();
    } else if (strcmp(argv[1], "applyChargingSwitch") == 0) {
        chargingSwitch();
    } else if (strcmp(argv[1], "restartBatteryMonitor") == 0) {
        restartBatteryMonitor();
    } else if (strcmp(argv[1], "killBatteryMonitor") == 0) {
        killBatteryMonitor();
    } else if (strcmp(argv[1], "setValue") == 0) {
        if (argc != 4) {
            printf("Usage: %s setValue key value\n", argv[0]);
            return 1;
        }
        setValue(argv[2], argv[3]);
    } else {
        printf("Invalid argument.\n");
        return 1;
    }

    return 0;
}
