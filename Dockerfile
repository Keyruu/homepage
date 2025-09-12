# Stage 1: Build the application with Mill
FROM ghcr.io/graalvm/graalvm-community:21 AS builder

# Install Mill
RUN curl -L https://github.com/com-lihaoyi/mill/releases/download/0.12.5/0.12.5 > /usr/local/bin/mill && \
    chmod +x /usr/local/bin/mill

WORKDIR /build

# Copy build files first (for better caching)
COPY build.sc ./
COPY .scalafmt.conf ./

# Fetch dependencies (cached layer)
RUN mill app.compile

# Copy source code
COPY homepage ./homepage
COPY graalvm-config ./graalvm-config

# Build fat JAR
RUN mill app.assembly

# Build native image
RUN native-image \
    -jar out/app/assembly.dest/out.jar \
    -o homepage \
    -H:ConfigurationFileDirectories=graalvm-config/META-INF/native-image \
    --no-fallback \
    --enable-http \
    --enable-https \
    --static \
    --libc=musl \
    -H:+ReportExceptionStackTraces

# Stage 2: Minimal runtime image (scratch)
FROM scratch

# Copy the native executable
COPY --from=builder /build/homepage /homepage

# Expose port (adjust as needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["/homepage"]