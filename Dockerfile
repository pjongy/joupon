FROM openjdk:12 as builder
COPY . /workspace
WORKDIR /workspace
RUN ./gradlew installDist

FROM openjdk:12
WORKDIR /root
COPY --from=builder /workspace/build/install/joupon ./joupon
CMD ["./joupon/bin/joupon", "run", "com.github.pjongy.MainVerticle"]
