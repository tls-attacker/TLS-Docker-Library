FROM golang:1.9-alpine as go

COPY tls_server.go /go/src/tls_server/tls_server.go
RUN CGO_ENABLED=0 go install -ldflags="-s -w" tls_server

FROM entrypoint
COPY --from=go /go/bin/tls_server /bin/tls_server
ENTRYPOINT ["server-entrypoint", "/bin/tls_server"]
