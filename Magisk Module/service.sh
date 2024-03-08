#!/system/bin/sh
#sleep 25

nohup /data/adb/modules/3C/batteryMonitor.sh &
nohup /data/adb/modules/3C/fastcharge.sh &

while [ "$(getprop sys.boot_completed)" != "1" ]; do
    sleep 1
done

sh /data/adb/modules/3C/3c.sh applyChargingSwitch
