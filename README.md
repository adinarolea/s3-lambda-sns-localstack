# s3-lambda-sns-localstack
An example on how to use localstack to simulate a real AWS environment. The project will detect the bucket change, trigger a lambda and lambda will publish in a sns topic

Check which services are running

``
curl localhost:4566/health
``

Create s3 bucket

``
  aws --endpoint=http://localhost:4566 s3 ls
  aws --endpoint=http://localhost:4566 s3api create-bucket --bucket cool-camel --region us-east-1

``

Create lambda function
``./mvnw  clean package shade:shade``

List lambda functions

``
   aws --endpoint=http://localhost:4566 lambda list-functions
``

Create lambda function in localstack

``
 aws --endpoint=http://localhost:4566 lambda create-function --function-name s3_change_handler \
 --zip-file fileb://s3-lambda-function/target/s3-lambda-function-1.0-SNAPSHOT.jar \
 --handler com.tutorial.ar.lambda.S3ChangeLambdaFunction --runtime java8 --role arn:aws:iam::123456789012:role/lambda-cli-role
 ``
 
 Update bucket configuration to send notifications
 
 ``
 aws --endpoint=http://localhost:4566 s3api put-bucket-notification-configuration --bucket cool-camel \
  --notification-configuration file://config/notification.json
``

Add a file to the bucket

 ``
 aws --endpoint=http://localhost:4566 s3 cp config/camel.json s3://cool-camel/camel1.json
``