# Stage 1: Build the application with SBT
FROM ghcr.io/graalvm/graalvm-community:21 AS builder

# Install SBT
RUN curl -L https://github.com/sbt/sbt/releases/download/v1.10.6/sbt-1.10.6.tgz | tar -xz -C /opt && \
    ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt

WORKDIR /build

# Copy build files first (for better caching)
COPY build.sbt ./
COPY project ./project
COPY .scalafmt.conf ./

# Fetch dependencies (cached layer)
RUN sbt compile

# Copy source code
COPY app ./app
COPY content ./content
COPY static ./static

# Build fat JAR
RUN sbt assembly

# Build native image
RUN sbt nativeImage && \
    cp target/native-image/homepage ./homepage

# Stage 2: Minimal runtime image (scratch)
FROM scratch

# Copy the native executable
COPY --from=builder /build/homepage /homepage

# Expose port (adjust as needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["/homepage"]
