#!/usr/bin/env bash

if [[ $# -eq 0 ]] ; then
    echo 'No AVD name provided'
    exit 1
fi

if ! [[ $(adb devices | grep 'emulator') = *emulator* ]]; then
	emulator -avd $1 &

	OUT=`adb shell getprop init.svc.bootanim`
	RES="stopped"

	while [[ ${OUT:0:7}  != 'stopped' ]]; do
    		OUT=`adb shell getprop init.svc.bootanim`
    		echo 'Waiting for emulator to fully boot...'
    		sleep 5
	done

	adb shell input keyevent 82
fi
