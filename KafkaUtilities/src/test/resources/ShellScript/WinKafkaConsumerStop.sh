#!/bin/sh

a=0
timer=0
totalpid=0

user=`whoami | cut -c1-8`
until [ $a -eq 1 -o $timer -eq 30 ]
do
        pid=`ps -ef | grep $user| grep $1 | grep -v "grep"| awk '{print $2}'`
        totalpid=`echo $pid | wc -w`
        if [ $totalpid == 1 ]
        then
                killpid=`echo $pid | awk '{print $1}'`
                echo $killpid
                echo kafka PID $killpid
                #sleep 10
                #echo a = $a
                kill -9 $killpid
                a=1
        fi
        timer=`expr $timer + 1`
        sleep 1
        #echo time = $timer

done

 if [ $totalpid -lt 1 ]
then
  echo Kafka consumer was not running
fi
