#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define CONFIG_FILE "/data/adb/modules/3C/3C.conf"
#define UEVENT_FILE "/sys/class/power_supply/battery/uevent"

int maxCapacity = 80;
int chargingLimit = 1;
int version = 1;

void loadConfig() {
    FILE *file = fopen(CONFIG_FILE, "r");
    if (!file) return;

    char line[128];
    while (fgets(line, sizeof(line), file)) {
        char key[64], value[64];
        if (sscanf(line, "%63[^=] = %63s", key, value) == 2) {
            if (strcmp(key, "maxCapacity") == 0)
                maxCapacity = atoi(value);
            else if (strcmp(key, "enableLimitCharging") == 0)
                chargingLimit = atoi(value);
            else if (strcmp(key, "version") == 0)
                version = atoi(value);
        }
    }
    fclose(file);
}

void setValue(const char *key, int value) {
    char cmd[256];
    snprintf(cmd, sizeof(cmd), "/data/adb/modules/3C/3c setValue %s %d", key, value);
    system(cmd);
}

void applyChargingSwitch() {
    system("/data/adb/modules/3C/3c applyChargingSwitch");
}

int main() {
    FILE *pidFile = fopen("/data/adb/modules/3C/bmPID", "w");
    if (pidFile) {
        fprintf(pidFile, "%d\n", getpid());
        fclose(pidFile);
    }

    loadConfig();

    char lastStatus[32] = "";
    char status[32], type[32];
    int capacity;

    while (1) {
        FILE *uevent = fopen(UEVENT_FILE, "r");
        if (!uevent) {
            sleep(3);
            continue;
        }

        char line[128];
        status[0] = type[0] = '\0';
        capacity = -1;

        while (fgets(line, sizeof(line), uevent)) {
            if (strncmp(line, "POWER_SUPPLY_STATUS=", 20) == 0) {
                sscanf(line + 20, "%31s", status);
            } else if (strncmp(line, "POWER_SUPPLY_CAPACITY=", 22) == 0) {
                capacity = atoi(line + 22);
            } else if (strncmp(line, "POWER_SUPPLY_CHARGE_TYPE=", 25) == 0) {
                sscanf(line + 25, "%31s", type);
            }
        }
        fclose(uevent);

        if (strcmp(status, lastStatus) != 0) {
            if (strcmp(status, "Charging") == 0 && capacity >= maxCapacity && chargingLimit == 1) {
                setValue("enableCharging", 0);
                setValue("chargingLimitTriggered", 1);
                if (version == 1) {
                    applyChargingSwitch();
                }
            } else if (strcmp(status, "Discharging") == 0) {
                setValue("enableCharging", 1);
                setValue("chargingLimitTriggered", 0);
                if (version == 1) {
                    applyChargingSwitch();
                }
            }
            strcpy(lastStatus, status);
        }

        sleep(3);
    }

    return 0;
}
