#!/system/bin/sh
pid=$$

# Write the PID to a text file
echo "$pid" > "/data/adb/modules/3C/PID"

FAST_CHARGE=$(grep 'chargingCurrent' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')
FAST_CHARGE1=`expr $FAST_CHARGE + 1000`
FC=`expr $FAST_CHARGE \* 1000`
FCC=`expr $FAST_CHARGE1 \* 1000`

Set_value()
{
    if [[ -f "$2" ]];
        then
            chmod 0666 "$2"
            echo "$1" > "$2"
            chmod 0444 "$2"
    fi
}

Fast_charge() {
    paths=`ls /sys/class/power_supply/*/$1`
        for path in $paths
            do
            Set_value $FC $path
    done
}

while true; do
    Set_value '1' /sys/kernel/fast_charge/force_fast_charge
    Set_value '1' /sys/class/power_supply/battery/system_temp_level
    Set_value '1' /sys/kernel/fast_charge/failsafe
    Set_value '1' /sys/class/power_supply/battery/allow_hvdcp3
    Set_value '1' /sys/class/power_supply/usb/pd_allowed
    Set_value '1' /sys/class/power_supply/battery/subsystem/usb/pd_allowed
    Set_value '0' /sys/class/power_supply/battery/input_current_limited
    Set_value '1' /sys/class/power_supply/battery/input_current_settled
    Set_value '0' /sys/class/qcom-battery/restricted_charging
    Set_value '0' /sys/class/qcom-battery/restrict_chg

    Set_value $FCC /sys/class/qcom-battery/restricted_current
    Set_value $FCC /sys/class/qcom-battery/restrict_cur

    Fast_charge current_max
    Fast_charge hw_current_max
    Fast_charge pd_current_max
    Fast_charge ctm_current_max
    Fast_charge constant_charge_current_max

    sleep 5
done

