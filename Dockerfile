# Dockerfile
FROM java:8
MAINTAINER ysxu
ADD target/nb-encrypted-test-server-0.0.1-SNAPSHOT.jar app.jar
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
ENTRYPOINT ["java","-jar","app.jar"]