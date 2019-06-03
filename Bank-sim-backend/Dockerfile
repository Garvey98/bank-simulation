FROM gradle:jdk8
ADD * /home/gradle/
RUN gradle build
CMD ["gradle", "run"]
EXPOSE 8001
