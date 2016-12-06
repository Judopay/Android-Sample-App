#!/usr/bin/expect -f

#if [[ $(adb devices | grep 'emulator') = *emulator* ]]; then
        set fp [open "~/.emulator_console_auth_token" r]
        set data [read $fp]
        spawn telnet localhost 5554
        expect "OK"
        send "auth $data\r"
        expect "OK"
        send "kill\r"
#fi