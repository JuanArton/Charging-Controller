#!/system/bin/sh
pid=$$

# Write the PID to a text file
echo "$pid" > "/data/adb/modules/3C/PID"

FAST_CHARGE=$(grep 'chargingCurrent' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')
FAST_CHARGE1=$(expr "$FAST_CHARGE" + 1000)
FC=$(expr "$FAST_CHARGE" \* 1000)
FCC=$(expr "$FAST_CHARGE1" \* 1000)
VERSION=$(grep 'version' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')

Set_value() {
    if [ -f "$2" ]; then
        chmod 0666 "$2"
        echo "$1" > "$2"
        chmod 0444 "$2"
    fi
}

Fast_charge() {
    paths=$(ls /sys/class/power_supply/*/"$1")
    for path in $paths; do
        Set_value "$FC" "$path"
    done
}

LIMIT_CHARGING_TRIGGERED=0

while true; do
    if [ "$VERSION" -eq 1 ]; then
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

        Set_value "$FCC" /sys/class/qcom-battery/restricted_current
        Set_value "$FCC" /sys/class/qcom-battery/restrict_cur

        Fast_charge current_max
        Fast_charge hw_current_max
        Fast_charge pd_current_max
        Fast_charge ctm_current_max
        Fast_charge constant_charge_current_max
    else
        ENABLE_CHARGING=$(grep 'enableCharging' /data/adb/modules/3C/3C.conf | awk -F '=' '{print $2}' | tr -d ' ')
        LIMIT_CHARGING_TRIGGERED=$(grep 'chargingLimitTriggered' $config_path | awk -F '=' '{print $2}' | tr -d ' ')
        controller_path="/sys/class/power_supply/battery/constant_charge_current"

        if [ "$ENABLE_CHARGING" -eq 1 ]; then
            if [ "$LIMIT_CHARGING_TRIGGERED" -eq 1 ]; then
                Set_value '0' "$controller_path"
            else
                Set_value "$FC" "$controller_path"
            fi
        else
            Set_value '0' "$controller_path"
        fi
    fi

    sleep 1
done
