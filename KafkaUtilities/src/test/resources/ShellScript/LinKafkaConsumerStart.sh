#!/bin/bash
# redirect stdout/stderr to a file
frameworkpath=`pwd`
exec >$frameworkpath/target/test-classes/kafkamessage.json 2>&1

#cd /home/ec2-user/Kafka/bin
cd $1
./kafka-console-consumer.sh --bootstrap-server $2 --topic $3 --from-beginning --consumer.config $4
echo $! > $frameworkpath/target/test-classes/KafkaConsumerPID.txt

