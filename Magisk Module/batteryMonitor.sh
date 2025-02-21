#!/system/bin/sh

pid=$$
echo "$pid" > "/data/adb/modules/3C/bmPID"
firstRun=1
firstRRun=1

file_path="/sys/class/power_supply/battery/uevent"
maxCapacity=$(grep 'maxCapacity' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')
chargingLimit=$(grep 'enableLimitCharging' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')
version=$(grep 'version' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')

while true; do

    while IFS='=' read -r key value; do
        case $key in
            "POWER_SUPPLY_STATUS") STATUS="$value" ;;
            "POWER_SUPPLY_CAPACITY") CAPACITY="$value" ;;
            "POWER_SUPPLY_CHARGE_TYPE") TYPE="$value" ;;
        esac
    done < "$file_path"

    if [ "$STATUS" = "Charging" ] && [ "$CAPACITY" -ge "$maxCapacity" ] &&  [ $chargingLimit -eq 1 ]; then
        if [ $firstRun -eq 1 ]; then
            /data/adb/modules/3C/3c.sh setValue enableCharging 0
            /data/adb/modules/3C/3c.sh setValue chargingLimitTriggered 1

            if ["$version" -eq 1]; then
                /data/adb/modules/3C/3c.sh applyChargingSwitch
            fi

            firstRun=0
        fi
    elif [ "$STATUS" = "Discharging" ]; then
        if [ $firstRRun -eq 1 ]; then
            /data/adb/modules/3C/3c.sh setValue enableCharging 1
            /data/adb/modules/3C/3c.sh setValue chargingLimitTriggered 0

            if ["$version" -eq 1]; then
                /data/adb/modules/3C/3c.sh applyChargingSwitch
            fi

            firstRRun=0
        fi
    elif [ "$STATUS" = "Charging" ] || [ "$STATUS" = "Not charging" ]; then
        firstRRun=1
        firstRun=1
    fi

    sleep 3
done
