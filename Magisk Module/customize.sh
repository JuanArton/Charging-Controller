ui_print "3C - Charging Current Controller"
ui_print "Installing ... "

set_perm $MODPATH/service.sh 0 0 0777
set_perm $MODPATH/fastcharge.sh 0 0 0777
set_perm $MODPATH/3c.sh 0 0 0777
set_perm $MODPATH/batteryMonitor.sh 0 0 0777
set_perm $MODPATH/system/bin/bindfs 0 0 0777
set_perm $MODPATH/system/bin/fusermount 0 0 0777
