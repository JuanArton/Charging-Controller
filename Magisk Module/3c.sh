#!/system/bin/sh

filepath="/data/adb/modules/3C"

function restartCurrentController() {
    pid=$(head -n 1 "/data/adb/modules/3C/PID")
    kill "$pid"
    nohup /data/adb/modules/3C/fastcharge.sh &
}

function killBateryMonitor() {
    pid=$(head -n 1 "/data/adb/modules/3C/bmPID")
    kill "$pid"
}

function restartBatteryMonitor() {
    killBateryMonitor
    nohup /data/adb/modules/3C/batteryMonitor.sh &
}

function chargingSwitch() {
    filepath="/sys/devices/platform/soc/c440000.qcom,spmi/spmi-0/spmi0-00/c440000.qcom,spmi:qcom,pm6150@0:qcom,qpnp-smb5/power_supply/battery/charging_enabled"
    charging=$(grep 'enableCharging' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')
    chmod 644 $filepath
    echo $charging > $filepath
    chmod 444 $filepath
}

function setValue() {
    config=$1
    value=$2

    sed -i "s/$config = .*/$config = $value/" $filepath/3C.conf
}

# Check the argument and call the appropriate function
if [ "$1" == "restartCurrentController" ]; then
    restartCurrentController
elif [ "$1" == "applyChargingSwitch" ]; then
    chargingSwitch
elif [ "$1" == "restartBatteryMonitor" ]; then
    restartBatteryMonitor
elif [ "$1" == "killBateryMonitor" ]; then
    killBateryMonitor
elif [ "$1" == "setValue" ]; then
    shift
    setValue "$@"
else
    echo "Invalid argument"
fi
